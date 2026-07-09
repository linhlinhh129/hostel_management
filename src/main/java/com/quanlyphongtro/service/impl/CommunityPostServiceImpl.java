package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.CommunityPostDAO;
import com.quanlyphongtro.dao.PostCommentDAO;
import com.quanlyphongtro.dao.PostReactionDAO;
import com.quanlyphongtro.dto.CommentDTO;
import com.quanlyphongtro.dto.NewsFeedDTO;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.CommunityPost;
import com.quanlyphongtro.model.PostComment;
import com.quanlyphongtro.service.CommunityPostService;

import java.util.List;

public class CommunityPostServiceImpl implements CommunityPostService {
    private final CommunityPostDAO postDAO;
    private final PostReactionDAO reactionDAO;
    private final PostCommentDAO commentDAO;

    public CommunityPostServiceImpl() {
        this.postDAO = new CommunityPostDAO();
        this.reactionDAO = new PostReactionDAO();
        this.commentDAO = new PostCommentDAO();
    }

    @Override
    public CommunityPost createPost(Integer tenantId, String title, String content, String imageUrl) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Tiêu đề không được để trống");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new ValidationException("Nội dung không được để trống");
        }

        CommunityPost post = new CommunityPost();
        post.setAuthorId(tenantId);
        post.setTitle(title.trim());
        post.setContent(content.trim());
        post.setImageUrl(imageUrl != null && !imageUrl.isEmpty() ? imageUrl : null);
        post.setStatus("PENDING");

        return postDAO.createPost(post);
    }

    @Override
    public List<NewsFeedDTO> getNewsFeed(Integer currentUserId) {
        return postDAO.getNewsFeed(currentUserId);
    }

    @Override
    public List<NewsFeedDTO> getMyPosts(Integer authorId) {
        return postDAO.getMyPosts(authorId);
    }

    @Override
    public NewsFeedDTO getPostDetail(Integer postId, Integer currentUserId) {
        return postDAO.getPostDetail(postId, currentUserId);
    }

    @Override
    public boolean toggleLike(Integer postId, Integer currentUserId) {
        return reactionDAO.toggleReaction(postId, currentUserId);
    }

    @Override
    public CommentDTO addComment(Integer postId, Integer currentUserId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new ValidationException("Nội dung bình luận không được để trống");
        }
        if (content.length() > 1000) {
            throw new ValidationException("Nội dung bình luận tối đa 1000 ký tự");
        }

        PostComment comment = new PostComment();
        comment.setPostId(postId);
        comment.setUserId(currentUserId);
        comment.setContent(content.trim());

        return commentDAO.addComment(comment);
    }

    @Override
    public List<CommentDTO> getCommentsByPostId(Integer postId) {
        return commentDAO.getCommentsByPostId(postId);
    }

    @Override
    public boolean deletePost(Integer postId, Integer authorId) {
        return postDAO.softDeletePost(postId, authorId);
    }
}
