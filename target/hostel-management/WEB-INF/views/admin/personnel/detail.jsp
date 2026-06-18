<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <c:set var="ctx" value="${pageContext.request.contextPath}" />
        <c:set var="pageTitle" value="Chi tiết nhân sự - Admin" />
        <c:set var="pageRole" value="ADMIN" />
        <c:set var="activeMenu" value="personnel" />
        <jsp:include page="/WEB-INF/views/layout/head.jsp" />

        <body>
            <div class="app-shell">
                <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
                <div class="sidebar-overlay"></div>
                <div class="main-wrapper">
                    <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
                    <main class="page-content">
                        <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

                        <div class="page-header d-flex flex-wrap justify-content-between align-items-start gap-3">
                            <div>
                                <h1>
                                    <c:out value="${employee.fullName}" />
                                </h1>
                                <p>
                                    ID: #
                                    <c:out value="${employee.id}" /> ·
                                    <c:choose>
                                        <c:when test="${employee.role == 'MANAGER'}">Ban Quản lý</c:when>
                                        <c:when test="${employee.role == 'OPERATOR'}">Nhân viên vận hành</c:when>
                                        <c:otherwise>
                                            <c:out value="${employee.role}" />
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>
                            <div class="d-flex gap-2 flex-wrap">
                                <a href="${ctx}/admin/personnel/${employee.id}/edit" class="quick-action-btn">Sửa thông
                                    tin</a>

                                <%-- Khóa / Mở khóa --%>
                                    <c:if test="${employee.status == 'ACTIVE'}">
                                        <form method="post" action="${ctx}/admin/personnel/${employee.id}/status"
                                            style="margin:0">
                                            <input type="hidden" name="csrfToken" value="${csrfToken}" />
                                            <input type="hidden" name="status" value="INACTIVE" />
                                            <button type="submit" class="quick-action-btn"
                                                style="color:var(--hms-danger)"
                                                onclick="return confirm('Khóa tài khoản này?')">
                                                Khóa tài khoản
                                            </button>
                                        </form>
                                    </c:if>
                                    <c:if test="${employee.status != 'ACTIVE'}">
                                        <form method="post" action="${ctx}/admin/personnel/${employee.id}/status"
                                            style="margin:0">
                                            <input type="hidden" name="csrfToken" value="${csrfToken}" />
                                            <input type="hidden" name="status" value="ACTIVE" />
                                            <button type="submit" class="quick-action-btn"
                                                style="color:var(--hms-success)"
                                                onclick="return confirm('Mở khóa tài khoản này?')">
                                                Mở khóa
                                            </button>
                                        </form>
                                    </c:if>

                                    <a href="${ctx}/admin/personnel" class="quick-action-btn">← Danh sách</a>
                            </div>
                        </div>

                        <div class="row g-3">
                            <%-- Thông tin nhân sự --%>
                                <div class="col-lg-5">
                                    <div class="widget-surface">
                                        <div class="widget-surface-header">
                                            <h3>Thông tin cá nhân</h3>
                                        </div>
                                        <div class="widget-surface-body">
                                            <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                                                <tr>
                                                    <td style="padding:6px 0;color:var(--hms-text-muted);width:44%">Họ
                                                        tên</td>
                                                    <td style="padding:6px 0;font-weight:500">
                                                        <c:out value="${employee.fullName}" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding:6px 0;color:var(--hms-text-muted)">Email</td>
                                                    <td style="padding:6px 0">
                                                        <c:out value="${employee.email}" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding:6px 0;color:var(--hms-text-muted)">Số điện thoại
                                                    </td>
                                                    <td style="padding:6px 0">
                                                        <c:out value="${employee.phone}" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding:6px 0;color:var(--hms-text-muted)">Vai trò</td>
                                                    <td style="padding:6px 0">
                                                        <c:choose>
                                                            <c:when test="${employee.role == 'MANAGER'}">
                                                                <span class="badge-hms badge-info">Ban Quản lý</span>
                                                            </c:when>
                                                            <c:when test="${employee.role == 'OPERATOR'}">
                                                                <span class="badge-hms badge-neutral">Nhân viên vận
                                                                    hành</span>
                                                            </c:when>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding:6px 0;color:var(--hms-text-muted)">Trạng thái
                                                    </td>
                                                    <td style="padding:6px 0">
                                                        <c:choose>
                                                            <c:when test="${employee.status == 'ACTIVE'}">
                                                                <span class="badge-hms badge-success">Hoạt động</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge-hms badge-danger">Bị khóa</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding:6px 0;color:var(--hms-text-muted)">Ngày tạo</td>
                                                    <td style="padding:6px 0;font-size:0.8125rem">
                                                        <c:out value="${employee.createdAt}" />
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding:6px 0;color:var(--hms-text-muted)">Cập nhật</td>
                                                    <td style="padding:6px 0;font-size:0.8125rem">
                                                        <c:out value="${employee.updatedAt}" />
                                                    </td>
                                                </tr>
                                            </table>
                                        </div>
                                    </div>
                                </div>

                                <%-- Cơ sở phụ trách --%>
                                    <div class="col-lg-7">
                                        <div class="widget-surface">
                                            <div class="widget-surface-header">
                                                <h3>Cơ sở phụ trách</h3>
                                                <c:if
                                                    test="${employee.role == 'MANAGER' or employee.role == 'OPERATOR'}">
                                                    <form method="post"
                                                        action="${ctx}/admin/personnel/${employee.id}/facilities"
                                                        style="margin:0" id="facilityAssignForm">
                                                        <input type="hidden" name="csrfToken" value="${csrfToken}" />
                                                        <%-- checkboxes được inject bên dưới --%>
                                                    </form>
                                                </c:if>
                                            </div>
                                            <div class="widget-surface-body">
                                                <c:choose>
                                                    <c:when test="${not empty employee.facilityNames}">
                                                        <ul style="list-style:none;padding:0;margin:0">
                                                            <c:forEach var="fname" items="${employee.facilityNames}">
                                                                <li
                                                                    style="padding:6px 0;border-bottom:1px solid var(--hms-border-soft);font-size:0.875rem">
                                                                    <svg width="14" height="14" viewBox="0 0 24 24"
                                                                        fill="none" stroke="var(--hms-accent)"
                                                                        stroke-width="2" style="margin-right:6px">
                                                                        <path
                                                                            d="M3 21h18M5 21V7l8-4v18M19 21V11l-6-4" />
                                                                    </svg>
                                                                    <c:out value="${fname}" />
                                                                </li>
                                                            </c:forEach>
                                                        </ul>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp">
                                                            <jsp:param name="message" value="Chưa gán cơ sở — nhân sự không thể truy cập dữ liệu" />
                                                        </jsp:include>
                                                    </c:otherwise>
                                                </c:choose>

                                                <c:if
                                                    test="${employee.role == 'MANAGER' or employee.role == 'OPERATOR'}">
                                                    <div class="mt-3">
                                                        <a href="${ctx}/admin/personnel/${employee.id}/edit"
                                                            class="quick-action-btn" style="font-size:0.8125rem">
                                                            Cập nhật cơ sở phụ trách
                                                        </a>
                                                    </div>
                                                </c:if>
                                            </div>
                                        </div>
                                    </div>
                        </div>
                    </main>
                </div>
            </div>
            <jsp:include page="/WEB-INF/views/layout/footer.jsp" />