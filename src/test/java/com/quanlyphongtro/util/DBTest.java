package com.quanlyphongtro.util;

import org.junit.jupiter.api.Test;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DBTest {
    @Test
    public void testConnection() throws Exception {
        TestDBInitializer.initJNDI();
        // Since DatabaseUtil has a static block that lookups JNDI, 
        // we must ensure TestDBInitializer.initJNDI() is called BEFORE DatabaseUtil class is loaded.
        Connection conn = DatabaseUtil.getConnection();
        assertNotNull(conn);
        conn.close();
    }
}
