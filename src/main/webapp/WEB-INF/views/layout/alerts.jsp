<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger alert-dismissible fade show mb-3" role="alert">
        <c:out value="${errorMessage}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
    </div>
</c:if>
<c:if test="${not empty successMessage}">
    <div class="alert alert-success alert-dismissible fade show mb-3" role="alert">
        <c:out value="${successMessage}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
    </div>
</c:if>
<c:if test="${not empty warningMessage}">
    <div class="alert alert-warning alert-dismissible fade show mb-3" role="alert">
        <c:out value="${warningMessage}"/>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
    </div>
</c:if>
<c:if test="${not empty flashMessage}">
    <c:choose>
        <c:when test="${flashType == 'success'}">
            <div class="alert alert-success alert-dismissible fade show mb-3" role="alert">
                <c:out value="${flashMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
            </div>
        </c:when>
        <c:otherwise>
            <div class="alert alert-danger alert-dismissible fade show mb-3" role="alert">
                <c:out value="${flashMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Đóng"></button>
            </div>
        </c:otherwise>
    </c:choose>
</c:if>
