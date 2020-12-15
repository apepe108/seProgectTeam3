package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.AccessRecordDecorator;
import it.unisa.diem.se.team3.dbinteract.UserDecorator;
import it.unisa.diem.se.team3.models.AccessRecord;
import it.unisa.diem.se.team3.models.JsonUtil;
import it.unisa.diem.se.team3.models.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static it.unisa.diem.se.team3.servlet.ServletUtil.connectDb;
import static it.unisa.diem.se.team3.servlet.ServletUtil.getProperty;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login", "/logout", "/access-record"})
public class LoginServlet extends HttpServlet {
    private UserDecorator db;
    private AccessRecordDecorator db1;

    @Override
    public void init() {
        db = new UserDecorator(connectDb());
        db.connect();
        db1 = new AccessRecordDecorator(db);
    }

    @Override
    public void destroy() {
        db.disconnect();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String path = ServletUtil.getRequestPath(request);
        if ("/login".equals(path)) {
            login(request, response);
        } else if ("/logout".equals(path)) {
            logout(request, response);
        }
        response.flushBuffer();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String path = ServletUtil.getRequestPath(request);
        if ("/access-record".equals(path)) {
            renderAll(response);
        }
        response.flushBuffer();
    }

    /**
     * Verify if the client who request the resource is logged in and have the correct authority.
     *
     * @param req:            the client request;
     * @param authorityLevel: the roles who can access at the resources;
     * @return true if the level is correct, else false.
     */
    public static boolean verifyAuthority(HttpServletRequest req, String[] authorityLevel) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return false;
        }
        String role = ServletUtil.getRole(session);
        for (String level : authorityLevel) {
            if (level.equals(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verify current session and, in case, do login.
     *
     * @param req:  the request of login;
     * @param resp: the response at login request;
     * @throws IOException if the proprieties file not found.
     */
    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String url;

        url = doLogin(req);
        if (url == null) {
            url = ServletUtil.getProperty("httpserver.login") + "?err=true";
        }
        resp.sendRedirect(url);
    }

    /**
     * Do login with the parameter sent by the user.
     *
     * @param req: the request of login;
     * @return true if login success, otherwise false.
     */
    private String doLogin(HttpServletRequest req) {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        if (email != null && password != null) {
            User u = db.getUsers(email);
            if (u != null && u.getEmail().equals(email) && u.getPassword().equals(password)) {  // if correct auth
                HttpSession oldSession = req.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                // create new session
                HttpSession currentSession = req.getSession();
                // set attribute
                currentSession.setAttribute("role", u.getRole());
                currentSession.setAttribute("name", u.getName());
                // set time inactivity;
                currentSession.setMaxInactiveInterval(Integer.parseInt(Objects.requireNonNull(getProperty("inactivity.time"))));
                // set access id
                currentSession.setAttribute("access_id", db1.createAccess(u.getEmail(), u.getName(), u.getRole()));

                return ServletUtil.getProperty("httpserver." + u.getRole().toLowerCase());
            } else {
                // Wrong credential
                return null;
            }
        } else {
            // Error on parameters
            return null;
        }
    }

    /**
     * Invalidate session and execute logout.
     *
     * @param req:  the request of logout;
     * @param resp: teh response at the logout;
     */
    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession oldSession = req.getSession(false);
        if (oldSession != null) {
            db1.endAccess((Long) oldSession.getAttribute("access_id"));
            oldSession.invalidate();
        }
        resp.sendRedirect(ServletUtil.getProperty("httpserver.login"));
    }

    /**
     * Returns the list of requested objects in JSON format to the client.
     *
     * @param res: the HttpServletResponse to return the response to.
     * @throws IOException if a communication error occurs with the client.
     */
    private void renderAll(HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        res.setHeader("Access-Control-Allow-Origin", "*");

        // Get competencies
        List<AccessRecord> AccessRecord = db1.getAccessRecord();
        if(AccessRecord != null) {

            // If no error occur send json
            res.getWriter().print(JsonUtil.toJson(AccessRecord));
            res.setStatus(HttpServletResponse.SC_OK);
        } else {

            // Request generated an error
            res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }
}
