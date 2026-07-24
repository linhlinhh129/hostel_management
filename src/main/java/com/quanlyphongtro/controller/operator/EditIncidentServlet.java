package com.quanlyphongtro.controller.operator;
import java.io.FileWriter;
import java.io.PrintWriter;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dao.RequestDAO;
import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.dto.UserSessionDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/operator/incidents/edit")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class EditIncidentServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String source = request.getParameter("source");
        if (source == null || source.trim().isEmpty()) { source = "my-reports"; }

        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + ("requests".equals(source) ? "/operator/requests" : "/operator/incidents/my-reports"));
            return;
        }

        RequestDAO dao = new RequestDAO();
        Request reqObj = dao.getRequestById(Integer.parseInt(idParam));

        if (reqObj == null || !"PENDING".equals(reqObj.getStatus())) {
            response.sendRedirect(request.getContextPath() + ("requests".equals(source) ? "/operator/requests?error=invalid_status" : "/operator/incidents/my-reports?error=invalid_status"));
            return;
        }

        String parsedPriority = "Bình thường";
        String parsedFacilityName = "";
        String parsedRoomCode = "";
        String parsedDescription = reqObj.getContent();

        if (reqObj.getTitle() != null && reqObj.getTitle().startsWith("[Khẩn cấp]")) {
            parsedPriority = "Khẩn cấp";
        }
        
        if (reqObj.getTitle() != null && reqObj.getTitle().contains("(") && reqObj.getTitle().endsWith(")")) {
            parsedFacilityName = reqObj.getTitle().substring(reqObj.getTitle().lastIndexOf("(") + 1, reqObj.getTitle().length() - 1);
        }
        
        if (reqObj.getContent() != null) {
            int descIdx = reqObj.getContent().indexOf("Nội dung chi tiết: ");
            if (descIdx != -1) {
                parsedDescription = reqObj.getContent().substring(descIdx + "Nội dung chi tiết: ".length());
            }
            if (reqObj.getContent().startsWith("Vị trí: Phòng ")) {
                int endIdx = reqObj.getContent().indexOf("\n");
                if (endIdx > 0) {
                    parsedRoomCode = reqObj.getContent().substring("Vị trí: Phòng ".length(), endIdx);
                }
            }
        }

        request.setAttribute("requestObj", reqObj);
        request.setAttribute("parsedPriority", parsedPriority);
        request.setAttribute("parsedFacilityName", parsedFacilityName);
        request.setAttribute("parsedRoomCode", parsedRoomCode);
        request.setAttribute("parsedDescription", parsedDescription);
        request.setAttribute("source", source);

        request.getRequestDispatcher("/WEB-INF/views/operator/incidents/edit.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserSessionDTO currentUser = getCurrentUser(request);
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            request.setCharacterEncoding("UTF-8");
            String requestIdStr = request.getParameter("requestId");
            String source = request.getParameter("source");
            if (source == null || source.trim().isEmpty()) { source = "my-reports"; }

            if (requestIdStr == null) {
                response.sendRedirect(request.getContextPath() + ("requests".equals(source) ? "/operator/requests" : "/operator/incidents/my-reports"));
                return;
            }
            
            int requestId = Integer.parseInt(requestIdStr);
            String category = request.getParameter("category");
            String priority = request.getParameter("priority");
            String description = request.getParameter("description");
            String facilityName = request.getParameter("facilityName");

            RequestDAO dao = new RequestDAO();
            Request existingReq = dao.getRequestById(requestId);
            
            if (existingReq == null || !"PENDING".equals(existingReq.getStatus()) || existingReq.getSenderId() != currentUser.getId()) {
                response.sendRedirect(request.getContextPath() + ("requests".equals(source) ? "/operator/requests?error=unauthorized" : "/operator/incidents/my-reports?error=unauthorized"));
                return;
            }

            String locationStr = "Khu vực chung";
            if (existingReq.getContent() != null && existingReq.getContent().startsWith("Vị trí: ")) {
                int endIdx = existingReq.getContent().indexOf("\n");
                if (endIdx > 0) {
                    locationStr = existingReq.getContent().substring("Vị trí: ".length(), endIdx);
                }
            }

            String formattedTitle = String.format("[%s] Sự cố %s tại %s (%s)", priority, category, locationStr, facilityName);
            String formattedContent = String.format("Vị trí: %s\nNội dung chi tiết: %s", locationStr, description);

            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            StringBuilder attachmentUrls = new StringBuilder(existingReq.getAttachmentUrls1() != null ? existingReq.getAttachmentUrls1() : "");
            for (Part part : request.getParts()) {
                if (part.getName().equals("attachments") && part.getSize() > 0) {
                    String fileName = UUID.randomUUID().toString() + "_" + extractFileName(part);
                    part.write(uploadPath + File.separator + fileName);
                    if (attachmentUrls.length() > 0) attachmentUrls.append(",");
                    attachmentUrls.append("/uploads/").append(fileName);
                }
            }

            existingReq.setCategory(category);
            existingReq.setTitle(formattedTitle);
            existingReq.setContent(formattedContent);
            existingReq.setAttachmentUrls1(attachmentUrls.toString());

            boolean success = dao.updateIncidentReport(existingReq);

            if (success) {
                response.sendRedirect(request.getContextPath() + ("requests".equals(source) ? "/operator/requests?success=edit" : "/operator/incidents/my-reports?success=edit"));
            } else {
                response.sendRedirect(request.getContextPath() + "/operator/incidents/edit?id=" + requestId + "&error=update_failed&source=" + source);
            }
        } catch (Throwable t) {
            try {
                FileWriter fw = new FileWriter("f:\\SU26\\New folder\\hostel_management\\error.log", true);
                PrintWriter pw = new PrintWriter(fw);
                pw.println("--- ERROR IN EditIncidentServlet ---");
                t.printStackTrace(pw);
                pw.close();
            } catch (Exception ex) {}
            t.printStackTrace();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("<h3>Đã xảy ra lỗi hệ thống (500):</h3><pre>");
            t.printStackTrace(response.getWriter());
            response.getWriter().write("</pre>");
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                String fileName = s.substring(s.indexOf("=") + 2, s.length() - 1);
                return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
            }
        }
        return "";
    }
}
