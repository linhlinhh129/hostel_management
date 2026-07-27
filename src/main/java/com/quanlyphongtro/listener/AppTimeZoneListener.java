package com.quanlyphongtro.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.jsp.jstl.core.Config;
import java.util.TimeZone;

@WebListener
public class AppTimeZoneListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        TimeZone vnTz = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
        System.setProperty("user.timezone", "Asia/Ho_Chi_Minh");
        TimeZone.setDefault(vnTz);
        Config.set(sce.getServletContext(), Config.FMT_TIME_ZONE, vnTz);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
    }
}
