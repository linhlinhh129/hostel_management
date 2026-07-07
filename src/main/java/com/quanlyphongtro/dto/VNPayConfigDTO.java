package com.quanlyphongtro.dto;

public class VNPayConfigDTO {
    private String payUrl;
    private String returnUrl;
    private String tmnCode;
    private String apiUrl;
    private String updatedAt;
    private String updatedBy;

    public VNPayConfigDTO() {}

    public VNPayConfigDTO(String payUrl, String returnUrl, String tmnCode, String apiUrl, String updatedAt, String updatedBy) {
        this.payUrl = payUrl;
        this.returnUrl = returnUrl;
        this.tmnCode = tmnCode;
        this.apiUrl = apiUrl;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getTmnCode() {
        return tmnCode;
    }

    public void setTmnCode(String tmnCode) {
        this.tmnCode = tmnCode;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
