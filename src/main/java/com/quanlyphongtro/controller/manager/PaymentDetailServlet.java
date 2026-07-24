package com.quanlyphongtro.controller.manager;
import com.quanlyphongtro.model.User;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.PaymentDetailDTO;
import com.quanlyphongtro.service.PaymentService;
import com.quanlyphongtro.service.impl.PaymentServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.lang.reflect.Method;

@WebServlet("/manager/payments/*")
public class PaymentDetailServlet extends BaseServlet {
    private final PaymentService paymentService = new PaymentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Payment not found");
            return;
        }

        try {
            int paymentId = Integer.parseInt(pathInfo.substring(1));
            int userId = 1;
            try {
                HttpSession session = request.getSession(false);
                if (session != null && session.getAttribute("currentUser") != null) {
                    Object userObj = session.getAttribute("currentUser");
                    if (userObj instanceof User) {
                        userId = ((User) userObj).getId();
                    } else {
                        Method m = userObj.getClass().getMethod("getId");
                        userId = (Integer) m.invoke(userObj);
                    }
                }
            } catch (Exception e) {}

            PaymentDetailDTO payment = paymentService.findById(userId, paymentId);
            
            if (payment == null) {
                request.setAttribute("error", "Giao dịch không tồn tại.");
                request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
                return;
            }
            
            request.setAttribute("payment", payment);
            request.getRequestDispatcher("/WEB-INF/views/manager/payments/detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid payment ID");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String[] parts = pathInfo.split("/");
        if (parts.length < 3) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int paymentId = Integer.parseInt(parts[1]);
            String action = parts[2];
            
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("currentUser") == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Object userObj = session.getAttribute("currentUser");
            int userId = 1;
            try {
                if (userObj instanceof User) {
                    userId = ((User) userObj).getId();
                } else if (userObj != null) {
                    Method m = userObj.getClass().getMethod("getId");
                    userId = (Integer) m.invoke(userObj);
                }
            } catch (Exception e) {}

            if ("approve".equals(action)) {
                paymentService.approvePayment(paymentId, userId);
                request.getSession().setAttribute("success", "Đã duyệt giao dịch thành công.");
            } else if ("reject".equals(action)) {
                paymentService.rejectPayment(paymentId, userId);
                request.getSession().setAttribute("success", "Đã từ chối giao dịch.");
            }

            response.sendRedirect(request.getContextPath() + "/manager/payments/" + paymentId);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid payment ID");
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            String[] partsError = pathInfo.split("/");
            if (partsError.length > 1) {
                response.sendRedirect(request.getContextPath() + "/manager/payments/" + partsError[1]);
            } else {
                response.sendRedirect(request.getContextPath() + "/manager/payments");
            }
        }
    }
}
