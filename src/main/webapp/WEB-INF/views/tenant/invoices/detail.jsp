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
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/><div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient">
                <div class="d-flex justify-content-between align-items-center flex-wrap">
                    <div>
                        <h1>Hóa đơn kỳ <c:out value="${invoice.billingPeriod}"/></h1>
                        <p>Mã: <c:out value="${invoice.code}"/></p>
                    </div>
                    <div>
                        <a href="${ctx}/tenant/invoices" class="btn-mintlify-secondary text-decoration-none">
                            ← Danh sách hóa đơn
                        </a>
                    </div>
                </div>
            </div>

            <div class="row g-3">
                <div class="col-lg-8">
                    <%-- Chi tiết các khoản --%>
                    <div class="widget-surface mb-3">
                        <div class="widget-surface-header"><h3>Chi tiết khoản phí</h3></div>
                        <div class="widget-surface-body p-0">
                            <table class="table-mintlify" style="font-size:0.875rem">
                                <tbody>
                                <tr>
                                    <td style="padding:10px 1.25rem">Tiền phòng cố định</td>
                                    <td style="padding:10px 1.25rem;text-align:right;font-weight:600">
                                        <fmt:formatNumber value="${invoice.roomFee}" pattern="#,##0"/> đ
                                    </td>
                                </tr>
                                <tr>
                                    <td style="padding:10px 1.25rem">
                                        Tiền điện
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
                                        Tiền nước
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
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <c:if test="${not empty invoice.note}">
                        <div class="widget-surface mb-3">
                            <div class="widget-surface-header"><h3>Ghi chú</h3></div>
                            <div class="widget-surface-body">
                                <p style="font-size:0.875rem;margin:0"><c:out value="${invoice.note}"/></p>
                            </div>
                        </div>
                    </c:if>
                </div>

                <div class="col-lg-4">
                    <%-- Tổng tiền nổi bật --%>
                    <div class="widget-surface mb-3" style="border: 2px solid ${invoice.status == 'OVERDUE' ? 'var(--hms-danger)' : invoice.status == 'PAID' ? 'var(--hms-success)' : 'var(--hms-warning)'}; box-shadow: none;">
                        <div class="widget-surface-body text-center">
                            <div style="font-size:0.875rem;font-weight:700;text-transform:uppercase;letter-spacing:0.05em;color:var(--hms-stone);margin-bottom:0.5rem">
                                Tổng cộng phải trả
                            </div>
                            <div style="font-size:2.25rem;font-weight:800;letter-spacing:-1px;margin-bottom:0.5rem;
                                        color:${invoice.status == 'PAID' ? 'var(--hms-success)' : invoice.status == 'OVERDUE' ? 'var(--hms-danger)' : 'var(--hms-ink)'}">
                                <fmt:formatNumber value="${invoice.totalAmount}" pattern="#,##0"/> đ
                            </div>
                            <div class="mb-2">
                                <c:choose>
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
                                Hạn thanh toán: <strong><c:out value="${invoice.dueDate}"/></strong>
                            </div>
                        </div>
                    </div>

                    <%-- Hướng dẫn thanh toán & VNPAY --%>
                    <c:if test="${not invoice.hasPendingPayment and (invoice.status == 'UNPAID' or invoice.status == 'OVERDUE')}">
                        <div class="widget-surface" style="background:var(--hms-accent-bg); border-color:var(--hms-accent);">
                            <div class="widget-surface-header border-bottom-0 pb-0">
                                <h3 style="color:var(--hms-ink)">💳 Thanh toán</h3>
                            </div>
                            <div class="widget-surface-body">
                                <div style="font-size:0.8125rem;color:var(--hms-slate);line-height:1.7;margin-bottom:1rem">
                                    <div>Ngân hàng: <strong>Vietcombank</strong></div>
                                    <div>Số tài khoản: <strong style="font-family:var(--hms-font-mono)">1234567890</strong></div>
                                    <div>Chủ tài khoản: <strong>Công ty Quản lý Nhà trọ</strong></div>
                                    <div>Nội dung CK: <strong style="font-family:var(--hms-font-mono)">
                                        <c:out value="${invoice.code}"/>
                                    </strong></div>
                                </div>
                                <hr/>
                                <div class="d-grid mt-3">
                                    <form method="post" action="${ctx}/tenant/payment/create">
                                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                                        <input type="hidden" name="invoiceId" value="${invoice.id}"/>
                                        <input type="hidden" name="amount" value="${invoice.totalAmount}"/>
                                        <button type="submit" class="btn btn-mintlify-primary w-100">
                                            Thanh toán qua VNPAY
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </c:if>
                </div>
            </div>
        </main>
    </div></div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
