package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.CompetenciesRoleDecorator;
import it.unisa.diem.se.team3.models.CompetenciesRole;
import it.unisa.diem.se.team3.models.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CompetenciesRoleServlet", urlPatterns = {"/role-competencies", "/edit-role-competencies"})
public class CompetenciesRoleServlet extends HttpServlet {
    private CompetenciesRoleDecorator db;

    @Override
    public void init() {
        db = new CompetenciesRoleDecorator(ServletUtil.connectDb());
        db.connect();
    }

    @Override
    public void destroy() {
        db.disconnect();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String path = ServletUtil.getRequestPath(request);
        //  Edit role competence
        if ("/edit-role-competencies".equals(path)) {
            editCompetenciesRole(request, response);
        }
        response.flushBuffer();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");

        String path = ServletUtil.getRequestPath(request);
        // Get role data
        if ("/role-competencies".equals(path)) {
            viewActivity(request, response);
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
    private void viewActivity(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Set response type JSON
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
        CompetenciesRole cr = db.getCompetenciesRole(id);
        if (cr != null) {
            // get success
            res.getWriter().print(cr.toJSON());
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
        List<CompetenciesRole> crs = db.getCompetenciesRole();
        if (crs != null) {
            // get success
            res.getWriter().print(JsonUtil.toJson(crs));
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
    private void editCompetenciesRole(HttpServletRequest req, HttpServletResponse res) {
        String roleId = req.getParameter("id-role");
        if (roleId != null) {
            long longRoleId = Long.parseLong(roleId);
            long[] longCompetencesIds = ServletUtil.getIdsArray(req.getParameterValues("id-competence"));
            if (db.editCompetence(longRoleId, longCompetencesIds)) {
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
}
