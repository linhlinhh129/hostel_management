<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Bản tin cộng đồng"/>
<c:set var="pageRole" value="TENANT"/>
<c:set var="activeMenu" value="news-feed"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2>Bản tin cộng đồng</h2>
                <a href="${ctx}/tenant/post/create" class="btn btn-primary">
                    <i class="bi bi-pencil-square me-2"></i> Đăng bài viết
                </a>
            </div>

            <div class="row row-cols-1 row-cols-md-2 row-cols-xl-3 g-4">
                <c:choose>
                    <c:when test="${empty posts}">
                        <div class="col-12 text-center py-5 text-muted">
                            <i class="bi bi-journal-x fs-1"></i>
                            <p class="mt-3">Chưa có bài viết nào được phê duyệt.</p>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="post" items="${posts}">
                            <div class="col">
                                <div class="card h-100 shadow-sm border-0">
                                    <c:if test="${not empty post.imageUrl}">
                                        <img src="${ctx}/${post.imageUrl}" class="card-img-top" alt="Post thumbnail"
                                             style="height: 200px; object-fit: cover; border-radius: var(--hms-radius-md) var(--hms-radius-md) 0 0;">
                                    </c:if>
                                    <div class="card-body d-flex flex-column">
                                        <h5 class="card-title text-truncate" title="${fn:escapeXml(post.title)}">
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
                                            <span>
                                                <c:if test="${not empty post.publishedAt}">
                                                    ${fn:substring(post.publishedAt.toString(), 0, 16)}
                                                </c:if>
                                            </span>
                                        </div>
                                        <p class="card-text flex-grow-1 text-secondary"
                                           style="display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden;">
                                            <c:out value="${post.content}"/>
                                        </p>
                                        <div class="d-flex justify-content-between align-items-center mt-3 pt-3 border-top">
                                            <div class="text-muted small fw-medium">
                                                <i class="bi ${post.likedByCurrentUser ? 'bi-heart-fill text-danger' : 'bi-heart text-secondary'} fs-6 me-1"></i> ${post.likeCount}
                                                <i class="bi bi-chat-dots text-secondary ms-3 me-1 fs-6"></i> ${post.commentCount}
                                            </div>
                                            <a href="${ctx}/tenant/post/detail?id=${post.postId}" class="btn btn-sm btn-light fw-medium">
                                                Xem chi tiết
                                            </a>
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
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
