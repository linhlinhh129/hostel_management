<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${empty param.message}">
    <p class="text-muted mb-0" style="font-size:0.875rem"><c:out value="Không có dữ liệu hiển thị."/></p>
</c:if>
<c:if test="${not empty param.message}">
    <p class="text-muted mb-0" style="font-size:0.875rem"><c:out value="${param.message}"/></p>
</c:if>
