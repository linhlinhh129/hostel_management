<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Bài viết của tôi"/>
<c:set var="pageRole" value="TENANT"/>
<c:set var="activeMenu" value="my-posts"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="position:relative;z-index:1">
                    <h1>Bài viết của tôi</h1>
                    <p>Quản lý các bài viết cộng đồng bạn đã đăng</p>
                </div>
                <div style="display:flex;gap:8px;flex-wrap:wrap;position:relative;z-index:1">
                    <a href="${ctx}/tenant/post/create" class="btn-accent">
                        <i class="bi bi-pencil-square"></i> Đăng bài viết
                    </a>
                </div>
            </div>

            <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
                <c:choose>
                    <c:when test="${empty posts}">
                        <div class="col-12 text-center py-5 text-muted">
                            <i class="bi bi-journal-text fs-1"></i>
                            <p class="mt-3">Bạn chưa có bài viết nào.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="post" items="${posts}">
                            <div class="col" id="postCard-${post.postId}">
                                <div class="card h-100 shadow-sm border-0 position-relative">
                                    <!-- Status Badge -->
                                    <div class="position-absolute top-0 end-0 p-2 z-1">
                                        <c:choose>
                                            <c:when test="${post.status == 'APPROVED'}">
                                                <span class="badge bg-success shadow-sm">Đã duyệt</span>
                                            </c:when>
                                            <c:when test="${post.status == 'REJECTED'}">
                                                <span class="badge bg-danger shadow-sm">Từ chối</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-warning text-dark shadow-sm">Chờ duyệt</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <c:if test="${not empty post.imageUrl}">
                                        <img src="${ctx}/${post.imageUrl}" class="card-img-top" alt="Post thumbnail"
                                             style="height: 180px; object-fit: cover; border-radius: var(--hms-radius-md) var(--hms-radius-md) 0 0;">
                                    </c:if>
                                    <div class="card-body d-flex flex-column">
                                        <h5 class="card-title text-truncate pe-5" title="${fn:escapeXml(post.title)}">
                                            <c:out value="${post.title}"/>
                                        </h5>
                                        <div class="mb-2 text-muted small d-flex align-items-center gap-2">
                                            <c:choose>
                                                <c:when test="${empty post.authorAvatarUrl}">
                                                    <c:set var="finalAuthorAvatar" value="${ctx}/assets/images/default-avatar.png" />
                                                </c:when>
                                                <c:when test="${fn:startsWith(post.authorAvatarUrl, 'http')}">
                                                    <c:set var="finalAuthorAvatar" value="${post.authorAvatarUrl}" />
                                                </c:when>
                                                <c:when test="${fn:startsWith(post.authorAvatarUrl, '/')}">
                                                    <c:set var="finalAuthorAvatar" value="${ctx}${post.authorAvatarUrl}" />
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="finalAuthorAvatar" value="${ctx}/${post.authorAvatarUrl}" />
                                                </c:otherwise>
                                            </c:choose>
                                            <img src="${finalAuthorAvatar}" alt="Avatar" class="rounded-circle object-fit-cover shadow-sm" width="24" height="24">
                                            <span class="fw-medium"><c:out value="${post.authorName}"/></span>
                                            <span>&bull;</span>
                                            <i class="bi bi-clock"></i>
                                            <span>
                                                <c:if test="${not empty post.publishedAt}">
                                                    ${fn:substring(post.publishedAt.toString(), 0, 16).replace('T', ' ')}
                                                </c:if>
                                            </span>
                                        </div>
                                        <p class="card-text flex-grow-1 text-secondary"
                                           style="display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden;">
                                            <c:out value="${post.content}"/>
                                        </p>
                                        <div class="d-flex justify-content-between align-items-center mt-3 pt-3 border-top">
                                            <a href="${ctx}/tenant/post/detail?id=${post.postId}" class="btn btn-sm btn-light fw-medium">
                                                Xem chi tiết
                                            </a>
                                            <!-- Chỉ cho phép xóa khi chưa được duyệt (Pending hoặc Rejected) -->
                                            <c:if test="${post.status != 'APPROVED'}">
                                                <button class="btn btn-sm btn-outline-danger fw-medium" onclick="deletePost(${post.postId})">
                                                    <i class="bi bi-trash3"></i> Xóa
                                                </button>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
</div>
<script>
    var csrfToken = '${csrfToken}';
    
    function deletePost(postId) {
        if (!confirm('Bạn có chắc chắn muốn xóa bài viết này không? Hành động này không thể hoàn tác.')) {
            return;
        }

        fetch('${ctx}/api/v1/posts/delete', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-CSRF-Token': csrfToken
            },
            body: 'postId=' + postId
        })
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (data.success) {
                var card = document.getElementById('postCard-' + postId);
                if (card) {
                    card.remove();
                }
            } else {
                alert(data.error?.message || 'Có lỗi xảy ra, không thể xóa bài viết.');
            }
        })
        .catch(function(error) {
            console.error('Error:', error);
            alert('Lỗi kết nối máy chủ.');
        });
    }
</script>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
