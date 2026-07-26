package com.quanlyphongtro.dto;

import java.time.LocalDateTime;

public class PostReactionDTO {
    private Integer postId;
    private Integer userId;
    private LocalDateTime createdAt;
    
    public PostReactionDTO() {}
    
    public PostReactionDTO(Integer postId, Integer userId, LocalDateTime createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
