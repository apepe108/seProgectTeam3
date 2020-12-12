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
            view(request, response);
        } else if ("/delete-material".equals(path)) {
            // Delete a material
            delete(request, response);
        }
        response.flushBuffer();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String path = ServletUtil.getRequestPath(request);
        // Edit materials
        if ("/edit-material".equals(path)) {
            edit(request, response);
        } else if("/create-material".equals(path)){
            create(request, response);
        }
        response.flushBuffer();
    }

    /**
     * Check the entered parameters and, if they are correct, create them. Otherwise it returns an error to the client.
     *
     * @param req: the HttpServletRequest from which to get the parameters;
     * @param res: the HttpServletResponse to return the response to.
     */
    private void create(HttpServletRequest req, HttpServletResponse res) {
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        if (name != null && description != null) {
            if (db.addMaterials(name, description)) {
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
     * Check the entered parameters and, if they are correct, add them. Otherwise it returns an error to the client.
     *
     * @param req: the HttpServletRequest from which to get the parameters;
     * @param res: the HttpServletResponse to return the response to.
     * @throws IOException if a communication error occurs with the client.
     */
    private void view(HttpServletRequest req, HttpServletResponse res) throws IOException {
        // Set response type JSON
        res.setContentType("application/json");
        String id = req.getParameter("id");
        if (id != null) {
            // Return single material by id
            renderOne(res, Long.parseLong(id));
        } else {
            // Return all material list
            renderAll(res);
        }
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

        // Get materials
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

    /**
     * Returns the requested object in JSON format to the client.
     *
     * @param res: the HttpServletResponse to return the response to.
     * @param id:  the id of the requested object.
     * @throws IOException if a communication error occurs with the client.
     */
    private void renderOne(HttpServletResponse res, long id) throws IOException {
        Materials materials = db.getMaterial(id);
        if (materials != null) {
            // No error, send Json
            res.getWriter().print(materials.toJSON());
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Request generated an error, send error
            res.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
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
            // Do delete
            if (db.deleteMaterials(Long.parseLong(id))) {
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
     * Check the entered parameters and, if they are correct, edit them. Otherwise it returns an error to the client.
     *
     * @param req: the HttpServletRequest from which to get the parameters;
     * @param res: the HttpServletResponse to return the response to.
     */
    private void edit(HttpServletRequest req, HttpServletResponse res) {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String description = req.getParameter("description");
        if (id != null && name != null && description != null) {
            if (db.editMaterials(Long.parseLong(id), name, description)) {
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
