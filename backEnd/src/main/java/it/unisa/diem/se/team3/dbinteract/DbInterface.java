package it.unisa.diem.se.team3.dbinteract;

import java.sql.Connection;

public interface DbInterface {

    /**
     * This method connects the current object to a database, making them usable.
     */
    void connect();

    /**
     * This method disconnects the current object to a database, making them unusable until that is reconnected.
     */
    void disconnect();

    /**
     * This method indicates whether the current object is already connected to the database.
     *
     * @return true if is connected, else false.
     */
    boolean isConnected();

    /**
     * Getter method for the connection object.
     *
     * @return the SQLConnection object representing the connection to the database.
     */
    Connection getConn();
}
