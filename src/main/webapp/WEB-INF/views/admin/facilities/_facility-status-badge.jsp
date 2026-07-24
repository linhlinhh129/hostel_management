<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  Fragment: _facility-status-badge.jsp
  Tham số: statusValue (qua jsp:param) — giá trị facility.status
--%>
<c:choose>
    <c:when test="${param.statusValue == 'ACTIVE'}">
        <span class="badge-hms badge-success">Hoạt động</span>
    </c:when>
    <c:when test="${param.statusValue == 'DRAFT'}">
        <span class="badge-hms badge-warning">Chưa kích hoạt</span>
    </c:when>
    <c:otherwise>
        <span class="badge-hms badge-neutral">Vô hiệu</span>
    </c:otherwise>
</c:choose>
