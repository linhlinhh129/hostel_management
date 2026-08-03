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
    private Integer electricUsage;
    private Integer waterUsage;

    private String electricStatus;
    private Integer electricOldFinal;
    private Integer electricNewStart;

    private String waterStatus;
    private Integer waterOldFinal;
    private Integer waterNewStart;

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

    public Integer getElectricUsage() { return electricUsage; }
    public void setElectricUsage(Integer electricUsage) { this.electricUsage = electricUsage; }

    public Integer getWaterUsage() { return waterUsage; }
    public void setWaterUsage(Integer waterUsage) { this.waterUsage = waterUsage; }

    public String getElectricStatus() { return electricStatus; }
    public void setElectricStatus(String electricStatus) { this.electricStatus = electricStatus; }

    public Integer getElectricOldFinal() { return electricOldFinal; }
    public void setElectricOldFinal(Integer electricOldFinal) { this.electricOldFinal = electricOldFinal; }

    public Integer getElectricNewStart() { return electricNewStart; }
    public void setElectricNewStart(Integer electricNewStart) { this.electricNewStart = electricNewStart; }

    public String getWaterStatus() { return waterStatus; }
    public void setWaterStatus(String waterStatus) { this.waterStatus = waterStatus; }

    public Integer getWaterOldFinal() { return waterOldFinal; }
    public void setWaterOldFinal(Integer waterOldFinal) { this.waterOldFinal = waterOldFinal; }

    public Integer getWaterNewStart() { return waterNewStart; }
    public void setWaterNewStart(Integer waterNewStart) { this.waterNewStart = waterNewStart; }
}
