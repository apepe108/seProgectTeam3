package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.UserDecorator;
import it.unisa.diem.se.team3.models.JsonUtil;
import it.unisa.diem.se.team3.models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "UserServlet", urlPatterns = {"/user", "/edit-user", "/create-user", "/delete-user"})
public class UserServlet extends HttpServlet {
    private UserDecorator db;

    @Override
    public void init() {
        db = new UserDecorator(ServletUtil.connectDb());
        db.connect();
    }

    @Override
    public void destroy() {
        db.disconnect();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String path = ServletUtil.getRequestPath(request);
        // Add workspace
        if ("/create-user".equals(path)) {
            create(request, response);
        } else if ("/edit-user".equals(path)) {
            edit(request, response);
        }
        response.flushBuffer();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String path = ServletUtil.getRequestPath(request);
        if ("/user".equals(path)) {
            view(request, response);
        } else if ("/delete-user".equals(path)) {
            delete(request, response);
        }
        response.flushBuffer();
    }

    /**
     * Check the entered parameters and, if they are correct, add them. Otherwise it returns an error to the client.
     *
     * @param req: the HttpServletRequest from which to get the parameters;
     * @param res: the HttpServletResponse to return the response to.
     * @throws IOException if a communication error occurs with the client.
     */
    private void view(HttpServletRequest req, HttpServletResponse res) throws IOException {
        res.setContentType("application/json");
        String id = req.getParameter("id");
        // Get single
        if (id != null) {
            renderOne(res, Long.parseLong(id));
            // Get all list
        } else {
            renderAll(res);
        }
    }

    /**
     * Returns the requested object in JSON format to the client.
     *
     * @param res: the HttpServletResponse to return the response to.
     * @param id:  the id of the requested object.
     * @throws IOException if a communication error occurs with the client.
     */
    private void renderOne(HttpServletResponse res, long id) throws IOException {
        User wn = db.getUsers(id);
        if (wn != null) {
            // get success
            res.getWriter().print(wn.toJSON());
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Request generated an error, send error
            res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }

    /**
     * Returns the list of requested objects in JSON format to the client.
     *
     * @param res: the HttpServletResponse to return the response to.
     * @throws IOException if a communication error occurs with the client.
     */
    private void renderAll(HttpServletResponse res) throws IOException {
        List<User> wns = db.getUsers();
        if (wns != null) {
            // get success
            res.getWriter().print(JsonUtil.toJson(db.getUsers()));
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Request generated an error, send error
            res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }

    /**
     * Check the entered parameters and, if they are correct, edit them. Otherwise it returns an error to the client.
     *
     * @param req: the HttpServletRequest from which to get the parameters;
     * @param res: the HttpServletResponse to return the response to.
     */
    private void edit(HttpServletRequest req, HttpServletResponse res) {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String[] roleIds = req.getParameterValues("role-ids");
        if (id != null && name != null && email != null && password != null) {
            if (db.editUser(Long.parseLong(id), name, email, password, ServletUtil.getIdsArray(roleIds))) {
                // edit success
                res.setStatus(HttpServletResponse.SC_OK);
            } else {
                // Request generated an error, send error
                res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            }
        } else {
            // Error on parameters
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Check the entered parameters and, if they are correct, delete them. Otherwise it returns an error to the client.
     *
     * @param req: the HttpServletRequest from which to get the parameters;
     * @param res: the HttpServletResponse to return the response to.
     */
    private void delete(HttpServletRequest req, HttpServletResponse res) {
        String id = req.getParameter("id");
        if (id != null) {
            if (db.deleteUsers(Long.parseLong(id))) {
                // edit success
                res.setStatus(HttpServletResponse.SC_OK);
            } else {
                // Request generated an error, send error
                res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            }
        } else {
            // Error on parameters
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Check the entered parameters and, if they are correct, create them. Otherwise it returns an error to the client.
     *
     * @param req: the HttpServletRequest from which to get the parameters;
     * @param res: the HttpServletResponse to return the response to.
     */
    private void create(HttpServletRequest req, HttpServletResponse res) {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");
        String[] roleIds = req.getParameterValues("role-ids");
        if (name != null && email != null && password != null && role != null) {
            if (db.addUsers(name, email, password, role, ServletUtil.getIdsArray(roleIds))) {
                // add success
                res.setStatus(HttpServletResponse.SC_OK);
            } else {
                // Request generated an error, send error
                res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            }
        } else {
            // Error on parameters
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
