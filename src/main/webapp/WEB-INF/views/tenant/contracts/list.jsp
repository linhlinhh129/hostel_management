<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="Hợp đồng của tôi" scope="request" />
<c:set var="pageRole" value="TENANT" scope="request" />
<c:set var="activeMenu" value="contracts" scope="request" />

<jsp:include page="/WEB-INF/views/layout/head.jsp" />
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" /><div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

            <div class="page-header hero-sky-gradient" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="display:flex;justify-content:space-between;align-items:flex-end;flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>Hợp đồng của tôi</h1>
                        <p>Xem danh sách các hợp đồng thuê phòng của bạn</p>
                    </div>
                </div>
            </div>

            <jsp:include page="/WEB-INF/views/layout/inline_alerts.jsp" />

            <c:choose>
                <c:when test="${empty contracts}">
                    <jsp:include page="/WEB-INF/views/layout/fragments/empty-state.jsp">
                        <jsp:param name="message" value="Bạn chưa có hợp đồng thuê nào trong hệ thống."/>
                    </jsp:include>
                </c:when>
                <c:otherwise>
                    <div class="widget-surface">
                        <div class="widget-surface-header">
                            <h3>Danh sách hợp đồng</h3>
                        </div>
                        <div class="widget-surface-body p-0">
                            <div class="table-responsive">
                                <table class="table-mintlify table-hover">
                                    <thead>
                                        <tr>
                                            <th>Mã hợp đồng</th>
                                            <th>Mã phòng</th>
                                            <th>Ngày bắt đầu</th>
                                            <th>Ngày hết hạn</th>
                                            <th>Tiền thuê</th>
                                            <th>Tiền cọc</th>
                                            <th>Trạng thái</th>
                                            <th class="text-center">Hành động</th>
                                        </tr>
                                    </thead>
                                    <tbody id="tenantContractsTbody">
                                        <c:forEach var="c" items="${contracts}" varStatus="st">
                                            <tr style="animation:fadeInUp 0.4s ease ${st.index * 0.04}s both">
                                                <td style="font-weight:600;color:var(--hms-ink)">
                                                    <strong><c:out value="${c.code}" /></strong>
                                                </td>
                                                <td>
                                                    <span class="badge-hms badge-info">
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
                                                <td style="font-weight:600;color:var(--hms-ink)">
                                                    <c:if test="${c.room != null}">
                                                        <fmt:formatNumber value="${c.room.roomFee}" pattern="#,##0" /> đ
                                                    </c:if>
                                                </td>
                                                <td style="font-weight:600;color:var(--hms-stone)">
                                                    <c:if test="${c.room != null}">
                                                        <fmt:formatNumber value="${c.room.depositAmount}" pattern="#,##0" /> đ
                                                    </c:if>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${c.status == 'ACTIVE'}">
                                                            <span class="badge-hms badge-success">Đang hiệu lực</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge-hms badge-neutral">Đã kết thúc</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="text-center">
                                                    <a href="${ctx}/tenant/contracts?id=${c.contractId}" class="btn-mintlify-secondary" style="padding: 0.25rem 0.5rem; font-size: 0.75rem;">
                                                        Chi tiết
                                                    </a>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2" id="tenantContractsFooter">
                              <span class="text-muted" style="font-size:0.875rem">
                                Tổng <strong id="tenantContractsTotal"></strong> hợp đồng
                                · Trang <span id="tenantContractsPage">1</span> / <span id="tenantContractsTotalPages">1</span>
                              </span>
                              <div class="d-flex gap-1" id="tenantContractsBtns"></div>
                            </div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
<script>clientPaginate('tenantContractsTbody','tenantContractsTotal','tenantContractsPage','tenantContractsTotalPages','tenantContractsBtns');</script>
