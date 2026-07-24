package com.quanlyphongtro.controller.tenant;

import com.google.gson.Gson;
import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.CommentDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.service.CommunityPostService;
import com.quanlyphongtro.service.impl.CommunityPostServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "TenantCommentServlet", urlPatterns = "/api/v1/posts/comment")
public class TenantCommentServlet extends BaseServlet {

    private final CommunityPostService postService = new CommunityPostServiceImpl();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", Map.of("code", "UNAUTHORIZED"));
            resp.getWriter().write(gson.toJson(error));
            return;
        }

        String postIdStr = req.getParameter("postId");
        String content = req.getParameter("content");

        if (postIdStr == null || content == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int postId = Integer.parseInt(postIdStr);
            CommentDTO newComment = postService.addComment(postId, currentUser.getId(), content);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("data", newComment);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson(responseMap));
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", Map.of("code", "INVALID_COMMENT", "message", e.getMessage()));
            resp.getWriter().write(gson.toJson(error));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error adding comment", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
