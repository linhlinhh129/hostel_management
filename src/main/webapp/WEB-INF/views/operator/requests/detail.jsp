<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết yêu cầu sửa chữa"/>
<c:set var="pageRole" value="OPERATOR"/>
<c:set var="activeMenu" value="tickets"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<style>
    .page-content      { max-width: 1400px; margin: 0 auto; }
    .center-prose      { padding-right: 48px; }
    .right-panel       { border-left: 1px solid var(--color-hairline-soft); padding-left: 32px; min-height: calc(100vh - 200px); }
    @media (max-width: 991.98px) {
        .center-prose  { padding-right: 15px; }
        .right-panel   { border-left: none; padding-left: 15px; padding-top: 32px; border-top: 1px solid var(--color-hairline-soft); margin-top: 32px; min-height: auto; }
    }
    .mintlify-prose-title   { font-family: 'Inter', sans-serif; font-size: 36px; font-weight: 600; color: var(--color-ink); letter-spacing: -0.5px; margin-bottom: 8px; line-height: 1.2; }
    .mintlify-prose-meta    { color: var(--color-steel); font-size: 14px; margin-bottom: 32px; }
    .mintlify-prose-content { font-size: 16px; color: var(--color-charcoal); line-height: 1.6; margin-bottom: 32px; }
    .mintlify-section-header { font-size: 11px; font-weight: 600; color: var(--color-steel); text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 16px; }
</style>

