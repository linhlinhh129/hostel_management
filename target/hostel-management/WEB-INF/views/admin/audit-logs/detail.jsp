<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx"       value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết Audit Log - Admin"/>
<c:set var="pageRole"  value="ADMIN"/>
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

            <div class="page-header d-flex flex-wrap justify-content-between align-items-start gap-3">
                <div>
                    <h1>Log <span style="font-family:var(--hms-font-mono);color:var(--hms-accent-deep)">#<c:out value="${auditLog.id}"/></span></h1>
                    <p>Chi tiết nhật ký kiểm tra</p>
                </div>
                <a href="${ctx}/admin/audit-logs" class="quick-action-btn">← Danh sách</a>
            </div>

            <div class="row g-3" style="max-width:900px">
                <!-- Chi tiết chính -->
                <div class="col-lg-6">
                    <div class="widget-surface">
                        <div class="widget-surface-header"><h3>Thông tin chung</h3></div>
                        <div class="widget-surface-body">
                            <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                                <tr>
                                    <td style="padding:8px 0;color:var(--hms-text-muted);width:44%;font-weight:500">Log ID</td>
                                    <td style="padding:8px 0;font-family:var(--hms-font-mono);font-weight:700;color:var(--hms-accent-deep)">
                                        #<c:out value="${auditLog.id}"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:8px 0;color:var(--hms-text-muted);font-weight:500">Thời gian</td>
                                    <td style="padding:8px 0"><c:out value="${auditLog.createdAt}"/></td>
                                </tr>
                                <tr>
                                    <td style="padding:8px 0;color:var(--hms-text-muted);font-weight:500">Người thực hiện</td>
                                    <td style="padding:8px 0;font-weight:600"><c:out value="${auditLog.createdBy}"/></td>
                                </tr>
                                <tr>
                                    <td style="padding:8px 0;color:var(--hms-text-muted);font-weight:500">Chức năng</td>
                                    <td style="padding:8px 0">
                                        <span class="badge-hms badge-neutral" style="font-family:var(--hms-font-mono)">
                                            <c:out value="${auditLog.entityType}"/>
                                        </span>
                                        &nbsp;ID: <code><c:out value="${auditLog.entityId}"/></code>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:8px 0;color:var(--hms-text-muted);font-weight:500">Hành động</td>
                                    <td style="padding:8px 0">
                                        <c:choose>
                                            <c:when test="${auditLog.action == 'CREATE' or auditLog.action == 'CREATE_EMPLOYEE'}">
                                                <span class="badge-hms badge-success"><c:out value="${auditLog.action}"/></span>
                                            </c:when>
                                            <c:when test="${auditLog.action == 'DELETE' or auditLog.action == 'LOCK_EMPLOYEE' or auditLog.action == 'DEACTIVATE'}">
                                                <span class="badge-hms badge-danger"><c:out value="${auditLog.action}"/></span>
                                            </c:when>
                                            <c:when test="${auditLog.action == 'UPDATE' or auditLog.action == 'UPDATE_STATUS' or auditLog.action == 'UPDATE_AREA'}">
                                                <span class="badge-hms badge-info"><c:out value="${auditLog.action}"/></span>
                                            </c:when>
                                            <c:when test="${auditLog.action == 'ACTIVATE' or auditLog.action == 'UNLOCK_EMPLOYEE'}">
                                                <span class="badge-hms badge-accent"><c:out value="${auditLog.action}"/></span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-hms badge-neutral"><c:out value="${auditLog.action}"/></span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <c:if test="${not empty auditLog.comment}">
                                    <tr>
                                        <td style="padding:8px 0;color:var(--hms-text-muted);font-weight:500">Ghi chú</td>
                                        <td style="padding:8px 0"><c:out value="${auditLog.comment}"/></td>
                                    </tr>
                                </c:if>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Thay đổi dữ liệu -->
                <div class="col-lg-6">
                    <div class="widget-surface">
                        <div class="widget-surface-header"><h3>Thay đổi dữ liệu</h3></div>
                        <div class="widget-surface-body">
                            <c:if test="${not empty auditLog.oldValue}">
                                <div class="mb-3">
                                    <div style="font-size:0.75rem;font-weight:700;text-transform:uppercase;
                                                letter-spacing:0.05em;color:var(--hms-danger);margin-bottom:6px">
                                        Trước
                                    </div>
                                    <div style="background:var(--hms-danger-bg);border:1px solid var(--hms-danger-border);
                                                border-radius:var(--hms-radius);padding:0.75rem;
                                                font-family:var(--hms-font-mono);font-size:0.8125rem;
                                                word-break:break-all;line-height:1.6;color:var(--hms-danger)">
                                        <c:out value="${auditLog.oldValue}"/>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${not empty auditLog.newValue}">
                                <div>
                                    <div style="font-size:0.75rem;font-weight:700;text-transform:uppercase;
                                                letter-spacing:0.05em;color:var(--hms-success);margin-bottom:6px">
                                        Sau
                                    </div>
                                    <div style="background:var(--hms-success-bg);border:1px solid var(--hms-success-border);
                                                border-radius:var(--hms-radius);padding:0.75rem;
                                                font-family:var(--hms-font-mono);font-size:0.8125rem;
                                                word-break:break-all;line-height:1.6;color:var(--hms-success)">
                                        <c:out value="${auditLog.newValue}"/>
                                    </div>
                                </div>
                            </c:if>
                            <c:if test="${empty auditLog.oldValue and empty auditLog.newValue}">
                                <p class="text-muted" style="font-size:0.875rem">Không có dữ liệu thay đổi.</p>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
