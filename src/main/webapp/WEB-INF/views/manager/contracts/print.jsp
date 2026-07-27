<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Mẫu In Hợp Đồng - ${contract.code}</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <style>
    :root {
      --hms-font: 'Times New Roman', Times, serif;
    }
    body {
      background-color: #525659;
      font-family: var(--hms-font);
      font-size: 13pt;
      line-height: 1.6;
      color: #000;
      margin: 0;
      padding: 0;
    }
    
    .toolbar-header {
      position: sticky;
      top: 0;
      z-index: 1000;
      background: #1e293b;
      color: #fff;
      padding: 12px 24px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      box-shadow: 0 2px 8px rgba(0,0,0,0.2);
    }

    .paper-container {
      width: 210mm;
      min-height: 297mm;
      margin: 20px auto;
      padding: 20mm 25mm;
      background: white;
      box-shadow: 0 4px 20px rgba(0,0,0,0.15);
      box-sizing: border-box;
    }

    .header-national {
      text-align: center;
      margin-bottom: 24px;
    }
    .header-national h4 {
      font-weight: bold;
      font-size: 14pt;
      margin-bottom: 4px;
      text-transform: uppercase;
    }
    .header-national h5 {
      font-weight: bold;
      font-size: 13pt;
      margin-bottom: 8px;
    }
    .header-line {
      width: 160px;
      height: 1px;
      background: #000;
      margin: 0 auto 20px auto;
    }

    .contract-title {
      text-align: center;
      margin: 24px 0 16px 0;
    }
    .contract-title h3 {
      font-weight: bold;
      font-size: 17pt;
      text-transform: uppercase;
      margin-bottom: 4px;
    }
    .contract-title p {
      font-style: italic;
      font-size: 11pt;
    }

    .section-header {
      font-weight: bold;
      font-size: 13pt;
      margin-top: 16px;
      margin-bottom: 8px;
      text-transform: uppercase;
    }

    .field-row {
      margin-bottom: 6px;
    }

    .table-custom {
      width: 100%;
      border-collapse: collapse;
      margin: 12px 0;
    }
    .table-custom th, .table-custom td {
      border: 1px solid #000;
      padding: 6px 10px;
      font-size: 12pt;
    }

    .signature-section {
      margin-top: 40px;
      display: flex;
      justify-content: space-between;
      page-break-inside: avoid;
    }
    .signature-box {
      width: 45%;
      text-align: center;
    }

    @media print {
      body {
        background: white !important;
      }
      .toolbar-header, .no-print {
        display: none !important;
      }
      .paper-container {
        margin: 0 !important;
        box-shadow: none !important;
        width: 100% !important;
        padding: 10mm 15mm !important;
      }
      @page {
        size: A4;
        margin: 10mm;
      }
    }
  </style>
