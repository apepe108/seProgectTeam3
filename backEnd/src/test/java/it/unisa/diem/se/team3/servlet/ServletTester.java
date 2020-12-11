package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.servlet.ServletUtil;
import org.apache.log4j.Appender;
import org.apache.log4j.AsyncAppender;
import org.apache.log4j.BasicConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

public class ServletTester {
    Server server;
    private final Class<? extends HttpServlet> servlet;
    private final String[] urlPatterns;

    /**
     * This method get a Http request and read the received page.
     *
     * @param conn: An HttpURLConnection to read.
     * @return a string with the page contents.
     * @throws IOException if the connection generate an error.
     */
    public String readPage(HttpURLConnection conn) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * Get the servlet class and the urlPatterns for starting a server with that on http://localhost:8080/.
     *
     * @param servlet:     the servlet class.
     * @param urlPatterns: a string array with the url to listen.
     */
    public ServletTester(Class<? extends HttpServlet> servlet, String[] urlPatterns) {
        this.servlet = servlet;
        this.urlPatterns = urlPatterns;

        Appender appender = new AsyncAppender();
        BasicConfigurator.configure(appender);
    }

    /**
     * Setup the server and run that.
     *
     * @throws Exception if something went wrong.
     */
    public void startServer() throws Exception {
        // Create Server
        server = new Server(8080);
        ServletContextHandler context = new ServletContextHandler();
        ServletHolder defaultServ = new ServletHolder("default", servlet);
        defaultServ.setInitParameter("resourceBase", System.getProperty("user.dir"));
        defaultServ.setInitParameter("dirAllowed", "true");
        for (String url : urlPatterns) {
            context.addServlet(defaultServ, url);
        }

        SessionIdManager idManager = new DefaultSessionIdManager(server);
        server.setSessionIdManager(idManager);
        SessionHandler sessionsHandler = new SessionHandler();
        context.setHandler(sessionsHandler);

        server.setHandler(context);

        // Start Server
        server.start();
        ServletUtil.test = true;
    }

    /**
     * Shutdown the server.
     *
     * @throws Exception if something went wrong.
     */
    public void shutdownServer() throws Exception {
        server.stop();
        ServletUtil.test = false;
    }
}
