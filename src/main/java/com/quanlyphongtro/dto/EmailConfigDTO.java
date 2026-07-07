package com.quanlyphongtro.dto;

public class EmailConfigDTO {
    private String host;
    private String port;
    private String username;
    private String from;
    private String updatedAt;
    private String updatedBy;

    public EmailConfigDTO() {}

    public EmailConfigDTO(String host, String port, String username, String from, String updatedAt, String updatedBy) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.from = from;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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
