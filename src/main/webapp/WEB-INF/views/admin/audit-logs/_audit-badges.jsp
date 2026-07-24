<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
  Fragment: _audit-badges.jsp
  Gộp badge hành động và nhãn đối tượng thành 1 file.

  Tham số:
    type            — "action" hoặc "entity"
    actionValue     — (khi type=action)  giá trị action của audit log
    entityTypeValue — (khi type=entity)  giá trị entityType của audit log
--%>
<c:choose>

    <%-- Badge hành động --%>
    <c:when test="${param.type == 'action'}">
        <c:choose>
            <c:when test="${param.actionValue == 'CREATE' or param.actionValue == 'CREATE_EMPLOYEE'}">
                <span class="badge-hms badge-success">
                    <c:choose>
                        <c:when test="${param.actionValue == 'CREATE'}">Tạo mới</c:when>
                        <c:otherwise>Tạo nhân sự</c:otherwise>
                    </c:choose>
                </span>
            </c:when>
            <c:when test="${param.actionValue == 'DELETE' or param.actionValue == 'LOCK_EMPLOYEE'
                            or param.actionValue == 'DEACTIVATE' or param.actionValue == 'DELETE_EMPLOYEE'}">
                <span class="badge-hms badge-danger">
                    <c:choose>
                        <c:when test="${param.actionValue == 'DELETE'}">Xóa</c:when>
                        <c:when test="${param.actionValue == 'DELETE_EMPLOYEE'}">Xóa nhân sự</c:when>
                        <c:when test="${param.actionValue == 'LOCK_EMPLOYEE'}">Khóa tài khoản</c:when>
                        <c:otherwise>Vô hiệu hóa</c:otherwise>
                    </c:choose>
                </span>
            </c:when>
            <c:when test="${param.actionValue == 'UPDATE' or param.actionValue == 'UPDATE_STATUS'
                            or param.actionValue == 'UPDATE_AREA' or param.actionValue == 'UPDATE_EMPLOYEE'
                            or param.actionValue == 'UPDATE_ELECTRICITY' or param.actionValue == 'UPDATE_WATER'}">
                <span class="badge-hms badge-info">
                    <c:choose>
                        <c:when test="${param.actionValue == 'UPDATE'}">Cập nhật</c:when>
                        <c:when test="${param.actionValue == 'UPDATE_STATUS'}">Đổi trạng thái</c:when>
                        <c:when test="${param.actionValue == 'UPDATE_AREA'}">Cập nhật diện tích</c:when>
                        <c:when test="${param.actionValue == 'UPDATE_ELECTRICITY'}">Cập nhật số điện</c:when>
                        <c:when test="${param.actionValue == 'UPDATE_WATER'}">Cập nhật số nước</c:when>
                        <c:otherwise>Sửa nhân sự</c:otherwise>
                    </c:choose>
                </span>
            </c:when>
            <c:when test="${param.actionValue == 'ACTIVATE' or param.actionValue == 'UNLOCK_EMPLOYEE'}">
                <span class="badge-hms badge-accent">
                    <c:choose>
                        <c:when test="${param.actionValue == 'ACTIVATE'}">Kích hoạt</c:when>
                        <c:otherwise>Mở khóa</c:otherwise>
                    </c:choose>
                </span>
            </c:when>
            <c:otherwise>
                <span class="badge-hms badge-neutral"><c:out value="${param.actionValue}"/></span>
            </c:otherwise>
        </c:choose>
    </c:when>

    <%-- Nhãn đối tượng --%>
    <c:when test="${param.type == 'entity'}">
        <c:choose>
            <c:when test="${param.entityTypeValue == 'facilities'}">Cơ sở</c:when>
            <c:when test="${param.entityTypeValue == 'rooms'}">Phòng</c:when>
            <c:when test="${param.entityTypeValue == 'users'}">Nhân sự</c:when>
            <c:when test="${param.entityTypeValue == 'notifications'}">Thông báo</c:when>
            <c:when test="${param.entityTypeValue == 'invoices'}">Hóa đơn</c:when>
            <c:when test="${param.entityTypeValue == 'payments'}">Thanh toán</c:when>
            <c:when test="${param.entityTypeValue == 'requests'}">Yêu cầu</c:when>
            <c:when test="${param.entityTypeValue == 'meter_readings'}">Số điện nước</c:when>
            <c:otherwise>Hệ thống</c:otherwise>
        </c:choose>
    </c:when>

</c:choose>
