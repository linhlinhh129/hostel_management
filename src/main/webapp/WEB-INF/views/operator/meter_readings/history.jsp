<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Lịch sử điện nước"/>
<c:set var="pageRole" value="OPERATOR"/>
<c:set var="activeMenu" value="meter-readings-history"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <h1>Lịch sử điện nước</h1>
            </div>

            <div class="data-surface">
                <%-- Filter bar đồng bộ với admin --%>
                <form method="get" action="${ctx}/operator/meter-readings/history"
                      style="background:#fff; border:1px solid var(--hms-border-soft); border-radius:8px; padding:20px; margin-bottom:20px; box-shadow:0 1px 3px rgba(0,0,0,0.02)">
                    <div style="display:flex; flex-wrap:wrap; gap:20px; margin-bottom:20px;">
                        <div style="flex:2; min-width:180px;">
                            <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Mã phòng</label>
                            <input type="text" class="form-control" name="roomCode"
                                   placeholder="Mã phòng..."
                                   value="<c:out value='${searchRoomCode}'/>" style="width:100%">
                        </div>
                        <div style="flex:1; min-width:120px;">
                            <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Tháng</label>
                            <select class="form-select" name="month" style="width:100%">
                                <c:forEach var="m" begin="1" end="12">
                                    <option value="${m}" ${selectedMonth == m ? 'selected' : ''}>Tháng ${m}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div style="flex:1; min-width:120px;">
                            <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Năm</label>
                            <select class="form-select" name="year" style="width:100%">
                                <c:forEach var="y" begin="${currentYear - 2}" end="${currentYear}">
                                    <option value="${y}" ${selectedYear == y ? 'selected' : ''}>Năm ${y}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <c:if test="${not empty facilities and facilities.size() > 0}">
                            <input type="hidden" name="facility" value="${facilities[0].name} (${facilities[0].code})"/>
                            <div style="flex:1; min-width:150px;">
                                <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Cơ sở</label>
                                <div class="form-control" style="background:var(--hms-bg-soft); color:var(--hms-stone); pointer-events:none;">
                                    <c:out value="${facilities[0].name} (${facilities[0].code})"/>
                                </div>
                            </div>
                        </c:if>
                    </div>
                    <div style="display:flex; justify-content:flex-end; gap:12px; border-top:1px dashed var(--hms-border-soft); padding-top:16px;">
                        <a href="${ctx}/operator/meter-readings/history"
                           style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); text-decoration:none; font-size:14px; font-weight:500;">Xóa bộ lọc</a>
                        <button type="submit"
                                style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); font-size:14px; font-weight:500; cursor:pointer;">Tìm kiếm</button>
                    </div>
                </form>

                <div class="table-responsive">
                    <table class="table-mintlify">
                        <thead>
                            <tr>
                                <th>Mã phòng</th>
                                <th>Số điện</th>
                                <th>Số nước</th>
                                <th class="d-none d-md-table-cell">Cập nhật lúc</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty meterList}">
                                    <tr>
                                        <td colspan="6" class="text-center py-4" style="color:var(--hms-stone)">Không có dữ liệu hiển thị.</td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="item" items="${meterList}">
                                        <tr>
                                            <td style="font-weight:500; color:var(--hms-ink)"><c:out value="${item.roomCode}"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty item.currentElectricReading}">
                                                        <span style="font-weight:600; color:var(--hms-ink)"><c:out value="${item.currentElectricReading}"/></span>
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty item.currentWaterReading}">
                                                        <span style="font-weight:600; color:var(--hms-ink)"><c:out value="${item.currentWaterReading}"/></span>
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="d-none d-md-table-cell" style="font-size:0.8125rem; color:var(--hms-stone)">
                                                <c:choose>
                                                    <c:when test="${not empty item.updatedAt}">
                                                        <fmt:formatDate value="${item.updatedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                    </c:when>
                                                    <c:otherwise>-</c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${item.status == 'DA_CAP_NHAT'}">
                                                        <span class="badge-hms badge-success">ĐÃ CẬP NHẬT</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge-hms badge-warning">CHƯA CẬP NHẬT</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:if test="${item.status == 'DA_CAP_NHAT'}">
                                                    <button type="button"
                                                            class="btn-mintlify-secondary"
                                                            style="padding:4px 12px; font-size:12px;"
                                                            onclick="showDetailModal(this)"
                                                            data-room="${item.roomCode}"
                                                            data-prevelectric="${item.previousElectricReading}"
                                                            data-currelectric="${item.currentElectricReading}"
                                                            data-prevwater="${item.previousWaterReading}"
                                                            data-currwater="${item.currentWaterReading}"
                                                            data-electricimg="${item.electricImg}"
                                                            data-waterimg="${item.waterImg}"
                                                            data-updatedby="${item.updatedByName}">
                                                        Chi tiết
                                                    </button>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

            <%-- Modal Chi tiết --%>
            <div class="modal fade" id="detailModal" tabindex="-1" aria-labelledby="detailModalLabel" aria-hidden="true">
                <div class="modal-dialog modal-lg modal-dialog-centered">
                    <div class="modal-content" style="border-radius:var(--hms-radius-lg); border:none;">
                        <div class="modal-header border-0 pb-0">
                            <h5 class="modal-title fw-bold" id="detailModalLabel">
                                Chi tiết điện nước phòng <span id="modalRoomCode" class="text-primary"></span>
                            </h5>
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body">
                            <div class="row g-4 mb-3">
                                <div class="col-md-6">
                                    <div class="p-3 rounded" style="background:var(--hms-bg-soft)">
                                        <h6 class="fw-bold mb-3">⚡ Chỉ số điện</h6>
                                        <div class="d-flex justify-content-between mb-2">
                                            <span class="text-muted">Kỳ trước:</span>
                                            <span class="fw-medium" id="modalPrevElectric"></span>
                                        </div>
                                        <div class="d-flex justify-content-between mb-2">
                                            <span class="text-muted">Kỳ này:</span>
                                            <span class="fw-bold" id="modalCurrElectric"></span>
                                        </div>
                                        <div class="d-flex justify-content-between border-top pt-2 mt-2">
                                            <span class="text-muted">Tiêu thụ:</span>
                                            <span class="fw-bold text-danger"><span id="modalConsumeElectric"></span> kWh</span>
                                        </div>
                                        <div class="mt-3 text-center">
                                            <img id="modalElectricImg" src="" alt="Ảnh công tơ điện"
                                                 class="img-fluid rounded"
                                                 style="max-height:200px; object-fit:contain; display:none; width:100%; background:#f8f9fa;">
                                            <span id="noElectricImg" class="text-muted fst-italic" style="display:none">Không có ảnh</span>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="p-3 rounded" style="background:var(--hms-bg-soft)">
                                        <h6 class="fw-bold mb-3">💧 Chỉ số nước</h6>
                                        <div class="d-flex justify-content-between mb-2">
                                            <span class="text-muted">Kỳ trước:</span>
                                            <span class="fw-medium" id="modalPrevWater"></span>
                                        </div>
                                        <div class="d-flex justify-content-between mb-2">
                                            <span class="text-muted">Kỳ này:</span>
                                            <span class="fw-bold" id="modalCurrWater"></span>
                                        </div>
                                        <div class="d-flex justify-content-between border-top pt-2 mt-2">
                                            <span class="text-muted">Tiêu thụ:</span>
                                            <span class="fw-bold text-primary"><span id="modalConsumeWater"></span> khối</span>
                                        </div>
                                        <div class="mt-3 text-center">
                                            <img id="modalWaterImg" src="" alt="Ảnh công tơ nước"
                                                 class="img-fluid rounded"
                                                 style="max-height:200px; object-fit:contain; display:none; width:100%; background:#f8f9fa;">
                                            <span id="noWaterImg" class="text-muted fst-italic" style="display:none">Không có ảnh</span>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="text-end text-muted small">
                                Người cập nhật: <strong id="modalUpdatedBy"></strong>
                            </div>
                        </div>
                        <div class="modal-footer border-0 pt-0">
                            <button type="button" class="btn-mintlify-secondary" data-bs-dismiss="modal">Đóng</button>
                        </div>
                    </div>
                </div>
            </div>

        </main>
        <jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
    </div>
