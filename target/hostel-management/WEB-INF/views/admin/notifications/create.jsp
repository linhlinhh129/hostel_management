<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Tạo thông báo - Admin"/>
<c:set var="pageRole"   value="ADMIN"/>
<c:set var="activeMenu" value="notifications"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <h1>Tạo thông báo</h1>
                <%-- Admin.md §12: đối tượng gửi chỉ là "Tất cả cơ sở" --%>
                <p>Gửi thông báo đến toàn bộ cư dân trong hệ thống</p>
            </div>

            <div class="data-surface" style="max-width:680px">
                <form method="post" action="${ctx}/admin/notifications/create" class="p-4">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                    <%-- Admin chỉ gửi ALL — cố định theo Admin.md §12 --%>
                    <input type="hidden" name="recipientType" value="ALL"/>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger mb-3"><c:out value="${errorMessage}"/></div>
                    </c:if>

                    <!-- Thông tin đối tượng nhận (read-only, chỉ ALL) -->
                    <div class="alert"
                         style="background:var(--hms-accent-bg);border:1px solid var(--hms-border-accent);
                                border-radius:var(--hms-radius);padding:0.75rem 1rem;
                                font-size:0.8125rem;margin-bottom:1.25rem;color:var(--hms-ink)">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                             stroke="var(--hms-accent-deep)" stroke-width="2"
                             style="margin-right:6px;vertical-align:-2px">
                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                            <circle cx="9" cy="7" r="4"/>
                            <path d="M23 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>
                        </svg>
                        Đối tượng nhận: <strong>Tất cả cư dân trong hệ thống</strong>
                        <span class="badge-hms badge-accent ms-2">ALL</span>
                    </div>

                    <!-- Tiêu đề -->
                    <div class="mb-3">
                        <label for="title" class="form-label">
                            Tiêu đề thông báo <span class="text-danger">*</span>
                        </label>
                        <input type="text" class="form-control" id="title" name="title"
                               required maxlength="250"
                               placeholder="VD: Thông báo bảo trì thang máy..."
                               value="<c:out value='${dto.title}'/>">
                    </div>

                    <!-- Nội dung -->
                    <div class="mb-3">
                        <label for="content" class="form-label">
                            Nội dung <span class="text-danger">*</span>
                        </label>
                        <textarea class="form-control" id="content" name="content"
                                  rows="7" required maxlength="5000"
                                  placeholder="Nội dung thông báo chi tiết..."><c:out value="${dto.content}"/></textarea>
                        <div class="form-text">Tối đa 5000 ký tự</div>
                    </div>

                    <div class="d-flex gap-2 mt-3">
                        <button type="submit" class="btn btn-mintlify-primary" style="width:auto">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                                 stroke="currentColor" stroke-width="2.5" style="margin-right:4px">
                                <line x1="22" y1="2" x2="11" y2="13"/>
                                <polygon points="22 2 15 22 11 13 2 9 22 2"/>
                            </svg>
                            Gửi thông báo
                        </button>
                        <a href="${ctx}/admin/notifications"
                           class="btn-mintlify-secondary text-decoration-none">Hủy</a>
                    </div>
                </form>
            </div>

        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
