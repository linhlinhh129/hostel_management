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

        <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-center gap-3"
             style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
          <div>
            <h1>Chi tiết Hóa Đơn: <c:out value="${invoice.invoiceCode}" /></h1>
          </div>
          <div class="d-flex flex-column align-items-end gap-2" style="position:relative;z-index:1">
            <a href="${ctx}/manager/invoices" class="btn-mintlify-secondary text-decoration-none">← Danh sách</a>
            <div class="d-flex gap-2 flex-wrap align-items-center">
            <c:if test="${invoice.status ne 'PAID'}">
              <a href="${ctx}/manager/notifications/send-operator?invoiceId=${invoice.invoiceId}" class="btn text-white" style="background-color: #f59e0b; padding: 8px 16px; border-radius: 6px; font-weight: 500; font-size: 0.875rem; border:none; text-decoration:none;">Báo cáo sai số</a>
              <a href="${ctx}/manager/invoices/${invoice.invoiceId}/edit" class="btn-mintlify-secondary text-decoration-none">Sửa Hóa Đơn</a>
            </c:if>
            <button onclick="window.print()" class="btn-mintlify-primary">Xuất PDF / In</button>
          </div>
          </div><%-- end flex-column wrapper --%>
        </div>



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
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.roomFee != null ? invoice.roomFee : 0}" pattern="#,##0"/> đ</td>
                    </tr>
                    <tr>
                      <td><strong>Tiền điện</strong></td>
                      <td><c:out value="${invoice.oldElectricReading}"/></td>
                      <td><c:out value="${invoice.newElectricReading}"/></td>
                      <td><c:out value="${invoice.electricUsage}"/></td>
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.electricUnitPrice != null ? invoice.electricUnitPrice : 0}" pattern="#,##0"/></td>
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.electricAmount != null ? invoice.electricAmount : 0}" pattern="#,##0"/> đ</td>
                    </tr>
                    <tr>
                      <td><strong>Tiền nước</strong></td>
                      <td><c:out value="${invoice.oldWaterReading}"/></td>
                      <td><c:out value="${invoice.newWaterReading}"/></td>
                      <td><c:out value="${invoice.waterUsage}"/></td>
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.waterUnitPrice != null ? invoice.waterUnitPrice : 0}" pattern="#,##0"/></td>
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.waterAmount != null ? invoice.waterAmount : 0}" pattern="#,##0"/> đ</td>
                    </tr>
                    <tr>
                      <td><strong>Phí dịch vụ</strong></td>
                      <td>-</td>
                      <td>-</td>
                      <td>-</td>
                      <td style="text-align:right">-</td>
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.serviceFee != null ? invoice.serviceFee : 0}" pattern="#,##0"/> đ</td>
                    </tr>
                    <tr>
                      <td><strong>Tiền Internet</strong></td>
                      <td>-</td>
                      <td>-</td>
                      <td>-</td>
                      <td style="text-align:right">-</td>
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.internetFee != null ? invoice.internetFee : 0}" pattern="#,##0"/> đ</td>
                    </tr>
                    <tr>
                      <td><strong>Phí khác</strong></td>
                      <td>-</td>
                      <td>-</td>
                      <td>-</td>
                      <td style="text-align:right">-</td>
                      <td style="text-align:right"><fmt:formatNumber value="${invoice.otherFee != null ? invoice.otherFee : 0}" pattern="#,##0"/> đ</td>
                    </tr>
                    <c:if test="${invoice.lateFee != null and invoice.lateFee > 0}">
                    <tr style="background:#fff8e1">
                      <td><strong>Phí chậm nộp</strong></td>
                      <td>-</td>
                      <td>-</td>
                      <td>-</td>
                      <td style="text-align:right;color:#e65100;font-size:0.8rem">(1%/ngày)</td>
                      <td style="text-align:right;color:#e65100;font-weight:700"><fmt:formatNumber value="${invoice.lateFee}" pattern="#,##0"/> đ</td>
                    </tr>
                    </c:if>
                  </tbody>
                  <tfoot>
                    <tr style="background:var(--hms-primary-soft); color:var(--hms-primary-dark);">
                      <td colspan="5" style="text-align:right; font-size:1.1rem"><strong>Tổng tiền phải nộp:</strong></td>
                      <td style="text-align:right; font-size:1.1rem"><strong><fmt:formatNumber value="${invoice.totalAmount != null ? invoice.totalAmount : 0}" pattern="#,##0"/> đ</strong></td>
                    </tr>
                  </tfoot>
                </table>
              </div>
              
              <div class="mt-4">
                <strong>Ghi chú:</strong> <br/>
                <c:out value="${invoice.note}" default="Không có ghi chú" />
              </div>

              <c:if test="${not empty invoice.electricImg or not empty invoice.waterImg}">
                <div class="mt-4 pt-4 border-top">
                  <h5 class="fw-bold mb-3" style="font-size: 1.1rem; color: var(--hms-text-primary);">Ảnh chỉ số điện nước</h5>
                  <div class="row g-3">
                    <c:if test="${not empty invoice.electricImg}">
                      <div class="col-md-6">
                        <div class="card border h-100" style="border-radius: 8px; overflow: hidden; background: #fafafa;">
                          <div style="padding: 10px 15px; background: #f1f5f9; border-bottom: 1px solid #e2e8f0; font-weight: 600; font-size: 0.875rem;">
                            Ảnh công tơ điện
                          </div>
                          <div class="p-2 text-center">
                            <c:url value="${invoice.electricImg}" var="electricImgUrl"/>
                            <img src="${electricImgUrl}" alt="Ảnh chỉ số điện" title="Click để phóng to ảnh" style="max-width: 100%; max-height: 250px; border-radius: 6px; object-fit: contain; box-shadow: 0 2px 4px rgba(0,0,0,0.05); cursor: zoom-in; transition: transform 0.2s;" onmouseover="this.style.transform='scale(1.02)';" onmouseout="this.style.transform='scale(1)';" onclick="showFullImage(this.src)">
                          </div>
                        </div>
                      </div>
                    </c:if>
                    <c:if test="${not empty invoice.waterImg}">
                      <div class="col-md-6">
                        <div class="card border h-100" style="border-radius: 8px; overflow: hidden; background: #fafafa;">
                          <div style="padding: 10px 15px; background: #f1f5f9; border-bottom: 1px solid #e2e8f0; font-weight: 600; font-size: 0.875rem;">
                            Ảnh công tơ nước
                          </div>
                          <div class="p-2 text-center">
                            <c:url value="${invoice.waterImg}" var="waterImgUrl"/>
                            <img src="${waterImgUrl}" alt="Ảnh chỉ số nước" title="Click để phóng to ảnh" style="max-width: 100%; max-height: 250px; border-radius: 6px; object-fit: contain; box-shadow: 0 2px 4px rgba(0,0,0,0.05); cursor: zoom-in; transition: transform 0.2s;" onmouseover="this.style.transform='scale(1.02)';" onmouseout="this.style.transform='scale(1)';" onclick="showFullImage(this.src)">
                          </div>
                        </div>
                      </div>
                    </c:if>
                  </div>
                </div>
              </c:if>
            </div>
          </div>
          
          <div class="col-lg-4">
            <div class="data-surface p-4 mb-4">
              <h4 class="mb-4">Thông tin người thuê</h4>
              <ul class="list-unstyled">
                <li class="mb-3">
                  <span class="text-muted d-block" style="font-size:0.875rem">Họ tên</span>
                  <span class="fw-bold"><c:out value="${invoice.tenantName}" default="Phòng trống (Chưa có người thuê)"/></span>
                </li>
                <li class="mb-3">
                  <span class="text-muted d-block" style="font-size:0.875rem">Số điện thoại</span>
                  <span class="fw-bold"><c:out value="${invoice.tenantPhone}" default="-"/></span>
                </li>
                <li class="mb-0">
                  <span class="text-muted d-block" style="font-size:0.875rem">Email</span>
                  <span class="fw-bold"><c:out value="${invoice.tenantEmail}" default="-"/></span>
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
                  <span class="text-muted d-block" style="font-size:0.875rem">Kỳ hợp đồng</span>
                  <span class="fw-bold"><c:out value="${invoice.contractPeriod}" default="Chưa có hợp đồng"/></span>
                  <c:if test="${not empty invoice.contractCode}">
                    <span class="text-muted" style="font-size:0.8rem"> (Mã: <c:out value="${invoice.contractCode}"/>)</span>
                  </c:if>
                </li>
                <li class="mb-3">
                  <span class="text-muted d-block" style="font-size:0.875rem">Hạn thanh toán</span>
                  <span class="fw-bold"><c:out value="${invoice.dueDate}"/></span>
                </li>
                <li class="mb-3">
                  <span class="text-muted d-block" style="font-size:0.875rem">Ngày tạo</span>
                  <span><fmt:formatDate value="${invoice.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm:ss"/> bởi <c:out value="${invoice.createdByName}"/></span>
                </li>
                <li class="mb-3">
                  <span class="text-muted d-block" style="font-size:0.875rem">Cập nhật cuối</span>
                  <span><fmt:formatDate value="${invoice.updatedAtAsDate}" pattern="dd/MM/yyyy HH:mm:ss"/></span>
                </li>
              </ul>
            </div>
          </div>
        </div>

      </main>
    </div>
  </div>
  <script>
    function showFullImage(imageSrc) {
      document.getElementById('modalImagePreview').src = imageSrc;
      var imgModal = new bootstrap.Modal(document.getElementById('imageViewerModal'));
      imgModal.show();
    }
  </script>

  <!-- Modal xem ảnh lớn -->
  <div class="modal fade" id="imageViewerModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-xl">
      <div class="modal-content bg-transparent border-0">
        <div class="modal-header border-0 justify-content-end p-2">
          <button type="button" class="btn-close bg-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body text-center p-0">
          <img id="modalImagePreview" src="" style="max-width: 100%; max-height: 85vh; border-radius: 8px; object-fit: contain;" alt="Ảnh phóng to">
        </div>
      </div>
    </div>
  </div>
  <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
</html>
