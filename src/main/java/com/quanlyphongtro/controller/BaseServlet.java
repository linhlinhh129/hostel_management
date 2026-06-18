package com.quanlyphongtro.controller;

import com.quanlyphongtro.constant.ErrorMessageConstant;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.AppException;
import com.quanlyphongtro.exception.ForbiddenException;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected UserSessionDTO getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (UserSessionDTO) session.getAttribute("currentUser");
    }

    protected void setFlashMessage(HttpServletRequest request, String type, String message) {
        request.getSession(true).setAttribute("flashType", type);
        request.getSession(true).setAttribute("flashMessage", message);
    }

    protected void forwardError(HttpServletRequest request, HttpServletResponse response,
                                String view, Exception e) throws ServletException, IOException {
        logger.error("Request processing failed", e);
        request.setAttribute("errorMessage", ErrorMessageConstant.GENERIC_ERROR);
        request.getRequestDispatcher(view).forward(request, response);
    }

    protected void handleException(HttpServletRequest request, HttpServletResponse response,
                                   Exception e) throws ServletException, IOException {
        if (e instanceof ValidationException ve) {
            request.setAttribute("errorMessage", ve.getMessage());
            return;
        }
        if (e instanceof ForbiddenException) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        if (e instanceof NotFoundException) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (e instanceof AppException ae) {
            request.setAttribute("errorMessage", ae.getMessage());
            return;
        }
        logger.error("Unexpected error", e);
        request.setAttribute("errorMessage", ErrorMessageConstant.GENERIC_ERROR);
    }
}
