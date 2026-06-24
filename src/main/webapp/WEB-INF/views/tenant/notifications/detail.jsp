<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết thông báo - Cổng cư dân"/>
<c:set var="pageRole" value="TENANT"/>
<c:set var="activeMenu" value="notifications"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/><div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
            
            <div class="page-header hero-sky-gradient">
                <div class="d-flex justify-content-between align-items-center flex-wrap">
                    <div>
                        <h1>Nội dung thông báo</h1>
                        <p>Thông báo từ Ban quản lý</p>
                    </div>
                    <div>
                        <a href="${ctx}/tenant/notifications" class="btn-mintlify-secondary text-decoration-none">
                            ← Danh sách thông báo
                        </a>
                    </div>
                </div>
            </div>

            <div class="widget-surface" style="max-width: 900px; margin: 0 auto;">
                <div class="widget-surface-body" style="padding: 2.5rem;">
                    <div style="border-bottom: 1px solid var(--hms-border-soft); padding-bottom: 1.5rem; margin-bottom: 1.5rem;">
                        <h2 style="font-size: 1.75rem; font-weight: 800; color: var(--hms-ink); margin-bottom: 0.75rem; line-height: 1.3;">
                            <c:out value="${notification.title}"/>
                        </h2>
                        <div style="display: flex; align-items: center; gap: 0.5rem; color: var(--hms-stone); font-size: 0.875rem;">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/>
                            </svg>
                            <c:out value="${notification.createdDateLabel}"/>
                        </div>
                    </div>
                    
                    <div class="article-content" style="font-size: 1.0625rem; line-height: 1.8; color: var(--hms-slate); white-space: pre-wrap;">
                        <c:out value="${notification.content}"/>
                    </div>
                </div>
            </div>
            
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
