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
                                <input type="number" id="newElectric" name="newElectric" class="mintlify-text-input shadow-sm" required
                                       min="0" max="99999" step="1"
                                       value="${currentElectricReading}" placeholder="${empty meterId ? 'Nhập số điện mới (tối đa 5 chữ số)' : 'Sửa số điện (tối đa 5 chữ số)'}">
                                <div id="electricWarning" class="text-danger small mt-1" style="display: none;">
                                    <i class="fas fa-exclamation-circle"></i> Chỉ số điện không được vượt quá 99999 (tối đa 5 chữ số).
                                </div>
                            </div>
                            
                            <div class="col-12" id="electricStatusBlock">
                                <div class="p-3 bg-light rounded border mb-3">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" name="electricStatus" id="electricStatus" value="ROLLOVER" ${electricStatus == 'ROLLOVER' ? 'checked' : ''}>
                                        <label class="form-check-label fw-medium" for="electricStatus">
                                            Công tơ điện chạy hết vòng (reset về 0)
                                        </label>
                                    </div>
                                    <input type="hidden" name="electricMaxLimit" value="100000">
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
                                <input type="number" id="newWater" name="newWater" class="mintlify-text-input shadow-sm" required
                                       min="0" max="99999" step="1"
                                       value="${currentWaterReading}" placeholder="${empty meterId ? 'Nhập số nước mới (tối đa 5 chữ số)' : 'Sửa số nước (tối đa 5 chữ số)'}">
                                <div id="waterWarning" class="text-danger small mt-1" style="display: none;">
                                    <i class="fas fa-exclamation-circle"></i> Chỉ số nước không được vượt quá 99999 (tối đa 5 chữ số).
                                </div>
                            </div>

                            <div class="col-12" id="waterStatusBlock">
                                <div class="p-3 bg-light rounded border mb-3">
                                    <div class="form-check">
                                        <input class="form-check-input" type="checkbox" name="waterStatus" id="waterStatus" value="ROLLOVER" ${waterStatus == 'ROLLOVER' ? 'checked' : ''}>
                                        <label class="form-check-label fw-medium" for="waterStatus">
                                            Công tơ nước chạy hết vòng (reset về 0)
                                        </label>
                                    </div>
                                    <input type="hidden" name="waterMaxLimit" value="100000">
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


        // Hiển thị cảnh báo real-time khi nhập sai
    document.getElementById('newElectric').addEventListener('input', function() {
        if (this.value.includes('.') || this.value.includes(',')) {
            alert('Chỉ số không được chứa số thập phân.');
            this.value = this.value.replace(/[.,]/g, '');
        }
        var val = parseInt(this.value, 10);
        if (val < 0) {
            alert('Chỉ số không được là số âm.');
            this.value = '';
        } else if (val > 99999) {
            alert('Chỉ số điện không được vượt quá 99999 (tối đa 5 chữ số).');
            this.value = 99999;
        }
    });

    document.getElementById('newWater').addEventListener('input', function() {
        if (this.value.includes('.') || this.value.includes(',')) {
            alert('Chỉ số không được chứa số thập phân.');
            this.value = this.value.replace(/[.,]/g, '');
        }
        var val = parseInt(this.value, 10);
        if (val < 0) {
            alert('Chỉ số không được là số âm.');
            this.value = '';
        } else if (val > 99999) {
            alert('Chỉ số nước không được vượt quá 99999 (tối đa 5 chữ số).');
            this.value = 99999;
        }
    });
</script>
</body>
</html>
