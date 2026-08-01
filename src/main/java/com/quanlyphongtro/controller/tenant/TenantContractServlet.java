package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Contract;
import com.quanlyphongtro.service.ContractService;
import com.quanlyphongtro.service.impl.ContractServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "TenantContractServlet", urlPatterns = "/tenant/contracts")
public class TenantContractServlet extends BaseServlet {

    private final ContractService contractService = new ContractServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UserSessionDTO currentUser = getCurrentUser(req);
            if (currentUser == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }

            String idParam = req.getParameter("id");
            Contract contract = null;

            if (idParam != null && !idParam.trim().isEmpty()) {
                try {
                    int contractId = Integer.parseInt(idParam);
                    contract = contractService.getContractDetailForTenant(contractId, currentUser.getId());
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID hợp đồng không hợp lệ");
                    return;
                }
            } else {
                List<Contract> contracts = contractService.getContractsByTenant(currentUser.getId());
                if (contracts != null && !contracts.isEmpty()) {
                    contract = contractService.getContractDetailForTenant(contracts.get(0).getContractId(), currentUser.getId());
                }
            }

            req.setAttribute("contract", contract);
            req.setAttribute("activeMenu", "contracts");
            req.getRequestDispatcher("/WEB-INF/views/tenant/contracts/detail.jsp").forward(req, resp);

        } catch (Exception e) {
            forwardError(req, resp, "/WEB-INF/views/tenant/contracts/detail.jsp", e);
        }
    }
}
