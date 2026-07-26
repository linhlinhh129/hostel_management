package com.quanlyphongtro.dto;

import java.time.LocalDateTime;

public class PostCommentDTO {
    private Integer commentId;
    private Integer postId;
    private Integer userId;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isAuthor;

    public PostCommentDTO() {}

    public PostCommentDTO(Integer commentId, Integer postId, Integer userId, String authorName, String content, LocalDateTime createdAt, Boolean isAuthor) {
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
        this.authorName = authorName;
        this.content = content;
        this.createdAt = createdAt;
        this.isAuthor = isAuthor;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public java.util.Date getCreatedAtAsDate() {
        if (createdAt == null) return null;
        return java.sql.Timestamp.valueOf(createdAt);
    }

    public Boolean getIsAuthor() {
        return isAuthor;
    }

    public void setIsAuthor(Boolean isAuthor) {
        this.isAuthor = isAuthor;
    }
}
