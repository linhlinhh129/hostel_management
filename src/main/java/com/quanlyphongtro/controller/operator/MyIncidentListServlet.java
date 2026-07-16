package com.quanlyphongtro.controller.operator;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dao.RequestDAO;
import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.dto.UserSessionDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/operator/incidents/my-reports")
public class MyIncidentListServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int page = 1;
        int limit = 10;
        try {
            if (request.getParameter("page") != null) {
                page = Integer.parseInt(request.getParameter("page"));
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        int offset = (page - 1) * limit;

        String status = request.getParameter("status");
        String category = request.getParameter("category");

        RequestDAO dao = new RequestDAO();
        int total = dao.countIncidentsBySender(currentUser.getId(), status, category);
        List<Request> items = dao.getIncidentsBySender(currentUser.getId(), status, category, offset, limit);
        int totalPages = (int) Math.ceil((double) total / limit);

        request.setAttribute("items", items);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", total);
        request.setAttribute("paramStatus", status);
        request.setAttribute("paramCategory", category);
        request.setAttribute("availableCategories", dao.getDistinctCategories());

        request.getRequestDispatcher("/WEB-INF/views/operator/incidents/list.jsp").forward(request, response);
    }
}
