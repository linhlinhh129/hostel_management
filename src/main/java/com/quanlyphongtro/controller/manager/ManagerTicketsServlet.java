package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.RequestService;
import com.quanlyphongtro.service.impl.RequestServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ManagerTicketsServlet", urlPatterns = {
        "/manager/tickets",
        "/manager/tickets/*"
})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,       // 10MB
        maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class ManagerTicketsServlet extends BaseServlet {

    private final RequestService requestService = new RequestServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo)) {
            handleList(req, resp);
        } else {
            String idStr = pathInfo.substring(1);
            try {
                int ticketId = Integer.parseInt(idStr);
                handleDetail(ticketId, req, resp);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String[] parts = pathInfo.split("/");
        if (parts.length == 3) {
            try {
                int ticketId = Integer.parseInt(parts[1]);
                String action = parts[2];
                if ("receive".equals(action)) {
                    handleReceive(ticketId, req, resp);
                } else if ("reject".equals(action)) {
                    handleReject(ticketId, req, resp);
                } else if ("schedule".equals(action)) {
                    handleSchedule(ticketId, req, resp);
                } else if ("complete".equals(action)) {
                    handleComplete(ticketId, req, resp);
                } else if ("reschedule".equals(action)) {
                    handleReschedule(ticketId, req, resp);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String type = req.getParameter("type");
        if (type == null || type.trim().isEmpty()) {
            type = "TENANT";
        } else {
            type = type.trim().toUpperCase();
        }

        String keyword = req.getParameter("keyword");
        String status = req.getParameter("status");
        int page = 1;
        String pageStr = req.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (Exception e) {
                page = 1;
            }
        }
        int pageSize = 10;

        int totalTickets = requestService.countManagerTickets(currentUser.getId(), type, status, keyword);
        List<Map<String, Object>> tickets = requestService.getManagerTickets(currentUser.getId(), type, status, keyword, page, pageSize);

        int totalPages = totalTickets > 0 ? (int) Math.ceil((double) totalTickets / pageSize) : 1;

        Map<String, Object> pageObj = new HashMap<>();
        pageObj.put("items", tickets);
        pageObj.put("total", totalTickets);
        pageObj.put("page", page);
        pageObj.put("totalPages", totalPages);

        req.setAttribute("page", pageObj);
        req.setAttribute("keyword", keyword);
        req.setAttribute("filterStatus", status);
        req.setAttribute("filterType", type);

        req.getRequestDispatcher("/WEB-INF/views/manager/tickets/list.jsp").forward(req, resp);
    }

    private void handleDetail(int ticketId, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Map<String, Object> ticket = null;

        try {
            ticket = requestService.getManagerTicketDetail(ticketId, currentUser.getId());
        } catch (java.nio.file.AccessDeniedException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        } catch (Exception e) {
            logger.error("Failed to query ticket detail", e);
        }

        if (ticket == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("ticket", ticket);
        req.getRequestDispatcher("/WEB-INF/views/manager/tickets/detail.jsp").forward(req, resp);
    }

    private void handleReceive(int ticketId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        boolean success = requestService.receiveTicket(ticketId);
        if (success) {
            setFlashMessage(req, "success", "Tiếp nhận yêu cầu thành công!");
        } else {
            setFlashMessage(req, "danger", "Lỗi tiếp nhận yêu cầu.");
        }
        resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
    }

    private void handleReject(int ticketId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String reason = req.getParameter("reason");
        if (reason == null || reason.trim().isEmpty()) {
            setFlashMessage(req, "danger", "Vui lòng nhập lý do từ chối.");
            resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
            return;
        }

        boolean success = requestService.rejectTicket(ticketId, reason.trim());
        if (success) {
            setFlashMessage(req, "success", "Từ chối yêu cầu thành công!");
        } else {
            setFlashMessage(req, "danger", "Lỗi từ chối yêu cầu.");
        }
        resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
    }

    private void handleSchedule(int ticketId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String appointmentDateStr = req.getParameter("appointmentDate");
        java.time.LocalDateTime ldt = null;
        if (appointmentDateStr != null && !appointmentDateStr.trim().isEmpty()) {
            try {
                ldt = java.time.LocalDateTime.parse(appointmentDateStr.trim());
            } catch (Exception e) {
                logger.error("Failed to parse appointment date", e);
                setFlashMessage(req, "danger", "Ngày hẹn không đúng định dạng (yyyy-MM-dd'T'HH:mm).");
                resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
                return;
            }
        } else {
            setFlashMessage(req, "danger", "Vui lòng chọn ngày hẹn xử lý sự cố.");
            resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
            return;
        }
        boolean success = requestService.scheduleTicket(ticketId, ldt);
        if (success) {
            setFlashMessage(req, "success", "Bắt đầu xử lý yêu cầu thành công!");
        } else {
            setFlashMessage(req, "danger", "Lỗi cập nhật trạng thái.");
        }
        resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
    }

    private void handleReschedule(int ticketId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String appointmentDateStr = req.getParameter("appointmentDate");
        String reason = req.getParameter("reason");

        if (appointmentDateStr == null || appointmentDateStr.trim().isEmpty()) {
            setFlashMessage(req, "danger", "Vui lòng chọn ngày hẹn mới.");
            resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
            return;
        }

        if (reason == null || reason.trim().isEmpty()) {
            setFlashMessage(req, "danger", "Vui lòng nhập lý do dời lịch hẹn.");
            resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
            return;
        }

        java.time.LocalDateTime newLdt = null;
        try {
            newLdt = java.time.LocalDateTime.parse(appointmentDateStr.trim());
        } catch (Exception e) {
            logger.error("Failed to parse reschedule appointment date", e);
            setFlashMessage(req, "danger", "Ngày hẹn mới không đúng định dạng (yyyy-MM-dd'T'HH:mm).");
            resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
            return;
        }

        String ipAddress = req.getRemoteAddr();
        try {
            boolean success = requestService.rescheduleTicket(ticketId, newLdt, reason.trim(), currentUser.getId(), ipAddress);
            if (success) {
                setFlashMessage(req, "success", "Thay đổi lịch hẹn thành công!");
            } else {
                setFlashMessage(req, "danger", "Lỗi thay đổi lịch hẹn.");
            }
        } catch (java.nio.file.AccessDeniedException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        } catch (Exception e) {
            logger.error("Failed to reschedule ticket", e);
            setFlashMessage(req, "danger", "Lỗi: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
    }

    private void handleComplete(int ticketId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String notes = req.getParameter("notes");
        if (notes == null || notes.trim().isEmpty()) {
            setFlashMessage(req, "danger", "Ghi chú hoàn thành không được để trống.");
            resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
            return;
        }

        List<String> fileNames = new ArrayList<>();
        try {
            String uploadPath = req.getServletContext().getRealPath("") + java.io.File.separator + "uploads" + java.io.File.separator + "requests";
            java.io.File uploadDir = new java.io.File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            for (Part part : req.getParts()) {
                if ("after_images".equals(part.getName()) && part.getSize() > 0) {
                    String originalFileName = getFileName(part);
                    String contentType = part.getContentType();

                    if (!com.quanlyphongtro.util.ValidationUtil.isValidFileType(originalFileName) ||
                        !com.quanlyphongtro.util.ValidationUtil.isValidMimeType(contentType)) {
                        setFlashMessage(req, "danger", "File upload không hợp lệ. Chỉ chấp nhận các định dạng ảnh JPG, PNG hoặc tài liệu PDF.");
                        resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
                        return;
                    }

                    String fileName = java.util.UUID.randomUUID().toString() + "_" + originalFileName;
                    part.write(uploadPath + java.io.File.separator + fileName);
                    fileNames.add("/uploads/requests/" + fileName);
                }
            }
        } catch (Exception e) {
            logger.error("Error uploading completion images", e);
        }

        String attachmentUrls2 = fileNames.isEmpty() ? null : String.join(",", fileNames);

        boolean success = requestService.completeTicket(ticketId, notes.trim(), attachmentUrls2);
        if (success) {
            setFlashMessage(req, "success", "Xác nhận hoàn thành yêu cầu thành công!");
        } else {
            setFlashMessage(req, "danger", "Lỗi cập nhật hoàn thành.");
        }
        resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
    }

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }
}
