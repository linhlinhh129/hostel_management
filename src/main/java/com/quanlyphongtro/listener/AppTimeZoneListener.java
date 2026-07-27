package com.quanlyphongtro.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.TimeZone;

@WebListener
public class AppTimeZoneListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.setProperty("user.timezone", "Asia/Ho_Chi_Minh");
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
    }
}
