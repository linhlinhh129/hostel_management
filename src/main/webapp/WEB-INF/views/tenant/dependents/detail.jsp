<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết người phụ thuộc - Cổng cư dân"/>
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
            
            <div class="page-header hero-sky-gradient">
                <div class="d-flex justify-content-between align-items-center flex-wrap">
                    <div>
                        <h1>Hồ sơ thành viên</h1>
                        <p>Chi tiết thông tin người phụ thuộc</p>
                    </div>
                    <div>
                        <a href="${ctx}/tenant/dependents" class="btn-mintlify-secondary text-decoration-none">
                            ← Danh sách thành viên
                        </a>
                    </div>
                </div>
            </div>

            <div class="widget-surface" style="max-width: 600px;">
                <div class="widget-surface-header">
                    <h3>Thông tin cơ bản</h3>
                </div>
                <div class="widget-surface-body p-0">
                    <table class="table-mintlify" style="font-size: 0.9375rem;">
                        <tbody>
                            <tr>
                                <td style="padding: 1rem 1.5rem; color: var(--hms-stone); width: 40%;">Họ và tên</td>
                                <td style="padding: 1rem 1.5rem; font-weight: 700; color: var(--hms-ink);"><c:out value="${dependent.fullName}"/></td>
                            </tr>
                            <tr>
                                <td style="padding: 1rem 1.5rem; color: var(--hms-stone);">Ngày sinh</td>
                                <td style="padding: 1rem 1.5rem; font-weight: 600;"><c:out value="${dependent.dobLabel}"/></td>
                            </tr>
                            <tr>
                                <td style="padding: 1rem 1.5rem; color: var(--hms-stone);">Số điện thoại</td>
                                <td style="padding: 1rem 1.5rem; font-weight: 600;"><c:out value="${dependent.phone}"/></td>
                            </tr>
                            <tr>
                                <td style="padding: 1rem 1.5rem; color: var(--hms-stone);">CCCD/CMND</td>
                                <td style="padding: 1rem 1.5rem; font-weight: 600;"><c:out value="${dependent.maskedIdentityNumber}"/></td>
                            </tr>
                            <tr>
                                <td style="padding: 1rem 1.5rem; color: var(--hms-stone);">Mối quan hệ</td>
                                <td style="padding: 1rem 1.5rem;">
                                    <span class="badge-hms badge-neutral" style="font-size: 0.8125rem;"><c:out value="${dependent.relationship}"/></span>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
