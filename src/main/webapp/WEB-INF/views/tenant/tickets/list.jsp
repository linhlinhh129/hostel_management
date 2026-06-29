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
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/><div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="display:flex;justify-content:space-between;align-items:flex-end;flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>Yêu cầu của tôi</h1>
                        <p>Lịch sử yêu cầu hỗ trợ gửi đến Ban quản lý</p>
                    </div>
                    <a href="${ctx}/tenant/tickets/create" class="btn-accent" style="position:relative;z-index:1">
                        + Gửi yêu cầu mới
                    </a>
                </div>
            </div>

            <c:choose>
                <c:when test="${not empty tickets}">
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Danh sách yêu cầu</h3>
                        </div>
                        <div class="widget-surface-body p-0">
                            <div class="table-responsive">
                                <table class="table-mintlify table-hover">
                                    <thead>
                                    <tr>
                                        <th>Tiêu đề</th>
                                        <th class="d-none d-md-table-cell">Phân loại</th>
                                        <th class="d-none d-md-table-cell">Ngày gửi</th>
                                        <th class="text-center">Trạng thái</th>
                                        <th class="d-none d-md-table-cell text-center">Hành động</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="ticket" items="${tickets}" varStatus="st">
                                        <tr style="animation:fadeInUp 0.4s ease ${st.index * 0.04}s both">
                                            <td style="font-weight:600;color:var(--hms-ink); max-width: 250px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                                                <c:out value="${ticket.title}"/>
                                            </td>
                                            <td class="d-none d-md-table-cell">
                                                <span class="badge-hms badge-neutral"><c:out value="${ticket.category}"/></span>
                                            </td>
                                            <td class="d-none d-md-table-cell" style="font-size:0.875rem;color:var(--hms-stone)">
                                                <fmt:formatDate value="${ticket.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                            </td>
                                            <td class="text-center">
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
                                            </td>
                                            <td class="d-none d-md-table-cell text-center">
                                                <a href="${ctx}/tenant/tickets/${ticket.id}" class="btn-mintlify-secondary" style="padding: 0.25rem 0.5rem; font-size: 0.75rem;">Chi tiết</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="widget-surface">
                        <div class="widget-surface-body text-center" style="padding: 3rem 1rem;">
                            <div style="font-size:3rem;margin-bottom:0.75rem">📋</div>
                            <h4 style="font-weight:700;margin:0 0 0.5rem">Chưa có yêu cầu nào</h4>
                            <p style="color:var(--hms-stone);margin:0 0 1.25rem;font-size:0.875rem">
                                Gửi yêu cầu khi bạn cần hỗ trợ từ Ban quản lý
                            </p>
                            <a href="${ctx}/tenant/tickets/create" class="quick-action-btn primary d-inline-flex">
                                Gửi yêu cầu đầu tiên
                            </a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
    </div></div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
