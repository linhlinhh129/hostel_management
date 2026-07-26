package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.PostCommentDTO;

import java.util.List;

public interface PostInteractionService {
    
    // Post Reaction methods
    boolean toggleLike(int postId, int userId);
    boolean hasLiked(int postId, int userId);
    
    // Post Comment methods
    boolean addComment(int postId, int userId, String content);
    List<PostCommentDTO> getCommentsByPostId(int postId, int currentUserId);
    boolean deleteComment(int commentId, int userId, String userRole);
}
