
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Tạo Hợp đồng - BQL"/>
<c:set var="pageRole" value="MANAGER"/>
<c:set var="activeMenu" value="contracts"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>

<style>
  .form-section-title {
    font-size: 1.1rem;
    font-weight: 700;
    color: var(--hms-text-main);
    margin-bottom: 1.5rem;
    display: flex;
    align-items: center;
    gap: 10px;
    padding-bottom: 10px;
    border-bottom: 1px solid var(--hms-border);
  }
  .form-section-title svg {
    color: var(--hms-primary);
  }
  .form-control, .form-select {
    border-radius: 8px;
    padding: 0.6rem 1rem;
    border: 1px solid #e2e8f0;
    transition: all 0.2s;
  }
  .form-control:focus, .form-select:focus {
    border-color: var(--hms-primary);
    box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.1);
  }
  .sticky-sidebar {
    position: sticky;
    top: 80px;
  }
  .info-box {
    background: linear-gradient(135deg, rgba(59, 130, 246, 0.05) 0%, rgba(59, 130, 246, 0.1) 100%);
    border: 1px solid rgba(59, 130, 246, 0.2);
    border-radius: 12px;
    padding: 20px;
    color: var(--hms-text-secondary);
  }
</style>

<body>
<div class="app-shell">
  <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
  <div class="sidebar-overlay"></div>
  <div class="main-wrapper">
    <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
    <main class="page-content">
      <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-center gap-3">
        <div>
          <a href="${ctx}/manager/contracts" class="text-decoration-none text-muted mb-2 d-inline-block">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-right:4px; margin-top:-2px"><line x1="19" y1="12" x2="5" y2="12"></line><polyline points="12 19 5 12 12 5"></polyline></svg>
            Quay lại danh sách
          </a>
          <h1>Khởi Tạo Hợp Đồng Mới</h1>
          <p>Thiết lập thông tin và các điều khoản cơ bản cho khách thuê phòng</p>
        </div>
      </div>

      <c:if test="${not empty errorMessage}">
          <div class="alert alert-danger mt-3" style="font-weight:bold; font-size: 1.1rem; border-left: 4px solid red; background: #ffe6e6; padding: 15px;">
              <i class="fas fa-exclamation-triangle"></i> CẢNH BÁO: <c:out value="${errorMessage}"/>
          </div>
      </c:if>

      <form method="post" action="${ctx}/manager/contracts/create">
        <input type="hidden" name="csrfToken" value="${csrfToken}">
        <div class="row g-4 mt-2">
          
          <!-- LEFT COLUMN: Form Fields -->
          <div class="col-lg-8">
            
            <!-- Phần 1: Chọn phòng -->
            <div class="data-surface p-4 mb-4 shadow-sm" style="border-radius: 12px;">
              <h4 class="form-section-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg>
                Thông tin Phòng thuê
              </h4>
              <div class="mb-2">
                <label class="form-label fw-bold">Chọn Phòng (đang trống) <span class="text-danger">*</span></label>
                <select name="roomId" class="form-select" required>
                  <option value="">-- Click để chọn phòng --</option>
                  <c:forEach var="r" items="${availableRooms}">
                    <option value="${r.id}" ${not empty preselectedRoomId && r.id == preselectedRoomId ? 'selected' : ''}>Phòng ${r.code}</option>
                  </c:forEach>
                </select>
                <small class="form-text text-muted mt-2 d-block">Lưu ý: Chỉ những phòng đang ở trạng thái <strong>Trống</strong> mới có thể được tạo hợp đồng.</small>
              </div>
            </div>

            <!-- Phần 2: Người thuê -->
            <div class="data-surface p-4 mb-4 shadow-sm" style="border-radius: 12px;">
              <h4 class="form-section-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                Thông tin Người Thuê Đại Diện (Bên B)
              </h4>
              
              <div class="row g-4 mb-3">
                <div class="col-md-6">
                  <label class="form-label fw-bold">Họ và tên <span class="text-danger">*</span></label>
                  <input type="text" name="tenantFullName" class="form-control" required placeholder="Nhập đầy đủ họ tên"/>
                </div>
                <div class="col-md-6">
                  <label class="form-label fw-bold">Ngày sinh</label>
                  <input type="date" name="tenantDob" class="form-control"/>
                </div>
              </div>

              <div class="row g-4 mb-3">
                <div class="col-md-6">
                  <label class="form-label fw-bold">CCCD / CMND <span class="text-danger">*</span></label>
                  <input type="text" name="tenantIdentityNumber" class="form-control" required placeholder="Số thẻ căn cước"/>
                </div>
                <div class="col-md-6">
                  <label class="form-label fw-bold">Số điện thoại</label>
                  <input type="text" name="tenantPhone" class="form-control" placeholder="09xxxxxxxxx"/>
                </div>
              </div>

              <div class="row g-4 mb-3">
                <div class="col-md-6">
                  <label class="form-label fw-bold">Ngày cấp CCCD</label>
                  <input type="date" name="tenantIdentityIssueDate" class="form-control"/>
                </div>
                <div class="col-md-6">
                  <label class="form-label fw-bold">Nơi cấp</label>
                  <input type="text" name="tenantIdentityIssuePlace" class="form-control" placeholder="Cục cảnh sát..."/>
                </div>
              </div>

              <div class="mb-2">
                <label class="form-label fw-bold">Địa chỉ thường trú</label>
                <input type="text" name="tenantPermanentAddress" class="form-control" placeholder="Nhập địa chỉ theo sổ hộ khẩu"/>
              </div>
            </div>

            <!-- Phần 3: Điều khoản hợp đồng -->
            <div class="data-surface p-4 shadow-sm" style="border-radius: 12px;">
              <h4 class="form-section-title">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
                Thông tin Hợp đồng
              </h4>

              <div class="mb-4">
                <label class="form-label fw-bold">Giá thuê (bằng chữ)</label>
                <input type="text" name="amountInWords" class="form-control" placeholder="VD: Ba triệu năm trăm nghìn đồng chẵn"/>
              </div>

              <div class="row g-4">
                <div class="col-md-4">
                  <label class="form-label fw-bold">Ngày ký <span class="text-danger">*</span></label>
                  <input type="date" name="signedDate" class="form-control" required/>
                </div>
                <div class="col-md-4">
                  <label class="form-label fw-bold">Ngày bắt đầu <span class="text-danger">*</span></label>
                  <input type="date" name="startDate" class="form-control" required/>
                </div>
                <div class="col-md-4">
                  <label class="form-label fw-bold">Ngày hết hạn <span class="text-danger">*</span></label>
                  <input type="date" name="endDate" class="form-control" required/>
                </div>
              </div>
            </div>

          </div>
          
          <!-- RIGHT COLUMN: Summary & Submit -->
          <div class="col-lg-4">
            <div class="sticky-sidebar">
              <div class="data-surface p-4 shadow-sm" style="border-radius: 12px;">
                <div class="info-box mb-4">
                  <h5 class="fw-bold mb-3 d-flex align-items-center gap-2">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>
                    Lưu ý thao tác
                  </h5>
                  <ul class="mb-0 ps-3" style="font-size: 0.9rem; line-height: 1.6;">
                    <li class="mb-2">Kiểm tra kỹ số CCCD/CMND để tránh sai sót pháp lý.</li>
                    <li class="mb-2">Trường <strong>Giá thuê (bằng chữ)</strong> sẽ được hiển thị trên bản in cứng (PDF) nên cần ghi chính xác.</li>
                    <li>Sau khi tạo, mã hợp đồng sẽ được hệ thống sinh tự động. Bạn có thể in bản cứng ngay.</li>
                  </ul>
                </div>

                <div class="d-grid gap-3">
                  <button type="submit" class="btn-mintlify-primary w-100" style="padding: 12px; font-size: 1rem;">
                    Tạo Hợp Đồng
                  </button>
                  <a href="${ctx}/manager/contracts" class="btn-mintlify-secondary text-decoration-none w-100 text-center" style="padding: 12px; font-size: 1rem;">
                    Hủy thao tác
                  </a>
                </div>
              </div>
            </div>
          </div>

        </div>
      </form>

    </main>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
<script>
  // Tự động gán ngày hiện tại cho "Ngày ký" và "Ngày bắt đầu"
  document.addEventListener('DOMContentLoaded', function() {
    const today = new Date().toISOString().split('T')[0];
    document.querySelector('input[name="signedDate"]').value = today;
    document.querySelector('input[name="startDate"]').value = today;
  });
</script>
</body>
</html>
