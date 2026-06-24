<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"  %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Yêu cầu của tôi - Cổng cư dân"/>
<c:set var="pageRole"   value="TENANT"/>
<c:set var="activeMenu" value="tickets"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header d-flex justify-content-between align-items-center gap-3">
                <div><h1>Yêu cầu của tôi</h1><p>Lịch sử yêu cầu gửi đến Ban quản lý</p></div>
                <a href="${ctx}/tenant/tickets/create" class="quick-action-btn primary">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
                        <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
                    </svg>
                    Gửi yêu cầu
                </a>
            </div>

            <c:choose>
                <c:when test="${not empty tickets}">
                    <c:forEach var="ticket" items="${tickets}" varStatus="st">
                        <a href="${ctx}/tenant/tickets/${ticket.id}"
                           class="tenant-card"
                           style="animation-delay:${st.index * 0.05}s">
                            <div class="d-flex justify-content-between align-items-start gap-2">
                                <div style="flex:1;min-width:0">
                                    <div style="font-weight:700;font-size:0.9375rem;margin-bottom:4px;
                                                white-space:nowrap;overflow:hidden;text-overflow:ellipsis">
                                        <c:out value="${ticket.title}"/>
                                    </div>
                                    <div style="font-size:0.75rem;color:var(--hms-stone);display:flex;gap:8px;align-items:center">
                                        <span class="badge-hms badge-neutral" style="font-size:0.625rem">
                                            <c:out value="${ticket.category}"/>
                                        </span>
                                        <span><c:out value="${ticket.createdAt}"/></span>
                                    </div>
                                </div>
                                <div style="flex-shrink:0">
                                    <c:choose>
                                        <c:when test="${ticket.status == 'PENDING'}">
                                            <span class="badge-hms badge-info">Mới tạo</span>
                                        </c:when>
                                        <c:when test="${ticket.status == 'IN_PROGRESS'}">
                                            <span class="badge-hms badge-warning">Đang xử lý</span>
                                        </c:when>
                                        <c:when test="${ticket.status == 'DONE'}">
                                            <span class="badge-hms badge-success">Hoàn thành</span>
                                        </c:when>
                                        <c:when test="${ticket.status == 'REJECTED'}">
                                            <span class="badge-hms badge-danger">Từ chối</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-hms badge-neutral"><c:out value="${ticket.status}"/></span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div style="text-align:center;padding:3rem 1rem">
                        <div style="font-size:3rem;margin-bottom:0.75rem">📋</div>
                        <h4 style="font-weight:700;margin:0 0 0.5rem">Chưa có yêu cầu nào</h4>
                        <p style="color:var(--hms-stone);margin:0 0 1.25rem;font-size:0.875rem">
                            Gửi yêu cầu khi bạn cần hỗ trợ từ Ban quản lý
                        </p>
                        <a href="${ctx}/tenant/tickets/create" class="quick-action-btn primary">
                            Gửi yêu cầu đầu tiên
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
