<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="Chi tiết Hợp đồng ${contract.code}" scope="request" />
<c:set var="pageRole" value="TENANT" scope="request" />
<c:set var="activeMenu" value="contracts" scope="request" />

<jsp:include page="/WEB-INF/views/layout/head.jsp" />

<style>
  /* Styling cho container hiển thị hợp đồng trên màn hình */
  .document-viewer-wrapper {
    background-color: #f1f5f9;
    padding: 30px;
    border-radius: 12px;
    display: flex;
    justify-content: center;
    overflow-x: auto;
  }

  .a4-container {
    width: 210mm;
    min-height: 297mm;
    padding: 20mm 25mm; /* Tăng lề trái phải lên 25mm */
    background: white;
    box-shadow: 0 4px 24px rgba(0,0,0,0.08);
    font-family: 'Times New Roman', serif;
    font-size: 12pt;
    line-height: 1.5;
    color: #000;
  }
  .a4-container h1, .a4-container h2, .a4-container h3, .a4-container h4, .a4-container h5, .a4-container h6 {
    font-family: 'Times New Roman', serif;
    font-weight: bold;
    text-align: center;
    color: #000;
  }
  .a4-container .text-center { text-align: center; }
  .a4-container .text-right { text-align: right; }
  .a4-container .text-bold { font-weight: bold; }
  .a4-container .mt-4 { margin-top: 1.5rem; }
  .a4-container .mb-4 { margin-bottom: 1.5rem; }
  .a4-container p { margin-bottom: 0.5rem; }
  .a4-container ul { list-style-type: none; padding-left: 0; }
  .a4-container ul li::before { content: "- "; }
  
  @media print {
    @page {
      margin: 15mm;
    }
    body {
      background: white !important;
    }
    /* Hide layout elements completely */
    .sidebar, .topbar, .page-header, .sidebar-overlay, .main-footer, .no-print {
      display: none !important;
    }
    /* Reset wrappers to allow natural flow for print */
    .app-shell, .main-wrapper, .page-content, .document-viewer-wrapper {
      display: block !important;
      position: static !important;
      height: auto !important;
      width: auto !important;
      overflow: visible !important;
      margin: 0 !important;
      padding: 0 !important;
      background: transparent !important;
    }
    /* Expand A4 container to fit print page naturally */
    .a4-container {
      position: static !important;
      box-shadow: none !important;
      width: 100% !important;
      padding: 0 !important;
      margin: 0 !important;
      min-height: auto !important;
    }
  }
</style>

