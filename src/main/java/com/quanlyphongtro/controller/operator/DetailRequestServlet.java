package com.quanlyphongtro.controller.operator;

import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.RequestService;
import com.quanlyphongtro.util.AuditLogHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import jakarta.servlet.annotation.MultipartConfig;
@WebServlet("/operator/requests/detail")
@MultipartConfig(maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 25)
public class DetailRequestServlet extends HttpServlet {
    private RequestService requestService;
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    public void init() throws ServletException {
        this.requestService = new com.quanlyphongtro.service.impl.RequestServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/operator/requests");
            return;
        }

        try {
            int requestId = Integer.parseInt(idParam);
            Request reqDetail = requestService.getRequestDetail(requestId);

            if (reqDetail == null) {
                request.setAttribute("error", "Yêu cầu không tồn tại.");
                request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
                return;
            }

            request.setAttribute("reqDetail", reqDetail);
            request.getRequestDispatcher("/WEB-INF/views/operator/requests/detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/operator/requests");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // Fallback user ID to 1 if not fully configured with session, but generally should fetch from session
        int operatorId = 1;
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        if (currentUser != null) {
            operatorId = currentUser.getId();
        }

        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

        if (idParam == null || action == null) {
            response.sendRedirect(request.getContextPath() + "/operator/requests");
            return;
        }

        try {
            int requestId = Integer.parseInt(idParam);
            boolean success = false;

            if ("accept".equals(action)) {
                success = requestService.acceptRequest(requestId, operatorId);
            } else if ("reject".equals(action)) {
                String reason = request.getParameter("rejectReason");
                if (reason == null || reason.trim().isEmpty()) {
                    request.setAttribute("error", "Lý do từ chối không được để trống.");
                    doGet(request, response);
                    return;
                }
                success = requestService.rejectRequest(requestId, operatorId, reason.trim());
            } else if ("schedule".equals(action)) {
                String appointmentDateStr = request.getParameter("appointmentDate");
                if (appointmentDateStr == null || appointmentDateStr.trim().isEmpty()) {
                    request.setAttribute("error", "Ngày hẹn không được để trống.");
                    doGet(request, response);
                    return;
                }
                // Parse date and schedule properly
                try {
                    java.time.LocalDateTime appointSchedule = java.time.LocalDateTime.parse(appointmentDateStr.trim());
                    success = requestService.scheduleAppointment(requestId, appointSchedule);
                } catch (Exception e) {
                    request.setAttribute("error", "Định dạng ngày hẹn không hợp lệ.");
                    doGet(request, response);
                    return;
                }
            } else if ("complete".equals(action)) {
                String notes = request.getParameter("notes");
                String noImageCheckbox = request.getParameter("no_image_checkbox");
                boolean isNoImage = "on".equals(noImageCheckbox);
                
                if (notes == null || notes.trim().isEmpty()) {
                    request.setAttribute("error", "Ghi chú hoàn thành không được để trống.");
                    doGet(request, response);
                    return;
                }
                
                java.util.List<String> fileNames = new java.util.ArrayList<>();
                String uploadPath = getServletContext().getRealPath("") + java.io.File.separator + "uploads" + java.io.File.separator + "requests";
                java.io.File uploadDir = new java.io.File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                for (jakarta.servlet.http.Part part : request.getParts()) {
                    if ("after_images".equals(part.getName()) && part.getSize() > 0) {
                        String fileName = java.util.UUID.randomUUID().toString() + "_" + getFileName(part);
                        part.write(uploadPath + java.io.File.separator + fileName);
                        fileNames.add("/uploads/requests/" + fileName);
                    }
                }
                
                if (!isNoImage && fileNames.isEmpty()) {
                    request.setAttribute("error", "Vui lòng đính kèm ít nhất 1 ảnh minh chứng, hoặc tích chọn Lỗi đơn giản.");
                    doGet(request, response);
                    return;
                }
                
                String attachmentUrls2 = fileNames.isEmpty() ? null : String.join(",", fileNames);
                success = requestService.completeRequest(requestId, notes.trim(), attachmentUrls2);
            }

            if (success) {
                try {
                    String auditAction = action.toUpperCase();
                    String auditNew = null;
                    if ("accept".equals(action))   { auditAction = "UPDATE"; auditNew = "ASSIGNED"; session.setAttribute("successMessage", "Đã tiếp nhận yêu cầu thành công!"); }
                    else if ("reject".equals(action))   { auditAction = "UPDATE"; auditNew = "REJECTED"; session.setAttribute("successMessage", "Đã từ chối yêu cầu!"); }
                    else if ("complete".equals(action)) { auditAction = "UPDATE"; auditNew = "DONE"; session.setAttribute("successMessage", "Đã báo cáo hoàn thành yêu cầu!"); }
                    else if ("schedule".equals(action)) { auditAction = "UPDATE"; auditNew = "IN_PROGRESS"; session.setAttribute("successMessage", "Đã lên lịch hẹn thành công!"); }
                    AuditLogHelper.log(auditLogDAO, request, "requests", requestId,
                        auditAction, "PENDING", auditNew, operatorId);
                } catch (Exception ex) { /* ignore audit failure */ }
                // Redirect on success to prevent form resubmission
                response.sendRedirect(request.getContextPath() + "/operator/requests/detail?id=" + requestId);
            } else {
                // Optimistic locking failure
                request.setAttribute("error", "Thao tác không thành công! Yêu cầu này đã được tiếp nhận bởi người khác hoặc trạng thái đã thay đổi.");
                doGet(request, response);
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/operator/requests");
        }
    }

    private String getFileName(jakarta.servlet.http.Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length()-1);
            }
        }
        return "";
    }
}