<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <c:if test="${not empty error}">
                <div class="alert alert-danger border-0" style="border-radius: 8px; background-color: #ffeaea; color: #d45656; margin-bottom: 16px;"><c:out value="${error}"/></div>
            </c:if>
            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert alert-success border-0" style="border-radius: 8px; background-color: #e0f8ef; color: #1ba673; margin-bottom: 16px;"><c:out value="${sessionScope.successMessage}"/></div>
                <c:remove var="successMessage" scope="session"/>
            </c:if>

            <div class="row">
                <!-- Cột trái: nội dung chính -->
                <div class="col-lg-8 center-prose">
                    <div class="mb-4">
                        <a href="${ctx}/operator/requests" class="text-decoration-none"
                           style="color: var(--color-steel); font-size: 14px; font-weight: 500;">← Quay lại danh sách</a>
                    </div>

                    <h1 class="mintlify-prose-title"><c:out value="${reqDetail.title}"/></h1>
                    <div class="mintlify-prose-meta">
                        Mã yêu cầu: <span style="font-family: 'Geist Mono', monospace;"><c:out value="${reqDetail.code}"/></span>
                    </div>

                    <div class="mintlify-prose-content">${reqDetail.content}</div>

                    <c:set var="images" value="${reqDetail.images}"/>
                    <c:if test="${not empty images}">
                        <div class="mt-5">
                            <h4 style="font-size: 18px; font-weight: 600; color: var(--color-ink); margin-bottom: 16px;">
                                Ảnh đính kèm (${images.size()})
                            </h4>
                            <div class="d-flex flex-wrap gap-3">
                                <c:forEach var="img" items="${images}">
                                    <c:set var="finalImg">
                                        <c:choose>
                                            <c:when test="${fn:startsWith(img, ctx)}">${img}</c:when>
                                            <c:otherwise>${ctx}${img}</c:otherwise>
                                        </c:choose>
                                    </c:set>
                                    <img src="${finalImg}" alt="Attachment"
                                         style="width: 120px; height: 120px; object-fit: cover; cursor: pointer; border-radius: 8px; border: 1px solid var(--color-hairline-soft);"
                                         onclick="openLightbox('${finalImg}')"/>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>
                </div>

                <!-- Cột phải: thông tin + hành động -->
                <div class="col-lg-4 right-panel">
                    <div class="mintlify-section-header">Thông tin chi tiết</div>
                    <div class="mb-4">
                        <div class="mintlify-property-row">
                            <div class="mintlify-property-label">Trạng thái</div>
                            <div class="mintlify-property-value mt-1">
                                <c:choose>
                                    <c:when test="${reqDetail.status == 'PENDING'}">
                                        <span class="badge-hms badge-info">CHỜ XỬ LÝ</span>
                                    </c:when>
                                    <c:when test="${reqDetail.status == 'RECEIVED'}">
                                        <span class="badge-hms badge-info">ĐÃ TIẾP NHẬN</span>
                                    </c:when>
                                    <c:when test="${reqDetail.status == 'ASSIGNED'}">
                                        <span class="badge-hms badge-neutral">ĐÃ PHÂN CÔNG</span>
                                    </c:when>
                                    <c:when test="${reqDetail.status == 'IN_PROGRESS'}">
                                        <span class="badge-hms badge-warning">ĐANG XỬ LÝ</span>
                                    </c:when>
                                    <c:when test="${reqDetail.status == 'COMPLETED' || reqDetail.status == 'DONE' || reqDetail.status == 'RESOLVED'}">
                                        <span class="badge-hms badge-success">HOÀN THÀNH</span>
                                    </c:when>
                                    <c:when test="${reqDetail.status == 'REJECTED' || reqDetail.status == 'CANCELLED'}">
                                        <span class="badge-hms badge-danger">TỪ CHỐI</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge-hms badge-neutral"><c:out value="${reqDetail.status}"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="mintlify-property-row">
                            <div class="mintlify-property-label">Người gửi</div>
                            <div class="mintlify-property-value"><c:out value="${reqDetail.senderName}"/></div>
                        </div>
                        <div class="mintlify-property-row">
                            <div class="mintlify-property-label">Phòng / Cơ sở</div>
                            <div class="mintlify-property-value">P.<c:out value="${reqDetail.roomCode}"/> - <c:out value="${reqDetail.facilityName}"/></div>
                        </div>
                        <div class="mintlify-property-row">
                            <div class="mintlify-property-label">Ngày tạo</div>
                            <div class="mintlify-property-value">
                                <fmt:formatDate value="${reqDetail.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                            </div>
                        </div>
                        <c:if test="${reqDetail.status == 'REJECTED' && not empty reqDetail.rejectionReason}">
                            <div class="mintlify-property-row" style="border-bottom: none;">
                                <div class="mintlify-property-label text-danger">Lý do từ chối</div>
                                <div class="mintlify-property-value text-danger" style="font-weight: 400; font-size: 13px; margin-top: 4px;">
                                    <c:out value="${reqDetail.rejectionReason}"/>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${reqDetail.status == 'IN_PROGRESS' && not empty reqDetail.appointSchedule}">
                            <div class="mintlify-property-row" style="border-bottom: none;">
                                <div class="mintlify-property-label" style="color: var(--color-brand-primary);">Lịch hẹn xử lý</div>
                                <div class="mintlify-property-value" style="color: var(--color-brand-primary); font-weight: 500; font-size: 13px; margin-top: 4px;">
                                    <c:out value="${reqDetail.formattedAppointmentDate}"/>
                                </div>
                            </div>
                        </c:if>
                        <c:if test="${(reqDetail.status == 'COMPLETED' || reqDetail.status == 'DONE') && not empty reqDetail.rejectionReason}">
                            <div class="mintlify-property-row" style="border-bottom: none;">
                                <div class="mintlify-property-label" style="color: var(--color-brand-annotate);">Ghi chú hoàn thành</div>
                                <div class="mintlify-property-value" style="font-weight: 400; font-size: 13px; margin-top: 4px;">
                                    <c:out value="${reqDetail.rejectionReason}"/>
                                </div>
                            </div>
                        </c:if>
                    </div>

                    <div class="mintlify-section-header">Hành động</div>
                    <div class="mb-5">
                        <c:choose>
                            <c:when test="${reqDetail.status == 'PENDING'}">
                                <form action="${ctx}/operator/requests/detail" method="POST" class="m-0">
                                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                                    <input type="hidden" name="id" value="${reqDetail.requestId}"/>
                                    <input type="hidden" name="action" value="accept"/>
                                    <button type="submit" class="mintlify-btn-primary w-100"
                                            style="padding: 10px; font-weight: 500;"
                                            onclick="return confirm('Bạn có chắc chắn muốn nhận xử lý yêu cầu này?')">
                                        Xác nhận tiếp nhận
                                    </button>
                                </form>
                                <button type="button" class="mintlify-btn-secondary w-100 mt-2"
                                        onclick="openRejectModal()">Từ chối</button>
                            </c:when>

                            <c:when test="${reqDetail.status == 'IN_PROGRESS' && reqDetail.assignedStaffId == sessionScope.currentUser.id}">
                                <button type="button" class="mintlify-btn-primary w-100 border-0"
                                        style="background-color: var(--color-brand-green); color: var(--color-primary);"
                                        onclick="openCompleteModal()">Báo cáo hoàn thành</button>
                            </c:when>

                            <c:when test="${reqDetail.status == 'ASSIGNED' && reqDetail.assignedStaffId == sessionScope.currentUser.id}">
                                <div class="p-4" style="background: linear-gradient(145deg,#ffffff,#f8fafc); border: 1px solid #e2e8f0; border-radius: 12px;">
                                    <div class="d-flex align-items-center gap-2 mb-3">
                                        <div style="background: #e0f2fe; color: #0284c7; padding: 6px; border-radius: 8px;">
                                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                                        </div>
                                        <span style="font-size: 14px; font-weight: 600; color: #0f172a;">Lên lịch sửa chữa</span>
                                    </div>
                                    <c:if test="${not empty reqDetail.appointSchedule}">
                                        <div class="alert alert-info py-2 px-3 mb-3" style="font-size: 13px; border-radius: 8px; background-color: #f0f9ff; color: #0369a1; border-color: #bae6fd;">
                                            <strong>Đã có lịch hẹn.</strong> Bạn có thể thay đổi lịch bên dưới.
                                        </div>
                                    </c:if>
                                    <form action="${ctx}/operator/requests/detail" method="POST" class="m-0"
                                          onsubmit="return validateAppointment(this)">
                                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                                        <input type="hidden" name="id" value="${reqDetail.requestId}"/>
                                        <input type="hidden" name="action" value="schedule"/>
                                        <div class="mb-3">
                                            <input type="datetime-local" id="appointmentDateInput" name="appointmentDate"
                                                   value="${reqDetail.appointScheduleForInput}"
                                                   class="form-control" style="border-radius: 8px; font-size: 14px;" required/>
                                        </div>
                                        <button type="submit" class="w-100 mintlify-btn-primary border-0"
                                                style="background: #0ea5e9; color: white; justify-content: center;">
                                            Xác nhận lịch hẹn
                                        </button>
                                    </form>
                                </div>
                            </c:when>

                            <c:otherwise>
                                <div class="text-center p-3 rounded"
                                     style="background-color: var(--color-surface); border: 1px solid var(--color-hairline-soft); color: var(--color-steel); font-size: 13px;">
                                    <c:choose>
                                        <c:when test="${reqDetail.status == 'IN_PROGRESS'}">
                                            <span class="badge-hms badge-warning" style="display:inline-block;margin-bottom:6px">Đang xử lý</span>
                                            <p style="margin:0;font-size:0.8125rem">Yêu cầu đang được xử lý bởi nhân sự.</p>
                                        </c:when>
                                        <c:when test="${reqDetail.status == 'COMPLETED' || reqDetail.status == 'DONE'}">
                                            <span class="badge-hms badge-success" style="display:inline-block;margin-bottom:6px">Hoàn thành</span>
                                            <p style="margin:0;font-size:0.8125rem">Yêu cầu đã được xử lý thành công.</p>
                                        </c:when>
                                        <c:when test="${reqDetail.status == 'REJECTED'}">
                                            <span class="badge-hms badge-danger" style="display:inline-block;margin-bottom:6px">Đã từ chối</span>
                                        </c:when>
                                        <c:otherwise>Không có hành động khả dụng.</c:otherwise>
                                    </c:choose>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

        </main>
        <jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
    </div>