</div>

<script>
    function showDetailModal(btn) {
        var room        = btn.getAttribute('data-room');
        var prevE       = parseInt(btn.getAttribute('data-prevelectric'))  || 0;
        var currE       = parseInt(btn.getAttribute('data-currelectric'))  || 0;
        var prevW       = parseInt(btn.getAttribute('data-prevwater'))     || 0;
        var currW       = parseInt(btn.getAttribute('data-currwater'))     || 0;
        var electricImg = btn.getAttribute('data-electricimg');
        var waterImg    = btn.getAttribute('data-waterimg');
        var updatedBy   = btn.getAttribute('data-updatedby');

        document.getElementById('modalRoomCode').textContent        = room;
        document.getElementById('modalPrevElectric').textContent    = prevE;
        document.getElementById('modalCurrElectric').textContent    = currE;
        document.getElementById('modalConsumeElectric').textContent = currE - prevE;
        document.getElementById('modalPrevWater').textContent       = prevW;
        document.getElementById('modalCurrWater').textContent       = currW;
        document.getElementById('modalConsumeWater').textContent    = currW - prevW;
        document.getElementById('modalUpdatedBy').textContent       = updatedBy || 'N/A';

        var eImg = document.getElementById('modalElectricImg');
        var noE  = document.getElementById('noElectricImg');
        if (electricImg && electricImg.trim() !== '') {
            eImg.src = '${ctx}' + electricImg; eImg.style.display = 'block'; noE.style.display = 'none';
        } else {
            eImg.style.display = 'none'; noE.style.display = 'block';
        }
        var wImg = document.getElementById('modalWaterImg');
        var noW  = document.getElementById('noWaterImg');
        if (waterImg && waterImg.trim() !== '') {
            wImg.src = '${ctx}' + waterImg; wImg.style.display = 'block'; noW.style.display = 'none';
        } else {
            wImg.style.display = 'none'; noW.style.display = 'block';
        }
        new bootstrap.Modal(document.getElementById('detailModal')).show();
    }
</script>
</body>
</html>
