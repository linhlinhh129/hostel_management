package com.quanlyphongtro.dto;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Giữ lại dữ liệu form cơ sở khi validation thất bại,
 * để JSP có thể hiển thị lại giá trị người dùng đã nhập.
 */
public class FacilityFormDTO {

    private String code;
    private String name;
    private String address;
    private String floorCount;
    private String roomsPerFloor;

    public FacilityFormDTO() {}

    /** Tạo DTO từ request parameters. */
    public static FacilityFormDTO of(HttpServletRequest req) {
        FacilityFormDTO dto = new FacilityFormDTO();
        dto.code             = trim(req.getParameter("code"));
        dto.name             = trim(req.getParameter("name"));
        dto.address          = trim(req.getParameter("address"));
        dto.floorCount       = trim(req.getParameter("floorCount"));
        dto.roomsPerFloor    = trim(req.getParameter("roomsPerFloor"));
        return dto;
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getCode()             { return code; }
    public String getName()             { return name; }
    public String getAddress()          { return address; }
    public String getFloorCount()       { return floorCount; }
    public String getRoomsPerFloor()    { return roomsPerFloor; }
}
