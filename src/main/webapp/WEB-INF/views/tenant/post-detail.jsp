<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết bài viết"/>
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

            <div class="d-flex align-items-center mb-4">
                <a href="${ctx}/tenant/news-feed" class="btn btn-icon btn-light me-3 shadow-sm">
                    <i class="bi bi-arrow-left"></i>
                </a>
                <h2 class="mb-0">Chi tiết bản tin</h2>
            </div>

            <div class="row">
                <div class="col-lg-8">
                    <!-- Post Card -->
                    <div class="card shadow-sm border-0 mb-4">
                        <div class="card-body p-4">
                            <!-- Author info -->
                            <div class="d-flex align-items-center mb-4">
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
                                <img src="${finalAuthorAvatar}"
                                     alt="Avatar" class="rounded-circle object-fit-cover shadow-sm me-3" width="48" height="48">
                                <div>
                                    <h5 class="mb-0 fw-bold"><c:out value="${post.authorName}"/></h5>
                                    <c:if test="${not empty post.publishedAt}">
                                        <small class="text-muted">${fn:substring(post.publishedAt.toString(), 0, 16)}</small>
                                    </c:if>
                                </div>
                            </div>

                            <!-- Content -->
                            <h3 class="fw-bold mb-3"><c:out value="${post.title}"/></h3>
                            <p class="fs-5 text-secondary" style="white-space: pre-line;"><c:out value="${post.content}"/></p>

                            <!-- Image -->
                            <c:if test="${not empty post.imageUrl}">
                                <div class="mt-4">
                                    <img src="${ctx}/${post.imageUrl}" class="img-fluid rounded shadow-sm" style="max-height: 400px; object-fit: cover;" alt="Image">
                                </div>
                            </c:if>

                            <hr class="my-4">

                            <!-- Reactions -->
                            <div class="d-flex gap-3">
                                <button id="btnLike" class="btn ${post.likedByCurrentUser ? 'btn-danger' : 'btn-outline-secondary'} d-flex align-items-center gap-2 fw-medium rounded-pill px-3"
                                        onclick="toggleLike(${post.postId})">
                                    <i class="bi ${post.likedByCurrentUser ? 'bi-heart-fill' : 'bi-heart'}"></i>
                                    <span id="likeCount">${post.likeCount}</span> Thích
                                </button>
                                <button class="btn btn-light text-secondary d-flex align-items-center gap-2 fw-medium rounded-pill px-3">
                                    <i class="bi bi-chat-dots"></i>
                                    <span id="commentCount">${post.commentCount}</span> Bình luận
                                </button>
                            </div>
                        </div>
                    </div>

                    <!-- Comments Section -->
                    <div class="card shadow-sm border-0">
                        <div class="card-body p-4">
                            <h5 class="fw-bold mb-4">Bình luận</h5>

                            <!-- Comment Input -->
                            <div class="d-flex gap-3 mb-4">
                                <c:choose>
                                    <c:when test="${empty currentUser.avatarUrl}">
                                        <c:set var="finalCurrentAvatar" value="${ctx}/assets/images/default-avatar.png" />
                                    </c:when>
                                    <c:when test="${fn:startsWith(currentUser.avatarUrl, 'http')}">
                                        <c:set var="finalCurrentAvatar" value="${currentUser.avatarUrl}" />
                                    </c:when>
                                    <c:when test="${fn:startsWith(currentUser.avatarUrl, '/')}">
                                        <c:set var="finalCurrentAvatar" value="${ctx}${currentUser.avatarUrl}" />
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="finalCurrentAvatar" value="${ctx}/${currentUser.avatarUrl}" />
                                    </c:otherwise>
                                </c:choose>
                                <img src="${finalCurrentAvatar}"
                                     alt="Avatar" class="rounded-circle object-fit-cover shadow-sm" width="40" height="40">
                                <div class="flex-grow-1">
                                    <textarea id="commentContent" class="form-control bg-light" rows="2" placeholder="Viết bình luận của bạn..." maxlength="1000" oninput="checkCommentLength()"></textarea>
                                    <div class="d-flex justify-content-between align-items-center mt-2">
                                        <small class="text-muted" id="charCount">0/1000</small>
                                        <button id="btnSubmitComment" class="btn btn-primary px-4 fw-medium" onclick="submitComment(${post.postId})" disabled>Gửi</button>
                                    </div>
                                </div>
                            </div>

                            <!-- Comment List -->
                            <div id="commentList" class="d-flex flex-column gap-3 mt-4">
                                <c:forEach var="comment" items="${comments}">
                                    <div class="d-flex gap-3">
                                        <c:choose>
                                            <c:when test="${empty comment.authorAvatarUrl}">
                                                <c:set var="finalCmtAvatar" value="${ctx}/assets/images/default-avatar.png" />
                                            </c:when>
                                            <c:when test="${fn:startsWith(comment.authorAvatarUrl, 'http')}">
                                                <c:set var="finalCmtAvatar" value="${comment.authorAvatarUrl}" />
                                            </c:when>
                                            <c:when test="${fn:startsWith(comment.authorAvatarUrl, '/')}">
                                                <c:set var="finalCmtAvatar" value="${ctx}${comment.authorAvatarUrl}" />
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="finalCmtAvatar" value="${ctx}/${comment.authorAvatarUrl}" />
                                            </c:otherwise>
                                        </c:choose>
                                        <img src="${finalCmtAvatar}"
                                             alt="Avatar" class="rounded-circle object-fit-cover shadow-sm" width="40" height="40">
                                        <div class="flex-grow-1 bg-light p-3 rounded-4">
                                            <div class="d-flex justify-content-between align-items-center mb-1">
                                                <span class="fw-bold"><c:out value="${comment.authorName}"/></span>
                                                <c:if test="${not empty comment.createdAt}">
                                                    <small class="text-muted">${fn:substring(comment.createdAt.toString(), 0, 16)}</small>
                                                </c:if>
                                            </div>
                                            <p class="mb-0 text-secondary" style="white-space: pre-wrap;"><c:out value="${comment.content}"/></p>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>

