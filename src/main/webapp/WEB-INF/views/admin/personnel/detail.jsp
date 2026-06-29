<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết nhân sự - Admin"/>
<c:set var="pageRole" value="ADMIN"/>
<c:set var="activeMenu" value="personnel"/>
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
                    <h1><c:out value="${user.fullName}"/></h1>
                    <p>
            ·
                        <c:choose>
                            <c:when test="${user.role == 'MANAGER'}">Ban Quản lý</c:when>
                            <c:when test="${user.role == 'OPERATOR'}">Nhân viên vận hành</c:when>
                            <c:otherwise><c:out value="${user.role}"/></c:otherwise>
                        </c:choose>
                    </p>
                </div>
                <div class="d-flex gap-2 flex-wrap">
                    <a href="${ctx}/admin/personnel/${user.id}/edit" class="quick-action-btn">Sửa thông tin</a>

                    <c:if test="${user.status == 'ACTIVE'}">
                        <form method="post" action="${ctx}/admin/personnel/${user.id}/status" style="margin:0">
                            <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                            <input type="hidden" name="status" value="INACTIVE"/>
                            <button type="submit" class="quick-action-btn" style="color:var(--hms-danger)"
                                    onclick="return confirm('Khóa tài khoản này?')">Khóa tài khoản</button>
                        </form>
                    </c:if>
                    <c:if test="${user.status != 'ACTIVE'}">
                        <form method="post" action="${ctx}/admin/personnel/${user.id}/status" style="margin:0">
                            <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                            <input type="hidden" name="status" value="ACTIVE"/>
                            <button type="submit" class="quick-action-btn" style="color:var(--hms-success)"
                                    onclick="return confirm('Mở khóa tài khoản này?')">Mở khóa</button>
                        </form>
                        <form method="post" action="${ctx}/admin/personnel/${user.id}/delete" style="margin:0"
                              onsubmit="return confirm('Bạn có chắc chắn muốn XÓA nhân sự này không?\nHành động này không thể hoàn tác!')">
                            <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                            <button type="submit" class="quick-action-btn"
                                    style="color:var(--hms-danger)">
                                Xóa nhân sự
                            </button>
                        </form>
                    </c:if>

                    <a href="${ctx}/admin/personnel" class="quick-action-btn">← Danh sách</a>
                </div>
            </div>

            <div class="row g-3">
                <!-- Thông tin cá nhân -->
                <div class="col-lg-6">
                    <div class="widget-surface">
                        <div class="widget-surface-header"><h3>Thông tin cá nhân</h3></div>
                        <div class="widget-surface-body">
                            <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                                <tr>
                                    <td style="padding:7px 0;color:var(--hms-text-muted);width:42%">Họ tên</td>
                                    <td style="padding:7px 0;font-weight:600"><c:out value="${user.fullName}"/></td>
                                </tr>
                                <tr>
                                    <td style="padding:7px 0;color:var(--hms-text-muted)">Ngày sinh</td>
                                    <td style="padding:7px 0">
                                        <c:choose>
                                            <c:when test="${not empty user.dob}"><c:out value="${user.dob}"/></c:when>
                                            <c:otherwise><em class="text-muted">—</em></c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:7px 0;color:var(--hms-text-muted)">CCCD</td>
                                    <td style="padding:7px 0;font-family:var(--hms-font-mono)">
                                        <c:choose>
                                            <c:when test="${not empty user.identityNumber}"><c:out value="${user.identityNumber}"/></c:when>
                                            <c:otherwise><em class="text-muted">—</em></c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:7px 0;color:var(--hms-text-muted)">Giới tính</td>
                                    <td style="padding:7px 0">
                                        <c:choose>
                                            <c:when test="${user.gender == 'MALE'}">Nam</c:when>
                                            <c:when test="${user.gender == 'FEMALE'}">Nữ</c:when>
                                            <c:when test="${user.gender == 'OTHER'}">Khác</c:when>
                                            <c:otherwise><em class="text-muted">—</em></c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:7px 0;color:var(--hms-text-muted)">Email</td>
                                    <td style="padding:7px 0"><c:out value="${user.email}"/></td>
                                </tr>
                                <tr>
                                    <td style="padding:7px 0;color:var(--hms-text-muted)">Số điện thoại</td>
                                    <td style="padding:7px 0"><c:out value="${user.phone}"/></td>
                                </tr>
                                <tr>
                                    <td style="padding:7px 0;color:var(--hms-text-muted)">Địa chỉ thường trú</td>
                                    <td style="padding:7px 0;font-size:0.8125rem">
                                        <c:choose>
                                            <c:when test="${not empty user.permanentAddress}"><c:out value="${user.permanentAddress}"/></c:when>
                                            <c:otherwise><em class="text-muted">—</em></c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Tài khoản & Phân công -->
                <div class="col-lg-6">
                    <div class="widget-surface mb-3">
                        <div class="widget-surface-header"><h3>Tài khoản</h3></div>
                        <div class="widget-surface-body">
                            <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                                <tr>
                                    <td style="padding:7px 0;color:var(--hms-text-muted);width:42%">Tên đăng nhập</td>
                                    <td style="padding:7px 0;font-family:var(--hms-font-mono)">
                                        <c:out value="${user.username}"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:7px 0;color:var(--hms-text-muted)">Vai trò</td>
                                    <td style="padding:7px 0">
                                        <c:choose>
                                            <c:when test="${user.role == 'MANAGER'}">
                                                <span class="badge-hms badge-info">Ban Quản lý</span>
                                            </c:when>
                                            <c:when test="${user.role == 'OPERATOR'}">
                                                <span class="badge-hms badge-neutral">Nhân viên vận hành</span>
                                            </c:when>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:7px 0;color:var(--hms-text-muted)">Trạng thái</td>
                                    <td style="padding:7px 0">
                                        <c:choose>
                                            <c:when test="${user.status == 'ACTIVE'}">
                                                <span class="badge-hms badge-success">Hoạt động</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-hms badge-danger">Bị khóa</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:7px 0;color:var(--hms-text-muted)">Đổi mật khẩu lần đầu</td>
                                    <td style="padding:7px 0">
                                        <c:choose>
                                            <c:when test="${user.forceChangePass}">
                                                <span class="badge-hms badge-warning">Chưa đổi</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-hms badge-success">Đã đổi</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:7px 0;color:var(--hms-text-muted)">Ngày tạo</td>
                                    <td style="padding:7px 0;font-size:0.8125rem"><fmt:formatDate value="${user.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                                </tr>
                                <tr>
                                    <td style="padding:7px 0;color:var(--hms-text-muted)">Cập nhật</td>
                                    <td style="padding:7px 0;font-size:0.8125rem"><fmt:formatDate value="${user.updatedAtAsDate}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
                                </tr>
                            </table>
                        </div>
                    </div>

                    <!-- Cơ sở phụ trách -->
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Cơ sở phụ trách</h3>
                            <a href="${ctx}/admin/personnel/${user.id}/edit"
                               style="font-size:0.8125rem;color:var(--hms-accent-deep);font-weight:600;text-decoration:none">
                                Cập nhật →
                            </a>
                        </div>
                        <div class="widget-surface-body">
                            <c:choose>
                                <c:when test="${not empty user.facilityNames}">
                                    <ul style="list-style:none;padding:0;margin:0">
                                        <c:forEach var="fname" items="${user.facilityNames}">
                                            <li style="padding:7px 0;border-bottom:1px solid var(--hms-border-soft);
                                                       font-size:0.875rem;display:flex;align-items:center;gap:8px">
                                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                                                     stroke="var(--hms-accent)" stroke-width="2">
                                                    <path d="M3 21h18M5 21V7l8-4v18M19 21V11l-6-4"/>
                                                </svg>
                                                <c:out value="${fname}"/>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:when>
                                <c:otherwise>
                                    <p class="text-muted" style="font-size:0.875rem;margin:0">
                                        Chưa được phân công cơ sở.
                                    </p>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
