package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.service.RequestService;
import com.quanlyphongtro.service.TenantService;
import com.quanlyphongtro.service.impl.RequestServiceImpl;
import com.quanlyphongtro.service.impl.TenantServiceImpl;
import com.quanlyphongtro.service.UserService;
import com.quanlyphongtro.service.impl.UserServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.annotation.MultipartConfig;

@WebServlet(name = "TenantRequestServlet", urlPatterns = {"/tenant/tickets", "/tenant/tickets/*"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,       // 10MB
        maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class TenantRequestServlet extends BaseServlet {

    private final TenantService tenantService = new TenantServiceImpl();
    private final RequestService requestService = new RequestServiceImpl();
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        UserSessionDTO currentUser = getCurrentUser(req);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // List
                List<Request> tickets = requestService.getRequestsBySenderId(currentUser.getId());
                req.setAttribute("tickets", tickets);
                req.getRequestDispatcher("/WEB-INF/views/tenant/tickets/list.jsp").forward(req, resp);
            } else if (pathInfo.equals("/create")) {
                // Show Create Form
                Optional<Room> roomOpt = tenantService.getTenantRoom(currentUser.getId());
                roomOpt.ifPresent(room -> req.setAttribute("room", room));
                req.setAttribute("staffUsers", userService.getStaffUsersByTenantId(currentUser.getId()));
                req.getRequestDispatcher("/WEB-INF/views/tenant/tickets/create.jsp").forward(req, resp);
            } else {
                // Detail
                String idStr = pathInfo.substring(1);
                try {
                    int id = Integer.parseInt(idStr);
                    Optional<Request> reqOpt = requestService.getRequestById(id, currentUser.getId());
                    if (reqOpt.isPresent()) {
                        req.setAttribute("ticket", reqOpt.get());
                        req.getRequestDispatcher("/WEB-INF/views/tenant/tickets/detail.jsp").forward(req, resp);
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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if ("/create".equals(pathInfo)) {
            UserSessionDTO currentUser = getCurrentUser(req);
            
            String category = req.getParameter("category");
            String title = req.getParameter("title");
            String content = req.getParameter("content");

            Request request = new Request();
            request.setSenderId(currentUser.getId());
            request.setCategory(category);
            request.setTitle(title);
            request.setContent(content);
            
            String assignedStaffIdStr = req.getParameter("assignedStaffId");
            if (assignedStaffIdStr != null && !assignedStaffIdStr.isBlank()) {
                try {
                    request.setAssignedStaffId(Integer.parseInt(assignedStaffIdStr.trim()));
                } catch (NumberFormatException e) {
                    logger.error("Failed to parse assignedStaffId: '" + assignedStaffIdStr + "'", e);
                }
            }

            try {
                jakarta.servlet.http.Part filePart = req.getPart("attachment");
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = java.nio.file.Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String uploadDir = getServletContext().getRealPath("/") + "uploads" + java.io.File.separator + "tickets";
                    java.io.File dir = new java.io.File(uploadDir);
                    if (!dir.exists()) dir.mkdirs();

                    String uniqueFileName = System.currentTimeMillis() + "_" + fileName;
                    String savePath = uploadDir + java.io.File.separator + uniqueFileName;
                    filePart.write(savePath);
                    request.setAttachmentUrls1("/uploads/tickets/" + uniqueFileName);
                }
            } catch (Exception ex) {
                logger.error("Error uploading attachment", ex);
            }

            boolean success = requestService.createRequest(request);
            if (success) {
                setFlashMessage(req, "success", "Đã gửi yêu cầu thành công.");
                resp.sendRedirect(req.getContextPath() + "/tenant/tickets");
            } else {
                req.setAttribute("errorMessage", "Không thể tạo yêu cầu, vui lòng thử lại.");
                Optional<Room> roomOpt = tenantService.getTenantRoom(currentUser.getId());
                roomOpt.ifPresent(room -> req.setAttribute("room", room));
                req.getRequestDispatcher("/WEB-INF/views/tenant/tickets/create.jsp").forward(req, resp);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
