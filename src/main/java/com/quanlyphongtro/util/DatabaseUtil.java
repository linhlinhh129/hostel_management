package com.quanlyphongtro.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public final class DatabaseUtil {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseUtil.class);

    private DatabaseUtil() {}

    private static DataSource dataSource;

    static {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/HostelManagement");
        } catch (NamingException e) {
            throw new ExceptionInInitializerError("Cannot initialize DataSource: " + e.getMessage());
        }
    }

    /**
     * Lấy connection từ pool và tự động bật QUOTED_IDENTIFIER để tránh lỗi
     * với SQL Server filtered index (WHERE deleted_at IS NULL).
     * JDBC driver mặc định SET QUOTED_IDENTIFIER OFF — phải override lại.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = dataSource.getConnection();
        try (Statement st = conn.createStatement()) {
            st.execute("SET QUOTED_IDENTIFIER ON");
        } catch (SQLException e) {
            logger.warn("Could not SET QUOTED_IDENTIFIER ON: {}", e.getMessage());
        }
        return conn;
    }

    public static void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
        }
    }
}
