package com.quanlyphongtro.dto;

import java.math.BigDecimal;

public class ServicePriceDTO {
    private String priceType; // ELECTRICITY, WATER, SERVICE_FEE
    private String priceName; // Giá điện, Giá nước, Phí dịch vụ
    private String unit; // VNĐ/kWh, VNĐ/m3, VNĐ/tháng
    private BigDecimal currentPrice;
    private String updatedAt;
    private Integer updatedBy;
    private String updatedByName;

    public ServicePriceDTO() {
    }

    public ServicePriceDTO(String priceType, String priceName, String unit, BigDecimal currentPrice, String updatedAt, Integer updatedBy, String updatedByName) {
        this.priceType = priceType;
        this.priceName = priceName;
        this.unit = unit;
        this.currentPrice = currentPrice;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.updatedByName = updatedByName;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }

    public String getPriceName() {
        return priceName;
    }

    public void setPriceName(String priceName) {
        this.priceName = priceName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }
}
