package com.quanlyphongtro.dto;

import java.sql.Timestamp;

public class MeterStatusDTO {
    private int roomId;
    private String roomCode;
    private Integer previousElectricReading;
    private Integer previousWaterReading;
    private Integer currentElectricReading;
    private Integer currentWaterReading;
    private Timestamp updatedAt;
    private String status;
    private Integer meterId;
    private String electricImg;
    private String waterImg;
    private String updatedByName;

    public MeterStatusDTO() {}

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public Integer getPreviousElectricReading() {
        return previousElectricReading;
    }

    public void setPreviousElectricReading(Integer previousElectricReading) {
        this.previousElectricReading = previousElectricReading;
    }

    public Integer getPreviousWaterReading() {
        return previousWaterReading;
    }

    public void setPreviousWaterReading(Integer previousWaterReading) {
        this.previousWaterReading = previousWaterReading;
    }

    public Integer getCurrentElectricReading() {
        return currentElectricReading;
    }

    public void setCurrentElectricReading(Integer currentElectricReading) {
        this.currentElectricReading = currentElectricReading;
    }

    public Integer getCurrentWaterReading() {
        return currentWaterReading;
    }

    public void setCurrentWaterReading(Integer currentWaterReading) {
        this.currentWaterReading = currentWaterReading;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getMeterId() {
        return meterId;
    }

    public void setMeterId(Integer meterId) {
        this.meterId = meterId;
    }

    public String getElectricImg() {
        return electricImg;
    }

    public void setElectricImg(String electricImg) {
        this.electricImg = electricImg;
    }

    public String getWaterImg() {
        return waterImg;
    }

    public void setWaterImg(String waterImg) {
        this.waterImg = waterImg;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }
}
