package it.unisa.diem.se.team3.dbinteract;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class that implements the main utilities for using a postgresql database. It can be enriched to specify detailed
 * methods based on information.
 */
public class PostgresDb implements DbInterface {
    private final String url;
    private final String userDb;
    private final String password;
    private Connection conn;
    private boolean connected;

    /**
     * The object represents a particular Postgresql database connection, which will be created based on the specified
     * parameters.
     *
     * @param url:      the server and database to connect to.
     * @param userDb:   the user profile to use for the connection.
     * @param password: the password for the given user profile.
     */
    public PostgresDb(String url, String userDb, String password) {
        this.url = url;
        this.userDb = userDb;
        this.password = password;
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ignored) {
        }
        this.connected = false;
    }

    /**
     * This method connects the current object to a database, making them usable.
     */
    @Override
    public void connect() {
        if (!connected) {
            try {
                conn = DriverManager.getConnection(url, userDb, password);
            } catch (SQLException e) {
                return;
            }
            connected = true;
        }
    }

    /**
     * This method disconnects the current object to a database, making them unusable until that is reconnected.
     */
    @Override
    public void disconnect() {
        if (connected) {
            try {
                conn.close();
            } catch (SQLException e) {
                return;
            }
            connected = false;
        }
    }

    /**
     * This method indicates whether the current object is already connected to the database.
     *
     * @return true if is connected, else false.
     */
    @Override
    public boolean isConnected() {
        return connected;
    }

    /**
     * Getter method for the connection object.
     *
     * @return the SQLConnection object representing the connection to the database.
     */
    @Override
    public Connection getConn() {
        return conn;
    }
}
