package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Invoice;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.service.InvoiceService;
import com.quanlyphongtro.service.TenantService;
import com.quanlyphongtro.service.impl.InvoiceServiceImpl;
import com.quanlyphongtro.service.impl.TenantServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "TenantInvoiceServlet", urlPatterns = {"/tenant/invoices", "/tenant/invoices/*"})
public class TenantInvoiceServlet extends BaseServlet {

    private final TenantService tenantService = new TenantServiceImpl();
    private final InvoiceService invoiceService = new InvoiceServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        UserSessionDTO currentUser = getCurrentUser(req);

        try {
            Optional<Room> roomOpt = tenantService.getTenantRoom(currentUser.getId());
            if (roomOpt.isEmpty()) {
                req.setAttribute("errorMessage", "Không tìm thấy thông tin phòng.");
                req.getRequestDispatcher("/WEB-INF/views/tenant/invoices/list.jsp").forward(req, resp);
                return;
            }
            int roomId = roomOpt.get().getId();

            if (pathInfo == null || pathInfo.equals("/")) {
                // List
                List<Invoice> invoices = invoiceService.getInvoicesByRoomId(roomId);
                BigDecimal unpaidTotal = invoiceService.getUnpaidTotal(roomId);
                
                com.quanlyphongtro.dao.PaymentDAO paymentDAO = new com.quanlyphongtro.dao.PaymentDAO();
                for (Invoice inv : invoices) {
                    inv.setHasPendingPayment(paymentDAO.hasPendingPayment(inv.getId()));
                }
                
                req.setAttribute("invoices", invoices);
                req.setAttribute("unpaidTotal", unpaidTotal);
                req.getRequestDispatcher("/WEB-INF/views/tenant/invoices/list.jsp").forward(req, resp);
            } else {
                // Detail
                String idStr = pathInfo.substring(1);
                try {
                    int id = Integer.parseInt(idStr);
                    Optional<Invoice> invOpt = invoiceService.getInvoiceById(id, roomId);
                    if (invOpt.isPresent()) {
                        Invoice inv = invOpt.get();
                        inv.setHasPendingPayment(new com.quanlyphongtro.dao.PaymentDAO().hasPendingPayment(inv.getId()));
                        req.setAttribute("invoice", inv);

                        long overdueDays = 0;
                        java.math.BigDecimal penaltyAmount = java.math.BigDecimal.ZERO;
                        java.math.BigDecimal totalAmountToPay = inv.getTotalAmount();

                        if ("UNPAID".equals(inv.getStatus()) || "OVERDUE".equals(inv.getStatus())) {
                            java.time.LocalDate dueDate = inv.getDueDate();
                            java.time.LocalDate today = java.time.LocalDate.now();
                            if (dueDate != null && today.isAfter(dueDate)) {
                                overdueDays = java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
                                penaltyAmount = new com.quanlyphongtro.dao.InvoiceDAO().calculateRealtimeLatePenalty(inv.getId());
                                if (penaltyAmount != null) {
                                    totalAmountToPay = totalAmountToPay.add(penaltyAmount);
                                }
                            }
                        }

                        req.setAttribute("overdueDays", overdueDays);
                        req.setAttribute("penaltyAmount", penaltyAmount);
                        req.setAttribute("totalAmountToPay", totalAmountToPay);
                        req.getRequestDispatcher("/WEB-INF/views/tenant/invoices/detail.jsp").forward(req, resp);
                    } else {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        } catch (Exception e) {
            handleException(req, resp, e);
        }
    }
}
