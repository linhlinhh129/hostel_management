<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Lịch sử giá dịch vụ"/>
<c:set var="pageRole" value="MANAGER"/>
<c:set var="activeMenu" value="service-prices"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3">
                <div>
                    <a href="${ctx}/manager/service-prices" class="text-decoration-none text-muted mb-2 d-inline-block">
                        &larr; Quay lại danh sách
                    </a>
                    <h1>Lịch sử thay đổi giá</h1>
                    <p>
                        Loại phí: 
                        <strong>
                            <c:choose>
                                <c:when test="${priceType == 'ELECTRICITY'}">Giá điện</c:when>
                                <c:when test="${priceType == 'WATER'}">Giá nước</c:when>
                                <c:when test="${priceType == 'SERVICE_FEE'}">Phí dịch vụ</c:when>
                                <c:when test="${priceType == 'INTERNET'}">Phí Internet</c:when>
                                <c:otherwise><c:out value="${priceType}"/></c:otherwise>
                            </c:choose>
                        </strong>
                    </p>
                </div>
            </div>

            <div class="data-surface">
                <div class="table-responsive">
                    <table class="table-mintlify mb-0">
                    <thead>
                        <tr>
                            <th class="text-center">Ngày thay đổi</th>
                            <th class="text-center">Giá cũ</th>
                            <th class="text-center">Giá mới</th>
                            <th class="text-center">Người thay đổi</th>
                            <th class="text-center">Ghi chú</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty historyList}">
                                <c:forEach var="item" items="${historyList}">
                                    <tr>
                                        <td class="text-center align-middle"><c:out value="${item.changedAt}"/></td>
                                        <td class="text-center align-middle" style="color: var(--hms-text-muted);">
                                            <c:choose>
                                                <c:when test="${item.oldPrice != null}">
                                                    <fmt:formatNumber value="${item.oldPrice}" groupingUsed="true"/>
                                                </c:when>
                                                <c:otherwise>0</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-center align-middle" style="font-weight: 600; color: var(--hms-primary-color, #10b981);">
                                            <fmt:formatNumber value="${item.newPrice}" groupingUsed="true"/>
                                        </td>
                                        <td class="text-center align-middle"><c:out value="${item.changedByName}"/></td>
                                        <td class="text-center align-middle"><c:out value="${item.note}"/></td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="5" class="text-center text-muted">Chưa có lịch sử thay đổi nào</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
                </div>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
