package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.CommunityPostCreateDTO;
import com.quanlyphongtro.dto.CommunityPostDTO;
import jakarta.servlet.http.Part;
import java.util.List;

public interface CommunityPostService {
    int createPost(CommunityPostCreateDTO dto, Part imagePart, int authorId, String uploadPath, String userRole) throws Exception;
    List<CommunityPostDTO> getPostsForManager(int managerId, int cursor, int limit);
    CommunityPostDTO getPostById(int postId, int currentUserId);
    void approvePost(int postId, int reviewerId);
    void deletePost(int postId);
}
