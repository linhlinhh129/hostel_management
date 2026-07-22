<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  Fragment: _personnel-badges.jsp
  Gộp badge vai trò và badge trạng thái thành 1 file.

  Tham số:
    type        — "role" hoặc "status"
    roleValue   — (khi type=role)   MANAGER | OPERATOR
    statusValue — (khi type=status) ACTIVE | INACTIVE | LOCKED | ...
--%>
<c:choose>

    <%-- Badge vai trò --%>
    <c:when test="${param.type == 'role'}">
        <c:choose>
            <c:when test="${param.roleValue == 'MANAGER'}">
                <span class="badge-hms badge-info">Ban Quản lý</span>
            </c:when>
            <c:when test="${param.roleValue == 'OPERATOR'}">
                <span class="badge-hms badge-neutral">Nhân viên vận hành</span>
            </c:when>
            <c:otherwise>
                <span class="badge-hms badge-neutral"><c:out value="${param.roleValue}"/></span>
            </c:otherwise>
        </c:choose>
    </c:when>

    <%-- Badge trạng thái --%>
    <c:when test="${param.type == 'status'}">
        <c:choose>
            <c:when test="${param.statusValue == 'ACTIVE'}">
                <span class="badge-hms badge-success">Hoạt động</span>
            </c:when>
            <c:otherwise>
                <span class="badge-hms badge-danger">Ngừng hoạt động</span>
            </c:otherwise>
        </c:choose>
    </c:when>

</c:choose>
