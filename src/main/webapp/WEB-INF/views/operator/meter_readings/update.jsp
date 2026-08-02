<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Cập nhật điện nước"/>
<c:set var="pageRole" value="OPERATOR"/>
<c:set var="activeMenu" value="meter-readings"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
    <div class="app-shell">
        <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
        <div class="sidebar-overlay"></div>
        <div class="main-wrapper">
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
            <main class="page-content">
                <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

                <!-- Header -->
                <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-center gap-3"
                     style="border-radius: var(--hms-radius-lg); margin-bottom: 1.75rem;">
                    <div>
                        <h1>${empty meterId ? 'Cập nhật chỉ số điện nước' : 'Sửa chỉ số điện nước'}</h1>
                        <p>${empty meterId ? 'Nhập mã phòng và các chỉ số điện nước mới nhất.' : 'Chỉnh sửa lại số liệu điện nước đã nhập.'}</p>
                    </div>
                    <a href="${ctx}/operator/meter-readings${not empty meterId ? '/history' : ''}" class="btn-mintlify-secondary text-decoration-none" style="position: relative; z-index: 1;">← Quay lại ${not empty meterId ? 'lịch sử' : 'danh sách'}</a>
                </div>

                <c:if test="${not empty sessionScope.error}">
                    <div class="alert alert-danger border-0" style="background-color: #ffeaea; color: #d45656; border-radius: 8px; font-size: 14px; margin-bottom: 24px;">
                        <c:out value="${sessionScope.error}"/>
                    </div>
                    <c:remove var="error" scope="session"/>
                </c:if>

                <div class="mintlify-card-base">
                    <form action="${ctx}/operator/meter-readings/update" method="POST" enctype="multipart/form-data">
                        <%-- CSRF token đúng vị trí: hidden input, không phải query string --%>
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        
                        <c:if test="${not empty meterId}">
                            <input type="hidden" name="meterId" value="${meterId}">
                        </c:if>

                        <div class="row mb-4">
                            <div class="col-md-6">
                                <label class="d-block mb-2" style="font-size: 14px; font-weight: 500; color: var(--color-ink);">
                                    Mã phòng <span class="text-danger">*</span>
                                </label>
                                <input type="text" name="roomCode" class="mintlify-text-input shadow-sm" required
                                       placeholder="Ví dụ: P101"
                                       value="${not empty roomCode ? roomCode : param.roomCode}"
                                       ${not empty roomCode ? 'readonly style="background-color: #f8f9fa;"' : ''}>
                                <div style="font-size: 12px; color: var(--color-steel); margin-top: 6px;">
                                    ${not empty roomCode ? 'Mã phòng đã được chọn từ danh sách.' : 'Hệ thống sẽ tự động tra cứu chỉ số cũ dựa trên mã phòng này.'}
                                </div>
                            </div>
                        </div>

                        <hr style="border-color: var(--color-hairline-soft); margin: 24px 0;">

                        <h5 style="font-weight: 600; font-size: 16px; margin-bottom: 20px; color: var(--color-ink);">Chỉ số Điện</h5>
                        <c:if test="${empty meterId and not empty previousElectricReading}">
                            <div class="row mb-3">
                                <div class="col-12">
                                    <div class="p-3 rounded d-flex flex-wrap gap-3 align-items-start" style="background-color: #f8f9fa; border: 1px dashed #dee2e6;">
                                        <div>
                                            <span style="font-size: 13px; color: var(--color-steel); display: block;">Chỉ số kỳ trước</span>
                                            <span style="font-size: 18px; font-weight: 600; color: var(--color-ink);">${previousElectricReading} <small class="text-muted">kWh</small></span>
                                        </div>
                                        <c:if test="${not empty previousElectricMeterImageURL}">
                                            <div style="border-left: 1px solid #dee2e6; padding-left: 15px;">
                                                <span style="font-size: 13px; color: var(--color-steel); display: block; margin-bottom: 5px;">Ảnh minh chứng cũ</span>
                                                <c:url value="${previousElectricMeterImageURL}" var="electricImgUrl"/>
                                                <img src="${electricImgUrl}" alt="Ảnh công tơ điện cũ" 
                                                     style="height: 80px; object-fit: cover; border-radius: 6px; border: 1px solid #ccc; cursor: pointer;" 
                                                     onclick="window.open(this.src, '_blank')">
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="d-block mb-2" style="font-size: 14px; font-weight: 500; color: var(--color-ink);">
                                    ${empty meterId ? 'Số điện mới (kWh)' : 'Sửa thành số điện (kWh)'} <span class="text-danger">*</span>
                                </label>
                                <input type="number" id="newElectric" name="newElectric" class="mintlify-text-input shadow-sm" required min="0"
                                       value="${currentElectricReading}" placeholder="${empty meterId ? 'Nhập số điện mới' : 'Sửa số điện'}">
                            </div>
                            
                            <div class="col-12" id="electricStatusBlock">
                                <div class="p-3 bg-light rounded border mb-3">
                                    <h6 class="mb-3" style="font-weight: 600; color: var(--color-ink);">Trường hợp bất thường (Nếu có)</h6>
                                    <div class="row">
                                        <div class="col-md-12 mb-3">
                                            <label class="form-label">Nguyên nhân bất thường <span class="text-danger">*</span></label>
                                            <select name="electricStatus" id="electricStatus" class="form-select" onchange="toggleAnomalyFields('electric')">
                                                <option value="NORMAL">-- Chọn lý do --</option>
                                                <option value="REPLACED" ${electricStatus == 'REPLACED' ? 'selected' : ''}>Thay công tơ điện mới</option>
                                                <option value="ROLLOVER" ${electricStatus == 'ROLLOVER' ? 'selected' : ''}>Công tơ chạy hết vòng (reset về 0)</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div id="electricReplacedFields" style="display: none;" class="row">
                                        <div class="col-md-6 mb-2">
                                            <label class="form-label">Số chốt cuối của công tơ cũ <span class="text-danger">*</span></label>
                                            <input type="number" name="electricOldFinal" id="electricOldFinal" class="form-control" value="${electricOldFinal}">
                                        </div>
                                        <div class="col-md-6 mb-2">
                                            <label class="form-label">Số bắt đầu của công tơ mới <span class="text-danger">*</span></label>
                                            <input type="number" name="electricNewStart" id="electricNewStart" class="form-control" value="${electricNewStart}">
                                        </div>
                                    </div>
                                    <div id="electricRolloverFields" style="display: none;" class="row">
                                        <div class="col-md-12 mb-2">
                                            <label class="form-label">Giới hạn tối đa của công tơ <span class="text-danger">*</span></label>
                                            <input type="number" name="electricMaxLimit" id="electricMaxLimit" class="form-control" value="10000" readonly style="background-color: #e9ecef;">
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-12 mb-3">
                                <label class="d-block mb-2" style="font-size: 14px; font-weight: 500; color: var(--color-ink);">
                                    Ảnh minh chứng công tơ điện <c:if test="${empty meterId}"><span class="text-danger">*</span></c:if>
                                </label>
                                <input type="file" name="electricMeterImage" class="mintlify-file-input shadow-sm"
                                       accept="image/jpeg, image/png, image/jpg" ${empty meterId ? 'required' : ''}>
                                <c:if test="${not empty currentElectricImg}">
                                    <div class="mt-3 p-2" style="background-color: #f8f9fa; border-radius: 8px; border: 1px solid #e9ecef;">
                                        <div style="font-size: 13px; color: var(--color-steel); margin-bottom: 8px; font-weight: 500;">Ảnh hiện tại (Bỏ trống nếu không đổi):</div>
                                        <img src="${ctx}${currentElectricImg}" alt="Ảnh công tơ điện hiện tại" 
                                             style="width: 100%; height: auto; max-height: 200px; object-fit: contain; border-radius: 6px; border: 1px solid #dee2e6; background-color: #fff;">
                                    </div>
                                </c:if>
                            </div>
                        </div>

                        <hr style="border-color: var(--color-hairline-soft); margin: 24px 0;">

                        <h5 style="font-weight: 600; font-size: 16px; margin-bottom: 20px; color: var(--color-ink);">Chỉ số Nước</h5>
                        <c:if test="${empty meterId and not empty previousWaterReading}">
                            <div class="row mb-3">
                                <div class="col-12">
                                    <div class="p-3 rounded d-flex flex-wrap gap-3 align-items-start" style="background-color: #f8f9fa; border: 1px dashed #dee2e6;">
                                        <div>
                                            <span style="font-size: 13px; color: var(--color-steel); display: block;">Chỉ số kỳ trước</span>
                                            <span style="font-size: 18px; font-weight: 600; color: var(--color-ink);">${previousWaterReading} <small class="text-muted">m³</small></span>
                                        </div>
                                        <c:if test="${not empty previousWaterMeterImageURL}">
                                            <div style="border-left: 1px solid #dee2e6; padding-left: 15px;">
                                                <span style="font-size: 13px; color: var(--color-steel); display: block; margin-bottom: 5px;">Ảnh minh chứng cũ</span>
                                                <c:url value="${previousWaterMeterImageURL}" var="waterImgUrl"/>
                                                <img src="${waterImgUrl}" alt="Ảnh công tơ nước cũ" 
                                                     style="height: 80px; object-fit: cover; border-radius: 6px; border: 1px solid #ccc; cursor: pointer;" 
                                                     onclick="window.open(this.src, '_blank')">
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="d-block mb-2" style="font-size: 14px; font-weight: 500; color: var(--color-ink);">
                                    ${empty meterId ? 'Số nước mới (m³)' : 'Sửa thành số nước (m³)'} <span class="text-danger">*</span>
                                </label>
                                <input type="number" id="newWater" name="newWater" class="mintlify-text-input shadow-sm" required min="0"
                                       value="${currentWaterReading}" placeholder="${empty meterId ? 'Nhập số nước mới' : 'Sửa số nước'}">
                            </div>

                            <div class="col-12" id="waterStatusBlock">
                                <div class="p-3 bg-light rounded border mb-3">
                                    <h6 class="mb-3" style="font-weight: 600; color: var(--color-ink);">Trường hợp bất thường (Nếu có)</h6>
                                    <div class="row">
                                        <div class="col-md-12 mb-3">
                                            <label class="form-label">Nguyên nhân bất thường <span class="text-danger">*</span></label>
                                            <select name="waterStatus" id="waterStatus" class="form-select" onchange="toggleAnomalyFields('water')">
                                                <option value="NORMAL">-- Chọn lý do --</option>
                                                <option value="REPLACED" ${waterStatus == 'REPLACED' ? 'selected' : ''}>Thay công tơ nước mới</option>
                                                <option value="ROLLOVER" ${waterStatus == 'ROLLOVER' ? 'selected' : ''}>Công tơ chạy hết vòng (reset về 0)</option>
                                            </select>
                                        </div>
                                    </div>
                                    <div id="waterReplacedFields" style="display: none;" class="row">
                                        <div class="col-md-6 mb-2">
                                            <label class="form-label">Số chốt cuối của công tơ cũ <span class="text-danger">*</span></label>
                                            <input type="number" name="waterOldFinal" id="waterOldFinal" class="form-control" value="${waterOldFinal}">
                                        </div>
                                        <div class="col-md-6 mb-2">
                                            <label class="form-label">Số bắt đầu của công tơ mới <span class="text-danger">*</span></label>
                                            <input type="number" name="waterNewStart" id="waterNewStart" class="form-control" value="${waterNewStart}">
                                        </div>
                                    </div>
                                    <div id="waterRolloverFields" style="display: none;" class="row">
                                        <div class="col-md-12 mb-2">
                                            <label class="form-label">Giới hạn tối đa của công tơ <span class="text-danger">*</span></label>
                                            <input type="number" name="waterMaxLimit" id="waterMaxLimit" class="form-control" value="10000" readonly style="background-color: #e9ecef;">
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-12 mb-3">
                                <label class="d-block mb-2" style="font-size: 14px; font-weight: 500; color: var(--color-ink);">
                                    Ảnh minh chứng công tơ nước <c:if test="${empty meterId}"><span class="text-danger">*</span></c:if>
                                </label>
                                <input type="file" name="waterMeterImage" class="mintlify-file-input shadow-sm"
                                       accept="image/jpeg, image/png, image/jpg" ${empty meterId ? 'required' : ''}>
                                <c:if test="${not empty currentWaterImg}">
                                    <div class="mt-3 p-2" style="background-color: #f8f9fa; border-radius: 8px; border: 1px solid #e9ecef;">
                                        <div style="font-size: 13px; color: var(--color-steel); margin-bottom: 8px; font-weight: 500;">Ảnh hiện tại (Bỏ trống nếu không đổi):</div>
                                        <img src="${ctx}${currentWaterImg}" alt="Ảnh công tơ nước hiện tại" 
                                             style="width: 100%; height: auto; max-height: 200px; object-fit: contain; border-radius: 6px; border: 1px solid #dee2e6; background-color: #fff;">
                                    </div>
                                </c:if>
                            </div>
                        </div>

                        <div class="mt-4 pt-3 border-top" style="border-color: var(--color-hairline-soft) !important;">
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end mt-4">
                                <a href="${ctx}/operator/meter-readings" class="mintlify-btn-secondary text-center text-decoration-none">Hủy bỏ</a>
                                <button type="submit" class="mintlify-btn-primary text-center border-0">
                                    <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" class="me-2">
                                        <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
                                        <polyline points="17 21 17 13 7 13 7 21"/>
                                        <polyline points="7 3 7 8 15 8"/>
                                    </svg>
                                    Lưu chỉ số
                                </button>
                            </div>
                        </div>
                    </form>
                </div>

            </main>
            <jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
        </div>
    </div>
    <script>
        const prevWater = ${not empty previousWaterReading ? previousWaterReading : 0};

        function toggleAnomalyFields(type) {
            let status = document.getElementById(type + 'Status').value;
            let replacedFields = document.getElementById(type + 'ReplacedFields');
            let rolloverFields = document.getElementById(type + 'RolloverFields');
            
            if (status === 'REPLACED') {
                replacedFields.style.display = 'flex';
                rolloverFields.style.display = 'none';
                document.getElementById(type + 'OldFinal').required = true;
                document.getElementById(type + 'NewStart').required = true;
            } else if (status === 'ROLLOVER') {
                replacedFields.style.display = 'none';
                rolloverFields.style.display = 'block';
                document.getElementById(type + 'OldFinal').required = false;
                document.getElementById(type + 'NewStart').required = false;
            } else {
                replacedFields.style.display = 'none';
                rolloverFields.style.display = 'none';
                document.getElementById(type + 'OldFinal').required = false;
                document.getElementById(type + 'NewStart').required = false;
            }
        }

        // Initialize on load (for edit form)
        window.addEventListener('DOMContentLoaded', () => {
            toggleAnomalyFields('electric');
            toggleAnomalyFields('water');
        });
    </script>
</body>
</html>
