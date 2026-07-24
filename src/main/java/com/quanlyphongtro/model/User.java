package com.quanlyphongtro.model;
import java.util.Date;
import java.sql.Timestamp;

import com.quanlyphongtro.constant.StatusConstant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {
    private Integer id;
    private String username;
    private String passwordHash;
    private String role;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    private String avatarUrl;
    private boolean forceChangePass; // maps to force_change_pass (BIT) in DB

    // Tenant extensions
    private String identityNumber;
    private LocalDate dob;
    private String gender;
    private String permanentAddress;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    /** Names of facilities this user manages (populated on demand). */
    private List<String> facilityNames = new ArrayList<>();

    public User() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public boolean isForceChangePass() { return forceChangePass; }
    public void setForceChangePass(boolean forceChangePass) { this.forceChangePass = forceChangePass; }

    public String getIdentityNumber() { return identityNumber; }
    public void setIdentityNumber(String identityNumber) { this.identityNumber = identityNumber; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(String permanentAddress) { this.permanentAddress = permanentAddress; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public boolean isDeleted() { return deletedAt != null; }

    // Backward compatibility for JSP fmt:formatDate
    public Date getCreatedAtAsDate() {
        if (createdAt == null) return null;
        return Timestamp.valueOf(createdAt);
    }

    public Date getUpdatedAtAsDate() {
        if (updatedAt == null) return null;
        return Timestamp.valueOf(updatedAt);
    }

    public List<String> getFacilityNames() { return facilityNames; }
    public void setFacilityNames(List<String> facilityNames) {
        this.facilityNames = facilityNames != null ? facilityNames : new ArrayList<>();
    }

    public boolean isActive() {
        return StatusConstant.ACTIVE.equals(status);
    }

    public boolean isLocked() {
        return StatusConstant.LOCKED.equals(status);
    }
}
