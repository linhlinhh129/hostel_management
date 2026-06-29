<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết thông báo - Admin"/>
<c:set var="pageRole" value="ADMIN"/>
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

            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div>
                    <h1><c:out value="${notification.title}"/></h1>
                    <p>Mã: <c:out value="${notification.code}"/></p>
                </div>
                <a href="${ctx}/admin/notifications" class="btn-mintlify-secondary text-decoration-none" style="position:relative;z-index:1">← Danh sách</a>
            </div>

            <div class="row g-3">
                <!-- Nội dung thông báo -->
                <div class="col-lg-8">
                    <div class="widget-surface">
                        <div class="widget-surface-header"><h3>Nội dung thông báo</h3></div>
                        <div class="widget-surface-body">
                            <p style="white-space:pre-line;line-height:1.7">
                                <c:out value="${notification.content}"/>
                            </p>
                        </div>
                    </div>
                </div>

                <!-- Metadata -->
                <div class="col-lg-4">
                    <div class="widget-surface">
                        <div class="widget-surface-header"><h3>Thông tin</h3></div>
                        <div class="widget-surface-body">
                            <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                                <tr><td style="padding:6px 0;color:var(--hms-text-muted)">Mã</td>
                                    <td style="padding:6px 0"><c:out value="${notification.code}"/></td></tr>
                                <tr><td style="padding:6px 0;color:var(--hms-text-muted)">Người tạo</td>
                                    <td style="padding:6px 0"><c:out value="${notification.createdByName}"/></td></tr>
                                <tr><td style="padding:6px 0;color:var(--hms-text-muted)">Ngày tạo</td>
                                    <td style="padding:6px 0;font-size:0.8125rem"><c:out value="${notification.createdAtLabel}"/></td></tr>
                                <tr><td style="padding:6px 0;color:var(--hms-text-muted)">Gửi lúc</td>
                                    <td style="padding:6px 0;font-size:0.8125rem"><c:out value="${notification.sentAtLabel}"/></td></tr>
                                <tr><td style="padding:6px 0;color:var(--hms-text-muted)">Đối tượng</td>
                                    <td style="padding:6px 0">
                                        <c:choose>
                                            <c:when test="${notification.recipientType == 'ALL'}">
                                                <span class="badge-hms badge-info">Tất cả cư dân</span>
                                            </c:when>
                                            <c:when test="${notification.recipientType == 'FACILITY'}">
                                                <span class="badge-hms badge-neutral">Cơ sở #<c:out value="${notification.recipientId}"/></span>
                                            </c:when>
                                            <c:when test="${notification.recipientType == 'ROOM'}">
                                                <span class="badge-hms badge-neutral">Phòng #<c:out value="${notification.recipientId}"/></span>
                                            </c:when>
                                        </c:choose>
                                    </td></tr>
                                <tr><td style="padding:6px 0;color:var(--hms-text-muted)">Trạng thái</td>
                                    <td style="padding:6px 0">
                                        <c:choose>
                                            <c:when test="${notification.status == 'SENT'}">
                                                <span class="badge-hms badge-success">Đã gửi</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-hms badge-warning">Nháp</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td></tr>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
