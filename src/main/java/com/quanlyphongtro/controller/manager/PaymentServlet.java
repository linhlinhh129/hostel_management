package com.quanlyphongtro.controller.manager;
import jakarta.servlet.http.HttpSession;
import com.quanlyphongtro.model.User;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.PaymentListItemDTO;
import com.quanlyphongtro.service.PaymentService;
import com.quanlyphongtro.service.impl.PaymentServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.lang.reflect.Method;

@WebServlet("/manager/payments")
public class PaymentServlet extends BaseServlet {
    private final PaymentService paymentService = new PaymentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        String month = request.getParameter("month");
        String year = request.getParameter("year");
        
        int page = 1;
        int limit = 10;
        try {
            if (request.getParameter("page") != null) {
                page = Integer.parseInt(request.getParameter("page"));
            }
        } catch (NumberFormatException ignored) {}
        
        int offset = (page - 1) * limit;
        
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
        
        List<PaymentListItemDTO> payments = paymentService.findPayments(userId, keyword, status, fromDate, toDate, month, year, offset, limit);
        int totalItems = paymentService.countPayments(userId, keyword, status, fromDate, toDate, month, year);
        int totalPages = (int) Math.ceil((double) totalItems / limit);
        
        request.setAttribute("payments", payments);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", Math.max(1, totalPages));
        request.setAttribute("totalRecords", totalItems);
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);
        request.setAttribute("fromDate", fromDate);
        request.setAttribute("toDate", toDate);
        request.setAttribute("month", month);
        request.setAttribute("year", year);
        
        request.getRequestDispatcher("/WEB-INF/views/manager/payments/list.jsp").forward(request, response);
    }
}
