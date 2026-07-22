<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <c:set var="ctx" value="${pageContext.request.contextPath}" />
            <c:set var="pageTitle" value="Quản lý bài viết cộng đồng" />
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

                            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3"
                                style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                                <div style="position:relative;z-index:1">
                                    <h1>Danh sách bài viết</h1>
                                    <p>Quản lý các bài viết trên hệ thống</p>
                                </div>
                                <div style="display:flex;gap:8px;flex-wrap:wrap;position:relative;z-index:1">
                                    <a href="${ctx}/manager/articles/create" class="btn-accent">Tạo bài viết mới</a>
                                </div>
                            </div>

                            <c:if test="${param.success == 'create'}">
                                <div class="alert alert-success">Đã tạo bài viết thành công.</div>
                            </c:if>

                            <div class="widget-surface">
                                <div class="widget-surface-body p-0">
                                    <c:choose>
                                        <c:when test="${not empty posts}">
                                            <div class="table-responsive">
                                                <table class="table-mintlify">
                                                    <thead>
                                                        <tr>
                                                            <th>Tiêu đề</th>
                                                            <th>Tác giả</th>
                                                            <th>Thời gian đăng</th>
                                                            <th>Trạng thái</th>
                                                            <th>Thao tác</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="post" items="${posts}">
                                                            <tr id="post-row-${post.id}"
                                                                data-href="${ctx}/manager/articles/detail?id=${post.id}"
                                                                style="cursor:pointer;">
                                                                <td>
                                                                    <c:out value="${post.title}" />
                                                                </td>
                                                                <td>
                                                                    <c:out value="${post.authorName}" />
                                                                </td>
                                                                <td>
                                                                    <fmt:formatDate value="${post.createdAtAsDate}"
                                                                        pattern="dd/MM/yyyy HH:mm" />
                                                                </td>
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${post.status == 'PENDING'}">
                                                                            <span class="badge-hms badge-warning">Chờ
                                                                                duyệt</span>
                                                                        </c:when>
                                                                        <c:when test="${post.status == 'APPROVED'}">
                                                                            <span class="badge-hms badge-success">Đã
                                                                                duyệt</span>
                                                                        </c:when>
                                                                        <c:when test="${post.status == 'REJECTED'}">
                                                                            <span class="badge-hms badge-danger">Từ
                                                                                chối</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="badge-hms badge-neutral">
                                                                                <c:out value="${post.status}" />
                                                                            </span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td>
                                                                    <div class="d-flex gap-2 align-items-center">
                                                                        <a href="${ctx}/manager/articles/detail?id=${post.id}"
                                                                            class="btn-mintlify-secondary text-decoration-none d-inline-flex align-items-center gap-1"
                                                                            style="padding:6px 14px;font-size:0.8125rem">
                                                                            <i class="fa-solid fa-eye"></i> Chi tiết
                                                                        </a>
                                                                        <c:if test="${post.status == 'PENDING'}">
                                                                            <button
                                                                                class="btn-accent d-inline-flex align-items-center gap-1"
                                                                                style="padding:6px 14px;font-size:0.8125rem;border:none;border-radius:9999px"
                                                                                onclick="approvePost('${post.id}')">
                                                                                <i class="fa-solid fa-check"></i> Duyệt
                                                                            </button>
                                                                        </c:if>
                                                                        <button
                                                                            class="btn btn-outline-danger d-inline-flex align-items-center gap-1"
                                                                            style="padding:6px 14px;font-size:0.8125rem;border-radius:9999px;"
                                                                            onclick="deletePost('${post.id}')">
                                                                            <i class="fa-solid fa-trash"></i> Xóa
                                                                        </button>
                                                                    </div>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="p-4">
                                                <jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp">
                                                    <jsp:param name="message" value="Không có bài viết nào." />
                                                </jsp:include>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </main>
                    </div>
                </div>
                <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
                <script>
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
                                    window.location.reload();
                                } else {
                                    alert(data.error || "Lỗi khi xóa bài.");
                                }
                            })
                            .catch(e => {
                                console.error(e);
                                alert("Lỗi kết nối.");
                            });
                    }

                    // Click anywhere on row → navigate to detail
                    document.querySelectorAll('tr[data-href]').forEach(function(row) {
                        row.addEventListener('click', function(e) {
                            // Bỏ qua nếu click vào nút hoặc link trong cột Thao tác
                            if (e.target.closest('a, button')) return;
                            window.location.href = row.dataset.href;
                        });
                    });
                </script>
            </body>

            </html>