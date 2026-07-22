<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  Fragment: _notif-badges.jsp
  Gộp badge trạng thái và badge đối tượng nhận thành 1 file.

  Tham số:
    type          — "status" hoặc "recipient"
    statusValue   — (khi type=status)   SENT | DRAFT
    recipientType — (khi type=recipient) ALL | FACILITY | ROOM
    recipientId   — (khi type=recipient, tuỳ chọn) id cơ sở hoặc phòng
--%>
<c:choose>

    <%-- Badge trạng thái --%>
    <c:when test="${param.type == 'status'}">
        <c:choose>
            <c:when test="${param.statusValue == 'SENT'}">
                <span class="badge-hms badge-success">Đã gửi</span>
            </c:when>
            <c:otherwise>
                <span class="badge-hms badge-warning">Nháp</span>
            </c:otherwise>
        </c:choose>
    </c:when>

    <%-- Badge đối tượng nhận --%>
    <c:when test="${param.type == 'recipient'}">
        <c:choose>
            <c:when test="${param.recipientType == 'ALL'}">
                <span class="badge-hms badge-info">Tất cả</span>
            </c:when>
            <c:when test="${param.recipientType == 'FACILITY'}">
                <span class="badge-hms badge-neutral">Cơ sở<c:if test="${not empty param.recipientId}"> #<c:out value="${param.recipientId}"/></c:if></span>
            </c:when>
            <c:when test="${param.recipientType == 'ROOM'}">
                <span class="badge-hms badge-neutral">Phòng<c:if test="${not empty param.recipientId}"> #<c:out value="${param.recipientId}"/></c:if></span>
            </c:when>
        </c:choose>
    </c:when>

</c:choose>
