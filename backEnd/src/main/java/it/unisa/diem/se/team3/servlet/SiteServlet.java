package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.SiteDecorator;
import it.unisa.diem.se.team3.models.JsonUtil;
import it.unisa.diem.se.team3.models.Site;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "SiteServlet", urlPatterns = {"/site"})
public class SiteServlet extends HttpServlet {
    private SiteDecorator db;

    @Override
    public void init() {
        db = new SiteDecorator(ServletUtil.connectDb());
        db.connect();
    }

    @Override
    public void destroy() {
        db.disconnect();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        renderAll(response);
        response.flushBuffer();
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
        // Get sites
        List<Site> sites = db.getSite();
        if(sites != null) {
            // If no error occur send json
            res.getWriter().print(JsonUtil.toJson(sites));
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Request generated an error
            res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }
}
