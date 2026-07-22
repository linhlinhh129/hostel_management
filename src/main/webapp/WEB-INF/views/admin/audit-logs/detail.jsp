<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fn"  uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Chi tiết Audit Log - Admin"/>
<c:set var="pageRole"   value="ADMIN"/>
<c:set var="activeMenu" value="audit-logs"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <%-- Page header --%>
            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-center gap-3"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div>
                    <h1>Nhật ký
                        <span style="font-family:var(--hms-font-mono);color:var(--hms-accent-deep)">
                            #<c:out value="${auditLog.id}"/>
                        </span>
                    </h1>
                    <p>Chi tiết hoạt động thao tác trong hệ thống</p>
                </div>
                <a href="javascript:history.back()" class="btn-mintlify-secondary text-decoration-none"
                   style="position:relative;z-index:1">&#8592; Danh sách</a>
            </div>

            <%-- Content grid --%>
            <div class="row g-3" style="max-width:960px">

                <%-- Cột trái: Thông tin chung --%>
                <div class="col-lg-5">
                    <div class="widget-surface h-100">
                        <div class="widget-surface-header">
                            <h3 style="display:flex;align-items:center;gap:8px">
                                <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
                                     stroke="var(--hms-accent-deep)" stroke-width="2">
                                    <circle cx="12" cy="12" r="10"/>
                                    <line x1="12" y1="8" x2="12" y2="12"/>
                                    <line x1="12" y1="16" x2="12.01" y2="16"/>
                                </svg>
                                Thông tin chung
                            </h3>
                        </div>
                        <div class="widget-surface-body" style="padding:0">

                            <%-- Log ID --%>
                            <div class="audit-detail-row">
                                <span class="audit-detail-label">Log ID</span>
                                <span class="audit-detail-value--id">
                                    #<c:out value="${auditLog.id}"/>
                                </span>
                            </div>

                            <%-- Thời gian --%>
                            <div class="audit-detail-row">
                                <span class="audit-detail-label">Thời gian</span>
                                <span class="audit-detail-value">
                                    <fmt:formatDate value="${auditLog.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                </span>
                            </div>

                            <%-- Người thực hiện --%>
                            <div class="audit-detail-row">
                                <span class="audit-detail-label">Người thực hiện</span>
                                <span class="audit-detail-value--name">
                                    <c:out value="${auditLog.createdByName}"/>
                                </span>
                            </div>

                            <%-- Đối tượng --%>
                            <div class="audit-detail-row audit-detail-row--top">
                                <span class="audit-detail-label">Đối tượng</span>
                                <span class="audit-detail-value--name">
                                    <jsp:include page="_audit-badges.jsp">
                                        <jsp:param name="type"            value="entity"/>
                                        <jsp:param name="entityTypeValue" value="${auditLog.entityType}"/>
                                    </jsp:include>
                                </span>
                            </div>

                            <%-- Hành động --%>
                            <div class="audit-detail-row">
                                <span class="audit-detail-label">Hành động</span>
                                <span>
                                    <jsp:include page="_audit-badges.jsp">
                                        <jsp:param name="type"        value="action"/>
                                        <jsp:param name="actionValue" value="${auditLog.action}"/>
                                    </jsp:include>
                                </span>
                            </div>

                            <%-- IP --%>
                            <c:if test="${not empty auditLog.ipAddress}">
                                <div class="audit-detail-row">
                                    <span class="audit-detail-label">Địa chỉ IP</span>
                                    <span class="audit-detail-value--mono">
                                        <c:out value="${auditLog.ipAddress}"/>
                                    </span>
                                </div>
                            </c:if>

                            <%-- Ghi chú --%>
                            <c:if test="${not empty auditLog.comment}">
                                <div class="audit-detail-row audit-detail-row--top">
                                    <span class="audit-detail-label">Ghi chú</span>
                                    <span class="audit-detail-value" style="line-height:1.5">
                                        <c:out value="${auditLog.comment}"/>
                                    </span>
                                </div>
                            </c:if>

                        </div>
                    </div>
                </div>

                <%-- Cột phải: Thay đổi dữ liệu --%>
                <div class="col-lg-7">
                    <div class="widget-surface h-100">
                        <div class="widget-surface-header">
                            <h3 style="display:flex;align-items:center;gap:8px">
                                <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
                                     stroke="var(--hms-accent-deep)" stroke-width="2">
                                    <polyline points="16 3 21 3 21 8"/>
                                    <line x1="4" y1="20" x2="21" y2="3"/>
                                    <polyline points="21 16 21 21 16 21"/>
                                    <line x1="15" y1="15" x2="21" y2="21"/>
                                </svg>
                                Thay đổi dữ liệu
                            </h3>
                            <%-- Badge tóm tắt --%>
                            <c:choose>
                                <c:when test="${not empty auditLog.oldValue and not empty auditLog.newValue}">
                                    <span class="badge-hms badge-info" style="font-size:0.6875rem">Trước &amp; Sau</span>
                                </c:when>
                                <c:when test="${empty auditLog.oldValue and not empty auditLog.newValue}">
                                    <span class="badge-hms badge-success" style="font-size:0.6875rem">Tạo mới</span>
                                </c:when>
                                <c:when test="${not empty auditLog.oldValue and empty auditLog.newValue}">
                                    <span class="badge-hms badge-danger" style="font-size:0.6875rem">Đã xóa</span>
                                </c:when>
                            </c:choose>
                        </div>
                        <div class="widget-surface-body">
                            <jsp:include page="_diff-table.jsp"/>
                        </div>
                    </div>
                </div>

            </div><%-- /row --%>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
