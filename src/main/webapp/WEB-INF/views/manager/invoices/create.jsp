<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
      <c:set var="ctx" value="${pageContext.request.contextPath}" />
      <c:set var="pageTitle" value="Tạo Hóa đơn - BQL" />
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

              <div
                class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-center gap-3"
                style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div>
                  <h1>Tạo Hóa Đơn Mới</h1>
                  <p>Hệ thống sẽ tự động lấy đơn giá hiện tại và chỉ số điện nước để tính toán</p>
                </div>
                <a href="${ctx}/manager/invoices" class="btn-mintlify-secondary text-decoration-none"
                  style="position:relative;z-index:1">← Quay lại danh sách</a>
              </div>

              <div class="data-surface p-4" style="max-width: 800px;margin:0 auto">

                <form action="${ctx}/manager/invoices" method="post" id="createInvoiceForm">
                  <input type="hidden" name="action" value="create">
                  <input type="hidden" name="csrfToken" value="${csrfToken}">

                  <div class="row g-4">
                    <div class="col-md-6">
                      <label class="form-label fw-bold">Mã phòng <span class="text-danger">*</span></label>
                      <select class="form-select" name="roomCode" id="roomCodeSelect" required>
                        <option value="">-- Chọn mã phòng --</option>
                        <c:set var="foundPrefilled" value="false" />
                        <c:forEach var="r" items="${availableRooms}">
                          <c:if test="${not empty prefilledRoomCode and prefilledRoomCode == r.code}">
                            <c:set var="foundPrefilled" value="true" />
                          </c:if>
                          <option value="${r.code}" <c:if test="${prefilledRoomCode == r.code}">selected</c:if>>
                            Phòng
                            <c:out value="${r.code}" />
                          </option>
                        </c:forEach>
                        <c:if test="${not empty prefilledRoomCode and !foundPrefilled}">
                          <option value="${prefilledRoomCode}" selected>
                            Phòng
                            <c:out value="${prefilledRoomCode}" />
                          </option>
                        </c:if>
                      </select>
                      <c:if test="${empty availableRooms}">
                        <small class="text-warning d-block mt-1 fw-bold">
                          ⚠ Không tìm thấy phòng nào hợp lệ để tạo hóa đơn.
                        </small>
                      </c:if>
                      <small id="debtHint" class="text-muted mt-1 d-block"></small>
                    </div>

                    <div class="col-md-6">
                      <label class="form-label fw-bold">Kỳ hóa đơn (YYYYMM)</label>
                      <input type="text" class="form-control bg-light" name="billingPeriod" id="billingPeriodInput"
                        readonly
                        value="<c:out value='${param.billingPeriod != null ? param.billingPeriod : defaultBillingPeriod}'/>">
                    </div>

                    <div class="col-md-6">
                      <label class="form-label fw-bold">Hạn thanh toán <span class="text-danger">*</span></label>
                      <input type="date" class="form-control" name="dueDate" required
                        value="<c:out value='${param.dueDate}'/>">
                    </div>

                    <div class="col-md-6">
                      <label class="form-label fw-bold">Phí khác (VNĐ)</label>
                      <input type="number" class="form-control" name="otherFee" id="otherFeeInput"
                        value="<c:choose><c:when test='${not empty param.otherFee}'><c:out value='${param.otherFee}'/></c:when><c:otherwise>0</c:otherwise></c:choose>"
                        step="1000" max="50000000">
                    </div>

                    <div class="col-12">
                      <label class="form-label fw-bold">Ghi chú</label>
                      <textarea class="form-control" name="note" rows="3" maxlength="1000"
                        placeholder="Ghi chú thêm nếu có..."><c:out value='${param.note}'/></textarea>
                      <div class="form-text text-muted">Tối đa 1000 ký tự.</div>
                    </div>
                    <div class="col-12 mt-4" id="invoicePreviewSection" style="display:none;">
                      <h5 class="fw-bold text-dark border-bottom pb-2 mb-3">Chi Tiết Hóa Đơn (Tạm Tính)</h5>
                      <div class="row g-3">
                        <div class="col-md-3">
                          <label class="form-label text-muted">Tiền phòng</label>
                          <input type="text" class="form-control bg-light" id="previewRoomFee" readonly>
                        </div>
                        <div class="col-md-3">
                          <label class="form-label text-muted">Phí dịch vụ chung</label>
                          <input type="text" class="form-control bg-light" id="previewServiceFee" readonly>
                        </div>
                        <div class="col-md-3">
                          <label class="form-label text-muted">Tiền mạng (Internet)</label>
                          <input type="text" class="form-control bg-light" id="previewInternetFee" readonly>
                        </div>
                      </div>

                      <div class="row g-3 mt-1">
                        <div class="col-md-3">
                          <label class="form-label text-muted">Chỉ số Điện</label>
                          <div class="input-group">
                            <span class="input-group-text bg-light text-muted" id="previewOldElectric"
                              title="Chỉ số cũ">0</span>
                            <input type="text" class="form-control bg-light text-center" id="previewNewElectric"
                              title="Chỉ số mới" readonly>
                          </div>
                        </div>
                        <div class="col-md-3">
                          <label class="form-label text-muted">Đơn giá điện</label>
                          <input type="text" class="form-control bg-light" id="previewElectricPrice" readonly>
                        </div>
                        <div class="col-md-3">
                          <label class="form-label text-muted">Chỉ số Nước</label>
                          <div class="input-group">
                            <span class="input-group-text bg-light text-muted" id="previewOldWater"
                              title="Chỉ số cũ">0</span>
                            <input type="text" class="form-control bg-light text-center" id="previewNewWater"
                              title="Chỉ số mới" readonly>
                          </div>
                        </div>
                        <div class="col-md-3">
                          <label class="form-label text-muted">Đơn giá nước</label>
                          <input type="text" class="form-control bg-light" id="previewWaterPrice" readonly>
                        </div>
                      </div>

                      <div id="previewMeterImages" class="mt-4 pt-3 border-top" style="display:none;">
                        <h6 class="fw-bold mb-3" style="color: var(--hms-text-primary);">Ảnh chỉ số điện nước</h6>
                        <div class="row g-3">
                          <div class="col-md-6" id="electricImgCol" style="display:none;">
                            <div class="border rounded p-2 text-center bg-light">
                              <div class="fw-bold small mb-2 text-muted">Ảnh công tơ điện</div>
                              <img id="previewElectricImg" src="" alt="Ảnh công tơ điện" class="img-fluid rounded"
                                style="max-height:180px; object-fit:contain; cursor:zoom-in; box-shadow: 0 2px 4px rgba(0,0,0,0.05);"
                                onclick="showFullImage(this.src)">
                            </div>
                          </div>
                          <div class="col-md-6" id="waterImgCol" style="display:none;">
                            <div class="border rounded p-2 text-center bg-light">
                              <div class="fw-bold small mb-2 text-muted">Ảnh công tơ nước</div>
                              <img id="previewWaterImg" src="" alt="Ảnh công tơ nước" class="img-fluid rounded"
                                style="max-height:180px; object-fit:contain; cursor:zoom-in; box-shadow: 0 2px 4px rgba(0,0,0,0.05);"
                                onclick="showFullImage(this.src)">
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div id="previewError" class="alert alert-danger mt-3" style="display:none; border-radius:8px;">
                    </div>
                  </div>

                  <div class="mt-4 pt-3 border-top d-flex gap-2">
                    <button type="submit" class="btn-mintlify-primary">Tạo Hóa Đơn</button>
                    <a href="${ctx}/manager/invoices" class="btn-mintlify-secondary text-decoration-none">Hủy bỏ</a>
                  </div>
              </div>


              </form>
          </div>

          </main>
        </div>
        </div>
        <!-- Modal xem ảnh lớn -->
        <div class="modal fade" id="imageViewerModal" tabindex="-1" aria-hidden="true">
          <div class="modal-dialog modal-dialog-centered modal-xl">
            <div class="modal-content bg-transparent border-0">
              <div class="modal-header border-0 justify-content-end p-2">
                <button type="button" class="btn-close bg-white" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body text-center p-0">
                <img id="modalImagePreview" src=""
                  style="max-width: 100%; max-height: 85vh; border-radius: 8px; object-fit: contain;"
                  alt="Ảnh phóng to">
              </div>
            </div>
          </div>
        </div>


        </div>
        </div>

        <jsp:include page="/WEB-INF/views/layout/footer.jsp" />

        <script>
          var currentPreviewMeterId = null;
          var currentInvoiceData = null;

          function showFullImage(src) {
            document.getElementById('modalImagePreview').src = src;
            var modal = new bootstrap.Modal(document.getElementById('imageViewerModal'));
            modal.show();
          }

          (function () {
            var ctx = '<c:out value="${ctx}"/>';
            var roomCodeSelect = document.getElementById('roomCodeSelect');
            var otherFeeInput = document.getElementById('otherFeeInput');
            var userModifiedOtherFee = ${ not empty param.otherFee ?'true': 'false'};
            if (otherFeeInput) {
              otherFeeInput.addEventListener('input', function () {
                userModifiedOtherFee = true;
              });
            }

            var billingPeriodInput = document.getElementById('billingPeriodInput');
            var previewSection = document.getElementById('invoicePreviewSection');
            var previewError = document.getElementById('previewError');

            function formatMoney(amount) {
              return new Intl.NumberFormat('vi-VN').format(amount) + ' đ';
            }

            function updateInvoicePreview(roomCode, billingPeriod) {
              if (!roomCode || !billingPeriod || billingPeriod.length !== 6) {
                previewSection.style.display = 'none';
                return;
              }

              fetch(ctx + '/manager/invoices?action=getInvoicePreview&roomCode=' + encodeURIComponent(roomCode) + '&billingPeriod=' + encodeURIComponent(billingPeriod))
                .then(function (res) {
                  return res.json().then(function (data) {
                    if (!res.ok || data.error) throw new Error(data.error || 'Lỗi mạng');
                    return data;
                  });
                })
                .then(function (data) {
                  previewSection.style.display = 'block';
                  previewError.style.display = 'none';
                  document.getElementById('previewRoomFee').value = formatMoney(data.roomFee);
                  document.getElementById('previewServiceFee').value = formatMoney(data.serviceFee);
                  document.getElementById('previewInternetFee').value = formatMoney(data.internetFee);

                  document.getElementById('previewOldElectric').textContent = data.oldElectric;
                  document.getElementById('previewNewElectric').value = data.newElectric;
                  document.getElementById('previewElectricPrice').value = formatMoney(data.electricityPrice);

                  document.getElementById('previewOldWater').textContent = data.oldWater;
                  document.getElementById('previewNewWater').value = data.newWater;
                  document.getElementById('previewWaterPrice').value = formatMoney(data.waterPrice);

                  currentPreviewMeterId = data.meterId;
                  currentInvoiceData = data;

                  var ctx = '<c:out value="${ctx}"/>';
                  var electricImgCol = document.getElementById('electricImgCol');
                  var waterImgCol = document.getElementById('waterImgCol');
                  var previewMeterImages = document.getElementById('previewMeterImages');

                  var hasElectricImg = data.electricImg && data.electricImg.trim() !== '';
                  var hasWaterImg = data.waterImg && data.waterImg.trim() !== '';

                  if (hasElectricImg) {
                    document.getElementById('previewElectricImg').src = ctx + data.electricImg;
                    electricImgCol.style.display = 'block';
                  } else {
                    electricImgCol.style.display = 'none';
                  }

                  if (hasWaterImg) {
                    document.getElementById('previewWaterImg').src = ctx + data.waterImg;
                    waterImgCol.style.display = 'block';
                  } else {
                    waterImgCol.style.display = 'none';
                  }

                  if (hasElectricImg || hasWaterImg) {
                    previewMeterImages.style.display = 'block';
                  } else {
                    previewMeterImages.style.display = 'none';
                  }
                })
                .catch(function (err) {
                  currentPreviewMeterId = null;
                  previewSection.style.display = 'none';
                  previewError.style.display = 'block';
                  previewError.textContent = err.message;
                  document.getElementById('previewRoomFee').value = '';
                  document.getElementById('previewServiceFee').value = '';
                  document.getElementById('previewInternetFee').value = '';
                  document.getElementById('previewOldElectric').textContent = '0';
                  document.getElementById('previewNewElectric').value = '';
                  document.getElementById('previewElectricPrice').value = '';
                  document.getElementById('previewOldWater').textContent = '0';
                  document.getElementById('previewNewWater').value = '';
                  document.getElementById('previewWaterPrice').value = '';
                  document.getElementById('previewMeterImages').style.display = 'none';
                });
            }

            if (roomCodeSelect) {
              roomCodeSelect.addEventListener('change', function () {
                userModifiedOtherFee = false;
                var roomCode = roomCodeSelect.value.trim();
                var bp = billingPeriodInput ? billingPeriodInput.value.trim() : '';
                updateInvoicePreview(roomCode, bp);
              });
            }

            function updateAvailableRooms(bp) {
              if (!roomCodeSelect) return;
              var currentRoom = roomCodeSelect.value;
              fetch(ctx + '/manager/invoices?action=getAvailableRooms&billingPeriod=' + encodeURIComponent(bp))
                .then(function (res) { return res.json(); })
                .then(function (rooms) {
                  roomCodeSelect.innerHTML = '<option value="">-- Chọn mã phòng --</option>';
                  var found = false;
                  rooms.forEach(function (r) {
                    var option = document.createElement('option');
                    option.value = r.code;
                    option.textContent = 'Phòng ' + r.code;
                    if (r.code === currentRoom) {
                      option.selected = true;
                      found = true;
                    }
                    roomCodeSelect.appendChild(option);
                  });
                  if (currentRoom && !found) {
                    var option = document.createElement('option');
                    option.value = currentRoom;
                    option.textContent = 'Phòng ' + currentRoom;
                    option.selected = true;
                    roomCodeSelect.appendChild(option);
                  }
                })
                .catch(function (err) { console.error('Lỗi khi tải danh sách phòng:', err); });
            }


            // Tự động kiểm tra nếu đã chọn sẵn mã phòng khi vào trang
            if (roomCodeSelect && roomCodeSelect.value.trim()) {
              if (billingPeriodInput && billingPeriodInput.value.trim().length === 6) {
                updateInvoicePreview(roomCodeSelect.value.trim(), billingPeriodInput.value.trim());
              }
            }
          })();
        </script>
      </body>

      </html>