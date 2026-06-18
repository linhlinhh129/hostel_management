package com.quanlyphongtro.dto;

import java.io.Serializable;

public class UserSessionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String username;
    private String fullName;
    private String email;
    private String role;
    private String roleLabel;
    private String initials;
        private String avatarUrl;
    /** Mã cơ sở phụ trách (Manager/Operator) — hiển thị trong sidebar */
    private String facilityCode;
    /** Mã phòng (Tenant) — hiển thị trong sidebar */
    private String roomCode;
    /** true nếu force_change_pass=1 trong DB — bắt redirect về change-password */
    private boolean firstLogin;

    public UserSessionDTO() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) {
        this.role = role;
        this.roleLabel = resolveRoleLabel(role);
    }

    public String getRoleLabel() { return roleLabel; }
    public void setRoleLabel(String roleLabel) { this.roleLabel = roleLabel; }

    public String getInitials() { return initials; }
    public void setInitials(String initials) { this.initials = initials; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getFacilityCode() { return facilityCode; }
    public void setFacilityCode(String facilityCode) { this.facilityCode = facilityCode; }

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public boolean isFirstLogin() { return firstLogin; }
    public void setFirstLogin(boolean firstLogin) { this.firstLogin = firstLogin; }

    private String resolveRoleLabel(String role) {
        if (role == null) return "";
        return switch (role) {
            case "ADMIN"    -> "Quản trị viên";
            case "MANAGER"  -> "Ban Quản lý";
            case "TENANT"   -> "Người thuê";
            case "OPERATOR" -> "Nhân viên vận hành";
            default         -> role;
        };
    }

    public static String extractInitials(String fullName) {
        if (fullName == null || fullName.isBlank()) return "U";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) return String.valueOf(parts[0].charAt(0)).toUpperCase();
        return (String.valueOf(parts[0].charAt(0)) + String.valueOf(parts[parts.length - 1].charAt(0))).toUpperCase();
    }
}
