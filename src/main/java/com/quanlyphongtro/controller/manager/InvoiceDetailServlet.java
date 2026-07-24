package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.InvoiceDetailDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.InvoiceService;
import com.quanlyphongtro.service.impl.InvoiceServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/manager/invoices/*")
public class InvoiceDetailServlet extends BaseServlet {
    private InvoiceService invoiceService = new InvoiceServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        String[] parts = pathInfo.split("/");
        if (parts.length < 2) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        try {
            int invoiceId = Integer.parseInt(parts[1]);
            UserSessionDTO user = getCurrentUser(req);
            InvoiceDetailDTO invoice = invoiceService.getInvoiceDetail(user.getId(), invoiceId);
            
            if (parts.length == 3 && "edit".equals(parts[2])) {
                req.setAttribute("invoice", invoice);
                req.getRequestDispatcher("/WEB-INF/views/manager/invoices/edit.jsp").forward(req, resp);
            } else if (parts.length == 3 && "export".equals(parts[2])) {
                resp.setContentType("text/plain");
                resp.setHeader("Content-Disposition", "attachment; filename=\"" + invoice.getInvoiceCode() + ".txt\"");
                resp.getWriter().write("HÓA ĐƠN THU TIỀN\n");
                resp.getWriter().write("Mã hóa đơn: " + invoice.getInvoiceCode() + "\n");
                resp.getWriter().write("Phòng: " + invoice.getRoomCode() + "\n");
                resp.getWriter().write("Kỳ: " + invoice.getBillingPeriod() + "\n");
                resp.getWriter().write("Tổng tiền: " + invoice.getTotalAmount() + "\n");
                resp.getWriter().write("Trạng thái: " + invoice.getStatusLabel() + "\n");
            } else {
                req.setAttribute("invoice", invoice);
                req.getRequestDispatcher("/WEB-INF/views/manager/invoices/detail.jsp").forward(req, resp);
            }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID hóa đơn không hợp lệ.");
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        String[] parts = pathInfo.split("/");
        if (parts.length == 3 && "edit".equals(parts[2])) {
            try {
                int invoiceId = Integer.parseInt(parts[1]);
                UserSessionDTO user = getCurrentUser(req);
                String dueDate = req.getParameter("dueDate");
                String taxRate = req.getParameter("taxRate");
                String otherFee = req.getParameter("otherFee");
                String note = req.getParameter("note");

                invoiceService.updateInvoice(user.getId(), invoiceId, dueDate, taxRate, otherFee, note);
                resp.sendRedirect(req.getContextPath() + "/manager/invoices/" + invoiceId);
            } catch (IllegalArgumentException e) {
                setFlashMessage(req, "danger", e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/manager/invoices/" + parts[1] + "/edit");
            } catch (Exception e) {
                e.printStackTrace();
                setFlashMessage(req, "danger", "Đã xảy ra lỗi: " + e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/manager/invoices/" + parts[1] + "/edit");
            }
        } else if (parts.length == 3 && "update-status".equals(parts[2])) {
            try {
                int invoiceId = Integer.parseInt(parts[1]);
                UserSessionDTO user = getCurrentUser(req);
                String status = req.getParameter("status");
                
                invoiceService.updateStatus(user.getId(), invoiceId, status);
                resp.sendRedirect(req.getContextPath() + "/manager/invoices/" + invoiceId);
            } catch (Exception e) {
                e.printStackTrace();
                setFlashMessage(req, "danger", "Đã xảy ra lỗi: " + e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/manager/invoices/" + parts[1]);
            }
        } else if (parts.length == 3 && "delete".equals(parts[2])) {
            try {
                int invoiceId = Integer.parseInt(parts[1]);
                UserSessionDTO user = getCurrentUser(req);
                invoiceService.deleteInvoice(user.getId(), invoiceId);
                setFlashMessage(req, "success", "Xóa hóa đơn thành công!");
                resp.sendRedirect(req.getContextPath() + "/manager/invoices");
            } catch (Exception e) {
                e.printStackTrace();
                setFlashMessage(req, "danger", "Đã xảy ra lỗi: " + e.getMessage());
                resp.sendRedirect(req.getContextPath() + "/manager/invoices/" + parts[1]);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
