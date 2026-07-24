<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Người phụ thuộc - Cổng cư dân"/>
<c:set var="pageRole" value="TENANT"/>
<c:set var="activeMenu" value="dependents"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/><div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
            
            <div class="page-header hero-sky-gradient" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <h1>Người phụ thuộc</h1>
                <p>Danh sách thành viên đăng ký ở cùng</p>
            </div>
            
            <c:choose>
                <c:when test="${not empty dependents}">
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Danh sách thành viên</h3>
                        </div>
                        <div class="widget-surface-body p-0">
                            <div class="table-responsive">
                                <table class="table-mintlify table-hover">
                                    <thead>
                                    <tr>
                                        <th>Họ và tên</th>
                                        <th>Quan hệ</th>
                                        <th class="d-none d-md-table-cell">Ngày sinh</th>
                                        <th class="d-none d-md-table-cell text-center">Hành động</th>
                                    </tr>
                                    </thead>
                                    <tbody id="dependentsTbody">
                                    <c:forEach var="dep" items="${dependents}">
                                        <tr>
                                            <td style="font-weight:600;color:var(--hms-ink)">
                                                <c:out value="${dep.fullName}"/>
                                            </td>
                                            <td>
                                                <span class="badge-hms badge-neutral"><c:out value="${dep.relationship}"/></span>
                                            </td>
                                            <td class="d-none d-md-table-cell" style="font-size:0.875rem;color:var(--hms-stone)">
                                                <c:out value="${dep.dobLabel}"/>
                                            </td>
                                            <td class="d-none d-md-table-cell text-center">
                                                <a href="${ctx}/tenant/dependents/${dep.id}" class="btn-mintlify-secondary" style="padding: 0.25rem 0.5rem; font-size: 0.75rem;">Chi tiết</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2">
                              <span class="text-muted" style="font-size:0.875rem">
                                Tổng <strong id="dependentsTotal"></strong> thành viên
                                · Trang <span id="dependentsPage">1</span> / <span id="dependentsTotalPages">1</span>
                              </span>
                              <div class="d-flex gap-1" id="dependentsBtns"></div>
                            </div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="widget-surface">
                        <div class="widget-surface-body text-center" style="padding: 3rem 1rem;">
                            <div style="font-size:3rem;margin-bottom:0.75rem">👨‍👩‍👧‍👦</div>
                            <h4 style="font-weight:700;margin:0 0 0.5rem">Chưa có người phụ thuộc</h4>
                            <p style="color:var(--hms-stone);margin:0;font-size:0.875rem">
                                Hiện tại chưa có thành viên nào được đăng ký ở cùng với bạn.
                            </p>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
    </div></div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
<script>clientPaginate('dependentsTbody','dependentsTotal','dependentsPage','dependentsTotalPages','dependentsBtns');</script>