<script>
    var csrfToken = '${csrfToken}';

    function toggleLike(postId) {
        fetch('${ctx}/api/v1/posts/like', {
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
                var btn = document.getElementById('btnLike');
                var icon = btn.querySelector('i');
                var count = document.getElementById('likeCount');

                count.innerText = data.data.likeCount;

                if (data.data.liked) {
                    btn.classList.remove('btn-outline-secondary');
                    btn.classList.add('btn-danger');
                    icon.classList.remove('bi-heart');
                    icon.classList.add('bi-heart-fill');
                } else {
                    btn.classList.remove('btn-danger');
                    btn.classList.add('btn-outline-secondary');
                    icon.classList.remove('bi-heart-fill');
                    icon.classList.add('bi-heart');
                }
            }
        });
    }

    function checkCommentLength() {
        var content = document.getElementById('commentContent').value;
        var btn = document.getElementById('btnSubmitComment');
        var charCount = document.getElementById('charCount');

        charCount.innerText = content.length + '/1000';
        btn.disabled = !(content.trim().length > 0 && content.length <= 1000);
    }

    function submitComment(postId) {
        var content = document.getElementById('commentContent').value.trim();
        if (!content) return;

        var btn = document.getElementById('btnSubmitComment');
        btn.disabled = true;
        btn.innerText = 'Đang gửi...';

        fetch('${ctx}/api/v1/posts/comment', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-CSRF-Token': csrfToken
            },
            body: 'postId=' + postId + '&content=' + encodeURIComponent(content)
        })
        .then(function(response) { return response.json(); })
        .then(function(data) {
            if (data.success) {
                var commentList = document.getElementById('commentList');
                var c = data.data;
                var time = c.createdAt ? c.createdAt.substring(0, 16) : 'Vừa xong';

                var div = document.createElement('div');
                div.className = 'd-flex gap-3';
                div.innerHTML = '<img src="${finalCurrentAvatar}" class="rounded-circle object-fit-cover shadow-sm" width="40" height="40">' +
                    '<div class="flex-grow-1 bg-light p-3 rounded-4">' +
                        '<div class="d-flex justify-content-between align-items-center mb-1">' +
                            '<span class="fw-bold">' + c.authorName + '</span>' +
                            '<small class="text-muted">' + time + '</small>' +
                        '</div>' +
                        '<p class="mb-0 text-secondary" style="white-space: pre-wrap;">' + c.content + '</p>' +
                    '</div>';

                commentList.appendChild(div);
                document.getElementById('commentContent').value = '';
                checkCommentLength();

                var countEl = document.getElementById('commentCount');
                countEl.innerText = parseInt(countEl.innerText) + 1;
            }
        })
        .finally(function() {
            btn.innerText = 'Gửi';
        });
    }
</script>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
