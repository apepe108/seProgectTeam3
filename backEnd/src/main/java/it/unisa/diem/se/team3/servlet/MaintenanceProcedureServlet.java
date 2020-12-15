package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.MaintenanceProcedureDecorator;
import it.unisa.diem.se.team3.models.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;

@WebServlet(name = "MaintenanceProcedureServlet", urlPatterns = {"/procedure", "/view-smp", "/smp"})
@MultipartConfig(maxFileSize = 16177215)  // upload file's size up to 16MB
public class MaintenanceProcedureServlet extends HttpServlet {
    private MaintenanceProcedureDecorator db;

    @Override
    public void init() {
        db = new MaintenanceProcedureDecorator(ServletUtil.connectDb());
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
        if ("/smp".equals(path)) {
            create(request, response);
        }
        response.flushBuffer();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        String path = ServletUtil.getRequestPath(request);

        if ("/procedure".equals(path)) {
            // See procedure list
            renderAll(response);
        } else if ("/view-smp".equals(path)) {
            // View PDF
            String id = request.getParameter("id");
            if (id != null) {
                renderPdf(response, Long.parseLong(id));
            } else {
                // Error on parameters
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        response.flushBuffer();
    }

    /**
     * Returns the requested object in JSON format to the client.
     *
     * @param res: the HttpServletResponse to return the response to.
     * @param id:  the id of the requested object.
     * @throws IOException if a communication error occurs with the client.
     */
    private void renderPdf(HttpServletResponse res, long id) throws IOException {
        byte[] pdf = db.getSmp(id);
        if (pdf != null) {
            // set pdf content
            res.setContentType("application/pdf");
            // success
            BufferedOutputStream bos = new BufferedOutputStream(res.getOutputStream());
            bos.write(pdf);
            bos.flush();
            bos.close();
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
        res.setContentType("application/json");
        res.getWriter().print(JsonUtil.toJson(db.getMaintenanceProcedures()));
        res.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Check the entered parameters and, if they are correct, create them. Otherwise it returns an error to the client.
     *
     * @param req: the HttpServletRequest from which to get the parameters;
     * @param res: the HttpServletResponse to return the response to.
     */
    private void create(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String id = req.getParameter("id");
        Part filePart = req.getPart("file");
        if (id != null && filePart != null) {

            // String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.
            InputStream fileContent = filePart.getInputStream();
            if (db.associateSmp(Long.parseLong(id), fileContent)) {
                // success
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
