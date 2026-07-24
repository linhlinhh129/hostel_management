package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.CommunityPostCreateDTO;
import com.quanlyphongtro.dto.CommunityPostDTO;
import com.quanlyphongtro.dto.CommentDTO;
import com.quanlyphongtro.dto.NewsFeedDTO;
import com.quanlyphongtro.model.CommunityPost;
import jakarta.servlet.http.Part;
import java.util.List;

public interface CommunityPostService {

    int createPost(CommunityPostCreateDTO dto, Part imagePart, int authorId, String uploadPath, String userRole) throws Exception;
    List<CommunityPostDTO> getPostsForManager(int managerId, int cursor, int limit);
    CommunityPostDTO getPostById(int postId, int currentUserId);
    void approvePost(int postId, int reviewerId);
    void deletePost(int postId);

    CommunityPost createPost(Integer tenantId, String title, String content, String imageUrl);
    List<NewsFeedDTO> getNewsFeed(Integer currentUserId);
    List<NewsFeedDTO> getMyPosts(Integer authorId);
    NewsFeedDTO getPostDetail(Integer postId, Integer currentUserId);
    boolean toggleLike(Integer postId, Integer currentUserId);
    CommentDTO addComment(Integer postId, Integer currentUserId, String content);
    List<CommentDTO> getCommentsByPostId(Integer postId);
    boolean deletePost(Integer postId, Integer authorId);
}
