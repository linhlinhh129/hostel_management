<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Thông báo - Cổng cư dân"/>
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
            
            <div class="page-header hero-sky-gradient" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <h1>Thông báo</h1>
                <p>Các thông báo từ Ban quản lý</p>
            </div>
            
            <c:choose>
                <c:when test="${not empty notifications}">
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Tất cả thông báo</h3>
                        </div>
                        <div class="widget-surface-body p-0">
                            <ul style="list-style:none;margin:0;padding:0">
                                <c:forEach var="notif" items="${notifications}" varStatus="st">
                                    <li style="padding: 1rem 1.25rem; border-bottom: 1px solid var(--hms-border-soft); display: flex; gap: 1rem; align-items: flex-start; animation: fadeInUp 0.4s ease ${st.index * 0.04}s both; background: ${notif.unread ? 'rgba(37, 99, 235, 0.03)' : 'transparent'};">
                                        <div style="width:40px;height:40px;border-radius:var(--hms-radius-md); background:linear-gradient(135deg,var(--hms-accent),var(--hms-accent-soft)); display:flex;align-items:center;justify-content:center; color:#fff;font-size:1.25rem;font-weight:800;flex-shrink:0">
                                            🔔
                                        </div>
                                        <div style="flex:1;min-width:0;cursor:pointer" onclick="location.href='${ctx}/tenant/notifications/${notif.id}'">
                                            <div style="display:flex;justify-content:space-between;align-items:flex-start;margin-bottom:0.25rem;">
                                                <h4 style="margin:0;font-size:1rem;font-weight:700;color:${notif.unread ? 'var(--hms-ink)' : 'var(--hms-slate)'}">
                                                    <c:out value="${notif.title}"/>
                                                </h4>
                                                <c:if test="${notif.unread}">
                                                    <span class="badge-hms badge-info" style="font-size:0.625rem;padding:0.15rem 0.4rem;margin-left:8px;">Mới</span>
                                                </c:if>
                                            </div>
                                            <p style="margin:0 0 0.5rem;font-size:0.875rem;color:var(--hms-stone);line-height:1.5;">
                                                <c:out value="${notif.summary}"/>
                                            </p>
                                            <time style="font-size:0.75rem;color:var(--hms-muted);font-weight:500;">
                                                <c:out value="${notif.createdDateLabel}"/>
                                            </time>
                                        </div>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="widget-surface">
                        <div class="widget-surface-body text-center" style="padding: 3rem 1rem;">
                            <div style="font-size:3rem;margin-bottom:0.75rem">📭</div>
                            <h4 style="font-weight:700;margin:0 0 0.5rem">Không có thông báo</h4>
                            <p style="color:var(--hms-stone);margin:0;font-size:0.875rem">
                                Bạn đã đọc tất cả thông báo từ Ban quản lý
                            </p>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
    </div></div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
