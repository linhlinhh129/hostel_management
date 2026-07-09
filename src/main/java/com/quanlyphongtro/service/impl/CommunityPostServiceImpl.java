package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.CommunityPostDAO;
import com.quanlyphongtro.dto.CommunityPostCreateDTO;
import com.quanlyphongtro.dto.CommunityPostDTO;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.CommunityPost;
import com.quanlyphongtro.service.CommunityPostService;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

public class CommunityPostServiceImpl implements CommunityPostService {
    private static final Logger logger = LoggerFactory.getLogger(CommunityPostServiceImpl.class);
    private final CommunityPostDAO communityPostDAO;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public CommunityPostServiceImpl() {
        this.communityPostDAO = new CommunityPostDAO();
    }

    @Override
    public int createPost(CommunityPostCreateDTO dto, Part imagePart, int authorId, String uploadPath, String userRole) throws Exception {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new ValidationException("Tiêu đề không được để trống.");
        }
        if (dto.getTitle().length() > 250) {
            throw new ValidationException("Tiêu đề không được vượt quá 250 ký tự.");
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new ValidationException("Nội dung không được để trống.");
        }

        String imageUrl = null;
        if (imagePart != null && imagePart.getSize() > 0) {
            if (imagePart.getSize() > MAX_FILE_SIZE) {
                throw new ValidationException("Dung lượng ảnh tối đa là 5MB.");
            }
            String contentType = imagePart.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg"))) {
                throw new ValidationException("Định dạng ảnh không hợp lệ (chỉ hỗ trợ JPG/JPEG/PNG).");
            }
            // Save file logic
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String fileName = UUID.randomUUID().toString() + "_" + getSubmittedFileName(imagePart);
            Path filePath = Paths.get(uploadPath, fileName);
            Files.copy(imagePart.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            imageUrl = "/uploads/" + fileName;
        }

        CommunityPost post = new CommunityPost();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setImageUrl(imageUrl);
        post.setAuthorId(authorId);
        
        post.setStatus("PENDING");
        if ("TENANT".equals(userRole)) {
            Integer managerId = communityPostDAO.getManagerByTenant(authorId);
            post.setReviewedBy(managerId); // Gán người duyệt là quản lý của tòa nhà
        } else if ("MANAGER".equals(userRole)) {
            post.setReviewedBy(authorId);
            // Quản lý đăng bài thì vẫn là PENDING để chờ duyệt
        } else {
            post.setReviewedBy(null);
        }

        int postId = communityPostDAO.insert(post);
        if (postId <= 0) {
            throw new Exception("Lỗi khi tạo bài viết vào CSDL.");
        }
        return postId;
    }

    @Override
    public List<CommunityPostDTO> getPostsForManager(int managerId, int cursor, int limit) {
        return communityPostDAO.getPostsForManager(managerId, cursor, limit);
    }

    @Override
    public CommunityPostDTO getPostById(int postId, int currentUserId) {
        return communityPostDAO.getPostById(postId, currentUserId);
    }

    @Override
    public void approvePost(int postId, int reviewerId) {
        communityPostDAO.approvePost(postId, reviewerId);
    }

    @Override
    public void deletePost(int postId) {
        communityPostDAO.softDeletePost(postId);
    }

    private String getSubmittedFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                int lastIndex = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));
                if (lastIndex >= 0) {
                    return fileName.substring(lastIndex + 1);
                }
                return fileName;
            }
        }
        return "unknown";
    }
}
