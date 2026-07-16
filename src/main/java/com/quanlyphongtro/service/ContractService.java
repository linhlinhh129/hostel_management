package com.quanlyphongtro.service;

import com.quanlyphongtro.model.Contract;

import java.util.List;
import java.util.Map;

public interface ContractService {
    List<Contract> getContractsByManager(int managerId, String searchName);
    Contract getContractDetail(int contractId, int managerId);
    void createContract(Contract contract, int managerId) throws Exception;
    List<Contract> getContractsByTenant(int tenantId);
    Contract getContractDetailForTenant(int contractId, int tenantId);
    List<com.quanlyphongtro.model.Room> getAvailableRooms(int managerId);
    Map<String, Object> getContractForAddTenant(int contractId, int managerId) throws Exception;
    Map<String, Object> addTenantFromContract(int contractId, int roomId, String fullName, String phone, String email, String identityNumber, String permanentAddress, String gender, String dobStr, String contractStartDateStr, boolean confirmReactivate, int managerId, String loginLink) throws Exception;
    Map<String, String> verifyContractForDelete(int contractId, int managerId);
    boolean softDeleteContract(int contractId);
}
