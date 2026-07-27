<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <c:set var="ctx" value="${pageContext.request.contextPath}" />
        <c:set var="pageTitle" value="Bảng tin cộng đồng" />
        <c:set var="pageRole" value="MANAGER" />
        <c:set var="activeMenu" value="news-feed" />
        <jsp:include page="/WEB-INF/views/layout/head.jsp" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">



        <body>
            <div class="app-shell">
                <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
                <div class="sidebar-overlay"></div>
                <div class="main-wrapper">
                    <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
                    <main class="page-content">
                        <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

                        <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3"
                            style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                            <div style="position:relative;z-index:1">
                                <h1>Bảng tin 19 giờ</h1>
                                <p>Các bài viết được Ban quản lý phê duyệt trong 24 giờ qua</p>
                            </div>
                        </div>

                        <div class="news-feed-container">
                            <div class="row">
                                <div class="col-lg-8">
                                    <div id="feedContainer">
                                        <!-- Skeleton Loaders -->
                                        <div class="post-card skeleton-card">
                                            <div class="post-header">
                                                <div class="skeleton"
                                                    style="width: 40px; height: 40px; border-radius: 50%;">
                                                </div>
                                                <div class="ms-3">
                                                    <div class="skeleton mb-2" style="width: 120px; height: 16px;">
                                                    </div>
                                                    <div class="skeleton" style="width: 80px; height: 12px;"></div>
                                                </div>
                                            </div>
                                            <div class="skeleton mb-3" style="width: 70%; height: 24px;"></div>
                                            <div class="skeleton mb-2" style="width: 100%; height: 16px;"></div>
                                            <div class="skeleton mb-4" style="width: 90%; height: 16px;"></div>
                                            <div class="skeleton" style="width: 100%; height: 250px;"></div>
                                        </div>
                                    </div>

                                    <div class="text-center mt-4 mb-5" id="loadMoreContainer" style="display: none;">
                                        <button class="btn btn-outline-primary rounded-pill px-4"
                                            onclick="loadFeed()">Tải thêm
                                            bài viết</button>
                                    </div>
                                </div>
                                <div class="col-lg-4">
                                    <div class="widget-surface" style="position: sticky; top: 80px;">
                                        <div class="widget-surface-header">
                                            <h3 style="font-size:1.1rem;margin:0;"><i
                                                    class="fa-solid fa-fire text-danger"></i> Top Bài Nổi Bật</h3>
                                        </div>
                                        <div class="widget-surface-body" id="topPostsContainer" style="padding: 16px;">
                                            <div class="text-center py-3"><i class="fa fa-spinner fa-spin"></i> Đang
                                                tải...</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </main>
                </div>
            </div>
            <jsp:include page="/WEB-INF/views/layout/footer.jsp" />

            <script>
                const ctx = '${ctx}';
                const csrfToken = '${csrfToken}';
                let currentOffset = 0;
                const limit = 10;
                let isLoading = false;

                document.addEventListener('DOMContentLoaded', () => {
                    loadFeed(true);
                    loadTopPosts();
                });

                function escapeHtml(unsafe) {
                    return (unsafe || '').replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#039;");
                }

                function formatTime(isoString) {
                    if (!isoString) return '';
                    const date = new Date(isoString);
                    const time = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
                    const day = date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
                    return time + ' • ' + day;
                }

                async function loadTopPosts() {
                    try {
                        const res = await fetch(`${ctx}/api/v1/news-feed/top?limit=5`);
                        const data = await res.json();
                        const container = document.getElementById('topPostsContainer');
                        if (data.success && data.data.length > 0) {
                            container.innerHTML = '';
                            data.data.forEach(p => {
                                container.insertAdjacentHTML('beforeend', `
                                    <div class="top-post-card" onclick="window.location.href='${ctx}/manager/articles/detail?id=\${p.id}'">
                                        <div class="top-post-title">\${escapeHtml(p.title)}</div>
                                        <div class="top-post-meta">
                                            <span><i class="fa-regular fa-user"></i> \${escapeHtml(p.authorName)}</span>
                                            <span>
                                                <i class="fa-solid fa-thumbs-up text-primary"></i> \${p.totalLikes} &nbsp;
                                                <i class="fa-solid fa-comment text-secondary"></i> \${p.totalComments}
                                            </span>
                                        </div>
                                    </div>
                                `);
                            });
                        } else {
                            container.innerHTML = '<div class="text-muted small text-center py-3">Chưa có bài viết nổi bật.</div>';
                        }
                    } catch (e) {
                        console.error(e);
                        document.getElementById('topPostsContainer').innerHTML = '<div class="text-danger small text-center py-3">Lỗi tải dữ liệu.</div>';
                    }
                }

                async function loadFeed(isInitial = false) {
                    if (isLoading) return;
                    isLoading = true;

                    const container = document.getElementById('feedContainer');
                    const loadMoreBtn = document.getElementById('loadMoreContainer');

                    try {
                        const res = await fetch(`\${ctx}/api/v1/news-feed?offset=\${currentOffset}&limit=\${limit}`);
                        const data = await res.json();

                        if (isInitial) container.innerHTML = ''; // Remove skeleton

                        if (data.success) {
                            const posts = data.data;

                            if (posts.length === 0 && isInitial) {
                                container.innerHTML = `
                            <div class="empty-state">
                                <i class="fa fa-newspaper empty-icon"></i>
                                <h3>Chưa có bài viết nào trong 24 giờ qua</h3>
                                <p>Các bài viết được Ban quản lý phê duyệt sẽ xuất hiện tại đây.</p>
                            </div>
                        `;
                            }

                            posts.forEach(post => {
                                container.insertAdjacentHTML('beforeend', createPostHtml(post));
                            });

                            currentOffset += posts.length;

                            if (posts.length < limit) {
                                loadMoreBtn.style.display = 'none';
                            } else {
                                loadMoreBtn.style.display = 'block';
                            }
                        } else {
                            alert(data.error);
                        }
                    } catch (e) {
                        console.error(e);
                        if (isInitial) container.innerHTML = '<div class="alert alert-danger">Lỗi kết nối khi tải bảng tin.</div>';
                    } finally {
                        isLoading = false;
                    }
                }

                function createPostHtml(post) {
                    const avatarHtml = post.authorAvatar
                        ? `<img src="\${ctx}\${post.authorAvatar}" style="width:100%;height:100%;border-radius:50%;object-fit:cover;" alt="Avatar" />`
                        : (post.authorName ? post.authorName.charAt(0).toUpperCase() : 'U');
                    const imgHtml = post.imageUrl ? `<img src="\${ctx}\${post.imageUrl}" class="post-image" alt="Post image" style="cursor: zoom-in;" onclick="showFullImage(this.src)" />` : '';
                    const likedClass = post.likedByCurrentUser ? 'liked' : '';
                    const likeIcon = post.likedByCurrentUser ? 'fa-solid fa-thumbs-up' : 'fa-regular fa-thumbs-up';

                    return `
                <div class="post-card" id="post-\${post.id}">
                    <div class="post-header">
                        <div class="post-avatar" style="padding:0; overflow:hidden;">\${avatarHtml}</div>
                        <div class="post-meta">
                            <p class="post-author">\${escapeHtml(post.authorName)}</p>
                            <p class="post-time">\${formatTime(post.createdAt)}</p>
                        </div>
                    </div>
                    <div class="post-title">\${escapeHtml(post.title)}</div>
                    <div class="post-content">\${escapeHtml(post.content)}</div>
                    \${imgHtml}
                    
                    <div class="post-stats">
                        <span id="like-count-\${post.id}">👍 \${post.totalLikes} lượt thích</span>
                        <span id="comment-count-\${post.id}">💬 \${post.totalComments} bình luận</span>
                    </div>
                    <div class="post-divider"></div>
                    
                    <div class="post-actions">
                        <button class="action-btn \${likedClass}" id="btn-like-\${post.id}" onclick="toggleLike(\${post.id})">
                            <i class="\${likeIcon}"></i> Thích
                        </button>
                        <button class="action-btn" onclick="toggleComments(\${post.id})">
                            <i class="fa-regular fa-comment"></i> Bình luận
                        </button>
                    </div>
                    
                    <div class="comment-section" id="comments-section-\${post.id}" style="display: none;">
                        <div class="comment-list" id="comment-list-\${post.id}">
                            <!-- comments go here -->
                            <div class="text-center text-muted small py-2"><i class="fa fa-spinner fa-spin"></i> Đang tải...</div>
                        </div>
                        <div class="comment-input-area">
                            <div class="post-avatar" style="width:32px; height:32px; font-size:12px; padding:0; overflow:hidden;">
                                <c:choose>
                                    <c:when test="${not empty currentUser.avatarUrl}">
                                        <img src="${ctx}${currentUser.avatarUrl}" style="width:100%;height:100%;border-radius:50%;object-fit:cover;" />
                                    </c:when>
                                    <c:otherwise>ME</c:otherwise>
                                </c:choose>
                            </div>
                            <input type="text" class="comment-input" id="comment-input-\${post.id}" placeholder="Viết bình luận..." onkeypress="handleCommentEnter(event, \${post.id})" maxlength="1000">
                            <button class="btn btn-mintlify btn-send" onclick="submitComment(\${post.id})"><i class="fa fa-paper-plane"></i> Gửi</button>
                        </div>
                    </div>
                </div>
            `;
                }

                async function toggleLike(postId) {
                    const btn = document.getElementById(`btn-like-\${postId}`);
                    const countSpan = document.getElementById(`like-count-\${postId}`);

                    try {
                        const res = await fetch(`\${ctx}/api/v1/posts/\${postId}/reactions`, {
                            method: 'POST',
                            headers: { 'X-CSRF-Token': csrfToken }
                        });
                        const data = await res.json();

                        if (data.success) {
                            const isLiked = data.data.isLiked;
                            let currentCount = parseInt(countSpan.innerText.replace(/[^0-9]/g, '')) || 0;

                            if (isLiked) {
                                btn.classList.add('liked');
                                btn.innerHTML = '<i class="fa-solid fa-thumbs-up"></i> Thích';
                                currentCount++;
                            } else {
                                btn.classList.remove('liked');
                                btn.innerHTML = '<i class="fa-regular fa-thumbs-up"></i> Thích';
                                currentCount--;
                            }
                            countSpan.innerText = `👍 \${currentCount} lượt thích`;
                        }
                    } catch (e) {
                        console.error(e);
                    }
                }

                async function toggleComments(postId) {
                    const section = document.getElementById(`comments-section-\${postId}`);
                    if (section.style.display === 'none') {
                        section.style.display = 'block';
                        loadComments(postId);
                    } else {
                        section.style.display = 'none';
                    }
                }

                async function loadComments(postId) {
                    const list = document.getElementById(`comment-list-\${postId}`);
                    try {
                        const res = await fetch(`\${ctx}/api/v1/posts/\${postId}/comments`);
                        const data = await res.json();

                        if (data.success) {
                            list.innerHTML = '';
                            if (data.data.length === 0) {
                                list.innerHTML = '<p class="text-center text-muted small py-2">Chưa có bình luận nào.</p>';
                            } else {
                                data.data.forEach(c => {
                                    list.insertAdjacentHTML('beforeend', createCommentHtml(c));
                                });
                            }
                            // scroll to bottom
                            list.scrollTop = list.scrollHeight;
                        }
                    } catch (e) {
                        console.error(e);
                        list.innerHTML = '<p class="text-danger small">Lỗi tải bình luận</p>';
                    }
                }

                function createCommentHtml(comment) {
                    const avatarHtml = comment.userAvatar
                        ? `<img src="\${ctx}\${comment.userAvatar}" style="width:100%;height:100%;border-radius:50%;object-fit:cover;" alt="Avatar" />`
                        : (comment.userName ? comment.userName.charAt(0).toUpperCase() : 'U');
                    return `
                <div class="comment-item">
                    <div class="comment-avatar" style="padding:0; overflow:hidden;">\${avatarHtml}</div>
                    <div style="flex-grow:1">
                        <div class="comment-body">
                            <div class="comment-author">\${escapeHtml(comment.userName)}</div>
                            <p class="comment-text">\${escapeHtml(comment.content)}</p>
                        </div>
                        <div class="comment-time d-flex align-items-center gap-3">
                            <span>\${formatTime(comment.createdAt)}</span>
                            <span class="text-danger" style="cursor:pointer;font-weight:500;" onclick="deleteComment(\${comment.id}, \${comment.postId})">Xóa</span>
                        </div>
                    </div>
                </div>
            `;
                }

                async function deleteComment(commentId, postId) {
                    if (!confirm('Bạn có chắc chắn muốn xóa bình luận này?')) return;
                    try {
                        const res = await fetch(`\${ctx}/api/v1/comments/\${commentId}`, {
                            method: 'DELETE',
                            headers: {
                                'X-CSRF-Token': csrfToken
                            }
                        });
                        const data = await res.json();
                        if (data.success) {
                            loadComments(postId);
                        } else {
                            alert(data.error || 'Lỗi khi xóa bình luận');
                        }
                    } catch (e) {
                        console.error(e);
                        alert('Lỗi kết nối');
                    }
                }

                function handleCommentEnter(e, postId) {
                    if (e.key === 'Enter') {
                        submitComment(postId);
                    }
                }

                async function submitComment(postId) {
                    const input = document.getElementById(`comment-input-\${postId}`);
                    const content = input.value.trim();
                    if (!content) return;

                    try {
                        const res = await fetch(`\${ctx}/api/v1/posts/\${postId}/comments`, {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                                'X-CSRF-Token': csrfToken
                            },
                            body: JSON.stringify({ content: content })
                        });

                        const data = await res.json();
                        if (data.success) {
                            input.value = '';
                            const list = document.getElementById(`comment-list-\${postId}`);
                            // Remove empty message if exists
                            if (list.innerHTML.includes('Chưa có bình luận nào')) {
                                list.innerHTML = '';
                            }
                            list.insertAdjacentHTML('beforeend', createCommentHtml(data.data));
                            list.scrollTop = list.scrollHeight;

                            // Update count
                            const countSpan = document.getElementById(`comment-count-\${postId}`);
                            let currentCount = parseInt(countSpan.innerText.replace(/[^0-9]/g, '')) || 0;
                            countSpan.innerText = `💬 \${currentCount + 1} bình luận`;
                        } else {
                            alert(data.error);
                        }
                    } catch (e) {
                        console.error(e);
                        alert("Lỗi kết nối");
                    }
                }

                function showFullImage(imageSrc) {
                    document.getElementById('modalImagePreview').src = imageSrc;
                    var imgModal = new bootstrap.Modal(document.getElementById('imageViewerModal'));
                    imgModal.show();
                }
            </script>

            <!-- Modal xem ảnh lớn -->
            <div class="modal fade" id="imageViewerModal" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered modal-xl">
                    <div class="modal-content bg-transparent border-0">
                        <div class="modal-header border-0 justify-content-end p-2">
                            <button type="button" class="btn-close bg-white" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body text-center p-0">
                            <img id="modalImagePreview" src="" style="max-width: 100%; max-height: 85vh; border-radius: 8px; object-fit: contain;" alt="Ảnh phóng to">
                        </div>
                    </div>
                </div>
            </div>
        </body>

        </html>