package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.service.PostInteractionService;
import com.quanlyphongtro.service.impl.PostInteractionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/manager/articles/reaction")
public class PostReactionServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(PostReactionServlet.class);
    private PostInteractionService interactionService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.interactionService = new PostInteractionServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = (User) request.getSession().getAttribute("currentUser");
        if (currentUser == null) {
            sendJsonError(response, "Vui lòng đăng nhập để thực hiện", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            int postId = Integer.parseInt(request.getParameter("postId"));
            
            boolean success = interactionService.toggleLike(postId, currentUser.getUserId());
            boolean hasLiked = interactionService.hasLiked(postId, currentUser.getUserId());
            
            if (success) {
                // Determine if it was liked or unliked based on current state
                String msg = hasLiked ? "Đã thích bài viết" : "Đã bỏ thích bài viết";
                sendJsonSuccess(response, msg, hasLiked);
            } else {
                sendJsonError(response, "Không thể thực hiện hành động này", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (NumberFormatException e) {
            sendJsonError(response, "ID bài viết không hợp lệ", HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error in PostReactionServlet", e);
            sendJsonError(response, "Lỗi hệ thống", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
