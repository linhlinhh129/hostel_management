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
    private String previousElectricImg;
    private String previousWaterImg;
    private String updatedByName;
    private boolean invoicePaid;

    // Metadata fields for Rollover and Replaced meters
    private String electricStatus;
    private Integer electricOldFinal;
    private Integer electricNewStart;
    private Integer electricMaxLimit;
    private Integer electricUsage;

    private String waterStatus;
    private Integer waterOldFinal;
    private Integer waterNewStart;
    private Integer waterMaxLimit;
    private Integer waterUsage;
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

    public String getPreviousElectricImg() {
        return previousElectricImg;
    }

    public void setPreviousElectricImg(String previousElectricImg) {
        this.previousElectricImg = previousElectricImg;
    }

    public String getPreviousWaterImg() {
        return previousWaterImg;
    }

    public void setPreviousWaterImg(String previousWaterImg) {
        this.previousWaterImg = previousWaterImg;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    public boolean isInvoicePaid() {
        return invoicePaid;
    }

    public void setInvoicePaid(boolean invoicePaid) {
        this.invoicePaid = invoicePaid;
    }

    public boolean isEditable() {
        if (this.invoicePaid) return false;
        if (this.updatedAt == null) return true;
        long diffInMillies = Math.abs(System.currentTimeMillis() - this.updatedAt.getTime());
        long diffDays = java.util.concurrent.TimeUnit.DAYS.convert(diffInMillies, java.util.concurrent.TimeUnit.MILLISECONDS);
        return diffDays <= 5;
    }

    public String getElectricStatus() {
        return electricStatus;
    }

    public void setElectricStatus(String electricStatus) {
        this.electricStatus = electricStatus;
    }

    public Integer getElectricOldFinal() {
        return electricOldFinal;
    }

    public void setElectricOldFinal(Integer electricOldFinal) {
        this.electricOldFinal = electricOldFinal;
    }

    public Integer getElectricNewStart() {
        return electricNewStart;
    }

    public void setElectricNewStart(Integer electricNewStart) {
        this.electricNewStart = electricNewStart;
    }

    public Integer getElectricMaxLimit() {
        return electricMaxLimit;
    }

    public void setElectricMaxLimit(Integer electricMaxLimit) {
        this.electricMaxLimit = electricMaxLimit;
    }

    public Integer getElectricUsage() {
        return electricUsage;
    }

    public void setElectricUsage(Integer electricUsage) {
        this.electricUsage = electricUsage;
    }

    public String getWaterStatus() {
        return waterStatus;
    }

    public void setWaterStatus(String waterStatus) {
        this.waterStatus = waterStatus;
    }

    public Integer getWaterOldFinal() {
        return waterOldFinal;
    }

    public void setWaterOldFinal(Integer waterOldFinal) {
        this.waterOldFinal = waterOldFinal;
    }

    public Integer getWaterNewStart() {
        return waterNewStart;
    }

    public void setWaterNewStart(Integer waterNewStart) {
        this.waterNewStart = waterNewStart;
    }

    public Integer getWaterMaxLimit() {
        return waterMaxLimit;
    }

    public void setWaterMaxLimit(Integer waterMaxLimit) {
        this.waterMaxLimit = waterMaxLimit;
    }

    public Integer getWaterUsage() {
        return waterUsage;
    }

    public void setWaterUsage(Integer waterUsage) {
        this.waterUsage = waterUsage;
    }
}