</div>

<!-- Modal: Từ chối -->
<div id="rejectModal" class="custom-modal-backdrop" style="z-index: 9999;">
    <div class="custom-modal-dialog">
        <h5 style="font-weight: 600; margin-bottom: 20px;">Từ chối yêu cầu</h5>
        <form action="${ctx}/operator/requests/detail" method="POST">
            <input type="hidden" name="csrfToken" value="${csrfToken}"/>
            <input type="hidden" name="id" value="${reqDetail.requestId}"/>
            <input type="hidden" name="action" value="reject"/>
            <div class="mb-3">
                <label class="d-block mb-2" style="font-size: 14px; font-weight: 500; color: var(--color-ink);">
                    Lý do từ chối <span class="text-danger">*</span>
                </label>
                <textarea name="rejectReason" class="mintlify-text-input" rows="3" required
                          placeholder="Vui lòng nhập lý do cụ thể..."></textarea>
            </div>
            <div class="d-flex justify-content-end mt-4" style="gap: 12px;">
                <button type="button" class="btn-mintlify-secondary" onclick="closeRejectModal()">Hủy</button>
                <button type="submit" class="btn-mintlify-primary">Xác nhận Từ chối</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal: Báo cáo hoàn thành -->
<div id="completeModal" class="custom-modal-backdrop" style="z-index: 9999;">
    <div class="custom-modal-dialog">
        <h5 style="font-weight: 600; margin-bottom: 20px;">Báo cáo hoàn thành sửa chữa</h5>
        <form action="${ctx}/operator/requests/detail" method="POST" enctype="multipart/form-data">
            <input type="hidden" name="csrfToken" value="${csrfToken}"/>
            <input type="hidden" name="id" value="${reqDetail.requestId}"/>
            <input type="hidden" name="action" value="complete"/>
            <div class="mb-3">
                <label class="d-block mb-2" style="font-size: 14px; font-weight: 500; color: var(--color-ink);">
                    Ghi chú kết quả <span class="text-danger">*</span>
                </label>
                <textarea name="notes" class="mintlify-text-input" rows="3" required
                          placeholder="Nhập ghi chú hoặc kết quả xử lý..."></textarea>
            </div>
            <div class="mb-3">
                <label class="d-flex align-items-center gap-2 mb-2" style="font-size: 14px; font-weight: 500; color: var(--color-ink);">
                    <input type="checkbox" id="no_image_checkbox" name="no_image_checkbox"
                           onchange="toggleImageRequired()" style="width: 16px; height: 16px;">
                    Lỗi đơn giản (Chỉ cần ghi chú, không đính kèm ảnh)
                </label>
            </div>
            <div class="mb-3" id="image_upload_section">
                <label class="d-block mb-2" style="font-size: 14px; font-weight: 500; color: var(--color-ink);">
                    Ảnh minh chứng (Tối đa 5 ảnh) <span class="text-danger" id="image_required_star">*</span>
                </label>
                <input type="file" name="after_images" id="after_images_input" class="form-control"
                       multiple accept="image/jpeg, image/png, image/jpg" required>
                <div style="font-size: 12px; color: var(--color-steel); margin-top: 4px;">Định dạng: JPG, PNG. Tối đa 5MB/ảnh.</div>
            </div>
            <div class="d-flex justify-content-end mt-4" style="gap: 12px;">
                <button type="button" class="mintlify-btn-secondary" onclick="closeCompleteModal()">Hủy</button>
                <button type="submit" class="mintlify-btn-primary border-0"
                        style="background-color: var(--color-brand-green); color: var(--color-primary);">Xác nhận lưu</button>
            </div>
        </form>
    </div>
