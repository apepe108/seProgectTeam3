package it.unisa.diem.se.team3.servlet;

import it.unisa.diem.se.team3.dbinteract.DbInterface;
import it.unisa.diem.se.team3.dbinteract.PostgresDb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServletUtil {
    private static String propertyFilePath;
    public static boolean test;

    static {
        setPropertyFilePath("D:\\Alessio\\IdeaProjects\\DbService2\\src\\main\\resources\\config.properties");
        test = false;
    }

    /**
     * This function allows you to read the required parameter from the configuration file by passing its key to the method. Returns null if the parameter is not present.
     *
     * @param propertyKey :  the key of the searched parameter.
     * @return the searched value, null if the parameter is not present.
     */
    public static String getProperty(String propertyKey) {
        try (InputStream input = new FileInputStream(propertyFilePath)) {
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty(propertyKey);
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Transform an array of strings into an array of longs. If the array is null, it creates an empty long array.
     *
     * @param ids: array of strings
     * @return an array of long.
     */
    public static long[] getIdsArray(String[] ids) {
        if (ids == null) {
            return new long[0];
        }
        long[] longIds = new long[ids.length];
        for (int i = 0; i < ids.length; i++) {
            longIds[i] = Long.parseLong(ids[i]);
        }
        return longIds;
    }

    /**
     * Get internal path (inner to the context path) of a request.
     *
     * @param request: the request you would get the path.
     * @return the inner path.
     */
    public static String getRequestPath(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    /**
     * Create a new database connection.
     *
     * @return an already authenticated database interface.
     */
    public static DbInterface connectDb() {
        return new PostgresDb(getProperty("db.url"),
                getProperty("db.user"),
                getProperty("db.password"));
    }

    /**
     * Set the path of the .property configuration file from where to get the information useful to the application
     * (database credentials, url of the http server pages).
     *
     * @param propertyFilePath: the path of the .property configuration file
     */
    public static void setPropertyFilePath(String propertyFilePath) {
        ServletUtil.propertyFilePath = propertyFilePath;
    }

    /**
     * @param sess: the request.
     * @return the role of the request
     */
    public static String getRole(HttpSession sess) {
        return (String) sess.getAttribute("role");
    }

}