</head>
<body>

  <!-- Floating Toolbar -->
  <div class="toolbar-header no-print">
    <div>
      <h6 class="m-0 font-weight-bold" style="font-family: sans-serif;">📄 Bản In Hợp Đồng: <span class="text-warning">${contract.code}</span></h6>
    </div>
    <div class="d-flex gap-2">
      <button class="btn btn-success font-weight-bold" onclick="window.print();">
        🖨️ In Hợp Đồng (Print)
      </button>
      <a href="${ctx}/manager/contracts/detail?id=${contract.contractId}" class="btn btn-outline-light">
        ← Quay lại Chi Tiết
      </a>
    </div>
  </div>

  <!-- A4 Paper Document Container -->
  <div class="paper-container">
    
    <!-- Quốc hiệu & Tiêu ngữ -->
    <div class="header-national">
      <h4>CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</h4>
      <h5>Độc lập – Tự do – Hạnh phúc</h5>
      <div class="header-line"></div>
    </div>

    <!-- Tên Hợp Đồng -->
    <div class="contract-title">
      <h3>HỢP ĐỒNG THUÊ PHÒNG TRỌ</h3>
      <p>Số: <strong>${contract.code}</strong></p>
    </div>

    <!-- Thời gian & Địa điểm lập -->
    <div class="field-row">
      Hôm nay, ngày <strong>${contract.signedDate != null ? contract.signedDate.dayOfMonth : '__'}</strong> tháng <strong>${contract.signedDate != null ? contract.signedDate.monthValue : '__'}</strong> năm <strong>${contract.signedDate != null ? contract.signedDate.year : '2026'}</strong>, tại địa chỉ: <strong>${contract.room != null && contract.room.facilityId != null ? 'Cơ sở quản lý' : 'Cơ sở cho thuê phòng trọ'}</strong>.
    </div>
    <div class="field-row">
      Chúng tôi gồm các bên dưới đây:
    </div>

    <!-- BÊN A -->
    <div class="section-header">BÊN A (BÊN CHO THUÊ):</div>
    <div class="ps-3 mb-3">
      <div class="field-row">- Đại diện Ban quản lý: <strong>${contract.createdBy != null ? 'Ban Quản Lý Cơ Sở' : 'Đại Diện Cơ Sở'}</strong></div>
      <div class="field-row">- Số điện thoại liên hệ: <strong>0988 123 456</strong></div>
      <div class="field-row">- Địa chỉ cơ sở trọ: <strong>${contract.room != null ? 'Phòng ' : ''}${contract.room != null ? contract.room.code : ''}</strong></div>
    </div>

    <!-- BÊN B -->
    <div class="section-header">BÊN B (BÊN THUÊ PHÒNG):</div>
    <div class="ps-3 mb-3">
      <div class="field-row">- Họ và tên: <strong>${contract.tenantFullName}</strong></div>
      <div class="field-row">- Ngày sinh: <strong>${contract.tenantDob != null ? contract.tenantDob : '______'}</strong></div>
      <div class="field-row">- Số CMND/CCCD: <strong>${contract.tenantIdentityNumber}</strong></div>
      <div class="field-row">- Ngày cấp: <strong>${contract.tenantIdentityIssueDate != null ? contract.tenantIdentityIssueDate : '______'}</strong> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Nơi cấp: <strong>${contract.tenantIdentityIssuePlace != null ? contract.tenantIdentityIssuePlace : '______'}</strong></div>
      <div class="field-row">- Hộ khẩu thường trú: <strong>${contract.tenantPermanentAddress != null ? contract.tenantPermanentAddress : '______'}</strong></div>
      <div class="field-row">- Số điện thoại: <strong>${contract.tenantPhone != null ? contract.tenantPhone : '______'}</strong></div>
    </div>

    <div class="field-row mt-3">
      Hai bên cùng thỏa thuận và thống nhất ký kết Hợp đồng thuê phòng trọ với các điều khoản cụ thể như sau:
    </div>

    <!-- ĐIỀU 1 -->
    <div class="section-header">ĐIỀU 1: THÔNG TIN PHÒNG THUÊ & TRANG THIẾT BỊ</div>
    <div class="ps-3 mb-2">
      <div class="field-row">1.1. Bên A đồng ý cho Bên B thuê 01 phòng ở:</div>
      <div class="field-row ps-3">
        - Mã phòng: <strong>${contract.room != null ? contract.room.code : '______'}</strong> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
        - Tiền cọc đặt trước: <strong><fmt:formatNumber value="${contract.room != null ? contract.room.depositAmount : 0}" pattern="#,##0"/> VNĐ</strong>
      </div>
      <div class="field-row">1.2. Trang thiết bị và cơ sở vật chất sẵn có trong phòng bàn giao bao gồm:</div>
      <div class="field-row ps-3">
        <em>01 bình nóng lạnh, 01 máy điều hòa, 01 tủ quần áo, 01 giường ngủ, 01 bàn học + ghế tựa, hệ thống thiết bị vệ sinh và đèn chiếu sáng hoàn chỉnh.</em>
      </div>
    </div>

    <!-- ĐIỀU 2 -->
    <div class="section-header">ĐIỀU 2: GIÁ THUÊ, THỜI HẠN & CÁC KHOẢN PHÍ</div>
    <div class="ps-3 mb-2">
      <div class="field-row">2.1. Giá thuê phòng: <strong><fmt:formatNumber value="${contract.room != null ? contract.room.roomFee : 0}" pattern="#,##0"/> VNĐ/tháng</strong></div>
      <div class="field-row">2.2. Bằng chữ: <strong>${contract.amountInWords != null ? contract.amountInWords : '______________________________________'}</strong></div>
      <div class="field-row">2.3. Thời hạn hợp đồng: Kể từ ngày <strong>${contract.startDate != null ? contract.startDate : '______'}</strong> đến ngày <strong>${contract.endDate != null ? contract.endDate : '______'}</strong>.</div>
      <div class="field-row">2.4. Tiền cọc giữ phòng: Bên B đã giao cho Bên A số tiền cọc là <strong><fmt:formatNumber value="${contract.room != null ? contract.room.depositAmount : 0}" pattern="#,##0"/> VNĐ</strong>.</div>
      <div class="field-row">2.5. Các khoản chi phí dịch vụ hàng tháng:</div>
      <div class="ps-3">
        - Tiền điện: Tính theo chỉ số đồng hồ công tơ thực tế.<br/>
        - Tiền nước, internet, rác thải & vệ sinh chung: Theo biểu giá niêm yết của cơ sở.
      </div>
      <div class="field-row">2.6. Quy định thanh toán: Bên B thanh toán tiền phòng và dịch vụ định kỳ từ ngày 01 đến ngày 05 hàng tháng.</div>
    </div>

    <!-- ĐIỀU 3 -->
    <div class="section-header">ĐIỀU 3: TRÁCH NHIỆM CỦA CÁC BÊN</div>
    <div class="ps-3 mb-2">
      <div class="field-row"><strong>3.1. Trách nhiệm của Bên A:</strong></div>
      <div class="ps-3">
        - Bàn giao phòng và các thiết bị kèm theo đúng tình trạng hoạt động tốt.<br/>
        - Đảm bảo nguồn cấp điện, nước sinh hoạt và đường truyền internet cho Bên B sử dụng.<br/>
        - Hướng dẫn Bên B chấp hành đúng các quy định tạm trú của địa phương.
      </div>
      <div class="field-row mt-2"><strong>3.2. Trách nhiệm của Bên B:</strong></div>
      <div class="ps-3">
        - Thanh toán tiền phòng và các khoản phí dịch vụ đầy đủ, đúng hạn từ ngày 01 đến 05 hàng tháng.<br/>
        - Giữ gìn, bảo quản tài sản, thiết bị trong phòng. Không tự ý cải tạo, sửa chữa khi chưa có sự đồng ý của Bên A.<br/>
        - Tuân thủ quy định phòng cháy chữa cháy, giữ gìn an ninh trật tự và vệ sinh chung.<br/>
        - Không tàng trữ chất cấm, cờ bạc, hoặc thực hiện hành vi vi phạm pháp luật trong khu vực trọ.
      </div>
    </div>

    <!-- ĐIỀU 4 -->
    <div class="section-header">ĐIỀU 4: ĐIỀU KHOẢN CHUNG</div>
    <div class="ps-3 mb-2">
      <div class="field-row">- Hai bên cam kết thực hiện đúng và đầy đủ các điều khoản đã ghi trong Hợp đồng.</div>
      <div class="field-row">- Trường hợp chấm dứt hợp đồng trước thời hạn, bên muốn chấm dứt phải thông báo trước ít nhất 30 ngày. Nếu Bên B đơn phương hủy hợp đồng trước hạn sẽ không được hoàn lại tiền cọc.</div>
      <div class="field-row">- Hợp đồng này được lập thành 02 bản có giá trị pháp lý như nhau, mỗi bên giữ 01 bản để làm căn cứ thực hiện.</div>
    </div>

    <!-- CHỮ KÝ 2 BÊN -->
    <div class="signature-section">
      <div class="signature-box">
        <p class="fw-bold mb-1">ĐẠI DIỆN BÊN B</p>
        <p class="fst-italic text-muted mb-5">(Ký, ghi rõ họ tên)</p>
        <div style="height: 60px;"></div>
        <p class="fw-bold fs-6"><strong>${contract.tenantFullName}</strong></p>
      </div>

      <div class="signature-box">
        <p class="fw-bold mb-1">ĐẠI DIỆN BÊN A</p>
        <p class="fst-italic text-muted mb-5">(Ký, ghi rõ họ tên)</p>
        <div style="height: 60px;"></div>
        <p class="fw-bold fs-6"><strong>BAN QUẢN LÝ CƠ SỞ</strong></p>
      </div>
    </div>

  </div>

</body>
</html>
