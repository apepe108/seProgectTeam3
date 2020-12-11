package it.unisa.diem.se.team3.dbinteract;

import java.sql.Connection;

/**
 * Abstract class used to represent the base decorator class.
 */
public abstract class DbDecorator implements DbInterface {
    private final DbInterface db;

    /**
     * Base constructor, used to get an instance of the DbInterface interface used by the decorator classes.
     *
     * @param db: a concrete implementation of DbInterface.
     */
    public DbDecorator(DbInterface db) {
        super();
        this.db = db;
    }

    /**
     * This method connects the current object to a database, making them usable.
     */
    @Override
    public void connect() {
        db.connect();
    }

    /**
     * This method disconnects the current object to a database, making them unusable until that is reconnected.
     */
    @Override
    public void disconnect() {
        db.disconnect();
    }

    /**
     * This method indicates whether the current object is already connected to the database.
     *
     * @return true if is connected, else false.
     */
    @Override
    public boolean isConnected() {
        return db.isConnected();
    }

    /**
     * Getter method for the connection object.
     *
     * @return the SQLConnection object representing the connection to the database.
     */
    @Override
    public Connection getConn() {
        return db.getConn();
    }
}
