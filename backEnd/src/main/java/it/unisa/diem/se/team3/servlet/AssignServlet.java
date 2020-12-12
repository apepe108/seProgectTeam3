package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.ActivityDecorator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;


@WebServlet(name = "AssignServlet", urlPatterns = {"/assign"})
public class AssignServlet extends HttpServlet {
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String activityId = request.getParameter("activity-id");
        String maintainerId = request.getParameter("maintainer-id");
        String day = request.getParameter("day");
        String[] slotIds = request.getParameterValues("slot-id");
        String[] minutes = request.getParameterValues("minutes");
        if (activityId != null && maintainerId != null && day != null && slotIds != null && minutes != null) {
            doAssign(response, Long.parseLong(activityId), Long.parseLong(maintainerId), Integer.parseInt(day),
                    ServletUtil.getIdsArray(slotIds), Arrays.stream(ServletUtil.getIdsArray(minutes)).mapToInt(i -> (int) i).toArray());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void doAssign(HttpServletResponse res, long activityId, long maintainerId, int day, long[] slotIds, int[] minutes) {
        if (db.assignActivity(activityId, maintainerId, day, slotIds, minutes)) {
            res.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }
}