<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
        
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-center gap-3"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div>
                    <a href="${ctx}/tenant/contracts" class="text-decoration-none text-muted mb-2 d-inline-block">← Quay lại danh sách</a>
                    <h1>Chi tiết Hợp đồng: <c:out value="${contract.code}"/></h1>
                    <p>Phòng / Căn hộ: <span class="fw-bold"><c:out value="${contract.room != null ? contract.room.code : contract.roomId}"/></span></p>
                </div>
                <div class="d-flex gap-2 align-items-center mt-2 mt-md-0">
                    <c:choose>
                        <c:when test="${contract.status == 'ACTIVE'}">
                            <span class="badge bg-soft-success text-success px-3 py-2 me-2">Đang hiệu lực</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge bg-soft-secondary text-secondary px-3 py-2 me-2">Đã kết thúc</span>
                        </c:otherwise>
                    </c:choose>
                    <button onclick="window.print()" class="btn-mintlify-primary">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" style="margin-right:6px">
                            <polyline points="6 9 6 2 18 2 18 9"></polyline><path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"></path><rect x="6" y="14" width="12" height="8"></rect>
                        </svg>
                        In Hợp Đồng / Lưu PDF
                    </button>
                </div>
            </div>

            <div class="document-viewer-wrapper mt-4">
                <!-- A4 Document embedded visually inside the dashboard -->
                <div class="a4-container">
                    <div class="text-center mb-4">
                        <h4 class="mb-1">CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</h4>
                        <h5 class="mb-2"><u>Độc lập - Tự do - Hạnh phúc</u></h5>
                    </div>

                    <h3 class="text-center mt-4 mb-4">HỢP ĐỒNG THUÊ PHÒNG TRỌ</h3>
                    
                    <p>Hôm nay, ngày <strong><c:out value="${contract.signedDay}"/>/<c:out value="${contract.signedMonth}"/>/<c:out value="${contract.signedYear}"/></strong>, tại địa chỉ: <strong><c:out value="${contract.facility.address}"/></strong></p>
                    <p>Chúng tôi gồm:</p>
                    
                    <div class="mt-3 mb-3">
                        <p class="text-bold">1. Đại diện bên cho thuê phòng trọ (Bên A)</p>
                        <p>Ông/Bà: <strong>Ban Quản lý Khu trọ</strong></p>
                        <p>Địa chỉ cơ sở: <strong><c:out value="${contract.facility != null ? contract.facility.address : '---'}"/></strong></p>
                    </div>

                    <div class="mb-3">
                        <p class="text-bold">2. Bên thuê phòng trọ (Bên B)</p>
                        <p>Ông/Bà: <strong><c:out value="${contract.tenantFullName}"/></strong></p>
                        <p>Sinh ngày: <strong>
                            <fmt:parseDate value="${contract.tenantDob}" pattern="yyyy-MM-dd" var="tDob" type="date" />
                            <fmt:formatDate value="${tDob}" pattern="dd/MM/yyyy" />
                        </strong></p>
                        <p>Nơi đăng ký hộ khẩu thường trú: <strong><c:out value="${contract.tenantPermanentAddress}"/></strong></p>
                        <p>Số CMND/CCCD: <strong><c:out value="${contract.tenantIdentityNumber}"/></strong>, cấp ngày <strong>
                            <fmt:parseDate value="${contract.tenantIdentityIssueDate}" pattern="yyyy-MM-dd" var="tIssue" type="date" />
                            <fmt:formatDate value="${tIssue}" pattern="dd/MM/yyyy" />
                        </strong>, tại <strong><c:out value="${contract.tenantIdentityIssuePlace}"/></strong></p>
                        <p>Số điện thoại: <strong><c:out value="${contract.tenantPhone}"/></strong></p>
                    </div>

                    <p>Sau khi bàn bạc trên tinh thần dân chủ, hai bên cùng có lợi, cùng thống nhất như sau:</p>

                    <p class="text-bold mt-4" style="text-decoration: underline">Điều 1: Nội dung thuê phòng</p>
                    <p>Bên A đồng ý cho bên B thuê 01 phòng ở tại địa chỉ: <strong><c:out value="${contract.facility != null ? contract.facility.address : '---'}"/></strong></p>
                    
                    <p class="text-bold mt-4" style="text-decoration: underline">Điều 2: Giá thuê và hình thức thanh toán</p>
                    <p>Giá thuê: <strong><fmt:formatNumber value="${contract.room.roomFee}" pattern="#,##0"/> đ/tháng</strong></p>
                    <p>Bằng chữ: <strong><c:out value="${contract.amountInWords}"/></strong></p>
                    <p>Phòng số: <strong><c:out value="${contract.room != null ? contract.room.code : contract.roomId}"/></strong></p>
                    <p>Hình thức thanh toán: Tiền mặt hoặc chuyển khoản vào đầu tháng, từ ngày 01 đến ngày 05 hàng tháng.</p>
                    <p>Hợp đồng có giá trị kể từ <strong>
                        <fmt:parseDate value="${contract.startDate}" pattern="yyyy-MM-dd" var="sDt" type="date" />
                        <fmt:formatDate value="${sDt}" pattern="dd/MM/yyyy" />
                    </strong> đến <strong>
                        <fmt:parseDate value="${contract.endDate}" pattern="yyyy-MM-dd" var="eDt" type="date" />
                        <fmt:formatDate value="${eDt}" pattern="dd/MM/yyyy" />
                    </strong></p>
                    <p>Tiền điện: <strong><fmt:formatNumber value="${contract.facility != null ? contract.facility.electricityPrice : 0}" pattern="#,##0"/> đ/số</strong>, tính theo chỉ số công tơ, thanh toán vào cuối các tháng.</p>
                    <p>Tiền nước: <strong><fmt:formatNumber value="${contract.facility != null ? contract.facility.waterPrice : 0}" pattern="#,##0"/> đ/Khối</strong></p>
                    <p>Tiền Internet: <strong><fmt:formatNumber value="${contract.facility != null ? contract.facility.internetFee : 0}" pattern="#,##0"/> đ/phòng/tháng</strong></p>
                    <p>Tiền dịch vụ chung: <strong><fmt:formatNumber value="${contract.facility != null ? contract.facility.serviceFee : 0}" pattern="#,##0"/> đ/phòng/tháng</strong></p>
                    <p>Bên B đặt cọc cho bên A số tiền là: <strong><fmt:formatNumber value="${contract.room != null ? contract.room.depositAmount : 0}" pattern="#,##0"/> đ</strong></p>
                    <ul>
                        <li>Tiền cọc sẽ được hoàn trả đầy đủ cho bên thuê khi hợp đồng này kết thúc và bên thuê hoàn trả đầy đủ chi phí thuê.</li>
                        <li>Trường hợp bên B hủy hợp đồng trước thời hạn, bên B sẽ không được hoàn trả số tiền đã đặt cọc.</li>
                    </ul>
                    
                    <p class="text-bold mt-4" style="text-decoration: underline">Điều 3: Trách nhiệm chung</p>
                    <ul>
                        <li>Hai bên phải tạo điều kiện cho nhau thực hiện hợp đồng.</li>
                        <li>Một trong hai bên muốn chấm dứt hợp đồng trước thời hạn thì phải báo trước cho bên kia ít nhất 30 ngày và hai bên phải có sự thống nhất.</li>
                        <li>Trường hợp xảy ra tranh chấp hoặc một bên vi phạm hợp đồng thì hai bên cùng nhau giải quyết tranh chấp. Nếu không giải quyết được thì yêu cầu cơ quan có thẩm quyền giải quyết.</li>
                        <li>Hợp đồng được lập thành 02 bản có giá trị pháp lý như nhau, mỗi bên giữ 01 bản.</li>
                    </ul>

                    <div class="d-flex justify-content-between" style="display: flex; justify-content: space-around; margin-top: 80px; padding-bottom: 50px;">
                        <div class="text-center">
                            <p class="text-bold">ĐẠI DIỆN BÊN B</p>
                            <p><em>(Ký, ghi rõ họ tên)</em></p>
                            <br/><br/><br/><br/>
                            <p><strong><c:out value="${contract.tenantFullName}"/></strong></p>
                        </div>
                        <div class="text-center">
                            <p class="text-bold">ĐẠI DIỆN BÊN A</p>
                            <p><em>(Ký, ghi rõ họ tên)</em></p>
                            <br/><br/><br/><br/>
                            <p><strong>Đại diện Ban Quản Lý</strong></p>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>

<jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
</html>
