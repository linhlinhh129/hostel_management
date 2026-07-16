<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Danh sách yêu cầu sửa chữa"/>
<c:set var="pageRole" value="OPERATOR"/>
<c:set var="activeMenu" value="tickets"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <h1>Yêu cầu sửa chữa</h1>
                <p>Danh sách các yêu cầu sửa chữa từ khách thuê</p>
            </div>

            <%-- font: Inter toàn trang, Geist Mono riêng cột mã --%>
            <style>
                .page-content, .page-content th,
                .page-content td,
                .page-content input, .page-content select, .page-content button {
                    font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
                }
                .page-content td.col-code,
                .page-content td.col-code * {
                    font-family: 'Geist Mono', 'JetBrains Mono', monospace !important;
                }
            </style>

            <div class="data-surface">
                <%-- Filter bar --%>
                <form method="GET" action="${ctx}/operator/requests"
                      style="background:#fff; border:1px solid var(--hms-border-soft); border-radius:8px; padding:20px; margin-bottom:20px; box-shadow:0 1px 3px rgba(0,0,0,0.02)">
                    <div style="display:flex; flex-wrap:wrap; gap:20px; margin-bottom:20px;">
                        <div style="flex:2; min-width:160px;">
                            <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Trạng thái</label>
                            <select name="status" class="form-select" style="width:100%">
                                <option value="">Tất cả</option>
                                <option value="PENDING"     ${paramStatus == 'PENDING'     ? 'selected' : ''}>Chờ xử lý</option>
                                <option value="IN_PROGRESS" ${paramStatus == 'IN_PROGRESS' ? 'selected' : ''}>Đang xử lý</option>
                                <option value="COMPLETED"   ${paramStatus == 'COMPLETED'   ? 'selected' : ''}>Đã hoàn thành</option>
                                <option value="REJECTED"    ${paramStatus == 'REJECTED'    ? 'selected' : ''}>Đã từ chối</option>
                            </select>
                        </div>
                        <div style="flex:2; min-width:160px;">
                            <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Thể loại</label>
                            <select name="category" class="form-select" style="width:100%">
                                <option value="">Tất cả</option>
                                <c:forEach var="cat" items="${availableCategories}">
                                    <c:if test="${cat != 'Khác'}">
                                        <option value="${cat}" ${paramCategory == cat ? 'selected' : ''}>
                                            <c:choose>
                                                <c:when test="${cat == 'ELECTRIC'}">Điện</c:when>
                                                <c:when test="${cat == 'WATER'}">Nước</c:when>
                                                <c:when test="${cat == 'INTERNET'}">Internet</c:when>
                                                <c:when test="${cat == 'INFRASTRUCTURE'}">Cơ sở vật chất</c:when>
                                                <c:when test="${cat == 'MAINTENANCE'}">Bảo trì</c:when>
                                                <c:when test="${cat == 'CLEANING'}">Vệ sinh</c:when>
                                                <c:when test="${cat == 'COMPLAINT'}">Khiếu nại</c:when>
                                                <c:when test="${cat == 'OTHER'}">Khác</c:when>
                                                <c:otherwise><c:out value="${cat}"/></c:otherwise>
                                            </c:choose>
                                        </option>
                                    </c:if>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div style="display:flex; justify-content:flex-end; gap:12px; border-top:1px dashed var(--hms-border-soft); padding-top:16px;">
                        <a href="${ctx}/operator/requests"
                           style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); text-decoration:none; font-size:14px; font-weight:500;">Xóa bộ lọc</a>
                        <button type="submit"
                                style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); font-size:14px; font-weight:500; cursor:pointer;">Tìm kiếm</button>
                    </div>
                </form>

                <div class="table-responsive">
                    <table class="table-mintlify">
                        <thead>
                            <tr>
                                <th class="d-none d-md-table-cell">Mã YC</th>
                                <th>Tiêu đề</th>
                                <th class="d-none d-md-table-cell">Phòng</th>
                                <th class="d-none d-lg-table-cell">Thể loại</th>
                                <th class="d-none d-md-table-cell">Ngày tạo</th>
                                <th>Trạng thái</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty requestList}">
                                    <tr>
                                        <td colspan="6" class="text-center py-5" style="color:var(--hms-stone);">
                                            Không có yêu cầu nào phù hợp.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="req" items="${requestList}" varStatus="loop">
                                        <tr data-href="${ctx}/operator/requests/detail?id=${req.requestId}">
                                            <%-- col-code: Geist Mono --%>
                                            <td class="d-none d-md-table-cell col-code" style="font-size:13px; color:var(--hms-stone);">
                                                <c:set var="facCode" value="CG"/>
                                                <c:if test="${not empty req.roomCode}">
                                                    <c:set var="facCode" value="${fn:substring(req.roomCode, 0, 2)}"/>
                                                </c:if>
                                                REQ-${facCode}-<fmt:formatNumber value="${(currentPage != null ? (currentPage-1)*20 : 0) + loop.count}" pattern="0001"/>
                                            </td>
                                            <td style="font-weight:500; color:var(--hms-ink); max-width:200px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;">
                                                <c:out value="${req.title}"/>
                                            </td>
                                            <td class="d-none d-md-table-cell">
                                                <c:choose>
                                                    <c:when test="${not empty req.roomCode}">
                                                        <span style="font-weight:600; color:var(--hms-ink);">P.<c:out value="${req.roomCode}"/></span>
                                                        <c:if test="${not empty req.facilityName}">
                                                            <span style="color:var(--hms-stone); font-size:12px;"> (<c:out value="${req.facilityName}"/>)</span>
                                                        </c:if>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span style="color:var(--hms-stone); font-size:12px;">—</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="d-none d-lg-table-cell">
                                                <span class="badge-hms badge-neutral" style="text-transform:uppercase;">
                                                    <c:choose>
                                                        <c:when test="${req.category == 'MAINTENANCE'}">BẢO TRÌ</c:when>
                                                        <c:when test="${req.category == 'CLEANING'}">VỆ SINH</c:when>
                                                        <c:when test="${req.category == 'COMPLAINT'}">KHIẾU NẠI</c:when>
                                                        <c:when test="${req.category == 'OTHER'}">KHÁC</c:when>
                                                        <c:otherwise><c:out value="${req.category}"/></c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </td>
                                            <td class="d-none d-md-table-cell" style="font-size:0.8125rem; color:var(--hms-stone);">
                                                <fmt:formatDate value="${req.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${req.status == 'PENDING'}">    <span class="badge-hms badge-info">CHỜ XỬ LÝ</span></c:when>
                                                    <c:when test="${req.status == 'RECEIVED'}">   <span class="badge-hms badge-info">ĐÃ TIẾP NHẬN</span></c:when>
                                                    <c:when test="${req.status == 'ASSIGNED'}">   <span class="badge-hms badge-neutral">ĐÃ PHÂN CÔNG</span></c:when>
                                                    <c:when test="${req.status == 'IN_PROGRESS'}"><span class="badge-hms badge-warning">ĐANG XỬ LÝ</span></c:when>
                                                    <c:when test="${req.status == 'COMPLETED' or req.status == 'DONE' or req.status == 'RESOLVED'}">
                                                                                                  <span class="badge-hms badge-success">HOÀN THÀNH</span></c:when>
                                                    <c:when test="${req.status == 'REJECTED' or req.status == 'CANCELLED'}">
                                                                                                  <span class="badge-hms badge-danger">TỪ CHỐI</span></c:when>
                                                    <c:otherwise><span class="badge-hms badge-neutral"><c:out value="${req.status}"/></span></c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <%-- table-footer: luôn hiển thị, đồng bộ pattern admin --%>
                <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2 flex-wrap gap-2">
                    <span class="text-muted" style="font-size:0.875rem">
                        Tổng <fmt:formatNumber value="${totalRecords}" groupingUsed="true"/> yêu cầu
                        · Trang ${currentPage} / ${totalPages}
                    </span>
                    <div class="d-flex gap-1">
                        <c:if test="${currentPage > 1}">
                            <a href="?page=${currentPage-1}&status=${paramStatus}&category=${paramCategory}"
                               class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                        </c:if>
                        <c:if test="${currentPage < totalPages}">
                            <a href="?page=${currentPage+1}&status=${paramStatus}&category=${paramCategory}"
                               class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                        </c:if>
                    </div>
                </div>

            </div><%-- /data-surface --%>

        </main>
        <jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
    </div>
</div>
</body>
</html>
