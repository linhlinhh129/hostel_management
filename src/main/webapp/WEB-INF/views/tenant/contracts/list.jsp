<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="Hợp đồng của tôi" scope="request" />
<c:set var="pageRole" value="TENANT" scope="request" />
<c:set var="activeMenu" value="contracts" scope="request" />

<jsp:include page="/WEB-INF/views/layout/head.jsp" />
<jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
<jsp:include page="/WEB-INF/views/layout/topbar.jsp" />

<div class="content-wrapper">
    <div class="page-header d-flex justify-content-between align-items-center">
        <div>
            <h1 class="page-title">Hợp đồng của tôi</h1>
            <p class="text-muted">Xem danh sách các hợp đồng thuê phòng của bạn</p>
        </div>
    </div>

    <jsp:include page="/WEB-INF/views/layout/inline_alerts.jsp" />

    <div class="card mt-4">
        <div class="card-body">
            <c:choose>
                <c:when test="${empty contracts}">
                    <div class="empty-state">
                        <div class="empty-state-icon">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                                <polyline points="14 2 14 8 20 8" />
                                <line x1="16" y1="13" x2="8" y2="13" />
                                <line x1="16" y1="17" x2="8" y2="17" />
                            </svg>
                        </div>
                        <h3>Chưa có hợp đồng nào</h3>
                        <p class="text-muted">Bạn chưa có hợp đồng thuê nào trong hệ thống.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Mã hợp đồng</th>
                                    <th>Mã phòng</th>
                                    <th>Ngày bắt đầu</th>
                                    <th>Ngày hết hạn</th>
                                    <th>Tiền thuê</th>
                                    <th>Tiền cọc</th>
                                    <th>Trạng thái</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="c" items="${contracts}">
                                    <tr>
                                        <td><strong><c:out value="${c.code}" /></strong></td>
                                        <td>
                                            <span class="badge bg-soft-primary text-primary">
                                                <c:out value="${c.room != null ? c.room.code : c.roomId}" />
                                            </span>
                                        </td>
                                        <td>
                                            <fmt:parseDate value="${c.startDate}" pattern="yyyy-MM-dd" var="sDate" type="date" />
                                            <fmt:formatDate value="${sDate}" pattern="dd/MM/yyyy" />
                                        </td>
                                        <td>
                                            <fmt:parseDate value="${c.endDate}" pattern="yyyy-MM-dd" var="eDate" type="date" />
                                            <fmt:formatDate value="${eDate}" pattern="dd/MM/yyyy" />
                                        </td>
                                        <td>
                                            <c:if test="${c.room != null}">
                                                <fmt:formatNumber value="${c.room.roomFee}" pattern="#,###" /> đ
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${c.room != null}">
                                                <fmt:formatNumber value="${c.room.depositAmount}" pattern="#,###" /> đ
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${c.status == 'ACTIVE'}">
                                                    <span class="badge bg-soft-success text-success">Đang hiệu lực</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-soft-secondary text-secondary">Đã kết thúc</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <a href="${ctx}/tenant/contracts?id=${c.contractId}" class="btn btn-sm btn-outline-primary">
                                                <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" class="me-1">
                                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                                                    <circle cx="12" cy="12" r="3" />
                                                </svg>
                                                Chi tiết
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
