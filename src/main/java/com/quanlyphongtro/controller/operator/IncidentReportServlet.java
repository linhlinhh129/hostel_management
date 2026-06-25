package com.quanlyphongtro.controller.operator;

import com.quanlyphongtro.dao.RequestDAO;
import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.dto.UserSessionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/operator/incidents/create")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class IncidentReportServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(IncidentReportServlet.class);
    private RequestDAO requestDAO;

    @Override
    public void init() throws ServletException {
        requestDAO = new RequestDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");

        com.quanlyphongtro.dao.FacilityDAO facilityDAO = new com.quanlyphongtro.dao.FacilityDAO();
        java.util.List<com.quanlyphongtro.model.Facility> allFacilities = facilityDAO.findActiveList();
        java.util.List<com.quanlyphongtro.model.Facility> myFacilities = new java.util.ArrayList<>();
        for (com.quanlyphongtro.model.Facility f : allFacilities) {
            if (f.getOperatorId() != null && f.getOperatorId().equals(currentUser.getId())) {
                myFacilities.add(f);
            }
        }
        
        java.util.Map<Integer, java.util.List<com.quanlyphongtro.model.Room>> facilityRoomsMap = new java.util.HashMap<>();
        for (com.quanlyphongtro.model.Facility f : myFacilities) {
            facilityRoomsMap.put(f.getId(), facilityDAO.findRoomsByFacilityId(f.getId()));
        }

        request.setAttribute("facilities", myFacilities);
        request.setAttribute("facilityRoomsMap", facilityRoomsMap);

        request.getRequestDispatcher("/WEB-INF/views/operator/incidents/create.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        String facility = request.getParameter("facility");
        String locationType = request.getParameter("locationType");
        String locationDetail = request.getParameter("locationDetail");
        String category = request.getParameter("category");
        String priority = request.getParameter("priority");
        String incidentName = request.getParameter("incidentName");
        String content = request.getParameter("content");

        // Validate basic fields
        if (facility == null || facility.trim().isEmpty() ||
            incidentName == null || incidentName.trim().isEmpty() ||
            category == null || category.trim().isEmpty() ||
            content == null || content.trim().isEmpty()) {
            
            request.setAttribute("error", "Vui lòng nhập đầy đủ các trường bắt buộc.");
            doGet(request, response);
            return;
        }

        try {
            // Handle file upload
            StringBuilder attachmentUrls = new StringBuilder();
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            for (Part part : request.getParts()) {
                if (part.getName().equals("images") && part.getSize() > 0) {
                    String fileName = extractFileName(part);
                    // Ensure unique filename
                    String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
                    String filePath = uploadPath + File.separator + uniqueFileName;
                    part.write(filePath);

                    if (attachmentUrls.length() > 0) {
                        attachmentUrls.append(",");
                    }
                    attachmentUrls.append(request.getContextPath()).append("/uploads/").append(uniqueFileName);
                }
            }

            // Xây dựng title để tương thích với hàm parse trong Request.java
            // Ví dụ format mong đợi: [Khẩn cấp] Vỡ ống nước tại Phòng 102 (Cơ sở A)
            String locationStr = "Phòng".equalsIgnoreCase(locationType) ? "Phòng " + locationDetail : locationDetail;
            if (locationStr == null || locationStr.trim().isEmpty()) {
                locationStr = "Khu vực chung";
            }
            
            String formattedTitle = String.format("[%s] %s tại %s (%s)", priority, incidentName, locationStr, facility);

            // Generate a random code for the request
            String code = "INC-" + System.currentTimeMillis();

            Request req = new Request();
            req.setCode(code);
            req.setSenderId(currentUser.getId());
            req.setCategory(category);
            req.setTitle(formattedTitle);
            req.setContent(content);
            req.setAttachmentUrls1(attachmentUrls.toString());

            boolean success = requestDAO.insertIncidentReport(req);
            
            if (success) {
                // Thay vì chuyển hướng về list, có thể redirect hoặc forward kèm thông báo thành công
                // Tạm thời redirect về dashboard hoặc chính trang create với success message
                response.sendRedirect(request.getContextPath() + "/operator/incidents/my-reports");
            } else {
                request.setAttribute("error", "Đã xảy ra lỗi khi lưu báo cáo vào cơ sở dữ liệu.");
                doGet(request, response);
            }
        } catch (Exception e) {
            logger.error("Error creating incident report", e);
            request.setAttribute("error", "Có lỗi hệ thống xảy ra. Vui lòng thử lại sau.");
            doGet(request, response);
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return "";
    }
}
