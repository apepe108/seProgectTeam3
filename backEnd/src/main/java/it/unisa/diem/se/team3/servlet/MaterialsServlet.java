package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.MaterialsDecorator;
import it.unisa.diem.se.team3.models.JsonUtil;
import it.unisa.diem.se.team3.models.Materials;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "MaterialsServlet", urlPatterns = {"/material", "/edit-material", "/delete-material", "/create-material"})
public class MaterialsServlet extends HttpServlet {
    private MaterialsDecorator db;

    @Override
    public void init() {
        db = new MaterialsDecorator(ServletUtil.connectDb());
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
        // Get material
        if ("/material".equals(path)) {
            renderAll(response);
        }
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

        // Get competencies
        List<Materials> materials = db.getMaterials();
        if(materials != null) {
            // If no error occur send json
            res.getWriter().print(JsonUtil.toJson(materials));
            res.setStatus(HttpServletResponse.SC_OK);
        } else {

            // Request generated an error
            res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
        }
    }
}
