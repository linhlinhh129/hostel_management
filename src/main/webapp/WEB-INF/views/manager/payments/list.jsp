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
                  class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3">
                  <div>
                    <h1>Quản lý Thanh toán</h1>
                    <p>Duyệt các khoản thanh toán từ người thuê</p>
                  </div>
                </div>

                <%-- Filter bar --%>
                  <div class="data-surface">
                    <form class="filter-bar" method="get" action="${ctx}/manager/payments" id="filterForm">
                      <input type="text" class="form-control" name="keyword" value="<c:out value='${keyword}'/>"
                        placeholder="Tìm mã GD, phòng, người thuê..." style="max-width:250px" />

                      <select class="form-select" name="status" style="max-width:180px">
                        <option value="">Tất cả trạng thái</option>
                        <option value="PENDING" ${status=='PENDING' ? 'selected' : '' }>Chờ duyệt</option>
                        <option value="SUCCESS" ${status=='SUCCESS' ? 'selected' : '' }>Thành công</option>
                        <option value="REJECTED" ${status=='REJECTED' ? 'selected' : '' }>Từ chối</option>
                      </select>

                      <div style="display:flex;gap:8px;align-items:center;border-left:1px solid var(--hms-border);padding-left:12px;margin-left:4px">
                        <label style="font-size:0.875rem;font-weight:500;white-space:nowrap;color:var(--hms-text-primary)">Từ ngày:</label>
                        <input type="date" class="form-control" name="fromDate" value="<c:out value='${fromDate}'/>" 
                          style="max-width:160px" id="fromDate" />
                        
                        <label style="font-size:0.875rem;font-weight:500;white-space:nowrap;color:var(--hms-text-primary)">Đến ngày:</label>
                        <input type="date" class="form-control" name="toDate" value="<c:out value='${toDate}'/>" 
                          style="max-width:160px" id="toDate" />
                      </div>

                      <div style="display:flex;gap:8px;align-items:center;border-left:1px solid var(--hms-border);padding-left:12px;margin-left:4px">
                        <label style="font-size:0.875rem;font-weight:500;white-space:nowrap;color:var(--hms-text-primary)">Kỳ:</label>
                        <select class="form-select" name="month" style="max-width:120px" id="month">
                          <option value="">Tất cả tháng</option>
                          <option value="1" ${month=='1' ? 'selected' : '' }>Tháng 1</option>
                          <option value="2" ${month=='2' ? 'selected' : '' }>Tháng 2</option>
                          <option value="3" ${month=='3' ? 'selected' : '' }>Tháng 3</option>
                          <option value="4" ${month=='4' ? 'selected' : '' }>Tháng 4</option>
                          <option value="5" ${month=='5' ? 'selected' : '' }>Tháng 5</option>
                          <option value="6" ${month=='6' ? 'selected' : '' }>Tháng 6</option>
                          <option value="7" ${month=='7' ? 'selected' : '' }>Tháng 7</option>
                          <option value="8" ${month=='8' ? 'selected' : '' }>Tháng 8</option>
                          <option value="9" ${month=='9' ? 'selected' : '' }>Tháng 9</option>
                          <option value="10" ${month=='10' ? 'selected' : '' }>Tháng 10</option>
                          <option value="11" ${month=='11' ? 'selected' : '' }>Tháng 11</option>
                          <option value="12" ${month=='12' ? 'selected' : '' }>Tháng 12</option>
                        </select>
                        
                        <select class="form-select" name="year" style="max-width:110px" id="year">
                          <option value="">Năm</option>
                          <c:forEach var="y" begin="2020" end="2030">
                            <option value="${y}" ${year==y.toString() ? 'selected' : '' }>${y}</option>
                          </c:forEach>
                        </select>
                      </div>

                      <button type="submit" class="btn-mintlify-secondary">Lọc</button>
                      <a href="${ctx}/manager/payments" class="btn-mintlify-secondary text-decoration-none">Xóa bộ
                        lọc</a>
                    </form>

                    <script>
                      // Xử lý xung đột giữa bộ lọc khoảng thời gian và kỳ
                      document.addEventListener('DOMContentLoaded', function() {
                        const fromDate = document.getElementById('fromDate');
                        const toDate = document.getElementById('toDate');
                        const month = document.getElementById('month');
                        const year = document.getElementById('year');
                        
                        // Khi chọn khoảng thời gian, xóa lựa chọn kỳ
                        fromDate.addEventListener('change', function() {
                          if (this.value) {
                            month.value = '';
                            year.value = '';
                          }
                        });
                        
                        toDate.addEventListener('change', function() {
                          if (this.value) {
                            month.value = '';
                            year.value = '';
                          }
                        });
                        
                        // Khi chọn kỳ, xóa khoảng thời gian
                        month.addEventListener('change', function() {
                          if (this.value) {
                            fromDate.value = '';
                            toDate.value = '';
                          }
                        });
                        
                        year.addEventListener('change', function() {
                          if (this.value) {
                            fromDate.value = '';
                            toDate.value = '';
                          }
                        });
                      });
                    </script>

                    <%-- Table --%>
                      <c:choose>
                        <c:when test="${not empty payments}">
                          <div class="table-responsive">
                            <table class="table-mintlify">
                              <thead>
                                <tr>
                                  <th>Mã GD</th>
                                  <th>Phòng</th>
                                  <th>Người thuê</th>
                                  <th style="text-align:right">Số tiền</th>
                                  <th>Ngày TT</th>
                                  <th>Trạng thái</th>
                                  <th>Thao tác</th>
                                </tr>
                              </thead>
                              <tbody>
                                <c:forEach var="payment" items="${payments}">
                                  <tr>
                                    <td>
                                      <a href="${ctx}/manager/payments/${payment.paymentId}"
                                        style="font-weight:600;font-family:monospace;display:block;margin-bottom:2px">
                                        <c:out value="${payment.transactionCode}" />
                                      </a>
                                      <c:choose>
                                        <c:when test="${payment.paymentMethod == 'VNPAY'}">
                                          <span class="badge-hms" style="font-size:0.7rem;padding:2px 6px;background-color:#e3f2fd;color:#0d47a1;border:1px solid #bbdefb;border-radius:4px;font-weight:600">VNPAY</span>
                                        </c:when>
                                        <c:otherwise>
                                          <span class="badge-hms" style="font-size:0.7rem;padding:2px 6px;background-color:#e8f5e9;color:#1b5e20;border:1px solid #c8e6c9;border-radius:4px;font-weight:600">Chuyển khoản</span>
                                        </c:otherwise>
                                      </c:choose>
                                    </td>
                                    <td><span class="badge-hms badge-neutral">
                                        <c:out value="${payment.roomCode}" />
                                      </span></td>
                                    <td>
                                      <c:out value="${payment.tenantName}" />
                                    </td>
                                    <td style="text-align:right;font-weight:600">
                                      <fmt:formatNumber value="${payment.amount}" pattern="#,##0" /> đ
                                    </td>
                                    <td>
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
                                    <td>
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
                                    <a href="${prevUrl}" class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">← Trước</a>
                                  </c:if>
                                  <c:if test="${currentPage < totalPages}">
                                    <a href="${nextUrl}" class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau →</a>
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