</div>

<!-- Lightbox -->
<div id="lightboxModal" class="lightbox-modal">
    <span class="lightbox-close" onclick="closeLightbox()">&times;</span>
    <img class="lightbox-content" id="lightboxImage">
</div>

<script>
    function openLightbox(src) {
        document.getElementById('lightboxModal').style.display = 'block';
        document.getElementById('lightboxImage').src = src;
        var sidebar = document.querySelector('.sidebar');
        if (sidebar) sidebar.style.display = 'none';
    }
    function closeLightbox() {
        document.getElementById('lightboxModal').style.display = 'none';
        var sidebar = document.querySelector('.sidebar');
        if (sidebar) sidebar.style.display = '';
    }
    function openRejectModal()   { document.getElementById('rejectModal').style.display = 'block'; }
    function closeRejectModal()  { document.getElementById('rejectModal').style.display = 'none'; }
    function openCompleteModal() { document.getElementById('completeModal').style.display = 'block'; }
    function closeCompleteModal(){ document.getElementById('completeModal').style.display = 'none'; }

    function toggleImageRequired() {
        var cb    = document.getElementById('no_image_checkbox');
        var input = document.getElementById('after_images_input');
        var star  = document.getElementById('image_required_star');
        input.required     = !cb.checked;
        star.style.display = cb.checked ? 'none' : 'inline';
    }

    window.onclick = function(e) {
        ['lightboxModal','rejectModal','completeModal'].forEach(function(id) {
            var el = document.getElementById(id);
            if (e.target === el) {
                el.style.display = 'none';
                if (id === 'lightboxModal') {
                    var sidebar = document.querySelector('.sidebar');
                    if (sidebar) sidebar.style.display = '';
                }
            }
        });
    };

    document.addEventListener('DOMContentLoaded', function () {
        var apptInput = document.getElementById('appointmentDateInput');
        if (apptInput) {
            var now = new Date();
            now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
            apptInput.min = now.toISOString().slice(0, 16);
        }
    });

    function validateAppointment(form) {
        var apptInput = document.getElementById('appointmentDateInput');
        if (apptInput && apptInput.value) {
            if (new Date(apptInput.value) < new Date()) {
                alert('Không thể chọn lịch hẹn trong quá khứ. Vui lòng chọn thời gian từ hiện tại trở đi.');
                return false;
            }
        }
        return true;
    }
</script>
</body>
</html>
