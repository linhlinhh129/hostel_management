package com.quanlyphongtro.controller.tenant;
import com.quanlyphongtro.dao.PostReactionDAO;

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

@WebServlet(name = "TenantLikeServlet", urlPatterns = "/api/v1/posts/like")
public class TenantLikeServlet extends BaseServlet {

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
            boolean isLiked = postService.toggleLike(postId, currentUser.getId());
            
            // Lấy lại count để trả về
            int likeCount = new PostReactionDAO().getLikeCount(postId);

            Map<String, Object> data = new HashMap<>();
            data.put("liked", isLiked);
            data.put("likeCount", likeCount);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("success", true);
            responseMap.put("data", data);

            resp.getWriter().write(gson.toJson(responseMap));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error toggling like", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
