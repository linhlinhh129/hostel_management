package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.CommunityPostDAO;
import com.quanlyphongtro.dao.PostCommentDAO;
import com.quanlyphongtro.dao.PostReactionDAO;
import com.quanlyphongtro.dto.CommentDTO;
import com.quanlyphongtro.dto.CommunityPostCreateDTO;
import com.quanlyphongtro.dto.CommunityPostDTO;
import com.quanlyphongtro.dto.NewsFeedDTO;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.CommunityPost;
import com.quanlyphongtro.model.PostComment;
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
    private final CommunityPostDAO postDAO;
    private final PostReactionDAO reactionDAO;
    private final PostCommentDAO commentDAO;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public CommunityPostServiceImpl() {
        this.communityPostDAO = new CommunityPostDAO();
        this.postDAO = this.communityPostDAO;
        this.reactionDAO = new PostReactionDAO();
        this.commentDAO = new PostCommentDAO();
    }

    @Override
    public int createPost(CommunityPostCreateDTO dto, Part imagePart, int authorId, String uploadPath, String userRole)
            throws Exception {
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
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png")
                    && !contentType.equals("image/jpg"))) {
                throw new ValidationException("Định dạng ảnh không hợp lệ (chỉ hỗ trợ JPG/JPEG/PNG).");
            }
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists())
                uploadDir.mkdirs();

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
            post.setReviewedBy(managerId);
        } else if ("MANAGER".equals(userRole)) {
            post.setReviewedBy(authorId);
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
        Integer managerId = communityPostDAO.getManagerByTenant(tenantId);
        post.setReviewedBy(managerId);

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
    public void deletePost(int postId) {
        communityPostDAO.softDeletePost(postId);
    }

    @Override
    public boolean deletePost(Integer postId, Integer authorId) {
        return postDAO.softDeletePost(postId, authorId);
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
