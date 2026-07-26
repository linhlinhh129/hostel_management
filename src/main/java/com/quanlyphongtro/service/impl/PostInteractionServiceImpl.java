package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.PostCommentDAO;
import com.quanlyphongtro.dao.PostReactionDAO;
import com.quanlyphongtro.dto.PostCommentDTO;
import com.quanlyphongtro.service.PostInteractionService;

import java.util.List;

public class PostInteractionServiceImpl implements PostInteractionService {

    private final PostReactionDAO reactionDAO;
    private final PostCommentDAO commentDAO;

    public PostInteractionServiceImpl() {
        this.reactionDAO = new PostReactionDAO();
        this.commentDAO = new PostCommentDAO();
    }

    @Override
    public boolean toggleLike(int postId, int userId) {
        if (reactionDAO.hasReacted(postId, userId)) {
            return reactionDAO.removeReaction(postId, userId);
        } else {
            return reactionDAO.addReaction(postId, userId);
        }
    }

    @Override
    public boolean hasLiked(int postId, int userId) {
        return reactionDAO.hasReacted(postId, userId);
    }

    @Override
    public boolean addComment(int postId, int userId, String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        if (content.length() > 1000) {
            content = content.substring(0, 1000);
        }
        return commentDAO.addComment(postId, userId, content.trim());
    }

    @Override
    public List<PostCommentDTO> getCommentsByPostId(int postId, int currentUserId) {
        return commentDAO.getCommentsByPostId(postId, currentUserId);
    }

    @Override
    public boolean deleteComment(int commentId, int userId, String userRole) {
        // Only author or MANAGER/ADMIN can delete
        if ("MANAGER".equals(userRole) || "ADMIN".equals(userRole)) {
            return commentDAO.deleteComment(commentId);
        }
        
        Integer authorId = commentDAO.getCommentAuthorId(commentId);
        if (authorId != null && authorId == userId) {
            return commentDAO.deleteComment(commentId);
        }
        
        return false;
    }
}
