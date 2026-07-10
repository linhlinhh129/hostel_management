package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.CommentDAO;
import com.quanlyphongtro.dao.NewsFeedDAO;
import com.quanlyphongtro.dao.ReactionDAO;
import com.quanlyphongtro.dto.CommentDTO;
import com.quanlyphongtro.dto.CommunityPostDTO;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.service.NewsFeedService;

import java.util.List;

public class NewsFeedServiceImpl implements NewsFeedService {

    private final NewsFeedDAO newsFeedDAO = new NewsFeedDAO();
    private final ReactionDAO reactionDAO = new ReactionDAO();
    private final CommentDAO commentDAO = new CommentDAO();

    @Override
    public List<CommunityPostDTO> getNewsFeed(int currentUserId, int offset, int limit) {
        return newsFeedDAO.getNewsFeedForToday(currentUserId, offset, limit);
    }

    @Override
    public boolean toggleLike(int postId, int userId) {
        boolean isLiked = reactionDAO.checkUserLiked(postId, userId);
        if (isLiked) {
            return !reactionDAO.deleteReaction(postId, userId); // Return false if successfully unliked
        } else {
            return reactionDAO.insertReaction(postId, userId); // Return true if successfully liked
        }
    }

    @Override
    public CommentDTO addComment(int postId, int userId, String content) throws Exception {
        if (content == null || content.trim().isEmpty()) {
            throw new ValidationException("Nội dung bình luận không được để trống.");
        }
        if (content.length() > 1000) {
            throw new ValidationException("Bình luận không được vượt quá 1000 ký tự.");
        }
        
        CommentDTO newComment = commentDAO.insertComment(postId, userId, content.trim());
        if (newComment == null) {
            throw new Exception("Lỗi khi đăng bình luận.");
        }
        return newComment;
    }

    @Override
    public List<CommentDTO> getComments(int postId) {
        return commentDAO.getCommentsByPostId(postId);
    }

    @Override
    public List<CommunityPostDTO> getTopInteractivePosts(int limit) {
        return newsFeedDAO.getTopInteractivePosts(limit);
    }

    @Override
    public boolean deleteComment(int commentId) {
        return commentDAO.deleteComment(commentId);
    }
}
