<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <jsp:include page="/WEB-INF/views/layout/head.jsp">
                    <jsp:param name="title" value="Chi tiết yêu cầu sửa chữa" />
                </jsp:include>
                <link
                    href="https://fonts.googleapis.com/css2?family=Geist+Mono:wght@400;500&family=Inter:wght@400;500;600&display=swap"
                    rel="stylesheet">
                <style>
                    .page-content {
                        max-width: 1400px;
                        margin: 0 auto;
                    }

                    .center-prose {
                        padding-right: 48px;
                    }

                    .right-panel {
                        border-left: 1px solid var(--color-hairline-soft);
                        padding-left: 32px;
                        min-height: calc(100vh - 200px);
                    }

                    @media (max-width: 991.98px) {
                        .center-prose {
                            padding-right: 15px;
                        }

                        .right-panel {
                            border-left: none;
                            padding-left: 15px;
                            padding-top: 32px;
                            border-top: 1px solid var(--color-hairline-soft);
                            margin-top: 32px;
                            min-height: auto;
                        }
                    }

                    .mintlify-prose-title {
                        font-family: 'Inter', sans-serif;
                        font-size: 36px;
                        font-weight: 600;
                        color: var(--color-ink);
                        letter-spacing: -0.5px;
                        margin-bottom: 8px;
                        line-height: 1.2;
                    }

                    .mintlify-prose-meta {
                        color: var(--color-steel);
                        font-size: 14px;
                        margin-bottom: 32px;
                    }

                    .mintlify-prose-content {
                        font-size: 16px;
                        color: var(--color-charcoal);
                        line-height: 1.6;
                        margin-bottom: 32px;
                    }

                    .mintlify-section-header {
                        font-size: 11px;
                        font-weight: 600;
                        color: var(--color-steel);
                        text-transform: uppercase;
                        letter-spacing: 0.5px;
                        margin-bottom: 16px;
                    }

                    /* Block buttons */
                    .btn-block-full {
                        width: 100%;
                        display: block;
                        text-align: center;
                        margin-bottom: 12px;
                    }

                    .meta-row {
                        display: flex;
                        flex-direction: column;
                        padding: 12px 0;
                        border-bottom: 1px solid var(--color-hairline-soft);
                    }

                    .meta-label {
                        font-size: 13px;
                        color: var(--color-steel);
                        font-weight: 500;
                        margin-bottom: 4px;
                    }

                    .meta-value {
                        font-size: 14px;
                        color: var(--color-ink);
                        font-weight: 500;
                    }
                </style>
            </head>

            <body id="page-top">
                <div class="app-shell">
                    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
                    <div class="sidebar-overlay"></div>
                    <div class="main-wrapper" style="background-color: var(--color-canvas);">
                        <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
                        <main class="page-content">
                            <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

                            <c:if test="${not empty error}">
                                <div class="alert alert-danger">${error}</div>
                            </c:if>

                            <c:if test="${not empty sessionScope.successMessage}">
                                <div class="alert alert-success">${sessionScope.successMessage}</div>
                                <c:remove var="successMessage" scope="session" />
                            </c:if>

                            <div class="row">
                                <!-- Cột 2: Center Prose (8 columns) -->
                                <div class="col-lg-8 center-prose">
                                    <div class="mb-4">
                                        <a href="${pageContext.request.contextPath}/operator/requests"
                                            class="text-decoration-none"
                                            style="color: var(--color-steel); font-size: 14px; font-weight: 500;">&larr;
                                            Quay lại danh sách</a>
                                    </div>

                                    <div class="d-flex align-items-center gap-2 mb-2">
                                        <span class="mintlify-badge-type">${reqDetail.category}</span>
                                    </div>
                                    <h1 class="mintlify-prose-title">${reqDetail.title}</h1>
                                    <div class="mintlify-prose-meta">Mã yêu cầu: <span
                                            style="font-family: 'Geist Mono', monospace;">${reqDetail.code}</span></div>

                                    <div class="mintlify-prose-content">
                                        ${reqDetail.content}
                                    </div>

                                    <c:set var="images" value="${reqDetail.images}" />
                                    <c:if test="${not empty images}">
                                        <div class="mt-5">
                                            <h4
                                                style="font-size: 18px; font-weight: 600; color: var(--color-ink); margin-bottom: 16px;">
                                                Ảnh đính kèm (${images.size()})</h4>
                                            <div class="d-flex flex-wrap gap-3">
                                                <c:forEach var="img" items="${images}">
                                                    <img src="${img}" alt="Attachment"
                                                        style="width: 120px; height: 120px; object-fit: cover; cursor: pointer; border-radius: 8px; border: 1px solid var(--color-hairline-soft); box-shadow: 0 1px 2px rgba(0,0,0,0.05);"
                                                        onclick="openLightbox('${img}')" />
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </c:if>
                                </div>

                                <!-- Cột 3: Right Panel / TOC (4 columns) -->
                                <div class="col-lg-4 right-panel">
                                    <div class="mintlify-section-header">Hành động</div>
                                    <div class="mb-5">
                                        <c:if test="${reqDetail.status == 'PENDING'}">
                                            <form action="${pageContext.request.contextPath}/operator/requests/detail"
                                                method="POST" class="m-0">
                                                <input type="hidden" name="csrfToken" value="${csrfToken}" />
                                                <input type="hidden" name="id" value="${reqDetail.requestId}" />
                                                <input type="hidden" name="action" value="accept" />
                                                <button type="submit" class="mintlify-btn-primary w-100"
                                                    style="padding: 10px; border-radius: 6px; font-weight: 500;"
                                                    onclick="return confirm('Bạn có chắc chắn muốn nhận xử lý yêu cầu này?')">Xác nhận tiếp nhận</button>
                                            </form>
                                            <button type="button" class="mintlify-btn-secondary w-100 mt-2"
                                                onclick="openRejectModal()">Từ chối</button>
                                        </c:if>

                                        <c:if
                                            test="${reqDetail.status == 'IN_PROGRESS' && reqDetail.assignedStaffId == sessionScope.currentUser.id}">
                                            <button type="button" class="mintlify-btn-primary w-100"
                                                style="background-color: var(--color-brand-green); color: var(--color-primary); border: none;"
                                                onclick="openCompleteModal()">Báo cáo hoàn thành</button>
                                        </c:if>

                                        <c:if
                                            test="${reqDetail.status != 'PENDING' && (reqDetail.status != 'IN_PROGRESS' || reqDetail.assignedStaffId != sessionScope.currentUser.id)}">
                                            <div class="text-center p-3 rounded"
                                                style="background-color: var(--color-surface); border: 1px solid var(--color-hairline-soft); color: var(--color-steel); font-size: 13px;">
                                                Không có hành động khả dụng.
                                            </div>
                                        </c:if>
                                    </div>

                                    <div class="mintlify-section-header">Thông tin chi tiết</div>
                                    <div>
                                        <div class="mintlify-property-row">
                                            <div class="mintlify-property-label">Trạng thái</div>
                                            <div class="mintlify-property-value mt-1">
                                                <c:choose>
                                                    <c:when test="${reqDetail.status == 'PENDING'}">
                                                        <span class="mintlify-badge-status-pending">CHỜ XỬ LÝ</span>
                                                    </c:when>
                                                    <c:when test="${reqDetail.status == 'IN_PROGRESS'}">
                                                        <span class="mintlify-badge-status-inprogress">ĐANG XỬ LÝ</span>
                                                    </c:when>
                                                    <c:when test="${reqDetail.status == 'REJECTED'}">
                                                        <span class="mintlify-badge-status-rejected">TỪ CHỐI</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="mintlify-badge-type">${reqDetail.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                        <div class="mintlify-property-row">
                                            <div class="mintlify-property-label">Người gửi</div>
                                            <div class="mintlify-property-value">${reqDetail.senderName}</div>
                                        </div>
                                        <div class="mintlify-property-row">
                                            <div class="mintlify-property-label">Phòng / Cơ sở</div>
                                            <div class="mintlify-property-value">P.${reqDetail.roomCode} - ${reqDetail.facilityName}
                                            </div>
                                        </div>
                                        <div class="mintlify-property-row">
                                            <div class="mintlify-property-label">Ngày tạo</div>
                                            <div class="mintlify-property-value">
                                                <fmt:formatDate value="${reqDetail.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm:ss" />
                                            </div>
                                        </div>

                                        <c:if test="${reqDetail.status == 'PENDING' || reqDetail.status == 'IN_PROGRESS'}">
                                            <div class="mt-4 p-4" style="background: linear-gradient(145deg, #ffffff, #f8fafc); border: 1px solid #e2e8f0; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);">
                                                <div class="d-flex align-items-center gap-2 mb-3">
                                                    <div style="background: #e0f2fe; color: #0284c7; padding: 6px; border-radius: 8px;">
                                                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
                                                    </div>
                                                    <span style="font-size: 14px; font-weight: 600; color: #0f172a;">Lên lịch sửa chữa</span>
                                                </div>
                                                
                                                <c:if test="${not empty reqDetail.rejectionReason}">
                                                    <div class="alert alert-info py-2 px-3 mb-3" style="font-size: 13px; border-radius: 8px; border-color: #bae6fd; background-color: #f0f9ff; color: #0369a1;">
                                                        <strong>Đã có lịch hẹn.</strong> Bạn có thể thay đổi lịch bên dưới.
                                                    </div>
                                                </c:if>
                                                
                                                <form action="${pageContext.request.contextPath}/operator/requests/detail" method="POST" class="m-0">
                                                    <input type="hidden" name="csrfToken" value="${csrfToken}" />
                                                    <input type="hidden" name="id" value="${reqDetail.requestId}" />
                                                    <input type="hidden" name="action" value="schedule" />
                                                    
                                                    <div class="position-relative mb-3">
                                                        <input type="datetime-local" name="appointmentDate" value="${reqDetail.rejectionReason}" class="form-control shadow-sm" style="border-radius: 8px; border: 1px solid #cbd5e1; padding: 10px 14px; font-size: 14px; color: #334155; width: 100%; transition: all 0.2s; outline: none;" required onfocus="this.style.borderColor='#38bdf8'; this.style.boxShadow='0 0 0 3px rgba(56, 189, 248, 0.2)';" onblur="this.style.borderColor='#cbd5e1'; this.style.boxShadow='0 1px 2px rgba(0,0,0,0.05)';" />
                                                    </div>
                                                    
                                                    <button type="submit" class="w-100 btn d-flex align-items-center justify-content-center gap-2" style="background: #0ea5e9; color: white; border: none; padding: 10px; font-weight: 500; font-size: 14px; border-radius: 8px; transition: all 0.2s; box-shadow: 0 4px 6px -1px rgba(14, 165, 233, 0.3);" onmouseover="this.style.background='#0284c7'; this.style.transform='translateY(-1px)';" onmouseout="this.style.background='#0ea5e9'; this.style.transform='translateY(0)';">
                                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="m12 5 7 7-7 7"/></svg>
                                                        Xác nhận lịch hẹn
                                                    </button>
                                                </form>
                                            </div>
                                        </c:if>
                                        <c:if test="${not empty reqDetail.assignedStaffId}">
                                            <div class="mintlify-property-row">
                                                <div class="mintlify-property-label">Nhân viên tiếp nhận</div>
                                                <div class="mintlify-property-value" style="font-family: 'Geist Mono', monospace;">
                                                    ID: ${reqDetail.assignedStaffId}</div>
                                            </div>
                                        </c:if>
                                        <c:if
                                            test="${reqDetail.status == 'REJECTED' && not empty reqDetail.rejectionReason}">
                                            <div class="mintlify-property-row" style="border-bottom: none;">
                                                <div class="mintlify-property-label text-danger">Lý do từ chối</div>
                                                <div class="mintlify-property-value text-danger"
                                                    style="font-weight: 400; font-size: 13px; margin-top: 4px;">
                                                    ${reqDetail.rejectionReason}</div>
                                            </div>
                                        </c:if>
                                        <c:if
                                            test="${reqDetail.status == 'COMPLETED' && not empty reqDetail.rejectionReason}">
                                            <div class="mintlify-property-row" style="border-bottom: none;">
                                                <div class="mintlify-property-label" style="color: var(--color-brand-annotate);">Ghi
                                                    chú hoàn thành</div>
                                                <div class="mintlify-property-value"
                                                    style="font-weight: 400; font-size: 13px; margin-top: 4px;">
                                                    ${reqDetail.rejectionReason}</div>
                                            </div>
                                        </c:if>
                                    </div>
                                </div>
                            </div>

                        </main>
                        <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
                    </div>
                </div>

                <!-- Modal Nhập lý do từ chối -->
                <div id="rejectModal" class="custom-modal-backdrop" style="z-index: 9999;">
                    <div class="custom-modal-dialog">
                        <h5 style="font-weight: 600; margin-bottom: 20px;">Từ chối yêu cầu</h5>
                        <form action="${pageContext.request.contextPath}/operator/requests/detail" method="POST">
                            <input type="hidden" name="csrfToken" value="${csrfToken}" />
                            <input type="hidden" name="id" value="${reqDetail.requestId}" />
                            <input type="hidden" name="action" value="reject" />
                            <div class="mb-3">
                                <label class="d-block mb-2"
                                    style="font-size: 14px; font-weight: 500; color: var(--color-ink);">Lý do từ chối
                                    <span class="text-danger">*</span></label>
                                <textarea name="rejectReason" class="mintlify-text-input" rows="3" required
                                    placeholder="Vui lòng nhập lý do cụ thể..."></textarea>
                            </div>
                            <div class="d-flex justify-content-end mt-4" style="gap: 12px;">
                                <button type="button" class="btn-mintlify-secondary"
                                    onclick="closeRejectModal()">Hủy</button>
                                <button type="submit" class="btn-mintlify-primary"
                                    id="btnReject">Xác nhận Từ chối</button>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Modal Báo cáo hoàn thành -->
                <div id="completeModal" class="custom-modal-backdrop" style="z-index: 9999;">
                    <div class="custom-modal-dialog">
                        <h5 style="font-weight: 600; margin-bottom: 20px;">Báo cáo hoàn thành sửa chữa</h5>
                        <form action="${pageContext.request.contextPath}/operator/requests/detail?csrfToken=${csrfToken}" method="POST"
                            enctype="multipart/form-data">
                            <input type="hidden" name="id" value="${reqDetail.requestId}" />
                            <input type="hidden" name="action" value="complete" />
                            <div class="mb-3">
                                <label class="d-block mb-2"
                                    style="font-size: 14px; font-weight: 500; color: var(--color-ink);">Ghi chú kết quả
                                    <span class="text-danger">*</span></label>
                                <textarea name="notes" class="mintlify-text-input" rows="3" required
                                    placeholder="Nhập ghi chú hoặc kết quả xử lý..."></textarea>
                            </div>
                            <div class="mb-3">
                                <label class="d-flex align-items-center gap-2 mb-2" style="font-size: 14px; font-weight: 500; color: var(--color-ink);">
                                    <input type="checkbox" name="no_image_checkbox" id="no_image_checkbox" onchange="toggleImageRequired()" style="width: 16px; height: 16px;">
                                    Lỗi đơn giản (Chỉ cần ghi chú, không đính kèm ảnh)
                                </label>
                            </div>
                            <div class="mb-3" id="image_upload_section">
                                <label class="d-block mb-2"
                                    style="font-size: 14px; font-weight: 500; color: var(--color-ink);">Ảnh minh chứng
                                    (Tối đa 5 ảnh) <span class="text-danger" id="image_required_star">*</span></label>
                                <input type="file" name="after_images" id="after_images_input" class="form-control"
                                    multiple accept="image/jpeg, image/png, image/jpg" required>
                                <div style="font-size: 12px; color: var(--color-steel); margin-top: 4px;">Định dạng:
                                    JPG, PNG. Tối đa 5MB/ảnh.</div>
                            </div>
                            <div class="d-flex justify-content-end mt-4" style="gap: 12px;">
                                <button type="button" class="mintlify-btn-secondary"
                                    onclick="closeCompleteModal()">Hủy</button>
                                <button type="submit" class="mintlify-btn-primary"
                                    style="background-color: var(--color-brand-green); color: var(--color-primary); border: none;">Xác
                                    nhận lưu</button>
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
                        document.getElementById('lightboxModal').style.display = "block";
                        document.getElementById('lightboxImage').src = src;
                    }

                    function closeLightbox() {
                        document.getElementById('lightboxModal').style.display = "none";
                    }

                    function openRejectModal() {
                        document.getElementById('rejectModal').style.display = "block";
                    }

                    function closeRejectModal() {
                        document.getElementById('rejectModal').style.display = "none";
                    }

                    function toggleImageRequired() {
                        var checkbox = document.getElementById('no_image_checkbox');
                        var input = document.getElementById('after_images_input');
                        var star = document.getElementById('image_required_star');
                        if (checkbox.checked) {
                            input.required = false;
                            star.style.display = "none";
                        } else {
                            input.required = true;
                            star.style.display = "inline";
                        }
                    }

                    function openCompleteModal() {
                        document.getElementById('completeModal').style.display = "block";
                    }

                    function closeCompleteModal() {
                        document.getElementById('completeModal').style.display = "none";
                    }

                    window.onclick = function (event) {
                        var lbModal = document.getElementById('lightboxModal');
                        if (event.target == lbModal) {
                            lbModal.style.display = "none";
                        }
                        var rejectModal = document.getElementById('rejectModal');
                        if (event.target == rejectModal) {
                            rejectModal.style.display = "none";
                        }
                        var completeModal = document.getElementById('completeModal');
                        if (event.target == completeModal) {
                            completeModal.style.display = "none";
                        }
                    }
                </script>
            </body>

            </html>