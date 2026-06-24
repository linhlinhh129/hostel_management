<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"  %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Chi tiết hóa đơn - Cổng cư dân"/>
<c:set var="pageRole"   value="TENANT"/>
<c:set var="activeMenu" value="invoices"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient">
                <h1>Hóa đơn kỳ <c:out value="${invoice.billingPeriod}"/></h1>
                <p>Mã: <c:out value="${invoice.code}"/></p>
            </div>

            <%-- Tổng ti�?n nổi bật --%>
            <div class="tenant-card" style="margin-bottom:1rem;
                 ${invoice.status == 'OVERDUE' ? 'border-color:var(--hms-danger)' : invoice.status == 'PAID' ? 'border-color:var(--hms-success)' : 'border-color:var(--hms-warning)'}">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <span style="font-size:0.75rem;font-weight:700;text-transform:uppercase;
                                 letter-spacing:0.05em;color:var(--hms-stone)">Tổng cộng phải trả</span>
                    <c:choose>
                        <c:when test="${invoice.status == 'PAID'}">
                            <span class="badge-hms badge-success">✓ �?ã thanh toán</span>
                        </c:when>
                        <c:when test="${invoice.status == 'OVERDUE'}">
                            <span class="badge-hms badge-danger">⚠ Quá hạn</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge-hms badge-warning">Chưa thanh toán</span>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div style="font-size:2rem;font-weight:800;letter-spacing:-1px;
                            color:${invoice.status == 'PAID' ? 'var(--hms-success)' : invoice.status == 'OVERDUE' ? 'var(--hms-danger)' : 'var(--hms-ink)'}">
                    <fmt:formatNumber value="${invoice.totalAmount}" pattern="#,##0"/> đ
                </div>
                <div style="font-size:0.8125rem;color:var(--hms-stone);margin-top:4px">
                    Hạn thanh toán: <strong><c:out value="${invoice.dueDate}"/></strong>
                </div>
            </div>

            <%-- Chi tiết các khoản --%>
            <div class="widget-surface mb-3">
                <div class="widget-surface-header"><h3>Chi tiết khoản phí</h3></div>
                <div class="widget-surface-body p-0">
                    <table class="table-mintlify" style="font-size:0.875rem">
                        <tbody>
                        <tr>
                            <td style="padding:10px 1.25rem">Ti�?n phòng cố định</td>
                            <td style="padding:10px 1.25rem;text-align:right;font-weight:600">
                                <fmt:formatNumber value="${invoice.roomFee}" pattern="#,##0"/> đ
                            </td>
                        </tr>
                        <tr>
                            <td style="padding:10px 1.25rem">
                                Ti�?n điện
                                <span style="color:var(--hms-stone);font-size:0.75rem">
                                    (<c:out value="${invoice.oldElectricReading}"/>
                                     → <c:out value="${invoice.newElectricReading}"/> kWh)
                                </span>
                            </td>
                            <td style="padding:10px 1.25rem;text-align:right;font-weight:600">
                                <fmt:formatNumber value="${invoice.electricAmount}" pattern="#,##0"/> đ
                            </td>
                        </tr>
                        <tr>
                            <td style="padding:10px 1.25rem">
                                Ti�?n nước
                                <span style="color:var(--hms-stone);font-size:0.75rem">
                                    (<c:out value="${invoice.oldWaterReading}"/>
                                     → <c:out value="${invoice.newWaterReading}"/> m³)
                                </span>
                            </td>
                            <td style="padding:10px 1.25rem;text-align:right;font-weight:600">
                                <fmt:formatNumber value="${invoice.waterAmount}" pattern="#,##0"/> đ
                            </td>
                        </tr>
                        <tr>
                            <td style="padding:10px 1.25rem">Phí dịch vụ</td>
                            <td style="padding:10px 1.25rem;text-align:right;font-weight:600">
                                <fmt:formatNumber value="${invoice.serviceFee}" pattern="#,##0"/> đ
                            </td>
                        </tr>
                        <c:if test="${not empty invoice.otherFee and invoice.otherFee > 0}">
                            <tr>
                                <td style="padding:10px 1.25rem">Phí khác</td>
                                <td style="padding:10px 1.25rem;text-align:right;font-weight:600">
                                    <fmt:formatNumber value="${invoice.otherFee}" pattern="#,##0"/> đ
                                </td>
                            </tr>
                        </c:if>
                        <tr style="background:var(--hms-accent-bg)">
                            <td style="padding:12px 1.25rem;font-weight:800;font-size:1rem">Tổng cộng</td>
                            <td style="padding:12px 1.25rem;text-align:right;font-weight:800;font-size:1rem;color:var(--hms-ink)">
                                <fmt:formatNumber value="${invoice.totalAmount}" pattern="#,##0"/> đ
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>

            <%-- Hướng dẫn thanh toán & VNPAY --%>
            <c:if test="${invoice.status == 'UNPAID' or invoice.status == 'OVERDUE'}">
                <div class="tenant-card"
                     style="border-color:var(--hms-accent);background:var(--hms-accent-bg);margin-bottom:1rem">
                    <div style="font-weight:700;margin-bottom:0.5rem;color:var(--hms-ink)">
                        💳 Hướng dẫn thanh toán chuyển khoản
                    </div>
                    <div style="font-size:0.8125rem;color:var(--hms-slate);line-height:1.7;margin-bottom:1rem">
                        <div>Ngân hàng: <strong>Vietcombank</strong></div>
                        <div>Số tài khoản: <strong style="font-family:var(--hms-font-mono)">1234567890</strong></div>
                        <div>Chủ tài khoản: <strong>Công ty Quản lý Nhà tr�?</strong></div>
                        <div>Nội dung CK: <strong style="font-family:var(--hms-font-mono)">
                            <c:out value="${invoice.code}"/>
                        </strong></div>
                    </div>
                    
                    <hr/>
                    <div class="d-flex justify-content-end">
                        <form method="post" action="${ctx}/tenant/payment/create">
                            <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                            <input type="hidden" name="invoiceId" value="${invoice.id}"/>
                            <input type="hidden" name="amount" value="${invoice.totalAmount}"/>
                            <button type="submit" class="btn btn-mintlify-primary" style="width: auto;">
                                Thanh toán qua VNPAY
                            </button>
                        </form>
                    </div>
                </div>
            </c:if>

            <c:if test="${not empty invoice.note}">
                <div class="tenant-card" style="margin-top:0.75rem">
                    <div style="font-size:0.75rem;color:var(--hms-stone);font-weight:700;text-transform:uppercase;
                                letter-spacing:0.05em;margin-bottom:4px">Ghi chú</div>
                    <p style="font-size:0.875rem;margin:0"><c:out value="${invoice.note}"/></p>
                </div>
            </c:if>

            <a href="${ctx}/tenant/invoices"
               class="btn-mintlify-secondary text-decoration-none mt-3">
                �? Danh sách hóa đơn
            </a>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
