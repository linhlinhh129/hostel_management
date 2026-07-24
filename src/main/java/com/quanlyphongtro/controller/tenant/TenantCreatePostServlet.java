package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.service.CommunityPostService;
import com.quanlyphongtro.service.impl.CommunityPostServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

@WebServlet(name = "TenantCreatePostServlet", urlPatterns = "/tenant/post/create")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class TenantCreatePostServlet extends BaseServlet {

    private final CommunityPostService postService = new CommunityPostServiceImpl();

    private static final String[] ALLOWED_TYPES = { "image/jpeg", "image/png", "image/webp", "image/jpg" };

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/tenant/create-post.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UserSessionDTO currentUser = getCurrentUser(req);
            if (currentUser == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            String title = req.getParameter("title");
            String content = req.getParameter("content");

            // Xử lý upload ảnh (chỉ lưu 1 ảnh vào cột image_url NVARCHAR(500))
            String imageUrl = null;
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Collection<Part> parts = req.getParts();
            for (Part part : parts) {
                if (part.getName().equals("images") && part.getSize() > 0) {
                    // Validate MIME type
                    String contentType = part.getContentType();
                    if (!isAllowedType(contentType)) {
                        throw new ValidationException("Chỉ chấp nhận ảnh JPG, PNG hoặc WEBP");
                    }

                    String originalFileName = getSubmittedFileName(part);
                    String fileName = UUID.randomUUID().toString() + "_" + originalFileName;
                    part.write(uploadPath + File.separator + fileName);
                    imageUrl = "/uploads/" + fileName;
                    break; // Chỉ lấy ảnh đầu tiên (image_url là NVARCHAR(500), không phải MAX)
                }
            }

            postService.createPost(currentUser.getId(), title, content, imageUrl);

            setFlashMessage(req, "success", "Bài viết của bạn đã được gửi và đang chờ phê duyệt.");
            resp.sendRedirect(req.getContextPath() + "/tenant/my-posts");
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/tenant/create-post.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.error("Error creating post", e);
            req.setAttribute("errorMessage", "Có lỗi xảy ra khi đăng bài.");
            req.getRequestDispatcher("/WEB-INF/views/tenant/create-post.jsp").forward(req, resp);
        }
    }

    private boolean isAllowedType(String contentType) {
        if (contentType == null)
            return false;
        for (String allowed : ALLOWED_TYPES) {
            if (allowed.equalsIgnoreCase(contentType))
                return true;
        }
        return false;
    }

    private String getSubmittedFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "unknown";
    }
}
