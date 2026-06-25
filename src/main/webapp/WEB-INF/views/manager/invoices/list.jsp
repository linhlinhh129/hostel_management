<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="Quản lý Hóa đơn - BQL" />
<c:set var="pageRole" value="MANAGER" />
<c:set var="activeMenu" value="invoices" />
<jsp:include page="/WEB-INF/views/layout/head.jsp" />

<body>
  <div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
      <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
      <main class="page-content">
        <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

        <%-- Header --%>
        <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3">
          <div>
            <h1>Quản lý Hóa đơn</h1>
            <p>Theo dõi và thu các khoản phí định kỳ từ người thuê</p>
          </div>
          <div class="d-flex gap-2 align-items-center mt-2 mt-md-0">
            <a href="${ctx}/manager/invoices?action=create" class="btn-mintlify-primary text-decoration-none">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:6px"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
              Tạo hóa đơn
            </a>
          </div>
        </div>

        <%-- Filter bar --%>
        <div class="data-surface">
          <form class="filter-bar" method="get" action="${ctx}/manager/invoices">
            <input type="text" class="form-control" name="keyword" value="<c:out value='${keyword}'/>"
              placeholder="Tìm mã HĐ, phòng..." style="max-width:250px" />

            <input type="text" class="form-control" name="billingPeriod" value="<c:out value='${billingPeriod}'/>"
              placeholder="Kỳ (VD: 202606)" style="max-width:140px" />

            <select class="form-select" name="status" style="max-width:180px">
              <option value="">Tất cả trạng thái</option>
              <option value="UNPAID" ${status=='UNPAID' ? 'selected' : '' }>Chưa thanh toán</option>
              <option value="PAID" ${status=='PAID' ? 'selected' : '' }>Đã thanh toán</option>
              <option value="OVERDUE" ${status=='OVERDUE' ? 'selected' : '' }>Quá hạn</option>
            </select>

            <button type="submit" class="btn-mintlify-secondary">Lọc</button>
            <a href="${ctx}/manager/invoices" class="btn-mintlify-secondary text-decoration-none">Xóa lọc</a>
          </form>

          <%-- Table --%>
          <c:choose>
            <c:when test="${not empty invoices}">
              <div class="table-responsive">
                <table class="table-mintlify">
                  <thead>
                    <tr>
                      <th>Mã Hóa Đơn</th>
                      <th>Phòng</th>
                      <th>Kỳ HĐ</th>
                      <th style="text-align:right">Tổng tiền</th>
                      <th>Hạn TT</th>
                      <th>Trạng thái</th>
                      <th>Thao tác</th>
                    </tr>
                  </thead>
                  <tbody>
                    <c:forEach var="invoice" items="${invoices}">
                      <tr>
                        <td>
                          <a href="${ctx}/manager/invoices/${invoice.invoiceId}"
                            style="font-weight:600;font-family:monospace">
                            <c:out value="${invoice.invoiceCode}" />
                          </a>
                        </td>
                        <td><span class="badge-hms badge-neutral">
                            <c:out value="${invoice.roomCode}" />
                          </span></td>
                        <td>
                          <c:out value="${invoice.billingPeriod}" />
                        </td>
                        <td style="text-align:right;font-weight:600">
                          <fmt:formatNumber value="${invoice.totalAmount}" pattern="#,##0" /> đ
                        </td>
                        <td>
                          <c:out value="${invoice.dueDate}" />
                        </td>
                        <td>
                          <span class="${invoice.statusBadgeClass}">
                            <c:out value="${invoice.statusLabel}" />
                          </span>
                        </td>
                        <td>
                          <div class="d-flex gap-1">
                            <a href="${ctx}/manager/invoices/${invoice.invoiceId}"
                              class="btn-mintlify-secondary text-decoration-none"
                              style="padding:4px 12px;font-size:0.8125rem">Xem</a>
                            <c:if test="${invoice.status ne 'PAID'}">
                              <form action="${ctx}/manager/invoices/${invoice.invoiceId}/delete" method="POST" class="d-inline" onsubmit="return confirm('Bạn có chắc chắn muốn xóa hóa đơn này?');">
                                <input type="hidden" name="csrfToken" value="${csrfToken}">
                                <button type="submit" class="btn btn-sm btn-danger" style="background-color: var(--hms-danger); color: white; border: none; padding: 4px 12px; border-radius: 6px; font-size: 0.8125rem; font-weight: 500;">Xóa</button>
                              </form>
                            </c:if>
                          </div>
                        </td>
                      </tr>
                    </c:forEach>
                  </tbody>
                </table>
              </div>

              <%-- Phân trang --%>
              <c:if test="${totalPages > 1}">
                <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2">
                  <span style="font-size:0.875rem;color:var(--hms-text-muted)">
                    Trang <c:out value="${currentPage}" /> / <c:out value="${totalPages}" />
                  </span>
                  <div class="d-flex gap-1">
                    <c:if test="${currentPage > 1}">
                      <a href="${ctx}/manager/invoices?page=${currentPage - 1}&keyword=${keyword}&status=${status}&billingPeriod=${billingPeriod}"
                        class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">← Trước</a>
                    </c:if>
                    <c:if test="${currentPage < totalPages}">
                      <a href="${ctx}/manager/invoices?page=${currentPage + 1}&keyword=${keyword}&status=${status}&billingPeriod=${billingPeriod}"
                        class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau →</a>
                    </c:if>
                  </div>
                </div>
              </c:if>
            </c:when>

            <c:otherwise>
              <div class="empty-state p-5 text-center">
                <svg width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)"
                  stroke-width="1.2" style="margin-bottom:16px">
                  <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                  <polyline points="14 2 14 8 20 8"></polyline>
                  <line x1="16" y1="13" x2="8" y2="13"></line>
                  <line x1="16" y1="17" x2="8" y2="17"></line>
                  <polyline points="10 9 9 9 8 9"></polyline>
                </svg>
                <h4>Chưa có hóa đơn nào</h4>
                <p style="color:var(--hms-text-muted);max-width:360px;margin:0 auto 16px">
                  Không tìm thấy dữ liệu hóa đơn phù hợp.
                </p>
                <a href="${ctx}/manager/invoices?action=create" class="btn-mintlify-primary text-decoration-none">
                  Tạo mới ngay
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
