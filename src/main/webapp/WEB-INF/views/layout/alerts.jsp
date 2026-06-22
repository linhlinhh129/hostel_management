<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%-- ── Flash messages từ session (sau redirect) ──────────────────────────── --%>
<c:if test="${not empty sessionScope.flashMessage}">
    <c:choose>
        <c:when test="${sessionScope.flashType == 'success'}">
            <div class="alert alert-success alert-dismissible fade show mb-3" role="alert">
                <c:out value="${sessionScope.flashMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
            </div>
        </c:when>
        <c:when test="${sessionScope.flashType == 'error'}">
            <div class="alert alert-danger alert-dismissible fade show mb-3" role="alert">
                <c:out value="${sessionScope.flashMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
            </div>
        </c:when>
        <c:when test="${sessionScope.flashType == 'warning'}">
            <div class="alert alert-warning alert-dismissible fade show mb-3" role="alert">
                <c:out value="${sessionScope.flashMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-info alert-dismissible fade show mb-3" role="alert">
                <c:out value="${sessionScope.flashMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
            </div>
        </c:otherwise>
    </c:choose>
    <%-- Xóa sau khi đã hiển thị để không lặp lại khi F5 --%>
    <c:remove var="flashMessage" scope="session"/>
    <c:remove var="flashType"    scope="session"/>
</c:if>

<%-- ── Request-scope messages (forward, không redirect) ──────────────────── --%>
<c:if test="${not empty successMessage}">
    <div class="alert alert-success alert-dismissible fade show mb-3" role="alert">
        <c:out value="${successMessage}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
    </div>
</c:if>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger alert-dismissible fade show mb-3" role="alert">
        <c:out value="${errorMessage}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
    </div>
</c:if>
<c:if test="${not empty warningMessage}">
    <div class="alert alert-warning alert-dismissible fade show mb-3" role="alert">
        <c:out value="${warningMessage}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
    </div>
</c:if>
