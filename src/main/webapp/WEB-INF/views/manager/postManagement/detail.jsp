<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <c:set var="ctx" value="${pageContext.request.contextPath}" />
            <c:set var="pageTitle" value="Chi tiết bài viết - Ban Quản lý" />
            <c:set var="pageRole" value="MANAGER" />
            <c:set var="activeMenu" value="articles" />
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

                            <style>
                                .post-card {
                                    background: #fff;
                                    border-radius: 16px;
                                    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
                                    padding: 24px;
                                    margin: 0 auto;
                                    max-width: 800px;
                                    border: 1px solid #e5e5e5;
                                }

                                .post-header {
                                    display: flex;
                                    align-items: center;
                                    margin-bottom: 16px;
                                    justify-content: space-between;
                                }

                                .post-header-left {
                                    display: flex;
                                    align-items: center;
                                }

                                .post-avatar {
                                    width: 40px;
                                    height: 40px;
                                    border-radius: 50%;
                                    background: #e9ecef;
                                    display: flex;
                                    align-items: center;
                                    justify-content: center;
                                    font-weight: bold;
                                    color: #5a5a5c;
                                    margin-right: 12px;
                                }

                                .post-meta {
                                    flex-grow: 1;
                                }

                                .post-author {
                                    font-weight: 600;
                                    margin: 0;
                                    color: #0a0a0a;
                                }

                                .post-time {
                                    font-size: 0.85rem;
                                    color: #5a5a5c;
                                    margin: 0;
                                }

                                .post-title {
                                    font-weight: 700;
                                    font-size: 22px;
                                    margin-bottom: 12px;
                                    color: #0a0a0a;
                                }

                                .post-content {
                                    line-height: 1.6;
                                    color: #1c1c1e;
                                    margin-bottom: 16px;
                                    white-space: pre-line;
                                }

                                .post-image {
                                    width: 100%;
                                    border-radius: 12px;
                                    object-fit: cover;
                                    max-height: 400px;
                                    margin-bottom: 16px;
                                    display: block;
                                }

                            </style>

                            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3"
                                style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                                <div style="position:relative;z-index:1">
                                    <h1>Chi tiết bài viết</h1>
                                    <p>Xem thông tin, thống kê và tương tác với bài viết</p>
                                </div>
                                <div style="display:flex;gap:8px;flex-wrap:wrap;position:relative;z-index:1">
                                    <a href="${ctx}/manager/articles" class="btn-accent">Trở về danh sách</a>
                                </div>
                            </div>

                            <div class="news-feed-container" style="max-width: 800px; margin: 0 auto;">
                                <c:if test="${not empty post}">
                                    <div class="post-card">
                                        <div class="post-header">
                                            <div class="post-header-left">
                                                <c:set var="avatarInitial"
                                                    value="${not empty post.authorName ? post.authorName.substring(0,1).toUpperCase() : 'U'}" />
                                                <div class="post-avatar" style="padding:0; overflow:hidden;">
                                                    <c:choose>
                                                        <c:when test="${not empty post.authorAvatar}">
                                                            <img src="${ctx}${post.authorAvatar}" style="width:100%;height:100%;border-radius:50%;object-fit:cover;" />
                                                        </c:when>
                                                        <c:otherwise>
                                                            <c:out value="${avatarInitial}" />
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div class="post-meta">
                                                    <p class="post-author">
                                                        <c:out value="${post.authorName}" />
                                                    </p>
                                                    <p class="post-time">
                                                        <fmt:formatDate value="${post.createdAtAsDate}"
                                                            pattern="HH:mm • dd/MM/yyyy" />
                                                    </p>
                                                </div>
                                            </div>
                                            <div>
                                                <c:choose>
                                                    <c:when test="${post.status == 'PENDING'}"><span
                                                            class="badge bg-warning text-dark px-3 py-2 rounded-pill">Chờ
                                                            duyệt</span></c:when>
                                                    <c:when test="${post.status == 'APPROVED'}"><span
                                                            class="badge bg-primary px-3 py-2 rounded-pill">Đã
                                                            duyệt</span></c:when>
                                                    <c:otherwise><span class="badge bg-secondary">
                                                            <c:out value="${post.status}" />
                                                        </span></c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>

                                        <div class="post-title">
                                            <c:out value="${post.title}" />
                                        </div>

                                        <div class="post-content">
                                            <c:out value="${post.content}" />
                                        </div>

                                        <c:if test="${not empty post.imageUrl}">
                                            <img src="${ctx}${post.imageUrl}" class="post-image" alt="Bài viết" style="cursor: zoom-in;" onclick="showFullImage(this.src)" />
                                        </c:if>



                                        <div class="d-flex gap-3 justify-content-end mt-4">
                                            <c:if test="${post.status == 'PENDING'}">
                                                <button
                                                    class="btn-mintlify px-4 py-2 d-inline-flex align-items-center justify-content-center gap-2"
                                                    style="border:none;border-radius:8px"
                                                    onclick="approvePost('${post.id}')"><i class="fa fa-check"></i>
                                                    Duyệt bài</button>
                                            </c:if>
                                            <button
                                                class="btn btn-outline-danger px-4 py-2 d-inline-flex align-items-center justify-content-center gap-2"
                                                style="border-radius:8px" onclick="deletePost('${post.id}')"><i
                                                    class="fa fa-trash"></i>Xóa bài</button>
                                        </div>
                                    </div>
                                </c:if>
                                <c:if test="${empty post}">
                                    <div class="alert alert-danger">Không tìm thấy bài viết.</div>
                                </c:if>
                            </div>
                        </main>
                    </div>
                </div>
                <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
                <script>
                    const ctx = '${ctx}';
                    const csrfToken = '${csrfToken}';

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



                    function approvePost(postId) {
                        fetch('${ctx}/manager/articles/approve', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded',
                                'Accept': 'application/json',
                                'X-CSRF-Token': '${csrfToken}'
                            },
                            body: 'postId=' + postId
                        })
                            .then(r => r.json())
                            .then(data => {
                                if (data.success) {
                                    window.location.reload();
                                } else {
                                    alert(data.error || "Lỗi khi duyệt bài.");
                                }
                            })
                            .catch(e => {
                                console.error(e);
                                alert("Lỗi kết nối.");
                            });
                    }

                    function deletePost(postId) {
                        fetch('${ctx}/manager/articles/delete', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded',
                                'Accept': 'application/json',
                                'X-CSRF-Token': '${csrfToken}'
                            },
                            body: 'postId=' + postId
                        })
                            .then(r => r.json())
                            .then(data => {
                                if (data.success) {
                                    window.location.href = '${ctx}/manager/articles';
                                } else {
                                    alert(data.error || "Lỗi khi xóa bài.");
                                }
                            })
                            .catch(e => {
                                console.error(e);
                                alert("Lỗi kết nối.");
                            });
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