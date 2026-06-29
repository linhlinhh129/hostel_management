<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết công nợ"/>
<c:set var="pageRole" value="MANAGER"/>
<c:set var="activeMenu" value="debts"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-center gap-3">
                <div>
                    <h1 class="mb-1">Chi tiết công nợ <c:out value="${debt.invoiceCode}"/></h1>
                    <p class="mb-0 text-muted">Phòng: <c:out value="${debt.roomCode}"/> - Cơ sở: <c:out value="${debt.facilityName}"/></p>
                </div>
                <div class="d-flex flex-column align-items-end gap-2" style="position:relative;z-index:1">
                    <a href="${ctx}/manager/debts" class="btn-mintlify-secondary text-decoration-none">← Danh sách</a>
                    <div class="d-flex align-items-center gap-2 flex-wrap">
                        <a href="${ctx}/manager/invoices/${debt.invoiceId}" class="btn-mintlify-primary text-decoration-none">Xem hóa đơn gốc</a>
                        <c:choose>
                            <c:when test="${debt.status == 'UNPAID'}">
                                <span class="badge-hms badge-warning" style="font-size:0.9rem;padding:6px 14px">Chưa thanh toán</span>
                            </c:when>
                            <c:when test="${debt.status == 'OVERDUE'}">
                                <span class="badge-hms badge-danger" style="font-size:0.9rem;padding:6px 14px">Quá hạn</span>
                            </c:when>
                        </c:choose>
                    </div>
                </div>
            </div>

            <style>
                /* Shrink table font size on screen to fit without horizontal scroll */
                @media screen {
                    .table-mintlify { font-size: 0.85rem; }
                    .table-mintlify th, .table-mintlify td { padding: 0.5rem !important; }
                }
                .table-mintlify th, .table-mintlify td {
                    white-space: nowrap;
                }
            </style>

            <div class="row g-4">
                <div class="col-md-8">
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
                                        <td style="text-align:right"><fmt:formatNumber value="${debt.roomFee}" pattern="#,##0"/> đ</td>
                                    </tr>
                                    <tr>
                                        <td><strong>Tiền điện</strong></td>
                                        <td><c:out value="${debt.oldElectricReading}"/></td>
                                        <td><c:out value="${debt.newElectricReading}"/></td>
                                        <td><c:out value="${debt.electricUsage}"/></td>
                                        <td style="text-align:right"><fmt:formatNumber value="${debt.electricUnitPrice}" pattern="#,##0"/></td>
                                        <td style="text-align:right"><fmt:formatNumber value="${debt.electricAmount}" pattern="#,##0"/> đ</td>
                                    </tr>
                                    <tr>
                                        <td><strong>Tiền nước</strong></td>
                                        <td><c:out value="${debt.oldWaterReading}"/></td>
                                        <td><c:out value="${debt.newWaterReading}"/></td>
                                        <td><c:out value="${debt.waterUsage}"/></td>
                                        <td style="text-align:right"><fmt:formatNumber value="${debt.waterUnitPrice}" pattern="#,##0"/></td>
                                        <td style="text-align:right"><fmt:formatNumber value="${debt.waterAmount}" pattern="#,##0"/> đ</td>
                                    </tr>
                                    <tr>
                                        <td><strong>Phí dịch vụ</strong></td>
                                        <td>-</td>
                                        <td>-</td>
                                        <td>-</td>
                                        <td style="text-align:right">-</td>
                                        <td style="text-align:right"><fmt:formatNumber value="${debt.serviceFee}" pattern="#,##0"/> đ</td>
                                    </tr>
                                    <tr>
                                        <td><strong>Tiền Internet</strong></td>
                                        <td>-</td>
                                        <td>-</td>
                                        <td>-</td>
                                        <td style="text-align:right">-</td>
                                        <td style="text-align:right"><fmt:formatNumber value="${debt.internetFee}" pattern="#,##0"/> đ</td>
                                    </tr>
                                    <tr>
                                        <td><strong>Phí khác</strong></td>
                                        <td>-</td>
                                        <td>-</td>
                                        <td>-</td>
                                        <td style="text-align:right">-</td>
                                        <td style="text-align:right"><fmt:formatNumber value="${debt.otherFee}" pattern="#,##0"/> đ</td>
                                    </tr>
                                </tbody>
                                <tfoot>
                                    <tr>
                                        <td colspan="5" style="text-align:right"><strong>Tạm tính:</strong></td>
                                        <td style="text-align:right"><strong><fmt:formatNumber value="${debt.subtotal}" pattern="#,##0"/> đ</strong></td>
                                    </tr>
                                    <tr>
                                        <td colspan="5" style="text-align:right"><strong>Thuế (<c:out value="${debt.taxRate}"/>%):</strong></td>
                                        <td style="text-align:right"><strong><fmt:formatNumber value="${debt.taxAmount}" pattern="#,##0"/> đ</strong></td>
                                    </tr>
                                    <tr style="background:var(--hms-primary-soft); color:var(--hms-primary-dark);">
                                        <td colspan="5" style="text-align:right; font-size:1.1rem"><strong>Tổng tiền phải nộp:</strong></td>
                                        <td style="text-align:right; font-size:1.1rem"><strong><fmt:formatNumber value="${debt.invoiceTotalAmount}" pattern="#,##0"/> đ</strong></td>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>

                    
                    <c:if test="${not empty debt.note}">
                    <div class="data-surface p-4 mt-4">
                        <h4 class="mb-2">Ghi chú hóa đơn</h4>
                        <p class="mb-0 text-muted"><c:out value="${debt.note}"/></p>
                    </div>
                    </c:if>
                </div>

                <div class="col-md-4">
                    <div class="data-surface p-4 mb-4">
                        <h4 class="mb-4">Thông tin người thuê</h4>
                        <ul class="list-unstyled">
                            <li class="mb-3">
                                <span class="text-muted d-block" style="font-size:0.875rem">Họ tên</span>
                                <span class="fw-bold"><c:out value="${debt.tenantName}"/></span>
                            </li>
                            <li class="mb-3">
                                <span class="text-muted d-block" style="font-size:0.875rem">Số điện thoại</span>
                                <span class="fw-bold"><c:out value="${debt.tenantPhone}"/></span>
                            </li>
                            <li class="mb-0">
                                <span class="text-muted d-block" style="font-size:0.875rem">Email</span>
                                <span class="fw-bold"><c:out value="${debt.tenantEmail}"/></span>
                            </li>
                        </ul>
                    </div>

                    <div class="data-surface p-4 mb-4">
                        <h4 class="mb-4">Tình trạng Công nợ</h4>
                        <ul class="list-unstyled mb-0">
                            <li class="mb-3">
                                <span class="text-muted d-block" style="font-size:0.875rem">Kỳ hóa đơn</span>
                                <span class="fw-bold"><c:out value="${debt.billingPeriod}"/></span>
                            </li>
                            <li class="mb-3">
                                <span class="text-muted d-block" style="font-size:0.875rem">Hạn thanh toán</span>
                                <span class="fw-bold"><c:out value="${debt.dueDate}"/></span>
                            </li>
                        </ul>
                        
                        <hr class="my-3">
                        
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Tổng phải thu:</span>
                            <span class="fw-bold"><fmt:formatNumber value="${debt.invoiceTotalAmount}" pattern="#,##0"/> đ</span>
                        </div>
                        <div class="d-flex justify-content-between mb-2" style="color: var(--hms-success-dark, #166534)">
                            <span>Đã thanh toán:</span>
                            <span class="fw-bold"><fmt:formatNumber value="${debt.paidAmount}" pattern="#,##0"/> đ</span>
                        </div>
                        <div class="d-flex justify-content-between mb-3" style="color: var(--hms-danger-color, #ef4444); font-size: 1.1rem;">
                            <span class="fw-bold">CÒN NỢ:</span>
                            <span class="fw-bold"><fmt:formatNumber value="${debt.debtAmount}" pattern="#,##0"/> đ</span>
                        </div>

                        <hr class="my-3">

                        <div class="alert alert-warning mb-0">
                            <p class="mb-1"><strong>Số ngày quá hạn:</strong> <c:out value="${debt.overdueDays}"/> ngày</p>
                            <p class="mb-1"><strong>Phí chậm nộp tạm tính:</strong> <fmt:formatNumber value="${debt.lateFeePreview}" pattern="#,##0"/> đ</p>
                            <hr class="my-2" style="border-color: rgba(0,0,0,0.1)">
                            <small><em>Lưu ý: Phí chậm nộp chỉ là số tiền tham khảo, chưa được cộng vào hóa đơn gốc.</em></small>
                        </div>
                    </div>
                </div>
            </div>

        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
