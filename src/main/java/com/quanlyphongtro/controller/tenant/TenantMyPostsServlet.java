package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.controller.BaseServlet;
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

@WebServlet(name = "TenantMyPostsServlet", urlPatterns = "/tenant/my-posts")
public class TenantMyPostsServlet extends BaseServlet {

    private final CommunityPostService postService = new CommunityPostServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<NewsFeedDTO> posts = postService.getMyPosts(currentUser.getId());
        req.setAttribute("posts", posts);
        
        req.getRequestDispatcher("/WEB-INF/views/tenant/my-posts.jsp").forward(req, resp);
    }
}
