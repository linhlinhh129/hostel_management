<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <c:set var="ctx" value="${pageContext.request.contextPath}" />
            <c:set var="pageTitle" value="Chi tiết hóa đơn - Cổng cư dân" />
            <c:set var="pageRole" value="TENANT" />
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

                            <div class="page-header hero-sky-gradient">
                                <div class="d-flex justify-content-between align-items-center flex-wrap">
                                    <div>
                                        <h1>Hóa đơn kỳ
                                            <c:out value="${invoice.billingPeriod}" />
                                        </h1>
                                        <p>Mã:
                                            <c:out value="${invoice.code}" />
                                        </p>
                                    </div>
                                    <div>
                                        <a href="${ctx}/tenant/invoices"
                                            class="btn-mintlify-secondary text-decoration-none">
                                            ← Danh sách hóa đơn
                                        </a>
                                    </div>
                                </div>
                            </div>

                            <c:if test="${overdueDays > 0}">
                                <div class="alert alert-danger d-flex align-items-center gap-3 mb-4"
                                    style="background-color: #fef2f2; border: 1px solid #fca5a5; border-radius: var(--hms-radius-lg); padding: 1rem 1.25rem; color: #991b1b;">
                                    <div style="font-size: 1.75rem; line-height: 1;">⚠</div>
                                    <div>
                                        <h4
                                            style="margin: 0 0 0.25rem 0; font-weight: 700; font-size: 1rem; color: #991b1b;">
                                            Hóa đơn quá hạn thanh toán!</h4>
                                        <p style="margin: 0; font-size: 0.875rem; color: #b91c1c; line-height: 1.5;">
                                            Hóa đơn này đã trễ hạn thanh toán <strong>${overdueDays} ngày</strong> (Hạn
                                            chót: <strong>
                                                <c:out value="${invoice.dueDate}" />
                                            </strong>).
                                            Số tiền phạt trễ hạn được áp dụng là <strong>1% giá trị tiền phòng mỗi
                                                ngày</strong>, tương đương <strong>
                                                <fmt:formatNumber value="${penaltyAmount}" pattern="#,##0" /> đ
                                            </strong>.
                                        </p>
                                    </div>
                                </div>
                            </c:if>

                            <div class="row g-3">
                                <div class="col-lg-8">
                                    <%-- Chi tiết các khoản --%>
                                        <div class="widget-surface mb-3">
                                            <div class="widget-surface-header">
                                                <h3>Chi tiết khoản phí</h3>
                                            </div>
                                            <div class="widget-surface-body p-0">
                                                <table class="table-mintlify" style="font-size:0.875rem">
                                                    <tbody>
                                                        <tr>
                                                            <td style="padding:10px 1.25rem">Tiền phòng cố định</td>
                                                            <td
                                                                style="padding:10px 1.25rem;text-align:right;font-weight:600">
                                                                <fmt:formatNumber value="${invoice.roomFee}"
                                                                    pattern="#,##0" /> đ
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding:10px 1.25rem">
                                                                Tiền điện
                                                                <c:if test="${invoice.electricStatus eq 'REPLACED' or invoice.electricStatus eq 'ROLLOVER'}">
                                                                    <span class="text-danger fw-bold ms-1" title="Có tính toán đặc biệt, xem chi tiết bên dưới">*</span>
                                                                </c:if>
                                                                <span
                                                                    style="color:var(--hms-stone);font-size:0.75rem;display:block;">
                                                                    <c:choose>
                                                                        <c:when test="${invoice.electricStatus eq 'REPLACED' or invoice.electricStatus eq 'ROLLOVER'}">
                                                                            (Tổng tiêu thụ: ${invoice.electricUsage} kWh)
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            (${invoice.oldElectricReading} → ${invoice.newElectricReading} = ${invoice.electricUsage} kWh)
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </span>
                                                            </td>
                                                            <td
                                                                style="padding:10px 1.25rem;text-align:right;font-weight:600">
                                                                <fmt:formatNumber value="${invoice.electricAmount}"
                                                                    pattern="#,##0" /> đ
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding:10px 1.25rem">
                                                                Tiền nước
                                                                <span
                                                                    style="color:var(--hms-stone);font-size:0.75rem;display:block;">
                                                                    <c:choose>
                                                                        <c:when test="${invoice.waterStatus eq 'REPLACED' or invoice.waterStatus eq 'ROLLOVER'}">
                                                                            (Tổng tiêu thụ: ${invoice.waterUsage} m³)
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            (${invoice.oldWaterReading} → ${invoice.newWaterReading} = ${invoice.waterUsage} m³)
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </span>
                                                            </td>
                                                            <td
                                                                style="padding:10px 1.25rem;text-align:right;font-weight:600">
                                                                <fmt:formatNumber value="${invoice.waterAmount}"
                                                                    pattern="#,##0" /> đ
                                                            </td>
                                                        </tr>
                                                        <tr>
                                                            <td style="padding:10px 1.25rem">Phí dịch vụ</td>
                                                            <td
                                                                style="padding:10px 1.25rem;text-align:right;font-weight:600">
                                                                <fmt:formatNumber value="${invoice.serviceFee}"
                                                                    pattern="#,##0" /> đ
                                                            </td>
                                                        </tr>
                                                        <c:if
                                                            test="${not empty invoice.otherFee and invoice.otherFee > 0}">
                                                            <tr>
                                                                <td style="padding:10px 1.25rem">Phí khác</td>
                                                                <td
                                                                    style="padding:10px 1.25rem;text-align:right;font-weight:600">
                                                                    <fmt:formatNumber value="${invoice.otherFee}"
                                                                        pattern="#,##0" /> đ
                                                                </td>
                                                            </tr>
                                                        </c:if>
                                                        <c:if test="${overdueDays > 0}">
                                                            <tr>
                                                                <td
                                                                    style="padding:10px 1.25rem;color:var(--hms-danger)">
                                                                    Phí phạt trễ hạn<span class="text-danger fw-bold ms-1" title="Có tính toán đặc biệt">*</span>
                                                                    <span
                                                                    <span style="color:var(--hms-stone);font-size:0.75rem;display:block;">
                                                                        (Xem chi tiết bên dưới)
                                                                    </span>
                                                                </td>
                                                                <td
                                                                    style="padding:10px 1.25rem;text-align:right;font-weight:600;color:var(--hms-danger)">
                                                                    +
                                                                    <fmt:formatNumber value="${penaltyAmount}"
                                                                        pattern="#,##0" /> đ
                                                                </td>
                                                            </tr>
                                                        </c:if>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>

                                        <c:if test="${not empty invoice.electricImg or not empty invoice.waterImg}">
                                            <div class="widget-surface mb-3">
                                                <div class="widget-surface-header">
                                                    <h3>Ảnh chỉ số điện nước</h3>
                                                </div>
                                                <div class="widget-surface-body">
                                                    <div class="row g-3">
                                                        <c:if test="${not empty invoice.electricImg}">
                                                            <div class="col-md-6">
                                                                <div class="card border h-100"
                                                                    style="border-radius: 8px; overflow: hidden; background: #fafafa;">
                                                                    <div
                                                                        style="padding: 10px 15px; background: #f1f5f9; border-bottom: 1px solid #e2e8f0; font-weight: 600; font-size: 0.875rem;">
                                                                        Ảnh công tơ điện
                                                                    </div>
                                                                    <div class="p-2 text-center">
                                                                        <c:url value="${invoice.electricImg}"
                                                                            var="electricImgUrl" />
                                                                        <img src="${electricImgUrl}"
                                                                            alt="Ảnh chỉ số điện"
                                                                            title="Click để phóng to ảnh"
                                                                            style="max-width: 100%; max-height: 250px; border-radius: 6px; object-fit: contain; box-shadow: 0 2px 4px rgba(0,0,0,0.05); cursor: zoom-in; transition: transform 0.2s;"
                                                                            onmouseover="this.style.transform='scale(1.02)';"
                                                                            onmouseout="this.style.transform='scale(1)';"
                                                                            onclick="showFullImage(this.src)">
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </c:if>
                                                        <c:if test="${not empty invoice.waterImg}">
                                                            <div class="col-md-6">
                                                                <div class="card border h-100"
                                                                    style="border-radius: 8px; overflow: hidden; background: #fafafa;">
                                                                    <div
                                                                        style="padding: 10px 15px; background: #f1f5f9; border-bottom: 1px solid #e2e8f0; font-weight: 600; font-size: 0.875rem;">
                                                                        Ảnh công tơ nước
                                                                    </div>
                                                                    <div class="p-2 text-center">
                                                                        <c:url value="${invoice.waterImg}"
                                                                            var="waterImgUrl" />
                                                                        <img src="${waterImgUrl}" alt="Ảnh chỉ số nước"
                                                                            title="Click để phóng to ảnh"
                                                                            style="max-width: 100%; max-height: 250px; border-radius: 6px; object-fit: contain; box-shadow: 0 2px 4px rgba(0,0,0,0.05); cursor: zoom-in; transition: transform 0.2s;"
                                                                            onmouseover="this.style.transform='scale(1.02)';"
                                                                            onmouseout="this.style.transform='scale(1)';"
                                                                            onclick="showFullImage(this.src)">
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>
                                        <c:if test="${invoice.electricStatus eq 'ROLLOVER' or invoice.waterStatus eq 'ROLLOVER' or overdueDays > 0}">
                                            <div class="widget-surface mb-3">
                                                <div class="widget-surface-header d-flex align-items-center">
                                                    <i class="fas fa-info-circle text-danger me-2"></i>
                                                    <h3 class="text-danger mb-0">Chi tiết tính toán đặc biệt</h3>
                                                </div>
                                                <div class="widget-surface-body">
                                                    <div class="d-flex flex-column gap-2">

                                                        <c:if test="${invoice.electricStatus eq 'ROLLOVER'}">
                                                            <div class="p-3 w-100 text-start" style="background-color: var(--hms-primary-soft); border: 1px solid var(--hms-primary); border-radius: 8px; font-size: 0.9rem; color: var(--hms-primary-dark);">
                                                                <div style="font-weight: 600; margin-bottom: 4px;"><span style="color: var(--hms-primary-dark); margin-right: 4px;">⏮</span> Tràn vòng đồng hồ điện</div>
                                                                <div style="font-family: monospace; opacity: 0.9;">(Tối đa - ${invoice.oldElectricReading}) + ${invoice.newElectricReading} = <strong>${invoice.electricUsage} kWh</strong></div>
                                                            </div>
                                                        </c:if>


                                                        <c:if test="${invoice.waterStatus eq 'ROLLOVER'}">
                                                            <div class="p-3 w-100 text-start" style="background-color: var(--hms-primary-soft); border: 1px solid var(--hms-primary); border-radius: 8px; font-size: 0.9rem; color: var(--hms-primary-dark);">
                                                                <div style="font-weight: 600; margin-bottom: 4px;"><span style="color: var(--hms-primary-dark); margin-right: 4px;">⏮</span> Tràn vòng đồng hồ nước</div>
                                                                <div style="font-family: monospace; opacity: 0.9;">(Tối đa - ${invoice.oldWaterReading}) + ${invoice.newWaterReading} = <strong>${invoice.waterUsage} m³</strong></div>
                                                            </div>
                                                        </c:if>

                                                        <c:if test="${overdueDays > 0}">
                                                            <div class="p-3 w-100 text-start" style="background-color: var(--hms-primary-soft); border: 1px solid var(--hms-primary); border-radius: 8px; font-size: 0.9rem; color: var(--hms-primary-dark);">
                                                                <div style="font-weight: 600; margin-bottom: 4px;"><span style="color: var(--hms-primary-dark); margin-right: 4px;">⏰</span> Phí chậm nộp</div>
                                                                <div style="font-family: monospace; opacity: 0.9;"><fmt:formatNumber value="${invoice.roomFee}" pattern="#,##0" />đ × 1% × ${overdueDays} ngày = <strong><fmt:formatNumber value="${penaltyAmount}" pattern="#,##0" /> đ</strong></div>
                                                            </div>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>

                                        <c:if test="${not empty invoice.note}">
                                            <div class="widget-surface mb-3">
                                                <div class="widget-surface-header">
                                                    <h3>Ghi chú</h3>
                                                </div>
                                                <div class="widget-surface-body">
                                                    <p style="font-size:0.875rem;margin:0">
                                                        <c:out value="${invoice.note}" />
                                                    </p>
                                                </div>
                                            </div>
                                        </c:if>
                                </div>

                                <div class="col-lg-4">
                                    <%-- Tổng tiền nổi bật --%>
                                        <div class="widget-surface mb-3"
                                            style="border: 2px solid ${invoice.status == 'OVERDUE' ? 'var(--hms-danger)' : invoice.status == 'PAID' ? 'var(--hms-success)' : 'var(--hms-warning)'}; box-shadow: none;">
                                            <div class="widget-surface-body text-center">
                                                <div
                                                    style="font-size:0.875rem;font-weight:700;text-transform:uppercase;letter-spacing:0.05em;color:var(--hms-stone);margin-bottom:0.5rem">
                                                    Tổng cộng phải trả
                                                </div>
                                                <div
                                                    style="font-size:2.25rem;font-weight:800;letter-spacing:-1px;margin-bottom:0.5rem;
                                        color:${invoice.status == 'PAID' ? 'var(--hms-success)' : (invoice.status == 'OVERDUE' or overdueDays > 0) ? 'var(--hms-danger)' : 'var(--hms-ink)'}">
                                                    <fmt:formatNumber value="${totalAmountToPay}" pattern="#,##0" /> đ
                                                </div>

                                                <div class="mb-2">
                                                    <c:choose>
                                                        <c:when
                                                            test="${invoice.meterReadingStatus == 'REPORTED' or invoice.isMeterReported()}">
                                                            <span class="badge-hms badge-warning"
                                                                style="background-color:#f59e0b;color:#fff">⚠️ Đang xử
                                                                lý sai số điện nước</span>
                                                        </c:when>
                                                        <c:when test="${invoice.status == 'PAID'}">
                                                            <span class="badge-hms badge-success">✓ Đã thanh toán</span>
                                                        </c:when>
                                                        <c:when test="${invoice.hasPendingPayment}">
                                                            <span class="badge-hms badge-info">⌛ Chờ duyệt</span>
                                                        </c:when>
                                                        <c:when test="${invoice.status == 'OVERDUE'}">
                                                            <span class="badge-hms badge-danger">⚠ Quá hạn</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge-hms badge-warning">Chưa thanh toán</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </div>
                                                <div style="font-size:0.8125rem;color:var(--hms-stone)">
                                                    Hạn thanh toán: <strong>
                                                        <c:out value="${invoice.dueDate}" />
                                                    </strong>
                                                </div>
                                            </div>
                                        </div>

                                        <%-- Hướng dẫn thanh toán & VNPAY --%>
                                            <c:choose>
                                                <c:when
                                                    test="${invoice.meterReadingStatus == 'REPORTED' or invoice.isMeterReported()}">
                                                    <div class="widget-surface"
                                                        style="background:#fffbeb; border:1px solid #fde68a;">
                                                        <div class="widget-surface-body text-center p-3">
                                                            <div style="font-size:1.5rem;margin-bottom:0.25rem">⚠️</div>
                                                            <h4
                                                                style="font-size:0.9375rem;font-weight:700;color:#92400e;margin-bottom:0.25rem">
                                                                Đang xử lý sai số điện nước</h4>
                                                            <p
                                                                style="font-size:0.8125rem;color:#b45309;margin:0;line-height:1.5">
                                                                Hóa đơn này đang được nhân viên vận hành kiểm tra và xác
                                                                minh lại chỉ số điện nước. Tạm thời nút thanh toán bị
                                                                khóa cho tới khi có số liệu cập nhật.
                                                            </p>
                                                        </div>
                                                    </div>
                                                </c:when>
                                                <c:when
                                                    test="${not invoice.hasPendingPayment and (invoice.status == 'UNPAID' or invoice.status == 'OVERDUE')}">
                                                    <div class="widget-surface"
                                                        style="background:var(--hms-accent-bg); border-color:var(--hms-accent);">
                                                        <div class="widget-surface-header border-bottom-0 pb-0">
                                                            <h3 style="color:var(--hms-ink)">💳 Thanh toán</h3>
                                                        </div>
                                                        <div class="widget-surface-body">
                                                            <div
                                                                style="font-size:0.8125rem;color:var(--hms-slate);line-height:1.7;margin-bottom:1rem">
                                                                <div>Ngân hàng: <strong>Vietcombank</strong></div>
                                                                <div>Số tài khoản: <strong
                                                                        style="font-family:var(--hms-font-mono)">1234567890</strong>
                                                                </div>
                                                                <div>Chủ tài khoản: <strong>Công ty Quản lý Nhà
                                                                        trọ</strong></div>
                                                                <div>Nội dung CK: <strong
                                                                        style="font-family:var(--hms-font-mono)">
                                                                        <c:out value="${invoice.code}" />
                                                                    </strong></div>
                                                            </div>
                                                            <hr />
                                                            <div class="d-grid mt-3">
                                                                <form method="post"
                                                                    action="${ctx}/tenant/payment/create">
                                                                    <input type="hidden" name="csrfToken"
                                                                        value="${csrfToken}" />
                                                                    <input type="hidden" name="invoiceId"
                                                                        value="${invoice.id}" />
                                                                    <input type="hidden" name="amount"
                                                                        value="${totalAmountToPay}" />
                                                                    <button type="submit"
                                                                        class="btn btn-mintlify-primary w-100">
                                                                        Thanh toán qua VNPAY
                                                                    </button>
                                                                </form>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:when>
                                            </c:choose>
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
                                <button type="button" class="btn-close bg-white" data-bs-dismiss="modal"
                                    aria-label="Close"></button>
                            </div>
                            <div class="modal-body text-center p-0">
                                <img id="modalImagePreview" src=""
                                    style="max-width: 100%; max-height: 85vh; border-radius: 8px; object-fit: contain;"
                                    alt="Ảnh phóng to">
                            </div>
                        </div>
                    </div>
                </div>
                <jsp:include page="/WEB-INF/views/layout/footer.jsp" />