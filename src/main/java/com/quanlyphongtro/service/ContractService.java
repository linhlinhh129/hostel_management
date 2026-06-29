package com.quanlyphongtro.service;

import com.quanlyphongtro.model.Contract;

import java.util.List;

public interface ContractService {
    List<Contract> getContractsByManager(int managerId, String searchName);
    Contract getContractDetail(int contractId, int managerId);
    void createContract(Contract contract, int managerId) throws Exception;
    List<Contract> getContractsByTenant(int tenantId);
    Contract getContractDetailForTenant(int contractId, int tenantId);
}
