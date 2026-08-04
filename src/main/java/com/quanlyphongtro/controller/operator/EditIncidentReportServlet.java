package com.quanlyphongtro.controller.operator;

import com.quanlyphongtro.dao.FacilityDAO;
import com.quanlyphongtro.dao.RequestDAO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.model.Room;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/operator/incidents/edit")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2, // 2MB
    maxFileSize = 1024 * 1024 * 10,      // 10MB
    maxRequestSize = 1024 * 1024 * 50    // 50MB
)
public class EditIncidentReportServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(EditIncidentReportServlet.class);
    private RequestDAO requestDAO;
    private FacilityDAO facilityDAO;

    // Khởi tạo các DAO truy xuất dữ liệu sự cố và cơ sở
    @Override
    public void init() throws ServletException {
        requestDAO = new RequestDAO();
        facilityDAO = new FacilityDAO();
    }

    // Hiển thị giao diện chỉnh sửa báo cáo sự cố của Operator (chỉ áp dụng với báo cáo PENDING)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/operator/incidents/my-reports");
            return;
        }

        try {
            int requestId = Integer.parseInt(idParam);
            Request incident = requestDAO.getRequestById(requestId);

            // Kiểm tra xem báo cáo có tồn tại không
            if (incident == null) {
                response.sendRedirect(request.getContextPath() + "/operator/incidents/my-reports");
                return;
            }

            // Phân quyền: Chỉ cho phép chỉnh sửa báo cáo của chính mình và đang ở trạng thái PENDING
            if (incident.getSenderId() != currentUser.getId()) {
                request.setAttribute("error", "Bạn không có quyền chỉnh sửa báo cáo của người khác.");
                request.getRequestDispatcher("/WEB-INF/views/error/403.jsp").forward(request, response);
                return;
            }

            if (!"PENDING".equalsIgnoreCase(incident.getStatus())) {
                request.getSession().setAttribute("error", "Không thể chỉnh sửa báo cáo đã được tiếp nhận xử lý.");
                response.sendRedirect(request.getContextPath() + "/operator/incidents/my-reports");
                return;
            }

            loadFacilities(request, currentUser);
            
            // Phân tích dữ liệu từ Title và Content
            parseIncidentData(request, incident);
            
            request.setAttribute("incident", incident);
            request.getRequestDispatcher("/WEB-INF/views/operator/incidents/edit.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/operator/incidents/my-reports");
        } catch (Exception e) {
            logger.error("Error loading incident report for edit", e);
            response.sendRedirect(request.getContextPath() + "/operator/incidents/my-reports");
        }
    }

    // Xử lý cập nhật thông tin báo cáo sự cố (tên, mô tả, vị trí, ảnh đính kèm mới)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/operator/incidents/my-reports");
            return;
        }

        int requestId = Integer.parseInt(idParam);
        
        String facility = request.getParameter("facility");
        String locationType = request.getParameter("locationType");
        String locationDetail = request.getParameter("locationDetail");
        String category = request.getParameter("category");
        String priority = request.getParameter("priority");
        String incidentName = request.getParameter("incidentName");
        String content = request.getParameter("content");
        
        // existingImages chứa danh sách ảnh cũ (nếu có)
        String[] existingImages = request.getParameterValues("existingImages");

        // Validate basic fields
        if (facility == null || facility.trim().isEmpty() ||
            incidentName == null || incidentName.trim().isEmpty() ||
            category == null || category.trim().isEmpty() ||
            content == null || content.trim().isEmpty()) {
            
            request.setAttribute("error", "Vui lòng nhập đầy đủ các trường bắt buộc.");
            doGetForError(request, response, requestId, currentUser);
            return;
        }
        
        // Validate lengths
        if (incidentName.trim().length() > 50) {
            request.setAttribute("error", "Tiêu đề không được vượt quá 50 ký tự.");
            doGetForError(request, response, requestId, currentUser);
            return;
        }
        
        if (locationDetail != null && locationDetail.trim().length() > 50) {
            request.setAttribute("error", "Chi tiết vị trí không được vượt quá 50 ký tự.");
            doGetForError(request, response, requestId, currentUser);
            return;
        }
        
        if (content.trim().length() > 1000) {
            request.setAttribute("error", "Mô tả chi tiết không được vượt quá 1000 ký tự.");
            doGetForError(request, response, requestId, currentUser);
            return;
        }

        try {
            // Xử lý ảnh đính kèm
            StringBuilder attachmentUrls = new StringBuilder();
            
            // Thêm các ảnh cũ vào chuỗi
            if (existingImages != null && existingImages.length > 0) {
                for (String img : existingImages) {
                    if (attachmentUrls.length() > 0) {
                        attachmentUrls.append(",");
                    }
                    attachmentUrls.append(img);
                }
            }

            // Upload ảnh mới
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            for (Part part : request.getParts()) {
                if (part.getName().equals("images") && part.getSize() > 0) {
                    String fileName = extractFileName(part);
                    if (fileName != null && !fileName.isEmpty()) {
                        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
                        String filePath = uploadPath + File.separator + uniqueFileName;
                        part.write(filePath);

                        if (attachmentUrls.length() > 0) {
                            attachmentUrls.append(",");
                        }
                        attachmentUrls.append("/uploads/").append(uniqueFileName);
                    }
                }
            }
            
            // Xây dựng title để tương thích với hàm parse trong Request.java
            String locationStr = "Phòng".equalsIgnoreCase(locationType) ? "Phòng " + locationDetail : locationDetail;
            if (locationStr == null || locationStr.trim().isEmpty()) {
                locationStr = "Khu vực chung";
            }
            
            String formattedTitle = String.format("[%s] %s tại %s (%s)", priority, incidentName, locationStr, facility);

            Request req = new Request();
            req.setId(requestId);
            req.setSenderId(currentUser.getId());
            req.setCategory(category);
            req.setTitle(formattedTitle);
            
            String formattedContent = String.format("Vị trí: %s\nNội dung chi tiết: %s", locationStr, content);
            req.setContent(formattedContent);
            req.setAttachmentUrls1(attachmentUrls.toString());

            boolean success = requestDAO.updateIncidentReport(req);
            
            if (success) {
                request.getSession().setAttribute("success", "Cập nhật báo cáo sự cố thành công.");
                response.sendRedirect(request.getContextPath() + "/operator/incidents/my-reports");
            } else {
                request.setAttribute("error", "Cập nhật thất bại. Báo cáo này có thể đã được tiếp nhận xử lý hoặc bạn không có quyền.");
                doGetForError(request, response, requestId, currentUser);
            }
        } catch (Exception e) {
            logger.error("Error updating incident report", e);
            request.setAttribute("error", "Có lỗi hệ thống xảy ra. Vui lòng thử lại sau.");
            doGetForError(request, response, requestId, currentUser);
        }
    }
    
    // Tải lại dữ liệu lên form khi xảy ra lỗi trong quá trình submit dữ liệu
    private void doGetForError(HttpServletRequest request, HttpServletResponse response, int requestId, UserSessionDTO currentUser) throws ServletException, IOException {
        try {
            Request incident = requestDAO.getRequestById(requestId);
            if (incident != null) {
                loadFacilities(request, currentUser);
                parseIncidentData(request, incident);
                request.setAttribute("incident", incident);
            }
        } catch (Exception e) {
            logger.error("Error loading for error page", e);
        }
        request.getRequestDispatcher("/WEB-INF/views/operator/incidents/edit.jsp").forward(request, response);
    }
    
    // Lấy danh sách cơ sở và phòng thuộc quyền quản lý của Operator để hiển thị trên form
    private void loadFacilities(HttpServletRequest request, UserSessionDTO currentUser) {
        List<Facility> allFacilities = facilityDAO.findActiveList();
        List<Facility> myFacilities = new ArrayList<>();
        for (Facility f : allFacilities) {
            if (f.getOperatorId() != null && f.getOperatorId().equals(currentUser.getId())) {
                myFacilities.add(f);
            }
        }
        
        Map<Integer, List<Room>> facilityRoomsMap = new HashMap<>();
        for (Facility f : myFacilities) {
            facilityRoomsMap.put(f.getId(), facilityDAO.findRoomsByFacilityId(f.getId()));
        }

        request.setAttribute("facilities", myFacilities);
        request.setAttribute("facilityRoomsMap", facilityRoomsMap);
    }
    
    // Tách thông tin tiêu đề và nội dung báo cáo cũ để điền sẵn (pre-fill) vào form
    private void parseIncidentData(HttpServletRequest request, Request incident) {
        String title = incident.getTitle();
        String content = incident.getContent();
        
        String priority = "Bình thường";
        String incidentName = "";
        String locationStr = "";
        String facility = "";
        
        // Parse Title format: [%s] %s tại %s (%s)
        if (title != null) {
            Pattern pattern = Pattern.compile("^\\[(.*?)\\]\\s(.*?)\\stại\\s(.*?)\\s\\((.*?)\\)$");
            Matcher matcher = pattern.matcher(title);
            if (matcher.find()) {
                priority = matcher.group(1);
                incidentName = matcher.group(2);
                locationStr = matcher.group(3);
                facility = matcher.group(4);
            } else {
                incidentName = title; // fallback
            }
        }
        
        String parsedContent = content;
        if (content != null && content.startsWith("Vị trí:")) {
            int splitIdx = content.indexOf("\nNội dung chi tiết:");
            if (splitIdx != -1) {
                String locLine = content.substring(0, splitIdx);
                locationStr = locLine.replace("Vị trí: ", "").trim();
                parsedContent = content.substring(splitIdx + "\nNội dung chi tiết:".length()).trim();
            }
        }
        
        String locationType = "Khu vực chung";
        String locationDetail = locationStr;
        
        if (locationStr.startsWith("Phòng ")) {
            locationType = "Phòng";
            locationDetail = locationStr.substring(6).trim();
        }
        
        request.setAttribute("parsedPriority", priority);
        request.setAttribute("parsedIncidentName", incidentName);
        request.setAttribute("parsedFacility", facility);
        request.setAttribute("parsedLocationType", locationType);
        request.setAttribute("parsedLocationDetail", locationDetail);
        request.setAttribute("parsedContent", parsedContent);
    }

    // Trích xuất tên file từ header Content-Disposition của Multipart part
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
