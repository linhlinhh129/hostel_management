package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Dependent;
import com.quanlyphongtro.service.DependentService;
import com.quanlyphongtro.service.impl.DependentServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "TenantDependentServlet", urlPatterns = {"/tenant/dependents", "/tenant/dependents/*"})
public class TenantDependentServlet extends BaseServlet {

    private final DependentService dependentService = new DependentServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        UserSessionDTO currentUser = getCurrentUser(req);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // List
                List<Dependent> dependents = dependentService.getDependentsByTenantId(currentUser.getId());
                req.setAttribute("dependents", dependents);
                req.getRequestDispatcher("/WEB-INF/views/tenant/dependents/list.jsp").forward(req, resp);
            } else {
                // Detail
                String idStr = pathInfo.substring(1);
                try {
                    int id = Integer.parseInt(idStr);
                    Optional<Dependent> depOpt = dependentService.getDependentById(id, currentUser.getId());
                    if (depOpt.isPresent()) {
                        req.setAttribute("dependent", depOpt.get());
                        req.getRequestDispatcher("/WEB-INF/views/tenant/dependents/detail.jsp").forward(req, resp);
                    } else {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        } catch (Exception e) {
            handleException(req, resp, e);
        }
    }

}
