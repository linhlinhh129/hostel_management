<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="role" value="${not empty sessionScope.currentUser.role ? sessionScope.currentUser.role : pageRole}"/>
<c:set var="displayName" value="${not empty sessionScope.currentUser.fullName ? sessionScope.currentUser.fullName : 'Người dùng'}"/>
<c:set var="roleLabel" value="${not empty sessionScope.currentUser.roleLabel ? sessionScope.currentUser.roleLabel : ''}"/>
<c:if test="${empty roleLabel}">
    <c:choose>
        <c:when test="${role == 'ADMIN'}"><c:set var="roleLabel" value="Quản trị viên"/></c:when>
        <c:when test="${role == 'MANAGER'}"><c:set var="roleLabel" value="Ban Quản lý"/></c:when>
        <c:when test="${role == 'TENANT'}"><c:set var="roleLabel" value="Người thuê"/></c:when>
        <c:when test="${role == 'OPERATOR'}"><c:set var="roleLabel" value="Nhân viên vận hành"/></c:when>
    </c:choose>
</c:if>
<c:set var="initials" value="${not empty sessionScope.currentUser.initials ? sessionScope.currentUser.initials : 'U'}"/>

<header class="topbar">
    <button type="button" class="topbar-toggle" aria-label="Menu">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
    </button>
    <nav class="breadcrumb-nav" aria-label="breadcrumb">
        <ol>
            <c:if test="${not empty breadcrumbItems}">
                <c:forEach var="crumb" items="${breadcrumbItems}" varStatus="st">
                    <c:choose>
                        <c:when test="${st.last}">
                            <li class="active"><c:out value="${crumb.label}"/></li>
                        </c:when>
                        <c:otherwise>
                            <li><a href="${ctx}${crumb.url}"><c:out value="${crumb.label}"/></a></li>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </c:if>
            <c:if test="${empty breadcrumbItems}">
                <li class="active"><c:out value="${pageHeading != null ? pageHeading : ''}"/></li>
            </c:if>
        </ol>
    </nav>
    <div class="topbar-actions">
        <div class="user-menu">
            <div class="user-avatar"><c:out value="${initials}"/></div>
            <div class="user-info">
                <span><c:out value="${displayName}"/></span>
                <small><c:out value="${roleLabel}"/></small>
            </div>
            <!-- Dropdown Menu -->
            <div class="dropdown-menu">
                <a href="${ctx}/profile" class="dropdown-item">Hồ sơ cá nhân</a>
                <div class="dropdown-divider"></div>
                <a href="${ctx}/logout" class="dropdown-item text-danger">Đăng xuất</a>
            </div>
        </div>
    </div>
</header>
