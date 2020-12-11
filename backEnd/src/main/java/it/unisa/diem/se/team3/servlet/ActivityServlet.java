package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.ActivityDecorator;
import it.unisa.diem.se.team3.models.Activity;
import it.unisa.diem.se.team3.models.JsonUtil;
import it.unisa.diem.se.team3.servlet.ServletUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "ActivityServlet", urlPatterns = {"/activity", "/create-activity", "/edit-activity", "/delete-activity"})
public class ActivityServlet extends HttpServlet {
    private ActivityDecorator db;

    @Override
    public void init() {
        db = new ActivityDecorator(ServletUtil.connectDb());
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
        if ("/create-activity".equals(path)) {
            createActivity(request, response);
        } else if ("/edit-activity".equals(path)) {
            editActivity(request, response);
        }
        response.flushBuffer();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String path = ServletUtil.getRequestPath(request);
        if ("/activity".equals(path)) {
            viewActivity(request, response);
            // Delete a role
        } else if ("/delete-activity".equals(path)) {
            deleteActivity(request, response);
        }
        response.flushBuffer();
    }

    /**
     * Check the entered parameters and, if they are correct, delete them. Otherwise it returns an error to the client.
     *
     * @param req: the HttpServletRequest from which to get the parameters;
     * @param res: the HttpServletResponse to return the response to.
     */
    private void deleteActivity(HttpServletRequest req, HttpServletResponse res) {
        String id = req.getParameter("id");
        if (id != null) {
            // Do delete
            if (db.deleteActivity(Long.parseLong(id))) {
                // Send success
                res.setStatus(HttpServletResponse.SC_OK);
            } else {
                // Request generated an error, send error
                res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
            }
        } else {
            // No id passed as parameter
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Check the entered parameters and, if they are correct, add them. Otherwise it returns an error to the client.
     *
     * @param req: the HttpServletRequest from which to get the parameters;
     * @param res: the HttpServletResponse to return the response to.
     * @throws IOException if a communication error occurs with the client.
     */
    private void viewActivity(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String id = req.getParameter("id");
        String type = req.getParameter("type");
        String week = req.getParameter("week");
        String day = req.getParameter("day");
        String year = req.getParameter("year");

        if (id != null) {
            // Return single role by id
            renderOne(res, Long.parseLong(id));
        } else if (type != null && week != null && year != null) {
            renderAll(res, Long.parseLong(week), Long.parseLong(year), type.charAt(0));
        } else if (type != null) {
            // Return planned list
            renderAll(res, type);
        } else {
            // No parameter
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
        Activity activity = db.getActivity(id);
        if (activity != null) {
            // No error, send Json
            res.setContentType("application/json");
            res.getWriter().print(activity.toJSON());
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Request generated an error, send error
            res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }

    /**
     * Returns the list of requested objects in JSON format to the client.
     *
     * @param res:  the HttpServletResponse to return the response to.
     * @param type: the id of the requested object.
     * @throws IOException if a communication error occurs with the client.
     */
    private void renderAll(HttpServletResponse res, String type) throws IOException {
        List<Activity> activities = db.getActivity(type.charAt(0));
        if (activities != null) {
            // No error, send Json
            res.setContentType("application/json");
            res.getWriter().print(JsonUtil.toJson(activities));
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Request generated an error, send error
            res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }

    /**
     * Returns the list of requested objects in JSON format to the client.
     *
     * @param res:  the HttpServletResponse to return the response to.
     * @param year: the year for which the activity is planned.
     * @param week: the week for which the activity is planned.
     * @throws IOException if a communication error occurs with the client.
     */
    private void renderAll(HttpServletResponse res, long week, long year, char type) throws IOException {
        List<Activity> activityByWeek = db.getActivity(year, week, type);
        if (activityByWeek != null) {
            // No error, send Json
            res.setContentType("application/json");
            res.getWriter().print(JsonUtil.toJson(activityByWeek));
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Request generated an error, send error
            res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);

        }
    }

    /**
     * Check the entered parameters and, if they are correct, create them. Otherwise it returns an error to the client.
     *
     * @param req: the HttpServletRequest from which to get the parameters;
     * @param res: the HttpServletResponse to return the response to.
     */
    private void createActivity(HttpServletRequest req, HttpServletResponse res) {
        String year = req.getParameter("year");
        String week = req.getParameter("week");
        String day = req.getParameter("day");
        String type = req.getParameter("type");
        String interruptibility = req.getParameter("interruptibility");
        String time = req.getParameter("time");
        String description = req.getParameter("description");
        String typologyId = req.getParameter("typologyId");
        String procedureId = req.getParameter("procedureId");
        String siteId = req.getParameter("siteId");
        String workspaceDescription = req.getParameter("workspace");
        String[] longMaterials = req.getParameterValues("materials");
        String[] competencesIds = req.getParameterValues("skill-id");

        if (year != null && week != null && type != null && interruptibility != null &&
                typologyId != null && siteId != null && workspaceDescription != null) {
            if (db.addActivity(Integer.parseInt(year), Integer.parseInt(week), (day != null && !"".equals(day) ? Integer.parseInt(day) : 0),
                    type.charAt(0), Boolean.parseBoolean(interruptibility), (time != null && !"".equals(time) ? Integer.parseInt(time) : 0),
                    description, Long.parseLong(typologyId), (procedureId != null ? Long.parseLong(procedureId) : 0),
                    Long.parseLong(siteId), ServletUtil.getIdsArray(longMaterials), workspaceDescription, ServletUtil.getIdsArray(competencesIds))) {
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

    /**
     * Check the entered parameters and, if they are correct, edit them. Otherwise it returns an error to the client.
     *
     * @param req: the HttpServletRequest from which to get the parameters;
     * @param res: the HttpServletResponse to return the response to.
     */
    private void editActivity(HttpServletRequest req, HttpServletResponse res) {
        String id = req.getParameter("id");
        String year = req.getParameter("year");
        String week = req.getParameter("week");
        String day = req.getParameter("day");
        String type = req.getParameter("type");
        String interruptibility = req.getParameter("interruptibility");
        String time = req.getParameter("time");
        String description = req.getParameter("description");
        String typologyId = req.getParameter("typologyId");
        String procedureId = req.getParameter("procedureId");
        String siteId = req.getParameter("siteId");
        String workspaceDescription = req.getParameter("workspace");
        String[] longMaterials = req.getParameterValues("materials");
        String[] competencesIds = req.getParameterValues("skill-id");
        if (id != null && year != null && week != null && type != null && interruptibility != null
                && typologyId != null && siteId != null && workspaceDescription != null) {
            if (db.editActivity(Long.parseLong(id), Integer.parseInt(year), Integer.parseInt(week),
                    (day != null && !"".equals(day) ? Integer.parseInt(day) : 0),
                    type.charAt(0), Boolean.parseBoolean(interruptibility),
                    (time != null && !"".equals(time) ? Integer.parseInt(time) : 0),
                    description, Long.parseLong(typologyId), Long.parseLong(procedureId), Long.parseLong(siteId),
                    ServletUtil.getIdsArray(longMaterials), workspaceDescription, ServletUtil.getIdsArray(competencesIds))) {
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
