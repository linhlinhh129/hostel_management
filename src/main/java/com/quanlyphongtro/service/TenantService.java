package com.quanlyphongtro.service;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.model.User;
import java.util.List;
import java.util.Optional;

public interface TenantService {
    Optional<Room> getTenantRoom(int tenantId);
    Optional<Facility> getFacilityByRoomId(int roomId);
    Optional<User> getTenantProfile(int tenantId);

    // Manager tenant/dependent methods
    int countTenants(int managerId, String keyword, String status);
    List<java.util.Map<String, Object>> getTenants(int managerId, String keyword, String status, int page, int pageSize);
    java.util.Map<String, Object> getTenantDetail(int tenantId, int managerId) throws Exception;
    List<java.util.Map<String, Object>> getTenantDependents(int tenantId);
    boolean editTenant(int tenantId, int managerId, String fullName, String phone, String email, String identityNumber, String permanentAddress, String gender, java.time.LocalDate dob) throws Exception;
    boolean softDeleteTenant(int tenantId);
    boolean lockTenantAccount(int tenantId);
    boolean unlockTenantAccount(int tenantId, MapStringConsumer usernameResetOut);
    boolean endRental(int tenantId);
    java.util.Map<String, Object> getDependentDetail(int dependentId, int managerId) throws Exception;
    boolean removeDependent(int dependentId, int managerId) throws Exception;
    boolean editDependent(int dependentId, int managerId, String fullName, String relationship, String phone, String gender, java.time.LocalDate dob, String identityNumber) throws Exception;
    boolean addDependent(int tenantId, int managerId, String fullName, String relationship, String phone, String gender, java.time.LocalDate dob, String identityNumber) throws Exception;

    interface MapStringConsumer {
        void accept(String val);
    }
}
