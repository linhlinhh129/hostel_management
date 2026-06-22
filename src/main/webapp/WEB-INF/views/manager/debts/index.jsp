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
                                <form class="filter-bar" method="get" action="${ctx}/manager/debts">
                                    <input type="text" class="form-control" name="keyword"
                                        value="<c:out value='${keyword}'/>" placeholder="Tìm mã HĐ, phòng..."
                                        style="max-width:250px" />

                                    <select class="form-select" name="status" style="max-width:180px">
                                        <option value="">Tất cả trạng thái</option>
                                        <option value="UNPAID" ${status=='UNPAID' ? 'selected' : '' }>Chưa thanh toán
                                        </option>
                                        <option value="OVERDUE" ${status=='OVERDUE' ? 'selected' : '' }>Quá hạn</option>
                                    </select>

                                    <button type="submit" class="btn-mintlify-secondary">Lọc</button>
                                    <a href="${ctx}/manager/debts"
                                        class="btn-mintlify-secondary text-decoration-none">Xóa lọc</a>
                                </form>
                                <c:choose>
                                    <c:when test="${not empty debts}">
                                        <div class="table-responsive">
                                            <table class="table-mintlify mb-0">
                                                <thead>
                                                    <tr>
                                                        <th>Mã HĐ</th>
                                                        <th>Phòng</th>
                                                        <th>Người thuê</th>
                                                        <th style="white-space: nowrap; width: 5%;">Kỳ HĐ</th>
                                                        <th style="text-align:right; width: 20%;">Tổng tiền</th>
                                                        <th style="text-align:right; width: 15%;">Phí quá hạn</th>
                                                        <th style="white-space: nowrap">Hạn nộp</th>
                                                        <th>Trạng thái</th>
                                                        <th>Thao tác</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="debt" items="${debts}">
                                                        <tr>
                                                            <td>
                                                                <a href="${ctx}/manager/debts?action=detail&id=${debt.invoiceId}"
                                                                    style="font-weight:600;font-family:monospace">
                                                                    <c:out value="${debt.invoiceCode}" />
                                                                </a>
                                                            </td>
                                                            <td>
                                                                <span class="badge-hms badge-neutral">
                                                                    <c:out value="${debt.roomCode}" />
                                                                </span>
                                                            </td>
                                                            <td>
                                                                <c:out value="${debt.tenantName}" />
                                                            </td>
                                                            <td>
                                                                <c:out value="${debt.billingPeriod}" />
                                                            </td>
                                                            <td style="text-align:right; font-weight:600">
                                                                <fmt:formatNumber value="${debt.invoiceTotalAmount}"
                                                                    pattern="#,##0" /> đ
                                                            </td>
                                                            <td
                                                                style="text-align:right; font-weight:600; color:var(--hms-danger-color, #ef4444);">
                                                                <fmt:formatNumber value="${debt.lateFeePreview}"
                                                                    pattern="#,##0" /> đ
                                                            </td>
                                                            <td>
                                                                <c:out value="${debt.dueDate}" />
                                                            </td>
                                                            <td>
                                                                <c:choose>
                                                                    <c:when test="${debt.status == 'UNPAID'}">
                                                                        <span class="badge"
                                                                            style="background-color: var(--hms-warning-color, #f59e0b);">Chưa
                                                                            thanh toán</span>
                                                                    </c:when>
                                                                    <c:when test="${debt.status == 'OVERDUE'}">
                                                                        <span class="badge"
                                                                            style="background-color: var(--hms-danger-color, #ef4444);">Quá
                                                                            hạn
                                                                            <c:if test="${debt.overdueDays > 0}">
                                                                                (${debt.overdueDays} ngày)
                                                                            </c:if>
                                                                        </span>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <span class="badge bg-secondary">
                                                                            <c:out value="${debt.status}" />
                                                                        </span>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </td>
                                                            <td>
                                                                <a href="${ctx}/manager/debts?action=detail&id=${debt.invoiceId}"
                                                                    class="btn-mintlify-secondary text-decoration-none"
                                                                    style="padding:4px 12px;font-size:0.8125rem">
                                                                    Xem
                                                                </a>
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