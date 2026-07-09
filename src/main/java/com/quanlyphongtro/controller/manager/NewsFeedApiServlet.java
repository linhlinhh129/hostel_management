package com.quanlyphongtro.controller.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.quanlyphongtro.dto.CommentDTO;
import com.quanlyphongtro.dto.CommunityPostDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.service.NewsFeedService;
import com.quanlyphongtro.service.impl.NewsFeedServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "NewsFeedApiServlet", urlPatterns = {"/api/v1/news-feed", "/api/v1/news-feed/*", "/api/v1/posts/*", "/api/v1/comments/*"})
public class NewsFeedApiServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(NewsFeedApiServlet.class);
    private final NewsFeedService newsFeedService = new NewsFeedServiceImpl();
    
    // Custom GSON to handle LocalDateTime
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String path = request.getRequestURI().substring(request.getContextPath().length());
        HttpSession session = request.getSession(false);
        UserSessionDTO currentUser = (session != null) ? (UserSessionDTO) session.getAttribute("currentUser") : null;
        
        if (currentUser == null) {
            sendError(out, "Chưa đăng nhập", 401, response);
            return;
        }

        try {
            if ("/api/v1/news-feed".equals(path)) {
                int offset = 0;
                int limit = 10;
                String offsetParam = request.getParameter("offset");
                String limitParam = request.getParameter("limit");
                if (offsetParam != null) offset = Integer.parseInt(offsetParam);
                if (limitParam != null) limit = Integer.parseInt(limitParam);

                List<CommunityPostDTO> posts = newsFeedService.getNewsFeed(currentUser.getId(), offset, limit);
                sendSuccess(out, posts);
            } else if ("/api/v1/news-feed/top".equals(path)) {
                int limit = 5;
                String limitParam = request.getParameter("limit");
                if (limitParam != null) limit = Integer.parseInt(limitParam);
                
                List<CommunityPostDTO> topPosts = newsFeedService.getTopInteractivePosts(limit);
                sendSuccess(out, topPosts);
            } else if (path.matches("/api/v1/posts/\\d+/comments")) {
                int postId = extractPostId(path);
                List<CommentDTO> comments = newsFeedService.getComments(postId);
                sendSuccess(out, comments);
            } else {
                sendError(out, "Not found", 404, response);
            }
        } catch (Exception e) {
            logger.error("Error in GET NewsFeedApi", e);
            sendError(out, "Lỗi server", 500, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String path = request.getRequestURI().substring(request.getContextPath().length());
        HttpSession session = request.getSession(false);
        UserSessionDTO currentUser = (session != null) ? (UserSessionDTO) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            sendError(out, "Chưa đăng nhập", 401, response);
            return;
        }

        try {
            if (path.matches("/api/v1/posts/\\d+/reactions")) {
                int postId = extractPostId(path);
                boolean isLiked = newsFeedService.toggleLike(postId, currentUser.getId());
                Map<String, Object> data = new HashMap<>();
                data.put("isLiked", isLiked);
                sendSuccess(out, data);
            } else if (path.matches("/api/v1/posts/\\d+/comments")) {
                int postId = extractPostId(path);
                
                // Read JSON body
                StringBuilder sb = new StringBuilder();
                BufferedReader reader = request.getReader();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                
                Map payload = gson.fromJson(sb.toString(), Map.class);
                String content = payload != null ? (String) payload.get("content") : "";
                
                CommentDTO comment = newsFeedService.addComment(postId, currentUser.getId(), content);
                sendSuccess(out, comment);
            } else {
                sendError(out, "Not found", 404, response);
            }
        } catch (ValidationException e) {
            sendError(out, e.getMessage(), 400, response);
        } catch (Exception e) {
            logger.error("Error in POST NewsFeedApi", e);
            sendError(out, "Lỗi server", 500, response);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String path = request.getRequestURI().substring(request.getContextPath().length());
        HttpSession session = request.getSession(false);
        UserSessionDTO currentUser = (session != null) ? (UserSessionDTO) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            sendError(out, "Chưa đăng nhập", 401, response);
            return;
        }

        try {
            if (path.matches("/api/v1/comments/\\d+")) {
                String[] parts = path.split("/");
                int commentId = Integer.parseInt(parts[parts.length - 1]);
                
                boolean success = newsFeedService.deleteComment(commentId);
                if (success) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("success", true);
                    sendSuccess(out, data);
                } else {
                    sendError(out, "Không thể xóa bình luận", 400, response);
                }
            } else {
                sendError(out, "Not found", 404, response);
            }
        } catch (Exception e) {
            logger.error("Error in DELETE NewsFeedApi", e);
            sendError(out, "Lỗi server", 500, response);
        }
    }

    private int extractPostId(String path) {
        String[] parts = path.split("/");
        for (int i = 0; i < parts.length; i++) {
            if ("posts".equals(parts[i]) && i + 1 < parts.length) {
                return Integer.parseInt(parts[i + 1]);
            }
        }
        return -1;
    }

    private void sendSuccess(PrintWriter out, Object data) {
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("data", data);
        out.print(gson.toJson(res));
    }

    private void sendError(PrintWriter out, String message, int status, HttpServletResponse response) {
        response.setStatus(status);
        Map<String, Object> res = new HashMap<>();
        res.put("success", false);
        res.put("error", message);
        out.print(gson.toJson(res));
    }
}
