package it.unisa.diem.se.team3.dbinteract;

import it.unisa.diem.se.team3.dbinteract.PostgresDb;
import it.unisa.diem.se.team3.servlet.ServletUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostgresDbTest {
    private PostgresDb db;

    @BeforeEach
    void setUp() {
        ServletUtil.setPropertyFilePath("./src/test/resources/config.properties");
        db = (PostgresDb) ServletUtil.connectDb();
    }

    @Test
    void connectTest() {
        assertFalse(db.isConnected());

        // After connect is connected
        db.connect();
        assertTrue(db.isConnected());

        // Already connected
        db.connect();
        assertTrue(db.isConnected());

        db.disconnect();
    }

    @Test
    void disconnectTest() {
        db.connect();

        // After is disconnecter
        db.disconnect();
        assertFalse(db.isConnected());

        // Already not connected
        db.disconnect();
        assertFalse(db.isConnected());
    }
}