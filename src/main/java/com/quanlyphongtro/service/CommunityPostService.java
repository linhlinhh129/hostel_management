package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.CommentDTO;
import com.quanlyphongtro.dto.NewsFeedDTO;
import com.quanlyphongtro.model.CommunityPost;

import java.util.List;

public interface CommunityPostService {
    CommunityPost createPost(Integer tenantId, String title, String content, String imageUrl);
    List<NewsFeedDTO> getNewsFeed(Integer currentUserId);
    List<NewsFeedDTO> getMyPosts(Integer authorId);
    NewsFeedDTO getPostDetail(Integer postId, Integer currentUserId);
    boolean toggleLike(Integer postId, Integer currentUserId);
    CommentDTO addComment(Integer postId, Integer currentUserId, String content);
    List<CommentDTO> getCommentsByPostId(Integer postId);
    boolean deletePost(Integer postId, Integer authorId);
}
