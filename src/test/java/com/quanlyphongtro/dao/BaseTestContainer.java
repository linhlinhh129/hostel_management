package com.quanlyphongtro.dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.sql.Connection;
import java.sql.DriverManager;

public abstract class BaseTestContainer {
    
    protected static Connection connection;

    @BeforeAll
    static void startDb() throws Exception {
        // Cấu hình không dùng Docker, kết nối thẳng vào database SQL Server Local của bạn
        String jdbcUrl = "jdbc:sqlserver://localhost:1433;databaseName=HostelManagement;encrypt=true;trustServerCertificate=true;";
        String username = "sa";
        String password = "123";
        
        connection = DriverManager.getConnection(jdbcUrl, username, password);
    }

    @AfterAll
    static void stopDb() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
