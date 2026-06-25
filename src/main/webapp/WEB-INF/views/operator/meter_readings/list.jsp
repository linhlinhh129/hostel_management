<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp">
        <jsp:param name="title" value="Danh sách điện nước" />
    </jsp:include>
    <link href="https://fonts.googleapis.com/css2?family=Geist+Mono:wght@400;500&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <style>
        .page-content { margin: auto; }
        .table-responsive { overflow-x: auto; }
        .custom-table th { 
            font-weight: 600; 
            color: var(--color-ink); 
            background-color: var(--color-surface-soft); 
            border-bottom: 1px solid var(--color-hairline-soft);
            padding: 12px 16px;
            font-size: 14px;
        }
        .custom-table td { 
            color: var(--color-steel); 
            vertical-align: middle; 
            padding: 12px 16px;
            font-size: 14px;
        }
    </style>
</head>
<body id="page-top">
    <div class="app-shell">
        <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
        <div class="sidebar-overlay"></div>
        <div class="main-wrapper">
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
            <main class="page-content">
                    <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

                    <!-- Header -->
                    <div class="page-header hero-sky-gradient" style="border-radius: var(--hms-radius-lg, 12px); margin-bottom: 1.75rem;">
                        <h1>Danh sách điện nước</h1>
                        <p>Kỳ đo đạc tháng ${currentMonth}/${currentYear}</p>
                    </div>

                    <!-- Filter -->
                    <form action="${pageContext.request.contextPath}/operator/meter-readings" method="get" class="mb-4">
                        <div class="row g-3">
                            <div class="col-md-4">
                                <select name="facility" class="mintlify-text-input" style="height: 38px;">
                                    <option value="" ${empty selectedFacility ? 'selected' : ''}>-- Chọn cơ sở --</option>
                                    <c:forEach var="f" items="${facilities}">
                                        <c:set var="facVal" value="${f.name} (${f.code})"/>
                                        <option value="${facVal}" ${selectedFacility == facVal ? 'selected' : ''}><c:out value="${facVal}"/></option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-4">
                                <input type="text" name="roomCode" class="mintlify-text-input" placeholder="Nhập mã phòng..." value="${searchRoomCode}" style="height: 38px;">
                            </div>
                            <div class="col-md-4">
                                <button type="submit" class="btn-mintlify-primary w-100" style="height: 38px; padding: 0; justify-content: center;">Lọc danh sách</button>
                            </div>
                        </div>
                    </form>

                    <!-- Main Content -->
                    <div class="data-surface mb-4">
                        <div class="table-responsive">
                            <table class="table table-hover custom-table mt-3">
                                <thead>
                                    <tr>
                                        <th>Mã phòng</th>
                                        <th>Số điện</th>
                                        <th>Số nước</th>
                                        <th class="d-none d-md-table-cell">Cập nhật lúc</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:choose>
                                        <c:when test="${empty meterList}">
                                            <tr>
                                                <td colspan="6" class="text-center py-4">Không có dữ liệu hiển thị.</td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="item" items="${meterList}">
                                                <tr>
                                                    <td style="font-weight: 500; color: var(--color-ink);">${item.roomCode}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty item.currentElectricReading}">
                                                                <span style="font-weight: 600; color: var(--color-ink);">${item.currentElectricReading}</span>
                                                            </c:when>
                                                            <c:otherwise>-</c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty item.currentWaterReading}">
                                                                <span style="font-weight: 600; color: var(--color-ink);">${item.currentWaterReading}</span>
                                                            </c:when>
                                                            <c:otherwise>-</c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td class="d-none d-md-table-cell">
                                                        <c:choose>
                                                            <c:when test="${not empty item.updatedAt}">
                                                                <fmt:formatDate value="${item.updatedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                            </c:when>
                                                            <c:otherwise>-</c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${item.status == 'DA_CAP_NHAT'}">
                                                                <span class="mintlify-badge-status-inprogress">ĐÃ CẬP NHẬT</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="mintlify-badge-status-pending">CHƯA CẬP NHẬT</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:if test="${item.status != 'DA_CAP_NHAT'}">
                                                            <a href="${pageContext.request.contextPath}/operator/meter-readings/update?roomCode=${item.roomCode}"
                                                               class="btn-mintlify-primary btn-sm px-3 py-1 text-decoration-none"
                                                               style="font-size:13px; display:inline-flex; align-items:center;">
                                                                Cập nhật
                                                            </a>
                                                        </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:otherwise>
                                    </c:choose>
                                </tbody>
                            </table>
                        </div>
                    </div>

            </main>
            <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
        </div>
    </div>
</body>
</html>
