<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="Chi tiết Hóa đơn - BQL" />
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

        <div class="page-header d-flex flex-wrap justify-content-between align-items-center gap-3">
          <div>
            <a href="${ctx}/manager/invoices" class="text-decoration-none text-muted mb-2 d-inline-block">← Quay lại danh sách</a>
            <h1>Chi tiết Hóa Đơn: <c:out value="${invoice.invoiceCode}" /></h1>
          </div>
          <div class="d-flex gap-2">
            <a href="${ctx}/manager/requests/create?category=TECHNICAL&title=Phát hiện sai số: ${invoice.invoiceCode}" class="btn-mintlify-danger text-decoration-none" style="background-color: var(--hms-danger); color: white; padding: 8px 16px; border-radius: 6px;">Báo cáo sai số</a>
            <c:if test="${invoice.status ne 'PAID'}">
              <a href="${ctx}/manager/invoices/${invoice.invoiceId}/edit" class="btn-mintlify-secondary text-decoration-none">Sửa Hóa Đơn</a>
            </c:if>
            <button onclick="window.print()" class="btn-mintlify-primary">Xuất PDF / In</button>
          </div>
        </div>

        <style>
          /* Shrink table font size on screen to fit without horizontal scroll */
          @media screen {
            .table-mintlify { font-size: 0.85rem; }
            .table-mintlify th, .table-mintlify td { padding: 0.5rem !important; }
          }
          @media print {
            body { background-color: #fff !important; margin: 0; padding: 0; }
            .sidebar, .topbar, .page-header, .sidebar-overlay, .btn-mintlify-primary, .btn-mintlify-secondary, .btn-mintlify-danger, footer, .alert {
              display: none !important;
            }
            .app-shell { display: block; }
            .main-wrapper { margin: 0 !important; padding: 0 !important; width: 100%; }
            .page-content { margin: 0 !important; padding: 20px !important; }
            .data-surface { box-shadow: none !important; border: none !important; padding: 0 !important; }
            .table-responsive { overflow: visible !important; }
            .row { display: flex; flex-direction: column; }
            .col-lg-8, .col-lg-4 { width: 100% !important; flex: none !important; margin-bottom: 20px; }
            .table-mintlify th, .table-mintlify td { padding: 4px !important; font-size: 10pt !important; color: #000 !important; border-bottom: 1px solid #ccc !important; }
            h1, h4 { margin-top: 0 !important; color: #000 !important; }
            .badge-hms { border: 1px solid #000; color: #000 !important; background: transparent !important; }
          }
        </style>

        <div class="row g-4">
          <div class="col-lg-8">
            <div class="data-surface p-4">
              <h4 class="mb-4">Thông tin Tính tiền</h4>
              
              <div class="table-responsive">
                <table class="table-mintlify" style="min-width: 100%;">
                  <thead>
                    <tr>
                      <th>Khoản mục</th>
                      <th>Chỉ số cũ</th>
                      <th>Chỉ số mới</th>
                      <th>Sử dụng</th>
                      <th style="text-align:right">Đơn giá</th>
                      <th style="text-align:right">Thành tiền</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td><strong>Tiền phòng</strong></td>
                      <td>-</td>
                      <td>-</td>
                      <td>-</td>
                      <td style="text-align:right">-</td>
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.roomFee}" pattern="#,##0"/> đ</td>
                    </tr>
                    <tr>
                      <td><strong>Tiền điện</strong></td>
                      <td><c:out value="${invoice.oldElectricReading}"/></td>
                      <td><c:out value="${invoice.newElectricReading}"/></td>
                      <td><c:out value="${invoice.electricUsage}"/></td>
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.electricUnitPrice}" pattern="#,##0"/></td>
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.electricAmount}" pattern="#,##0"/> đ</td>
                    </tr>
                    <tr>
                      <td><strong>Tiền nước</strong></td>
                      <td><c:out value="${invoice.oldWaterReading}"/></td>
                      <td><c:out value="${invoice.newWaterReading}"/></td>
                      <td><c:out value="${invoice.waterUsage}"/></td>
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.waterUnitPrice}" pattern="#,##0"/></td>
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.waterAmount}" pattern="#,##0"/> đ</td>
                    </tr>
                    <tr>
                      <td><strong>Phí dịch vụ</strong></td>
                      <td>-</td>
                      <td>-</td>
                      <td>-</td>
                      <td style="text-align:right">-</td>
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.serviceFee}" pattern="#,##0"/> đ</td>
                    </tr>
                    <tr>
                      <td><strong>Tiền Internet</strong></td>
                      <td>-</td>
                      <td>-</td>
                      <td>-</td>
                      <td style="text-align:right">-</td>
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.internetFee}" pattern="#,##0"/> đ</td>
                    </tr>
                    <tr>
                      <td><strong>Phí khác</strong></td>
                      <td>-</td>
                      <td>-</td>
                      <td>-</td>
                      <td style="text-align:right">-</td>
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.otherFee}" pattern="#,##0"/> đ</td>
                    </tr>
                  </tbody>
                  <tfoot>
                    <tr>
                      <td colspan="5" style="text-align:right"><strong>Tạm tính:</strong></td>
                      <td style="text-align:right"><strong><fmt:formatNumber value="${invoice.subtotal}" pattern="#,##0"/> đ</strong></td>
                    </tr>
                    <tr>
                      <td colspan="5" style="text-align:right"><strong>Thuế (<c:out value="${invoice.taxRate}"/>%):</strong></td>
                      <td style="text-align:right"><strong><fmt:formatNumber value="${invoice.taxAmount}" pattern="#,##0"/> đ</strong></td>
                    </tr>
                    <tr style="background:var(--hms-primary-soft); color:var(--hms-primary-dark);">
                      <td colspan="5" style="text-align:right; font-size:1.1rem"><strong>Tổng tiền phải nộp:</strong></td>
                      <td style="text-align:right; font-size:1.1rem"><strong><fmt:formatNumber value="${invoice.totalAmount}" pattern="#,##0"/> đ</strong></td>
                    </tr>
                  </tfoot>
                </table>
              </div>
              
              <div class="mt-4">
                <strong>Ghi chú:</strong> <br/>
                <c:out value="${invoice.note}" default="Không có ghi chú" />
              </div>
            </div>
          </div>
          
          <div class="col-lg-4">
            <div class="data-surface p-4 mb-4">
              <h4 class="mb-4">Thông tin người thuê</h4>
              <ul class="list-unstyled">
                <li class="mb-3">
                  <span class="text-muted d-block" style="font-size:0.875rem">Họ tên</span>
                  <span class="fw-bold"><c:out value="${invoice.tenantName}"/></span>
                </li>
                <li class="mb-3">
                  <span class="text-muted d-block" style="font-size:0.875rem">Số điện thoại</span>
                  <span class="fw-bold"><c:out value="${invoice.tenantPhone}"/></span>
                </li>
                <li class="mb-0">
                  <span class="text-muted d-block" style="font-size:0.875rem">Email</span>
                  <span class="fw-bold"><c:out value="${invoice.tenantEmail}"/></span>
                </li>
              </ul>
            </div>

            <div class="data-surface p-4">
              <h4 class="mb-4">Thông tin chung</h4>
              <ul class="list-unstyled">
                <li class="mb-3">
                  <span class="text-muted d-block" style="font-size:0.875rem">Trạng thái</span>
                  <span class="${invoice.statusBadgeClass}"><c:out value="${invoice.statusLabel}"/></span>
                </li>
                <li class="mb-3">
                  <span class="text-muted d-block" style="font-size:0.875rem">Phòng</span>
                  <span class="fw-bold"><c:out value="${invoice.roomCode}"/></span>
                </li>
                <li class="mb-3">
                  <span class="text-muted d-block" style="font-size:0.875rem">Kỳ hóa đơn</span>
                  <span class="fw-bold"><c:out value="${invoice.billingPeriod}"/></span>
                </li>
                <li class="mb-3">
                  <span class="text-muted d-block" style="font-size:0.875rem">Hạn thanh toán</span>
                  <span class="fw-bold"><c:out value="${invoice.dueDate}"/></span>
                </li>
                <li class="mb-3">
                  <span class="text-muted d-block" style="font-size:0.875rem">Ngày tạo</span>
                  <span><c:out value="${invoice.createdAt}"/> bởi <c:out value="${invoice.createdByName}"/></span>
                </li>
                <li class="mb-3">
                  <span class="text-muted d-block" style="font-size:0.875rem">Cập nhật cuối</span>
                  <span><c:out value="${invoice.updatedAt}"/></span>
                </li>
              </ul>
            </div>
          </div>
        </div>

      </main>
    </div>
  </div>
  <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
</html>
