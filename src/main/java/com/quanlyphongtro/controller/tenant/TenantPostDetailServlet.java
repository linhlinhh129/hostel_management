package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.CommentDTO;
import com.quanlyphongtro.dto.NewsFeedDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.CommunityPostService;
import com.quanlyphongtro.service.impl.CommunityPostServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "TenantPostDetailServlet", urlPatterns = "/tenant/post/detail")
public class TenantPostDetailServlet extends BaseServlet {

    private final CommunityPostService postService = new CommunityPostServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String idStr = req.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu ID bài viết");
            return;
        }

        try {
            int postId = Integer.parseInt(idStr);
            NewsFeedDTO post = postService.getPostDetail(postId, currentUser.getId());
            if (post == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Bài viết không tồn tại hoặc đã bị xóa");
                return;
            }

            List<CommentDTO> comments = postService.getCommentsByPostId(postId);
            req.setAttribute("post", post);
            req.setAttribute("comments", comments);

            req.getRequestDispatcher("/WEB-INF/views/tenant/post-detail.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID bài viết không hợp lệ");
        } catch (Exception e) {
            logger.error("Error displaying post detail", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi máy chủ nội bộ");
        }
    }
}
