package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.InvoiceDetailDTO;
import com.quanlyphongtro.dto.InvoiceListItemDTO;
import com.quanlyphongtro.dto.RoomDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.InvoiceService;
import com.quanlyphongtro.service.impl.InvoiceServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ManagerInvoiceServlet", urlPatterns = {"/manager/invoices", "/manager/invoices/*"})
public class ManagerInvoiceServlet extends BaseServlet {
    private InvoiceService invoiceService = new InvoiceServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            String action = req.getParameter("action");
            if ("create".equals(action)) {
                showCreateForm(req, resp);
            } else if ("getInvoicePreview".equals(action)) {
                handleGetInvoicePreview(req, resp);
            } else if ("getAvailableRooms".equals(action)) {
                handleGetAvailableRooms(req, resp);
            } else {
                showList(req, resp);
            }
        } else {
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
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            String action = req.getParameter("action");
            if ("create".equals(action)) {
                UserSessionDTO user = getCurrentUser(req);
                String roomCode = req.getParameter("roomCode");
                String billingPeriod = req.getParameter("billingPeriod");
                String dueDate = req.getParameter("dueDate");
                String otherFee = req.getParameter("otherFee");
                String note = req.getParameter("note");

                try {
                    invoiceService.createInvoice(user.getId(), roomCode, billingPeriod, dueDate, otherFee, note, user.getId());
                    resp.sendRedirect(req.getContextPath() + "/manager/invoices");
                } catch (IllegalArgumentException e) {
                    req.setAttribute("errorMessage", e.getMessage());
                    req.setAttribute("prefilledRoomCode", roomCode);
                    try {
                        List<RoomDTO> availableRooms = invoiceService.getAvailableRoomsForInvoice(user.getId(), billingPeriod);
                        req.setAttribute("availableRooms", availableRooms);
                        req.setAttribute("defaultBillingPeriod", billingPeriod);
                    } catch (Exception ignored) {
                    }
                    req.getRequestDispatcher("/WEB-INF/views/manager/invoices/create.jsp").forward(req, resp);
                } catch (Exception e) {
                    e.printStackTrace();
                    req.setAttribute("errorMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
                    req.setAttribute("prefilledRoomCode", roomCode);
                    try {
                        List<RoomDTO> availableRooms = invoiceService.getAvailableRoomsForInvoice(user.getId(), billingPeriod);
                        req.setAttribute("availableRooms", availableRooms);
                        req.setAttribute("defaultBillingPeriod", billingPeriod);
                    } catch (Exception ignored) {
                    }
                    req.getRequestDispatcher("/WEB-INF/views/manager/invoices/create.jsp").forward(req, resp);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            String[] parts = pathInfo.split("/");
            if (parts.length == 3 && "edit".equals(parts[2])) {
                try {
                    int invoiceId = Integer.parseInt(parts[1]);
                    UserSessionDTO user = getCurrentUser(req);
                    String dueDate = req.getParameter("dueDate");
                    String otherFee = req.getParameter("otherFee");
                    String note = req.getParameter("note");

                    invoiceService.updateInvoice(user.getId(), invoiceId, dueDate, otherFee, note);
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
            } else if (parts.length == 3 && "report-error".equals(parts[2])) {
                int invoiceId = Integer.parseInt(parts[1]);
                resp.sendRedirect(req.getContextPath() + "/manager/notifications/send-operator?invoiceId=" + invoiceId);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    private void handleGetInvoicePreview(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserSessionDTO user = getCurrentUser(req);
        if (user == null || (!"MANAGER".equals(user.getRole()) && !"ADMIN".equals(user.getRole()))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String roomCode = req.getParameter("roomCode");
        String billingPeriod = req.getParameter("billingPeriod");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Map<String, Object> preview = invoiceService.getInvoicePreview(user.getId(), roomCode, billingPeriod);
            StringBuilder json = new StringBuilder("{");
            json.append("\"roomFee\":").append(preview.get("roomFee")).append(",");
            json.append("\"serviceFee\":").append(preview.get("serviceFee")).append(",");
            json.append("\"internetFee\":").append(preview.get("internetFee")).append(",");
            json.append("\"electricityPrice\":").append(preview.get("electricityPrice")).append(",");
            json.append("\"waterPrice\":").append(preview.get("waterPrice")).append(",");
            json.append("\"oldElectric\":").append(preview.get("oldElectric")).append(",");
            json.append("\"newElectric\":").append(preview.get("newElectric")).append(",");
            json.append("\"oldWater\":").append(preview.get("oldWater")).append(",");
            json.append("\"newWater\":").append(preview.get("newWater")).append(",");
            
            json.append("\"electricUsage\":").append(preview.get("electricUsage")).append(",");
            json.append("\"waterUsage\":").append(preview.get("waterUsage")).append(",");
            
            json.append("\"electricStatus\":\"").append(preview.get("electricStatus") != null ? preview.get("electricStatus") : "NORMAL").append("\",");
            json.append("\"electricOldFinal\":").append(preview.get("electricOldFinal") != null ? preview.get("electricOldFinal") : "null").append(",");
            json.append("\"electricNewStart\":").append(preview.get("electricNewStart") != null ? preview.get("electricNewStart") : "null").append(",");
            
            json.append("\"waterStatus\":\"").append(preview.get("waterStatus") != null ? preview.get("waterStatus") : "NORMAL").append("\",");
            json.append("\"waterOldFinal\":").append(preview.get("waterOldFinal") != null ? preview.get("waterOldFinal") : "null").append(",");
            json.append("\"waterNewStart\":").append(preview.get("waterNewStart") != null ? preview.get("waterNewStart") : "null").append(",");
            json.append("\"meterId\":").append(preview.get("meterId")).append(",");
            json.append("\"electricImg\":\"").append(preview.get("electricImg") != null ? preview.get("electricImg") : "").append("\",");
            json.append("\"waterImg\":\"").append(preview.get("waterImg") != null ? preview.get("waterImg") : "").append("\"");
            json.append("}");
            resp.getWriter().write(json.toString());
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null) {
                errorMsg = errorMsg.replace("\"", "\\\"");
            }
            resp.getWriter().write("{\"error\":\"" + errorMsg + "\"}");
        }
    }

    private void handleGetAvailableRooms(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserSessionDTO user = getCurrentUser(req);
        if (user == null || (!"MANAGER".equals(user.getRole()) && !"ADMIN".equals(user.getRole()))) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        String billingPeriod = req.getParameter("billingPeriod");
        if (billingPeriod == null || billingPeriod.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            List<RoomDTO> availableRooms = invoiceService.getAvailableRoomsForInvoice(user.getId(), billingPeriod.trim());
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < availableRooms.size(); i++) {
                RoomDTO r = availableRooms.get(i);
                json.append("{\"code\":\"").append(r.getCode()).append("\"}");
                if (i < availableRooms.size() - 1)
                    json.append(",");
            }
            json.append("]");
            resp.getWriter().write(json.toString());
        } catch (Exception e) {
            resp.getWriter().write("[]");
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
                if (req.getParameter("page") != null)
                    page = Integer.parseInt(req.getParameter("page"));
            } catch (NumberFormatException e) {
            }

            List<InvoiceListItemDTO> invoices = invoiceService.getInvoices(user.getId(), keyword, status, billingPeriod, page, size);
            int total = invoiceService.countInvoices(user.getId(), keyword, status, billingPeriod);
            int totalPages = (int) Math.ceil((double) total / size);

            req.setAttribute("invoices", invoices);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", Math.max(1, totalPages));
            req.setAttribute("totalRecords", total);
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
        UserSessionDTO user = getCurrentUser(req);
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String roomCode = req.getParameter("roomCode");
        String billingPeriod = req.getParameter("billingPeriod");
        if (billingPeriod == null || billingPeriod.trim().isEmpty()) {
            billingPeriod = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        } else {
            billingPeriod = billingPeriod.trim();
        }

        try {
            List<RoomDTO> availableRooms = invoiceService.getAvailableRoomsForInvoice(user.getId(), billingPeriod);
            req.setAttribute("availableRooms", availableRooms);
            req.setAttribute("defaultBillingPeriod", billingPeriod);

            if (roomCode != null && !roomCode.trim().isEmpty()) {
                req.setAttribute("prefilledRoomCode", roomCode.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        req.getRequestDispatcher("/WEB-INF/views/manager/invoices/create.jsp").forward(req, resp);
    }
}
