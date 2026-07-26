package com.quanlyphongtro.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class SQLFixtureHelper {

    public static void executeSqlScript(String scriptPath) {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            InputStream is = SQLFixtureHelper.class.getClassLoader().getResourceAsStream(scriptPath);
            if (is == null) {
                throw new RuntimeException("Script file not found: " + scriptPath);
            }

            String sql;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                sql = reader.lines().collect(Collectors.joining("\n"));
            }

            // Chia các lệnh bằng GO (nếu là T-SQL) hoặc dấu ;
            // Chạy đơn giản bằng cách tách theo dấu chấm phẩy
            String[] statements = sql.split(";");
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    try {
                        stmt.execute(trimmed);
                    } catch (Exception e) {
                        System.err.println("Error executing SQL snippet: " + trimmed);
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error executing sql script: " + scriptPath, e);
        }
    }
}
