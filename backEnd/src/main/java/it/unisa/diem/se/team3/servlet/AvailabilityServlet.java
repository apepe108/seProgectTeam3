package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.AvailabilityDecorator;
import it.unisa.diem.se.team3.models.AvailabilityDaily;
import it.unisa.diem.se.team3.models.AvailabilityWeekly;
import it.unisa.diem.se.team3.models.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AvailabilityServlet", urlPatterns = {"/availability-weekly", "/availability-daily"})
public class AvailabilityServlet extends HttpServlet {
    private AvailabilityDecorator db;

    @Override
    public void init() {
        db = new AvailabilityDecorator(ServletUtil.connectDb());
        db.connect();
    }

    @Override
    public void destroy() {
        db.disconnect();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String path = ServletUtil.getRequestPath(request);
        if ("/availability-weekly".equals(path)) {
            view(request, response);
        } else if ("/availability-daily".equals(path)) {
            view1(request, response);
        }
    }

    /**
     * Check the entered parameters and, if they are correct, add them. Otherwise it returns an error to the client.
     *
     * @param req: the HttpServletRequest from which to get the parameters;
     * @param res: the HttpServletResponse to return the response to.
     * @throws IOException if a communication error occurs with the client.
     */
    private void view(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String activityId = req.getParameter("activity-id");
        String maintainerId = req.getParameter("maintainer-id");

        if (activityId != null) {
            if (maintainerId != null) {
                renderOne(res, Long.parseLong(activityId), Long.parseLong(maintainerId));
            } else {
                renderAll(res, Long.parseLong(activityId));
            }
        } else {
            // No parameter
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Returns the requested object in JSON format to the client.
     *
     * @throws IOException if a communication error occurs with the client.
     */
    private void renderOne(HttpServletResponse res, long activityId, long maintainerId) throws IOException {
        AvailabilityWeekly aw = db.getAvailabilityWeekly(activityId, maintainerId);
        if (aw != null) {
            // No error, send Json
            res.setContentType("application/json");
            res.getWriter().print(aw.toJSON());
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Request generated an error, send error
            res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }

    /**
     * Returns the list of requested objects in JSON format to the client.
     *
     * @throws IOException if a communication error occurs with the client.
     */
    private void renderAll(HttpServletResponse res, long activityId) throws IOException {
        List<AvailabilityWeekly> aws = db.getAvailabilityWeekly(activityId);
        if (aws != null) {
            // No error, send Json
            res.setContentType("application/json");
            res.getWriter().print(JsonUtil.toJson(aws));
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Request generated an error, send error
            res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }

    /**
     * Check the entered parameters and, if they are correct, add them. Otherwise it returns an error to the client.
     *
     * @param req: the HttpServletRequest from which to get the parameters;
     * @param res: the HttpServletResponse to return the response to.
     * @throws IOException if a communication error occurs with the client.
     */
    private void view1(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String activityId = req.getParameter("activity-id");
        String maintainerId = req.getParameter("maintainer-id");
        String day = req.getParameter("day");

        if (activityId != null && day != null) {
            if (maintainerId != null) {
                renderOne1(res, Long.parseLong(activityId), Long.parseLong(maintainerId), Integer.parseInt(day));
            } else {
                renderAll1(res, Long.parseLong(activityId), Integer.parseInt(day));
            }
        } else {
            // No parameter
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Returns the requested object in JSON format to the client.
     *
     * @throws IOException if a communication error occurs with the client.
     */
    private void renderOne1(HttpServletResponse res, long activityId, long maintainerId, int day) throws IOException {
        AvailabilityDaily aw = db.getAvailabilityDaily(activityId, maintainerId, day);
        if (aw != null) {
            // No error, send Json
            res.setContentType("application/json");
            res.getWriter().print(aw.toJSON());
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Request generated an error, send error
            res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }

    /**
     * Returns the list of requested objects in JSON format to the client.
     *
     * @throws IOException if a communication error occurs with the client.
     */
    private void renderAll1(HttpServletResponse res, long activityId, int day) throws IOException {
        List<AvailabilityDaily> aws = db.getAvailabilityDaily(activityId, day);
        if (aws != null) {
            // No error, send Json
            res.setContentType("application/json");
            res.getWriter().print(JsonUtil.toJson(aws));
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Request generated an error, send error
            res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }
}
