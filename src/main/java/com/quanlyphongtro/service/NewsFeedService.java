package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.CommentDTO;
import com.quanlyphongtro.dto.CommunityPostDTO;

import java.util.List;

public interface NewsFeedService {
    List<CommunityPostDTO> getNewsFeed(int currentUserId, int offset, int limit);
    
    boolean toggleLike(int postId, int userId);
    
    CommentDTO addComment(int postId, int userId, String content) throws Exception;
    
    List<CommentDTO> getComments(int postId);
    
    List<CommunityPostDTO> getTopInteractivePosts(int limit);
    
    boolean deleteComment(int commentId);
}
