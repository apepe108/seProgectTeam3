package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.WorkspaceNotesDecorator;
import it.unisa.diem.se.team3.models.JsonUtil;
import it.unisa.diem.se.team3.models.WorkspaceNotes;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "WorkspaceNotesServlet", urlPatterns = {"/workspaces", "/edit-workspaces", "/create-workspaces", "/delete-workspaces"})
public class WorkspaceNotesServlet extends HttpServlet {
    private WorkspaceNotesDecorator db;

    @Override
    public void init() {
        db = new WorkspaceNotesDecorator(ServletUtil.connectDb());
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
        // Add workspace
        if ("/create-workspaces".equals(path)) {
            create(request, response);
        } else if ("/edit-workspaces".equals(path)) {
            edit(request, response);
        }
        response.flushBuffer();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String path = ServletUtil.getRequestPath(request);
        if ("/workspaces".equals(path)) {
            view(request, response);
        } else if ("/delete-workspaces".equals(path)) {
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
        WorkspaceNotes wn = db.getWorkspaceNotes(id);
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
        List<WorkspaceNotes> wns = db.getWorkspaceNotes();
        if (wns != null) {
            // get success
            res.getWriter().print(JsonUtil.toJson(db.getWorkspaceNotes()));
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
        String description = req.getParameter("description");
        long[] siteIds = ServletUtil.getIdsArray(req.getParameterValues("id-site"));
        if (id != null && description != null) {
            if (db.editWorkspaceNotes(Long.parseLong(id), description, siteIds)) {
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
            if(db.deleteWorkspaceNotes(Long.parseLong(id))) {
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
        String description = req.getParameter("description");
        long[] siteIds = ServletUtil.getIdsArray(req.getParameterValues("id-site"));
        if (description != null) {
            if(db.addWorkspaceNotes(description, siteIds)) {
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
