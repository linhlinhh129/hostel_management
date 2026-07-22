<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Chi tiết thông báo - Admin"/>
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

            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div>
                    <h1><c:out value="${notification.title}"/></h1>
                    <p>Mã: <c:out value="${notification.code}"/></p>
                </div>
                <a href="${ctx}/admin/notifications"
                   class="btn-mintlify-secondary text-decoration-none"
                   style="position:relative;z-index:1">&#8592; Danh sách</a>
            </div>

            <div class="row g-3">

                <%-- Nội dung thông báo --%>
                <div class="col-lg-8">
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Nội dung thông báo</h3>
                        </div>
                        <div class="widget-surface-body">
                            <p style="white-space:pre-line;line-height:1.7">
                                <c:out value="${notification.content}"/>
                            </p>
                        </div>
                    </div>
                </div>

                <%-- Metadata --%>
                <div class="col-lg-4">
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Thông tin</h3>
                        </div>
                        <div class="widget-surface-body">
                            <table class="info-table">
                                <tr>
                                    <td class="info-label">Mã</td>
                                    <td class="info-value"><c:out value="${notification.code}"/></td>
                                </tr>
                                <tr>
                                    <td class="info-label">Người tạo</td>
                                    <td class="info-value"><c:out value="${notification.createdByName}"/></td>
                                </tr>
                                <tr>
                                    <td class="info-label">Ngày tạo</td>
                                    <td class="info-value" style="font-size:0.8125rem">
                                        <c:out value="${notification.createdAtLabel}"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label">Gửi lúc</td>
                                    <td class="info-value" style="font-size:0.8125rem">
                                        <c:out value="${notification.sentAtLabel}"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label">Đối tượng</td>
                                    <td class="info-value">
                                        <jsp:include page="_notif-badges.jsp">
                                            <jsp:param name="type"          value="recipient"/>
                                            <jsp:param name="recipientType" value="${notification.recipientType}"/>
                                            <jsp:param name="recipientId"   value="${notification.recipientId}"/>
                                        </jsp:include>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label">Trạng thái</td>
                                    <td class="info-value">
                                        <jsp:include page="_notif-badges.jsp">
                                            <jsp:param name="type"        value="status"/>
                                            <jsp:param name="statusValue" value="${notification.status}"/>
                                        </jsp:include>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>

            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
