package com.quanlyphongtro.controller.operator;
import com.quanlyphongtro.service.impl.RequestServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import jakarta.servlet.http.Part;
import java.util.UUID;

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
        this.requestService = new RequestServiceImpl();
    }

    @Override
    // Bước 1: Hiển thị chi tiết một yêu cầu khi Operator bấm vào xem
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        // Nếu không có ID yêu cầu, đẩy về trang danh sách
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/operator/requests");
            return;
        }

        try {
            int requestId = Integer.parseInt(idParam);
            // Lấy dữ liệu chi tiết của yêu cầu từ Service (DB)
            Request reqDetail = requestService.getRequestDetail(requestId);

            // Kiểm tra yêu cầu có tồn tại không
            if (reqDetail == null) {
                request.setAttribute("error", "Yêu cầu không tồn tại.");
                request.getRequestDispatcher("/WEB-INF/views/error/404.jsp").forward(request, response);
                return;
            }

            // Truyền dữ liệu sang giao diện JSP để render
            request.setAttribute("reqDetail", reqDetail);
            request.getRequestDispatcher("/WEB-INF/views/operator/requests/detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/operator/requests");
        }
    }

    @Override
    // Bước 2: Xử lý các thao tác của Operator (Tiếp nhận, Từ chối, Lên lịch, Hoàn thành)
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

            // Xử lý 1: Operator bấm "Tiếp nhận" yêu cầu
            if ("accept".equals(action)) {
                success = requestService.acceptRequest(requestId, operatorId);
            } 
            // Xử lý 2: Operator bấm "Từ chối" yêu cầu
            else if ("reject".equals(action)) {
                String reason = request.getParameter("rejectReason");
                if (reason == null || reason.trim().isEmpty()) {
                    request.setAttribute("error", "Lý do từ chối không được để trống.");
                    doGet(request, response); // Quay lại trang chi tiết hiện lỗi
                    return;
                }
                success = requestService.rejectRequest(requestId, operatorId, reason.trim());
            } 
            // Xử lý 3: Operator "Lên lịch hẹn" sửa chữa
            else if ("schedule".equals(action)) {
                String appointmentDateStr = request.getParameter("appointmentDate");
                if (appointmentDateStr == null || appointmentDateStr.trim().isEmpty()) {
                    request.setAttribute("error", "Ngày hẹn không được để trống.");
                    doGet(request, response);
                    return;
                }
                // Parse date and schedule properly
                try {
                    String cleanDateStr = appointmentDateStr.trim().replace(" ", "T");
                    if (cleanDateStr.length() == 16) {
                        cleanDateStr += ":00";
                    }
                    LocalDateTime appointSchedule = LocalDateTime.parse(cleanDateStr);
                    success = requestService.scheduleAppointment(requestId, appointSchedule, operatorId);
                } catch (Exception e) {
                    request.setAttribute("error", "Định dạng ngày hẹn không hợp lệ.");
                    doGet(request, response);
                    return;
                }
            } 
            // Xử lý 4: Operator báo cáo "Hoàn thành" yêu cầu kèm hình ảnh minh chứng
            else if ("complete".equals(action)) {
                String notes = request.getParameter("notes");
                String noImageCheckbox = request.getParameter("no_image_checkbox");
                boolean isNoImage = "on".equals(noImageCheckbox);
                
                if (notes == null) {
                    notes = "";
                }
                
                // Xử lý upload file hình ảnh (nếu có)
                List<String> fileNames = new ArrayList<>();
                String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads" + File.separator + "requests";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                for (Part part : request.getParts()) {
                    if ("after_images".equals(part.getName()) && part.getSize() > 0) {
                        String fileName = UUID.randomUUID().toString() + "_" + getFileName(part);
                        part.write(uploadPath + File.separator + fileName);
                        fileNames.add("/uploads/requests/" + fileName);
                    }
                }
                
                // Validate bắt buộc phải có ảnh trừ khi đánh dấu "Lỗi đơn giản không cần ảnh"
                if (!isNoImage && fileNames.isEmpty()) {
                    request.setAttribute("error", "Vui lòng đính kèm ít nhất 1 ảnh minh chứng, hoặc tích chọn Lỗi đơn giản.");
                    doGet(request, response);
                    return;
                }
                
                String attachmentUrls2 = fileNames.isEmpty() ? null : String.join(",", fileNames);
                success = requestService.completeRequest(requestId, notes.trim(), attachmentUrls2);
            }

            // Bước 3: Ghi nhận lịch sử (Audit Log) sau khi thao tác thành công
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
                // Redirect on success to prevent form resubmission (Pattern PRG)
                response.sendRedirect(request.getContextPath() + "/operator/requests/detail?id=" + requestId);
            } else {
                // Optimistic locking failure (Tránh lỗi tranh chấp khi 2 Operator cùng lúc bấm tiếp nhận)
                request.setAttribute("error", "Thao tác không thành công! Yêu cầu này đã được tiếp nhận bởi người khác hoặc trạng thái đã thay đổi.");
                doGet(request, response);
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/operator/requests");
        }
    }

    private String getFileName(Part part) {
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
