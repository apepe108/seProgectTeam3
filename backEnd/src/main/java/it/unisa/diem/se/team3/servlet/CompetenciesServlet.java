package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.CompetenciesDecorator;
import it.unisa.diem.se.team3.models.Competencies;
import it.unisa.diem.se.team3.models.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "CompetenciesServlet", urlPatterns = {"/competencies"})
public class CompetenciesServlet extends HttpServlet {
    private CompetenciesDecorator db;

    @Override
    public void init() {
        db = new CompetenciesDecorator(ServletUtil.connectDb());
        db.connect();
    }

    @Override
    public void destroy() {
        db.disconnect();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set response type JSON
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        renderAll(response);
        response.flushBuffer();
    }

    /**
     * Returns the list of requested objects in JSON format to the client.
     *
     * @param res:  the HttpServletResponse to return the response to.
     * @throws IOException if a communication error occurs with the client.
     */
    private void renderAll(HttpServletResponse res) throws IOException {
        List<Competencies> competencies = db.getCompetencies();
        if(competencies != null) {
            // If no error occur send json
            res.getWriter().print(JsonUtil.toJson(competencies));
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Request generated an error
            res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }
}
