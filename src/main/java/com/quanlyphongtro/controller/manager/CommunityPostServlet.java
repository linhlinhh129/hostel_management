package com.quanlyphongtro.controller.manager;

import com.google.gson.Gson;
import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.CommunityPostCreateDTO;
import com.quanlyphongtro.dto.CommunityPostDTO;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "CommunityPostServlet", urlPatterns = {"/manager/articles", "/manager/articles/*", "/manager/community-posts/*", "/manager/articles/detail"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 5 * 1024 * 1024, // 5MB
    maxRequestSize = 10 * 1024 * 1024 // 10MB
)
public class CommunityPostServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(CommunityPostServlet.class);
    private CommunityPostService communityPostService;
    private final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        super.init();
        this.communityPostService = new CommunityPostServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String servletPath = request.getServletPath();
        String path = servletPath + (pathInfo != null ? pathInfo : "");

        // Rate limiting headers
        response.setHeader("X-RateLimit-Limit", "100");
        response.setHeader("X-RateLimit-Remaining", "99");
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + 60));

        if ("/create".equals(pathInfo)) {
            request.getRequestDispatcher("/WEB-INF/views/manager/postManagement/create.jsp").forward(request, response);
            return;
        }

        if ("/manager/articles/detail".equals(path)) {
            try {
                int postId = Integer.parseInt(request.getParameter("id"));
                UserSessionDTO currentUser = (UserSessionDTO) request.getSession().getAttribute("currentUser");
                CommunityPostDTO post = communityPostService.getPostById(postId, currentUser.getId());
                if (post != null) {
                    request.setAttribute("post", post);
                    request.getRequestDispatcher("/WEB-INF/views/manager/postManagement/detail.jsp").forward(request, response);
                } else {
                    response.sendRedirect(request.getContextPath() + "/manager/articles?error=notfound");
                }
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/manager/articles?error=invalid");
            }
            return;
        }

        if ("/manager/articles".equals(path) || "/manager/articles/pending".equals(path)) {
            int cursor = 0;
            int limit = 100; // Load up to 100 posts since pagination was removed
            try {
                if (request.getParameter("cursor") != null) cursor = Integer.parseInt(request.getParameter("cursor"));
                if (request.getParameter("limit") != null) limit = Integer.parseInt(request.getParameter("limit"));
            } catch (NumberFormatException ignored) {}

            UserSessionDTO currentUser = (UserSessionDTO) request.getSession().getAttribute("currentUser");
            List<CommunityPostDTO> pendingPosts = communityPostService.getPostsForManager(currentUser.getId(), cursor, limit);
            
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With")) || "application/json".equals(request.getHeader("Accept"))) {
                sendJson(response, pendingPosts);
                return;
            }
            
            request.setAttribute("posts", pendingPosts);
            request.setAttribute("nextCursor", pendingPosts.isEmpty() ? 0 : pendingPosts.get(pendingPosts.size() - 1).getId());
            request.getRequestDispatcher("/WEB-INF/views/manager/postManagement/list-pending.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/manager/articles");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("X-RateLimit-Limit", "100");
        response.setHeader("X-RateLimit-Remaining", "99");
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() / 1000 + 60));

        String pathInfo = request.getPathInfo();
        String servletPath = request.getServletPath();
        String path = servletPath + (pathInfo != null ? pathInfo : "");
        UserSessionDTO currentUser = (UserSessionDTO) request.getSession().getAttribute("currentUser");

        try {
            if (path.endsWith("/manager/articles/create") || path.endsWith("/create")) {
                String title = request.getParameter("title");
                String content = request.getParameter("content");
                Part imagePart = request.getPart("image");

                CommunityPostCreateDTO dto = new CommunityPostCreateDTO();
                dto.setTitle(title);
                dto.setContent(content);

                String uploadPath = request.getServletContext().getRealPath("/uploads");
                communityPostService.createPost(dto, imagePart, currentUser.getId(), uploadPath, currentUser.getRole());
                response.sendRedirect(request.getContextPath() + "/manager/articles?success=create");
                return;
            }

            if (path.endsWith("/manager/articles/approve") || path.endsWith("/approve")) {
                int postId = Integer.parseInt(request.getParameter("postId"));
                communityPostService.approvePost(postId, currentUser.getId());
                Map<String, Object> res = new HashMap<>();
                res.put("success", true);
                res.put("message", "Bài viết đã được duyệt.");
                sendJson(response, res);
                return;
            }

            if (path.endsWith("/manager/articles/delete") || path.endsWith("/delete")) {
                int postId = Integer.parseInt(request.getParameter("postId"));
                communityPostService.deletePost(postId);
                Map<String, Object> res = new HashMap<>();
                res.put("success", true);
                res.put("message", "Bài viết đã được xóa.");
                sendJson(response, res);
                return;
            }
        } catch (ValidationException e) {
            if (path.endsWith("/manager/articles/create") || path.endsWith("/create")) {
                request.setAttribute("error", e.getMessage());
                request.getRequestDispatcher("/WEB-INF/views/manager/postManagement/create.jsp").forward(request, response);
            } else {
                Map<String, Object> res = new HashMap<>();
                res.put("success", false);
                res.put("error", e.getMessage());
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJson(response, res);
            }
            return;
        } catch (Exception e) {
            logger.error("Error in CommunityPostServlet", e);
            if (path.endsWith("/manager/articles/create") || path.endsWith("/create")) {
                request.setAttribute("error", e.getMessage() != null ? e.getMessage() : "Có lỗi xảy ra, vui lòng thử lại sau.");
                request.getRequestDispatcher("/WEB-INF/views/manager/postManagement/create.jsp").forward(request, response);
            } else {
                Map<String, Object> res = new HashMap<>();
                res.put("success", false);
                res.put("error", "Lỗi hệ thống.");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                sendJson(response, res);
            }
            return;
        }
        Map<String, Object> notFoundRes = new HashMap<>();
        notFoundRes.put("success", false);
        notFoundRes.put("error", "Action not found. path=" + path + " pathInfo=" + pathInfo);
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        sendJson(response, notFoundRes);
    }

    private void sendJson(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(data));
    }
}
