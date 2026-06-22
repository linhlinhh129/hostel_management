package com.quanlyphongtro.listener;

import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.util.SessionRegistry;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@WebListener
public class UserSessionListener implements HttpSessionListener, HttpSessionAttributeListener {

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        UserSessionDTO currentUser = (UserSessionDTO) se.getSession().getAttribute("currentUser");
        if (currentUser != null) {
            SessionRegistry.removeSession(currentUser.getId(), se.getSession());
        }
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        if ("currentUser".equals(event.getName())) {
            UserSessionDTO currentUser = (UserSessionDTO) event.getValue();
            if (currentUser != null) {
                SessionRegistry.addSession(currentUser.getId(), event.getSession());
            }
        }
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        if ("currentUser".equals(event.getName())) {
            // Remove old
            UserSessionDTO oldUser = (UserSessionDTO) event.getValue();
            if (oldUser != null) {
                SessionRegistry.removeSession(oldUser.getId(), event.getSession());
            }
            // Add new
            UserSessionDTO newUser = (UserSessionDTO) event.getSession().getAttribute("currentUser");
            if (newUser != null) {
                SessionRegistry.addSession(newUser.getId(), event.getSession());
            }
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        if ("currentUser".equals(event.getName())) {
            UserSessionDTO currentUser = (UserSessionDTO) event.getValue();
            if (currentUser != null) {
                SessionRegistry.removeSession(currentUser.getId(), event.getSession());
            }
        }
    }
}
