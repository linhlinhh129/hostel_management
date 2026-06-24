<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Giá dịch vụ"/>
<c:set var="pageRole" value="MANAGER"/>
<c:set var="activeMenu" value="service-prices"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3">
                <div>
                    <h1>Giá dịch vụ</h1>
                    <p>Quản lý các khoản phí và giá dịch vụ của cơ sở</p>
                </div>
            </div>

            <div class="data-surface">
                <div class="table-responsive">
                    <table class="table-mintlify mb-0">
                    <thead>
                        <tr>
                            <th class="text-center">Loại phí</th>
                            <th class="text-center">Giá hiện tại</th>
                            <th class="text-center">Đơn vị</th>
                            <th class="text-center">Cập nhật lần cuối</th>
                            <th class="text-center">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty servicePrices}">
                                <c:forEach var="price" items="${servicePrices}">
                                    <tr>
                                        <td class="text-center align-middle"><strong><c:out value="${price.priceName}"/></strong></td>
                                        <td class="text-center align-middle" style="font-weight: 600; color: var(--hms-primary-color, #10b981);">
                                            <fmt:formatNumber value="${price.currentPrice}" groupingUsed="true"/>
                                        </td>
                                        <td class="text-center align-middle"><c:out value="${price.unit}"/></td>
                                        <td class="text-center align-middle"><c:out value="${price.updatedAt}"/></td>
                                        <td class="text-center align-middle">
                                            <div class="d-flex gap-2 justify-content-center">
                                                <button type="button" class="btn-mintlify-secondary" style="padding:4px 12px;font-size:0.8125rem" onclick="openUpdateModal('${price.priceType}', '${price.priceName}', '${price.currentPrice}', '${price.unit}')">
                                                    Cập nhật
                                                </button>
                                                <a href="${ctx}/manager/service-prices?action=history&priceType=${price.priceType}" class="btn-mintlify-secondary text-decoration-none" style="padding:4px 12px;font-size:0.8125rem">
                                                    Lịch sử
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="5" class="text-center text-muted">Chưa có dữ liệu giá dịch vụ</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
                </div>
            </div>

            <!-- Update Price Modal -->
            <div class="modal fade" id="updatePriceModal" tabindex="-1" aria-labelledby="updatePriceModalLabel" aria-hidden="true">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <form id="updatePriceForm" action="${ctx}/manager/service-prices" method="post">
                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="csrfToken" value="${csrfToken}">
                            <div class="modal-header">
                                <h5 class="modal-title" id="updatePriceModalLabel">Cập nhật giá</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <input type="hidden" id="priceType" name="priceType">
                                <div class="mb-3">
                                    <label class="form-label">Loại phí</label>
                                    <input type="text" class="form-control" id="priceNameDisplay" readonly>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Giá hiện tại</label>
                                    <div class="input-group">
                                        <input type="text" class="form-control" id="currentPriceDisplay" readonly>
                                        <span class="input-group-text" id="currentUnitDisplay"></span>
                                    </div>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label required">Giá mới</label>
                                    <div class="input-group">
                                        <input type="number" class="form-control" id="newPrice" name="newPrice" min="0" step="0.01" required>
                                        <span class="input-group-text" id="newUnitDisplay"></span>
                                    </div>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label required">Ghi chú / Lý do thay đổi</label>
                                    <textarea class="form-control" id="note" name="note" rows="2" required></textarea>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn-mintlify-secondary" data-bs-dismiss="modal">Hủy</button>
                                <button type="submit" class="button-primary" style="padding: 10px 20px; border-radius: 9999px;">Lưu thay đổi</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>

<script>
    let updateModal;

    document.addEventListener("DOMContentLoaded", function() {
        if (typeof bootstrap !== 'undefined') {
            updateModal = new bootstrap.Modal(document.getElementById('updatePriceModal'));
        }
    });

    function openUpdateModal(priceType, priceName, currentPrice, unit) {
        document.getElementById('priceType').value = priceType;
        document.getElementById('priceNameDisplay').value = priceName;
        document.getElementById('currentPriceDisplay').value = currentPrice;
        document.getElementById('currentUnitDisplay').textContent = unit;
        document.getElementById('newUnitDisplay').textContent = unit;
        document.getElementById('newPrice').value = '';
        document.getElementById('note').value = '';
        
        if(updateModal) updateModal.show();
    }
</script>
</body>
</html>
