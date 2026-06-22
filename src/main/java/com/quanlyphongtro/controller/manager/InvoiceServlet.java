package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.InvoiceListItemDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.InvoiceService;
import com.quanlyphongtro.service.impl.InvoiceServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/manager/invoices")
public class InvoiceServlet extends BaseServlet {
    private InvoiceService invoiceService = new InvoiceServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("create".equals(action)) {
            showCreateForm(req, resp);
        } else {
            showList(req, resp);
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UserSessionDTO user = getCurrentUser(req);
            if (user == null || (!"MANAGER".equals(user.getRole()) && !"ADMIN".equals(user.getRole()))) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                return;
            }

            String keyword = req.getParameter("keyword");
            String status = req.getParameter("status");
            String billingPeriod = req.getParameter("billingPeriod");
            
            int page = 1;
            int size = 10;
            try {
                if (req.getParameter("page") != null) page = Integer.parseInt(req.getParameter("page"));
            } catch (NumberFormatException e) {}

            List<InvoiceListItemDTO> invoices = invoiceService.getInvoices(user.getId(), keyword, status, billingPeriod, page, size);
            int total = invoiceService.countInvoices(user.getId(), keyword, status, billingPeriod);
            int totalPages = (int) Math.ceil((double) total / size);

            req.setAttribute("invoices", invoices);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("keyword", keyword);
            req.setAttribute("status", status);
            req.setAttribute("billingPeriod", billingPeriod);

            req.getRequestDispatcher("/WEB-INF/views/manager/invoices/list.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi khi lấy danh sách hóa đơn.");
        }
    }

    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/manager/invoices/create.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("create".equals(action)) {
            try {
                UserSessionDTO user = getCurrentUser(req);
                String roomCode = req.getParameter("roomCode");
                String billingPeriod = req.getParameter("billingPeriod");
                String dueDate = req.getParameter("dueDate");
                String taxRate = req.getParameter("taxRate");
                String otherFee = req.getParameter("otherFee");
                String note = req.getParameter("note");

                invoiceService.createInvoice(user.getId(), roomCode, billingPeriod, dueDate, taxRate, otherFee, note, user.getId());
                resp.sendRedirect(req.getContextPath() + "/manager/invoices");
            } catch (IllegalArgumentException e) {
                req.setAttribute("errorMessage", e.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/manager/invoices/create.jsp").forward(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
                req.setAttribute("errorMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
                req.getRequestDispatcher("/WEB-INF/views/manager/invoices/create.jsp").forward(req, resp);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
