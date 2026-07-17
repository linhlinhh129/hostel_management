package com.quanlyphongtro.dto;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Giữ lại dữ liệu form nhân sự khi validation thất bại,
 * để JSP có thể hiển thị lại giá trị người dùng đã nhập.
 */
public class PersonnelFormDTO {

    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String identityNumber;
    private String dob;
    private String gender;
    private String permanentAddress;
    private String facilityId;

    public PersonnelFormDTO() {}

    /** Tạo DTO từ request parameters. */
    public static PersonnelFormDTO of(HttpServletRequest req) {
        PersonnelFormDTO dto = new PersonnelFormDTO();
        dto.fullName        = trim(req.getParameter("fullName"));
        dto.email           = trim(req.getParameter("email"));
        dto.phone           = trim(req.getParameter("phone"));
        dto.role            = trim(req.getParameter("role"));
        dto.identityNumber  = trim(req.getParameter("identityNumber"));
        dto.dob             = trim(req.getParameter("dob"));
        dto.gender          = trim(req.getParameter("gender"));
        dto.permanentAddress= trim(req.getParameter("permanentAddress"));
        dto.facilityId      = trim(req.getParameter("facilityId"));
        return dto;
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getFullName()         { return fullName; }
    public String getEmail()            { return email; }
    public String getPhone()            { return phone; }
    public String getRole()             { return role; }
    public String getIdentityNumber()   { return identityNumber; }
    public String getDob()              { return dob; }
    public String getGender()           { return gender; }
    public String getPermanentAddress() { return permanentAddress; }
    public String getFacilityId()       { return facilityId; }
}
