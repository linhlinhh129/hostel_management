package com.quanlyphongtro.controller.tenant;

import com.google.gson.Gson;
import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.CommunityPostService;
import com.quanlyphongtro.service.impl.CommunityPostServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "TenantDeletePostServlet", urlPatterns = "/api/v1/posts/delete")
public class TenantDeletePostServlet extends BaseServlet {

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
        if (postIdStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int postId = Integer.parseInt(postIdStr);
            boolean isDeleted = postService.deletePost(postId, currentUser.getId());

            Map<String, Object> responseMap = new HashMap<>();
            if (isDeleted) {
                responseMap.put("success", true);
            } else {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                responseMap.put("success", false);
                responseMap.put("error", Map.of("message", "Bạn không có quyền xóa bài viết này hoặc bài viết đã được duyệt."));
            }

            resp.getWriter().write(gson.toJson(responseMap));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error deleting post", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
