package com.quanlyphongtro.controller.operator;
import com.quanlyphongtro.dao.FacilityDAO;
import java.util.List;
import com.quanlyphongtro.model.Facility;
import java.util.ArrayList;
import java.util.Map;
import com.quanlyphongtro.model.Room;
import java.util.HashMap;

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
    // Bước 1: Hiển thị Form báo cáo sự cố (Incident Report)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");

        // Lấy danh sách Cơ sở (Facility) mà Operator này đang được phân công quản lý
        FacilityDAO facilityDAO = new FacilityDAO();
        List<Facility> allFacilities = facilityDAO.findActiveList();
        List<Facility> myFacilities = new ArrayList<>();
        for (Facility f : allFacilities) {
            if (f.getOperatorId() != null && f.getOperatorId().equals(currentUser.getId())) {
                myFacilities.add(f);
            }
        }
        
        // Lấy danh sách Phòng thuộc về các Cơ sở trên để đưa vào Dropdown chọn vị trí
        Map<Integer, List<Room>> facilityRoomsMap = new HashMap<>();
        for (Facility f : myFacilities) {
            facilityRoomsMap.put(f.getId(), facilityDAO.findRoomsByFacilityId(f.getId()));
        }

        request.setAttribute("facilities", myFacilities);
        request.setAttribute("facilityRoomsMap", facilityRoomsMap);

        request.getRequestDispatcher("/WEB-INF/views/operator/incidents/create.jsp").forward(request, response);
    }

    @Override
    // Bước 2: Xử lý submit Form báo cáo sự cố
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

        // Validate basic fields: Bắt buộc phải có Cơ sở, Tên sự cố, Danh mục, Nội dung chi tiết
        if (facility == null || facility.trim().isEmpty() ||
            incidentName == null || incidentName.trim().isEmpty() ||
            category == null || category.trim().isEmpty() ||
            content == null || content.trim().isEmpty()) {
            
            request.setAttribute("error", "Vui lòng nhập đầy đủ các trường bắt buộc.");
            doGet(request, response);
            return;
        }

        try {
            // Bước 3: Handle file upload (Tải lên ảnh minh chứng sự cố)
            StringBuilder attachmentUrls = new StringBuilder();
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            for (Part part : request.getParts()) {
                if (part.getName().equals("images") && part.getSize() > 0) {
                    String fileName = extractFileName(part);
                    // Ensure unique filename để tránh bị ghi đè file trùng tên
                    String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
                    String filePath = uploadPath + File.separator + uniqueFileName;
                    part.write(filePath);

                    if (attachmentUrls.length() > 0) {
                        attachmentUrls.append(",");
                    }
                    attachmentUrls.append("/uploads/").append(uniqueFileName);
                }
            }

            // Bước 4: Xây dựng title để tương thích với hàm parse trong Request.java
            // Ví dụ format mong đợi: [Khẩn cấp] Vỡ ống nước tại Phòng 102 (Cơ sở A)
            String locationStr = "Phòng".equalsIgnoreCase(locationType) ? "Phòng " + locationDetail : locationDetail;
            if (locationStr == null || locationStr.trim().isEmpty()) {
                locationStr = "Khu vực chung";
            }
            
            String formattedTitle = String.format("[%s] %s tại %s (%s)", priority, incidentName, locationStr, facility);

            // Tạo mã yêu cầu theo format REQ-INC-{SEQ} đồng bộ với các mã khác
            String code = requestDAO.generateCode("INC");

            // Khởi tạo Object Request để lưu xuống DB
            Request req = new Request();
            req.setCode(code);
            req.setSenderId(currentUser.getId()); // Người gửi là Operator hiện tại
            req.setCategory(category);
            req.setTitle(formattedTitle);
            
            // Định dạng nội dung chi tiết kèm theo vị trí để tương thích với phần EditIncidentServlet
            String formattedContent = String.format("Vị trí: %s\nNội dung chi tiết: %s", locationStr, content);
            req.setContent(formattedContent);
            req.setAttachmentUrls1(attachmentUrls.toString()); // Lưu danh sách các URL ảnh

            // Bước 5: Lưu vào Database
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
