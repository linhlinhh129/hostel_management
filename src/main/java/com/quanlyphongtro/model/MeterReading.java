package com.quanlyphongtro.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MeterReading {
    private Integer meterId;
    private Integer roomId;
    private Integer electric;
    private Integer water;
    private LocalDate readingDate;
    private String status;
    private Integer createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String waterImg;
    private String electricImg;

    public MeterReading() {}

    public Integer getMeterId() { return meterId; }
    public void setMeterId(Integer meterId) { this.meterId = meterId; }

    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }

    public Integer getElectric() { return electric; }
    public void setElectric(Integer electric) { this.electric = electric; }

    public Integer getWater() { return water; }
    public void setWater(Integer water) { this.water = water; }

    public LocalDate getReadingDate() { return readingDate; }
    public void setReadingDate(LocalDate readingDate) { this.readingDate = readingDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public String getWaterImg() { return waterImg; }
    public void setWaterImg(String waterImg) { this.waterImg = waterImg; }

    public String getElectricImg() { return electricImg; }
    public void setElectricImg(String electricImg) { this.electricImg = electricImg; }
}
