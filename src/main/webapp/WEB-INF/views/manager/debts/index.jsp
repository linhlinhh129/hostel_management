<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <c:set var="ctx" value="${pageContext.request.contextPath}" />
            <c:set var="pageTitle" value="Quản lý công nợ" />
            <c:set var="pageRole" value="MANAGER" />
            <c:set var="activeMenu" value="debts" />
            <jsp:include page="/WEB-INF/views/layout/head.jsp" />

            <body>
                <div class="app-shell">
                    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
                    <div class="sidebar-overlay"></div>
                    <div class="main-wrapper">
                        <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
                        <main class="page-content">
                            <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

                            <div
                                class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3">
                                <div>
                                    <h1>Quản lý công nợ</h1>
                                    <p>Theo dõi các khoản thu chưa hoàn thành và tình trạng trễ hạn</p>
                                </div>
                            </div>



                            <div class="data-surface">
                                <form method="get" action="${ctx}/manager/debts" id="filterForm"
                                    style="background:#fff; border:1px solid var(--hms-border-soft); border-radius:8px; padding:20px; margin-bottom:20px; box-shadow:0 1px 3px rgba(0,0,0,0.02)">
                                    <div style="display:flex; flex-wrap:wrap; gap:20px; margin-bottom:20px;">
                                        <div style="flex:2; min-width:200px;">
                                            <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Tìm kiếm</label>
                                            <input type="text" class="form-control" name="keyword"
                                                value="<c:out value='${keyword}'/>" placeholder="Tìm mã HĐ, phòng..." style="width:100%"/>
                                        </div>
                                        <div style="flex:1; min-width:150px;">
                                            <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Trạng thái</label>
                                            <select class="form-select" name="status" style="width:100%">
                                                <option value="">Tất cả</option>
                                                <option value="UNPAID"  ${status=='UNPAID'  ? 'selected' : ''}>Chưa thanh toán</option>
                                                <option value="OVERDUE" ${status=='OVERDUE' ? 'selected' : ''}>Quá hạn</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div style="display:flex; justify-content:flex-end; gap:12px; border-top:1px dashed var(--hms-border-soft); padding-top:16px;">
                                        <a href="${ctx}/manager/debts"
                                           style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); text-decoration:none; font-size:14px; font-weight:500;">Xóa bộ lọc</a>
                                        <button type="submit"
                                                style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); font-size:14px; font-weight:500; cursor:pointer;">Tìm kiếm</button>
                                    </div>
                                </form>
                                <c:choose>
                                    <c:when test="${not empty debts}">
                                        <div class="table-responsive">
                                            <table class="table-mintlify mb-0">
                                                <thead>
                                                    <tr>
                                                        <th style="white-space: nowrap;">Mã HĐ</th>
                                                        <th>Phòng</th>
                                                        <th class="d-none d-md-table-cell">Người thuê</th>
                                                        <th class="d-none d-md-table-cell">Kỳ HĐ</th>
                                                        <th style="text-align:right; white-space: nowrap;">Tổng tiền
                                                        </th>
                                                        <th class="d-none d-md-table-cell"
                                                            style="text-align:right; white-space: nowrap;">Phí quá hạn
                                                        </th>
                                                        <th class="d-none d-md-table-cell">Hạn nộp</th>
                                                        <th>Trạng thái</th>
                                                        <th class="d-none d-md-table-cell">Thao tác</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="debt" items="${debts}">
                                                        <tr
                                                            data-href="${ctx}/manager/debts?action=detail&id=${debt.invoiceId}">
                                                            <td style="white-space: nowrap;">
                                                                <a href="${ctx}/manager/debts?action=detail&id=${debt.invoiceId}"
                                                                    style="font-weight:600">
                                                                    <c:out value="${debt.invoiceCode}" />
                                                                </a>
                                                            </td>
                                                            <td>
                                                                <span class="badge-hms badge-neutral">
                                                                    <c:out value="${debt.roomCode}" />
                                                                </span>
                                                            </td>
                                                            <td class="d-none d-md-table-cell">
                                                                <c:out value="${debt.tenantName}" />
                                                            </td>
                                                            <td class="d-none d-md-table-cell">
                                                                <c:out value="${debt.billingPeriod}" />
                                                            </td>
                                                            <td
                                                                style="text-align:right; font-weight:600; white-space: nowrap;">
                                                                <fmt:formatNumber value="${debt.invoiceTotalAmount}"
                                                                    pattern="#,##0" /> đ
                                                            </td>
                                                            <td class="d-none d-md-table-cell"
                                                                style="text-align:right; font-weight:600; color:var(--hms-danger-color, #ef4444); white-space: nowrap;">
                                                                <fmt:formatNumber value="${debt.lateFeePreview}"
                                                                    pattern="#,##0" /> đ
                                                            </td>
                                                            <td class="d-none d-md-table-cell">
                                                                <c:out value="${debt.dueDate}" />
                                                            </td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${debt.status == 'UNPAID'}">
                                                                        <span class="badge-hms badge-warning">Chưa thanh toán</span>
                                                                    </c:when>
                                                                    <c:when test="${debt.status == 'OVERDUE'}">
                                                                        <span class="badge-hms badge-danger">Quá hạn
                                                                            <c:if test="${debt.overdueDays > 0}">
                                                                                (${debt.overdueDays} ngày)
                                                                            </c:if>
                                                                        </span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="badge-hms badge-neutral">
                                                                            <c:out value="${debt.status}" />
                                                                        </span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td class="d-none d-md-table-cell" style="white-space: nowrap;">
                                                                <a href="${ctx}/manager/debts?action=detail&id=${debt.invoiceId}"
                                                                    class="btn-mintlify-secondary text-decoration-none"
                                                                    style="padding:4px 12px;font-size:0.8125rem">
                                                                    Xem
                                                                </a>
                                                                <c:if test="${debt.status == 'OVERDUE'}">
                                                                    <a href="${ctx}/manager/notifications/send-debt-reminder?invoiceId=${debt.invoiceId}"
                                                                        class="btn-mintlify-primary text-decoration-none ms-1"
                                                                        style="padding:4px 12px;font-size:0.8125rem;background-color:#d97706;border-color:#d97706;color:#ffffff;display:inline-flex;align-items:center;gap:4px;"
                                                                        onclick="event.stopPropagation();"
                                                                        title="Gửi nhắc nhở thanh toán">
                                                                        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                                                            <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                                                                            <line x1="12" y1="9" x2="12" y2="13"/>
                                                                            <line x1="12" y1="17" x2="12.01" y2="17"/>
                                                                        </svg>
                                                                        Nhắc nợ
                                                                    </a>
                                                                </c:if>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>

                                        <%-- Phân trang --%>
                                            <c:if test="${totalPages > 1}">
                                                <div
                                                    class="table-footer d-flex justify-content-between align-items-center px-3 py-2">
                                                    <span style="font-size:0.875rem;color:var(--hms-text-muted)">
                                                        Trang
                                                        <c:out value="${currentPage}" /> /
                                                        <c:out value="${totalPages}" />
                                                    </span>
                                                    <div class="d-flex gap-1">
                                                        <c:if test="${currentPage > 1}">
                                                            <a href="${ctx}/manager/debts?page=${currentPage - 1}&keyword=${keyword}&status=${status}"
                                                                class="btn-mintlify-secondary text-decoration-none"
                                                                style="padding:6px 14px">← Trước</a>
                                                        </c:if>
                                                        <c:if test="${currentPage < totalPages}">
                                                            <a href="${ctx}/manager/debts?page=${currentPage + 1}&keyword=${keyword}&status=${status}"
                                                                class="btn-mintlify-secondary text-decoration-none"
                                                                style="padding:6px 14px">Sau →</a>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="empty-state p-5 text-center">
                                            <svg width="56" height="56" viewBox="0 0 24 24" fill="none"
                                                stroke="var(--hms-text-muted)" stroke-width="1.2"
                                                style="margin-bottom:16px">
                                                <path d="M22 12h-4l-3 9L9 3l-3 9H2"></path>
                                            </svg>
                                            <h4>Không có công nợ nào</h4>
                                            <p style="color:var(--hms-text-muted);max-width:360px;margin:0 auto 16px">
                                                Không tìm thấy dữ liệu công nợ phù hợp.
                                            </p>
                                            <a href="${ctx}/manager/invoices"
                                                class="btn-mintlify-primary text-decoration-none">
                                                Quản lý Hóa đơn
                                            </a>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                        </main>
                    </div>
                </div>
                <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
            </body>

            </html>