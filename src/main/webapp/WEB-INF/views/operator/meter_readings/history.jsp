<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp">
        <jsp:param name="title" value="Lịch sử điện nước" />
    </jsp:include>
    <link href="https://fonts.googleapis.com/css2?family=Geist+Mono:wght@400;500&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <style>
        .page-content { margin: auto; }
        .table-responsive { overflow-x: auto; }
        .custom-table th { 
            font-weight: 600; 
            color: var(--color-ink); 
            background-color: var(--color-surface-soft); 
            border-bottom: 1px solid var(--color-hairline-soft);
            padding: 12px 16px;
            font-size: 14px;
        }
        .custom-table td { 
            color: var(--color-steel); 
            vertical-align: middle; 
            padding: 12px 16px;
            font-size: 14px;
        }
    </style>
</head>
<body id="page-top">
    <div class="app-shell">
        <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
        <div class="sidebar-overlay"></div>
        <div class="main-wrapper">
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
            <main class="page-content">
                    <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

                    <!-- Header -->
                    <div class="page-header hero-sky-gradient" style="border-radius: var(--hms-radius-lg, 12px); margin-bottom: 1.75rem;">
                        <h1>Lịch sử điện nước</h1>
                    </div>

                    <!-- Filter -->
                    <form action="${pageContext.request.contextPath}/operator/meter-readings/history" method="get" class="mb-4">
                        <div class="row g-3">
                            <div class="col-md-3">
                                <!-- Hiển thị tên cơ sở thay vì Dropdown -->
                                <div class="mintlify-text-input d-flex align-items-center" style="height: 38px; background-color: var(--color-surface-soft); color: var(--color-steel);">
                                    <c:choose>
                                        <c:when test="${not empty facilities and facilities.size() > 0}">
                                            Cơ sở: <c:out value="${facilities[0].name} (${facilities[0].code})" />
                                            <input type="hidden" name="facility" value="${facilities[0].name} (${facilities[0].code})" />
                                        </c:when>
                                        <c:otherwise>
                                            Chưa phân công cơ sở
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <div class="col-md-2">
                                <input type="text" name="roomCode" class="mintlify-text-input" placeholder="Mã phòng..." value="${searchRoomCode}" style="height: 38px;">
                            </div>
                            <div class="col-md-2">
                                <select name="month" class="mintlify-text-input" style="height: 38px;">
                                    <c:forEach var="m" begin="1" end="12">
                                        <option value="${m}" ${selectedMonth == m ? 'selected' : ''}>Tháng ${m}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <select name="year" class="mintlify-text-input" style="height: 38px;">
                                    <c:forEach var="y" begin="${currentYear - 2}" end="${currentYear}">
                                        <option value="${y}" ${selectedYear == y ? 'selected' : ''}>Năm ${y}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <button type="submit" class="btn-mintlify-primary w-100" style="height: 38px; padding: 0; justify-content: center;">Lọc danh sách</button>
                            </div>
                        </div>
                    </form>

                    <!-- Main Content -->
                    <div class="data-surface mb-4">
                        <div class="table-responsive">
                            <table class="table table-hover custom-table mt-3">
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
                                                <td colspan="6" class="text-center py-4">Không có dữ liệu hiển thị.</td>
                                            </tr>
                                        </c:when>
                                        <c:otherwise>
                                            <c:forEach var="item" items="${meterList}">
                                                <tr>
                                                    <td style="font-weight: 500; color: var(--color-ink);">${item.roomCode}</td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty item.currentElectricReading}">
                                                                <span style="font-weight: 600; color: var(--color-ink);">${item.currentElectricReading}</span>
                                                            </c:when>
                                                            <c:otherwise>-</c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty item.currentWaterReading}">
                                                                <span style="font-weight: 600; color: var(--color-ink);">${item.currentWaterReading}</span>
                                                            </c:when>
                                                            <c:otherwise>-</c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td class="d-none d-md-table-cell">
                                                        <c:choose>
                                                            <c:when test="${not empty item.updatedAt}">
                                                                <fmt:formatDate value="${item.updatedAt}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                                            </c:when>
                                                            <c:otherwise>-</c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${item.status == 'DA_CAP_NHAT'}">
                                                                <span class="mintlify-badge-status-inprogress">ĐÃ CẬP NHẬT</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="mintlify-badge-status-pending">CHƯA CẬP NHẬT</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:if test="${item.status == 'DA_CAP_NHAT'}">
                                                            <button type="button" class="btn btn-sm btn-outline-primary"
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
                    
                    <!-- Modal Chi tiết -->
                    <div class="modal fade" id="detailModal" tabindex="-1" aria-labelledby="detailModalLabel" aria-hidden="true">
                        <div class="modal-dialog modal-lg modal-dialog-centered">
                            <div class="modal-content" style="border-radius: var(--hms-radius-lg); border: none;">
                                <div class="modal-header border-0 pb-0">
                                    <h5 class="modal-title fw-bold" id="detailModalLabel">Chi tiết điện nước phòng <span id="modalRoomCode" class="text-primary"></span></h5>
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <div class="row g-4 mb-3">
                                        <div class="col-md-6">
                                            <div class="p-3 rounded" style="background-color: var(--color-surface-soft);">
                                                <h6 class="fw-bold mb-3"><i class="fas fa-bolt text-warning me-2"></i>Chỉ số điện</h6>
                                                <div class="d-flex justify-content-between mb-2">
                                                    <span class="text-muted">Kỳ trước:</span>
                                                    <span class="fw-medium" id="modalPrevElectric"></span>
                                                </div>
                                                <div class="d-flex justify-content-between mb-2">
                                                    <span class="text-muted">Kỳ này:</span>
                                                    <span class="fw-bold text-dark" id="modalCurrElectric"></span>
                                                </div>
                                                <div class="d-flex justify-content-between border-top pt-2 mt-2">
                                                    <span class="text-muted">Tiêu thụ:</span>
                                                    <span class="fw-bold text-danger"><span id="modalConsumeElectric"></span> kWh</span>
                                                </div>
                                                <div class="mt-3 text-center">
                                                    <img id="modalElectricImg" src="" alt="Ảnh công tơ điện" class="img-fluid rounded" style="max-height: 200px; object-fit: contain; display: none; width: 100%; background: #f8f9fa;">
                                                    <span id="noElectricImg" class="text-muted fst-italic" style="display: none;">Không có ảnh</span>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="col-md-6">
                                            <div class="p-3 rounded" style="background-color: var(--color-surface-soft);">
                                                <h6 class="fw-bold mb-3"><i class="fas fa-tint text-info me-2"></i>Chỉ số nước</h6>
                                                <div class="d-flex justify-content-between mb-2">
                                                    <span class="text-muted">Kỳ trước:</span>
                                                    <span class="fw-medium" id="modalPrevWater"></span>
                                                </div>
                                                <div class="d-flex justify-content-between mb-2">
                                                    <span class="text-muted">Kỳ này:</span>
                                                    <span class="fw-bold text-dark" id="modalCurrWater"></span>
                                                </div>
                                                <div class="d-flex justify-content-between border-top pt-2 mt-2">
                                                    <span class="text-muted">Tiêu thụ:</span>
                                                    <span class="fw-bold text-primary"><span id="modalConsumeWater"></span> khối</span>
                                                </div>
                                                <div class="mt-3 text-center">
                                                    <img id="modalWaterImg" src="" alt="Ảnh công tơ nước" class="img-fluid rounded" style="max-height: 200px; object-fit: contain; display: none; width: 100%; background: #f8f9fa;">
                                                    <span id="noWaterImg" class="text-muted fst-italic" style="display: none;">Không có ảnh</span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="text-end text-muted small">
                                        Người cập nhật: <strong id="modalUpdatedBy"></strong>
                                    </div>
                                </div>
                                <div class="modal-footer border-0 pt-0">
                                    <button type="button" class="btn btn-secondary rounded-pill px-4" data-bs-dismiss="modal">Đóng</button>
                                </div>
                            </div>
                        </div>
                    </div>

            </main>
            <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
        </div>
    </div>

    <script>
        function showDetailModal(btn) {
            const room = btn.getAttribute('data-room');
            const prevE = parseInt(btn.getAttribute('data-prevelectric')) || 0;
            const currE = parseInt(btn.getAttribute('data-currelectric')) || 0;
            const prevW = parseInt(btn.getAttribute('data-prevwater')) || 0;
            const currW = parseInt(btn.getAttribute('data-currwater')) || 0;
            const electricImg = btn.getAttribute('data-electricimg');
            const waterImg = btn.getAttribute('data-waterimg');
            const updatedBy = btn.getAttribute('data-updatedby');

            document.getElementById('modalRoomCode').textContent = room;
            document.getElementById('modalPrevElectric').textContent = prevE;
            document.getElementById('modalCurrElectric').textContent = currE;
            document.getElementById('modalConsumeElectric').textContent = currE - prevE;
            
            document.getElementById('modalPrevWater').textContent = prevW;
            document.getElementById('modalCurrWater').textContent = currW;
            document.getElementById('modalConsumeWater').textContent = currW - prevW;
            
            document.getElementById('modalUpdatedBy').textContent = updatedBy || 'N/A';

            const eImgEl = document.getElementById('modalElectricImg');
            const noEImg = document.getElementById('noElectricImg');
            if (electricImg && electricImg.trim() !== '') {
                eImgEl.src = '${pageContext.request.contextPath}' + electricImg;
                eImgEl.style.display = 'block';
                noEImg.style.display = 'none';
            } else {
                eImgEl.style.display = 'none';
                noEImg.style.display = 'block';
            }

            const wImgEl = document.getElementById('modalWaterImg');
            const noWImg = document.getElementById('noWaterImg');
            if (waterImg && waterImg.trim() !== '') {
                wImgEl.src = '${pageContext.request.contextPath}' + waterImg;
                wImgEl.style.display = 'block';
                noWImg.style.display = 'none';
            } else {
                wImgEl.style.display = 'none';
                noWImg.style.display = 'block';
            }

            var detailModal = new bootstrap.Modal(document.getElementById('detailModal'));
            detailModal.show();
        }
    </script>
</body>
</html>
