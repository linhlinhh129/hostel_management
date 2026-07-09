package com.quanlyphongtro.dto;

public class RoomDTO {
    private int id;
    private String code;
    private double area;
    private String status;
    private Integer tenantId;
    private String tenantName;
    private String floor;
    private String roomNumber;

    public RoomDTO() {}

    public RoomDTO(int id, String code, double area, String status, Integer tenantId, String tenantName, String floor, String roomNumber) {
        this.id = id;
        this.code = code;
        this.area = area;
        this.status = status;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.floor = floor;
        this.roomNumber = roomNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}
