<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
      <c:set var="ctx" value="${pageContext.request.contextPath}" />
      <c:set var="pageTitle" value="Quản lý Thanh toán - BQL" />
      <c:set var="pageRole" value="MANAGER" />
      <c:set var="activeMenu" value="payments" />
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
                <div
                  class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3"
                  style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                  <div>
                    <h1>Quản lý Thanh toán</h1>
                    <p>Duyệt các khoản thanh toán từ người thuê</p>
                  </div>
                </div>

                <%-- Filter bar --%>
                  <div class="data-surface">
                    <form method="get" action="${ctx}/manager/payments" id="filterForm"
                      style="background:#fff; border:1px solid var(--hms-border-soft); border-radius:8px; padding:20px; margin-bottom:20px; box-shadow:0 1px 3px rgba(0,0,0,0.02)">
                      <div style="display:flex; flex-wrap:wrap; gap:20px; margin-bottom:20px;">
                        <div style="flex:2; min-width:180px;">
                          <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Tìm kiếm</label>
                          <input type="text" class="form-control" name="keyword" value="<c:out value='${keyword}'/>"
                            placeholder="Mã GD, phòng, người thuê..." style="width:100%"/>
                        </div>
                        <div style="flex:1; min-width:140px;">
                          <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Trạng thái</label>
                          <select class="form-select" name="status" style="width:100%">
                            <option value="">Tất cả</option>
                            <option value="PENDING"  ${status=='PENDING'  ? 'selected' : ''}>Chờ duyệt</option>
                            <option value="SUCCESS"  ${status=='SUCCESS'  ? 'selected' : ''}>Thành công</option>
                            <option value="REJECTED" ${status=='REJECTED' ? 'selected' : ''}>Từ chối</option>
                          </select>
                        </div>
                        <div style="flex:2; min-width:200px;">
                          <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Khoảng thời gian</label>
                          <div class="input-group">
                            <input type="date" class="form-control" name="fromDate" value="<c:out value='${fromDate}'/>" id="fromDate" title="Từ ngày"/>
                            <span class="input-group-text bg-light text-muted border-start-0 border-end-0">đến</span>
                            <input type="date" class="form-control" name="toDate"   value="<c:out value='${toDate}'/>"   id="toDate"   title="Đến ngày"/>
                          </div>
                        </div>
                      </div>
                      <div style="display:flex; justify-content:flex-end; gap:12px; border-top:1px dashed var(--hms-border-soft); padding-top:16px;">
                        <a href="${ctx}/manager/payments"
                           style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); text-decoration:none; font-size:14px; font-weight:500;">Xóa bộ lọc</a>
                        <button type="submit"
                                style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); font-size:14px; font-weight:500; cursor:pointer;">Tìm kiếm</button>
                      </div>
                    </form>

                    <%-- Table --%>
                      <c:choose>
                        <c:when test="${not empty payments}">
                          <div class="table-responsive">
                            <table class="table-mintlify">
                              <thead>
                                <tr>
                                  <th>Mã GD</th>
                                  <th>Phòng</th>
                                  <th class="d-none d-md-table-cell">Người thuê</th>
                                  <th class="d-none d-md-table-cell" style="text-align:right">Số tiền</th>
                                  <th class="d-none d-md-table-cell">Ngày TT</th>
                                  <th>Trạng thái</th>
                                  <th class="d-none d-md-table-cell">Thao tác</th>
                                </tr>
                              </thead>
                              <tbody>
                                <c:forEach var="payment" items="${payments}">
                                  <tr data-href="${ctx}/manager/payments/${payment.paymentId}">
                                    <td>
                                      <a href="${ctx}/manager/payments/${payment.paymentId}"
                                        style="font-weight:600;display:block;margin-bottom:2px">
                                        <c:out value="${payment.transactionCode}" />
                                      </a>
                                      <c:choose>
                                        <c:when test="${payment.paymentMethod == 'VNPAY'}">
                                          <span class="badge-hms"
                                            style="font-size:0.7rem;padding:2px 6px;background-color:#e3f2fd;color:#0d47a1;border:1px solid #bbdefb;border-radius:4px;font-weight:600">VNPAY</span>
                                        </c:when>
                                        <c:otherwise>
                                          <span class="badge-hms"
                                            style="font-size:0.7rem;padding:2px 6px;background-color:#e8f5e9;color:#1b5e20;border:1px solid #c8e6c9;border-radius:4px;font-weight:600">Chuyển
                                            khoản</span>
                                        </c:otherwise>
                                      </c:choose>
                                    </td>
                                    <td><span class="badge-hms badge-neutral">
                                        <c:out value="${payment.roomCode}" />
                                      </span></td>
                                    <td class="d-none d-md-table-cell">
                                      <c:out value="${payment.tenantName}" />
                                    </td>
                                    <td class="d-none d-md-table-cell" style="text-align:right;font-weight:600">
                                      <fmt:formatNumber value="${payment.amount}" pattern="#,##0" /> đ
                                    </td>
                                    <td class="d-none d-md-table-cell">
                                      <c:out value="${payment.paymentDate}" />
                                    </td>
                                    <td>
                                      <c:choose>
                                        <c:when test="${payment.status == 'PENDING'}">
                                          <span class="badge-hms badge-warning">Chờ duyệt</span>
                                        </c:when>
                                        <c:when test="${payment.status == 'SUCCESS'}">
                                          <span class="badge-hms badge-success">Thành công</span>
                                        </c:when>
                                        <c:otherwise>
                                          <span class="badge-hms badge-danger">Từ chối</span>
                                        </c:otherwise>
                                      </c:choose>
                                    </td>
                                    <td class="d-none d-md-table-cell">
                                      <a href="${ctx}/manager/payments/${payment.paymentId}"
                                        class="btn-mintlify-secondary text-decoration-none"
                                        style="padding:4px 12px;font-size:0.8125rem">Xem</a>
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
                                  Trang
                                  <c:out value="${currentPage}" /> /
                                  <c:out value="${totalPages}" />
                                </span>
                                <div class="d-flex gap-1">
                                  <c:url var="prevUrl" value="/manager/payments">
                                    <c:param name="page" value="${currentPage - 1}" />
                                    <c:if test="${not empty keyword}">
                                      <c:param name="keyword" value="${keyword}" />
                                    </c:if>
                                    <c:if test="${not empty status}">
                                      <c:param name="status" value="${status}" />
                                    </c:if>
                                    <c:if test="${not empty fromDate}">
                                      <c:param name="fromDate" value="${fromDate}" />
                                    </c:if>
                                    <c:if test="${not empty toDate}">
                                      <c:param name="toDate" value="${toDate}" />
                                    </c:if>
                                    <c:if test="${not empty month}">
                                      <c:param name="month" value="${month}" />
                                    </c:if>
                                    <c:if test="${not empty year}">
                                      <c:param name="year" value="${year}" />
                                    </c:if>
                                  </c:url>
                                  <c:url var="nextUrl" value="/manager/payments">
                                    <c:param name="page" value="${currentPage + 1}" />
                                    <c:if test="${not empty keyword}">
                                      <c:param name="keyword" value="${keyword}" />
                                    </c:if>
                                    <c:if test="${not empty status}">
                                      <c:param name="status" value="${status}" />
                                    </c:if>
                                    <c:if test="${not empty fromDate}">
                                      <c:param name="fromDate" value="${fromDate}" />
                                    </c:if>
                                    <c:if test="${not empty toDate}">
                                      <c:param name="toDate" value="${toDate}" />
                                    </c:if>
                                    <c:if test="${not empty month}">
                                      <c:param name="month" value="${month}" />
                                    </c:if>
                                    <c:if test="${not empty year}">
                                      <c:param name="year" value="${year}" />
                                    </c:if>
                                  </c:url>
                                  <c:if test="${currentPage > 1}">
                                    <a href="${prevUrl}" class="btn-mintlify-secondary text-decoration-none"
                                      style="padding:6px 14px">← Trước</a>
                                  </c:if>
                                  <c:if test="${currentPage < totalPages}">
                                    <a href="${nextUrl}" class="btn-mintlify-secondary text-decoration-none"
                                      style="padding:6px 14px">Sau →</a>
                                  </c:if>
                                </div>
                              </div>
                            </c:if>
                        </c:when>

                        <c:otherwise>
                          <div class="empty-state p-5 text-center">
                            <svg width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)"
                              stroke-width="1.2" style="margin-bottom:16px">
                              <rect x="1" y="4" width="22" height="16" rx="2" ry="2" />
                              <line x1="1" y1="10" x2="23" y2="10" />
                            </svg>
                            <h4>Không có giao dịch nào</h4>
                            <p style="color:var(--hms-text-muted);max-width:360px;margin:0 auto 16px">
                              Chưa có dữ liệu thanh toán phù hợp.
                            </p>
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