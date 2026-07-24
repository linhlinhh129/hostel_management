<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:choose>
    <c:when test="${source == 'requests'}">
        <c:set var="activeMenu" value="requests" scope="request"/>
    </c:when>
    <c:otherwise>
        <c:set var="activeMenu" value="my-incidents" scope="request"/>
    </c:otherwise>
</c:choose>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chỉnh sửa báo cáo sự cố"/>
<c:set var="pageRole" value="OPERATOR"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
    <div class="app-shell">
        <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
        <div class="sidebar-overlay"></div>
        <div class="main-wrapper">
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
            <main class="page-content">
                <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

                <div class="page-header hero-sky-gradient" style="border-radius: var(--hms-radius-lg); margin-bottom: 1.75rem;">
                    <h1>Chỉnh sửa báo cáo sự cố</h1>
                    <p>Cập nhật thông tin sự cố đã báo cáo</p>
                </div>

                <div class="mintlify-card-base">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h5 class="m-0" style="font-weight: 600;">Cập nhật thông tin sự cố</h5>
                        <c:choose>
                            <c:when test="${requestObj.status == 'PENDING' || empty requestObj.status}">
                                <span class="badge-hms badge-warning">CHỜ XỬ LÝ</span>
                            </c:when>
                            <c:when test="${requestObj.status == 'IN_PROGRESS'}">
                                <span class="badge-hms badge-info">ĐANG XỬ LÝ</span>
                            </c:when>
                            <c:when test="${requestObj.status == 'COMPLETED' || requestObj.status == 'DONE'}">
                                <span class="badge-hms badge-success">HOÀN THÀNH</span>
                            </c:when>
                            <c:when test="${requestObj.status == 'REJECTED'}">
                                <span class="badge-hms badge-danger">TỪ CHỐI</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge-hms badge-neutral"><c:out value="${requestObj.status}"/></span>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger border-0" style="border-radius: 8px; background-color: #ffeaea; color: #d45656; margin-bottom: 24px;">
                            <c:out value="${error}"/>
                        </div>
                    </c:if>

                    <form action="${ctx}/operator/incidents/edit" method="POST" enctype="multipart/form-data" class="needs-validation" novalidate>
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="requestId" value="${requestObj.requestId}">
                        <input type="hidden" name="source" value="${source}">

                        <div class="row mb-4">
                            <div class="col-md-6 mb-3 mb-md-0">
                                <label for="facilityName" class="form-label" style="font-weight: 500;">Cơ sở / Tòa nhà</label>
                                <input type="text" class="mintlify-text-input" id="facilityName" name="facilityName"
                                       value="${parsedFacilityName}" readonly
                                       style="background-color: var(--hms-bg-subtle); color: var(--hms-text-muted);">
                                <div style="font-size: 12px; color: var(--color-steel); margin-top: 4px;">Không thể thay đổi cơ sở sau khi đã báo cáo.</div>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label" style="font-weight: 500;">Vị trí sự cố</label>
                                <input type="text" class="mintlify-text-input"
                                       value="${parsedRoomCode ne '' ? parsedRoomCode : 'Khu vực chung'}" readonly
                                       style="background-color: var(--hms-bg-subtle); color: var(--hms-text-muted);">
                            </div>
                        </div>

                        <div class="row mb-4">
                            <div class="col-md-6 mb-3 mb-md-0">
                                <label for="category" class="form-label" style="font-weight: 500;">Phân loại <span style="color: #d45656;">*</span></label>
                                <select class="form-select mintlify-text-input" id="category" name="category" required>
                                    <option value="Điện"      ${requestObj.category == 'Điện'      ? 'selected' : ''}>Điện</option>
                                    <option value="Nước"      ${requestObj.category == 'Nước'      ? 'selected' : ''}>Nước</option>
                                    <option value="An ninh"   ${requestObj.category == 'An ninh'   ? 'selected' : ''}>An ninh</option>
                                    <option value="Nội thất"  ${requestObj.category == 'Nội thất'  ? 'selected' : ''}>Nội thất / CSVC</option>
                                    <option value="Vệ sinh"   ${requestObj.category == 'Vệ sinh'   ? 'selected' : ''}>Vệ sinh</option>
                                    <option value="Khác"      ${requestObj.category == 'Khác'      ? 'selected' : ''}>Khác</option>
                                </select>
                                <div class="invalid-feedback" style="font-size: 13px;">Vui lòng chọn phân loại.</div>
                            </div>
                            <div class="col-md-6">
                                <label for="priority" class="form-label" style="font-weight: 500;">Mức độ ưu tiên <span style="color: #d45656;">*</span></label>
                                <select class="form-select mintlify-text-input" id="priority" name="priority" required>
                                    <option value="Bình thường" ${parsedPriority == 'Bình thường' ? 'selected' : ''}>Bình thường</option>
                                    <option value="Khẩn cấp"   ${parsedPriority == 'Khẩn cấp'   ? 'selected' : ''}>Khẩn cấp</option>
                                </select>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label for="description" class="form-label" style="font-weight: 500;">Mô tả chi tiết <span style="color: #d45656;">*</span></label>
                            <textarea class="mintlify-text-input" id="description" name="description" rows="4" required><c:out value="${parsedDescription}"/></textarea>
                            <div class="invalid-feedback" style="font-size: 13px;">Vui lòng nhập mô tả chi tiết.</div>
                        </div>

                        <div class="mb-4">
                            <label for="attachments" class="form-label" style="font-weight: 500;">Thêm ảnh đính kèm (Không bắt buộc)</label>
                            <input class="form-control" type="file" id="attachments" name="attachments" accept="image/*" multiple>
                            <div style="font-size: 12px; color: var(--color-steel); margin-top: 4px;">Chọn thêm ảnh sẽ tải lên cùng với các ảnh đã có.</div>
                            <div id="imagePreviewContainer" class="d-flex flex-wrap gap-2 mt-3">
                                <c:forEach var="img" items="${requestObj.images}">
                                    <div style="width: 100px; height: 100px; border-radius: 8px; overflow: hidden; border: 1px solid var(--color-hairline-soft);">
                                        <img src="${ctx}${img}" class="w-100 h-100" style="object-fit: cover;" alt="Current Image">
                                    </div>
                                </c:forEach>
                            </div>
                        </div>

                        <div class="mt-4 pt-3 border-top" style="border-color: var(--color-hairline-soft) !important;">
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end mt-4">
                                <c:choose>
                                    <c:when test="${source == 'requests'}">
                                        <a href="${ctx}/operator/requests" class="mintlify-btn-secondary text-center text-decoration-none">Hủy</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${ctx}/operator/incidents/my-reports" class="mintlify-btn-secondary text-center text-decoration-none">Hủy</a>
                                    </c:otherwise>
                                </c:choose>
                                <button type="submit" class="mintlify-btn-primary text-center border-0">Lưu thay đổi</button>
                            </div>
                        </div>
                    </form>
                </div>

            </main>
            <jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
        </div>
    </div>

    <script>
    document.addEventListener('DOMContentLoaded', function () {
        // Image preview for new files
        var fileInput = document.getElementById('attachments');
        var previewContainer = document.getElementById('imagePreviewContainer');

        fileInput.addEventListener('change', function () {
            if (this.files && this.files.length > 0) {
                Array.from(this.files).forEach(function (file) {
                    if (file.type.startsWith('image/')) {
                        var reader = new FileReader();
                        reader.onload = function (e) {
                            var wrapper = document.createElement('div');
                            wrapper.style.cssText = 'width:100px;height:100px;border-radius:8px;overflow:hidden;border:2px solid var(--hms-success);';
                            var img = document.createElement('img');
                            img.src = e.target.result;
                            img.style.cssText = 'width:100%;height:100%;object-fit:cover;opacity:0.8;';
                            wrapper.appendChild(img);
                            previewContainer.appendChild(wrapper);
                        };
                        reader.readAsDataURL(file);
                    }
                });
            }
        });

        // Bootstrap validation
        document.querySelectorAll('.needs-validation').forEach(function (form) {
            form.addEventListener('submit', function (event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    });
    </script>
</body>
</html>
