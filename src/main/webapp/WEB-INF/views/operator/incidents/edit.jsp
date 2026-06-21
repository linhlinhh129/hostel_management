<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="activeMenu" value="incident-report"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp">
        <jsp:param name="title" value="Chỉnh sửa báo cáo sự cố" />
    </jsp:include>
    <link href="https://fonts.googleapis.com/css2?family=Geist+Mono:wght@400;500&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
</head>
<body id="page-top">
    <div class="app-shell">
        <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
        <div class="sidebar-overlay"></div>
        <div class="main-wrapper" style="background-color: var(--color-canvas);">
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
            <main class="page-content">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2 class="h3 mb-0" style="font-family: 'Inter', sans-serif; font-weight: 600; color: var(--color-ink);">Chỉnh sửa báo cáo sự cố</h2>
                </div>
        <div class="row">
            <div class="col-lg-12">
                <div class="card border rounded-4 shadow-sm" style="background-color: #ffffff;">
                    <div class="card-body p-4">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <h5 class="card-title fw-bold mb-0">Cập nhật thông tin sự cố</h5>
                            <span class="badge bg-warning text-dark px-3 py-2 rounded-pill">PENDING</span>
                        </div>
                        
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                ${error}
                                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                            </div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/operator/incidents/edit" method="POST" enctype="multipart/form-data" class="needs-validation" novalidate>
                            <input type="hidden" name="csrfToken" value="${csrfToken}">
                            <input type="hidden" name="requestId" value="${requestObj.requestId}">
                            
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="facilityName" class="form-label fw-medium text-dark">Cơ sở / Tòa nhà</label>
                                    <input type="text" class="form-control" id="facilityName" name="facilityName" value="${parsedFacilityName}" readonly style="background-color: #f7f7f7;">
                                    <div class="form-text">Không thể thay đổi cơ sở sau khi đã báo cáo.</div>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label class="form-label fw-medium text-dark">Vị trí sự cố</label>
                                    <input type="text" class="form-control" value="${parsedRoomCode ne '' ? parsedRoomCode : 'Khu vực chung'}" readonly style="background-color: #f7f7f7;">
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="category" class="form-label fw-medium text-dark">Phân loại <span class="text-danger">*</span></label>
                                    <select class="form-select" id="category" name="category" required>
                                        <option value="Điện" ${requestObj.category == 'Điện' ? 'selected' : ''}>Điện</option>
                                        <option value="Nước" ${requestObj.category == 'Nước' ? 'selected' : ''}>Nước</option>
                                        <option value="An ninh" ${requestObj.category == 'An ninh' ? 'selected' : ''}>An ninh</option>
                                        <option value="Nội thất" ${requestObj.category == 'Nội thất' ? 'selected' : ''}>Nội thất/CSVC</option>
                                        <option value="Vệ sinh" ${requestObj.category == 'Vệ sinh' ? 'selected' : ''}>Vệ sinh</option>
                                        <option value="Khác" ${requestObj.category == 'Khác' ? 'selected' : ''}>Khác</option>
                                    </select>
                                    <div class="invalid-feedback">Vui lòng chọn phân loại.</div>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="priority" class="form-label fw-medium text-dark">Mức độ ưu tiên <span class="text-danger">*</span></label>
                                    <select class="form-select" id="priority" name="priority" required>
                                        <option value="Bình thường" ${parsedPriority == 'Bình thường' ? 'selected' : ''}>Bình thường</option>
                                        <option value="Khẩn cấp" ${parsedPriority == 'Khẩn cấp' ? 'selected' : ''}>Khẩn cấp</option>
                                    </select>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="description" class="form-label fw-medium text-dark">Mô tả chi tiết <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="description" name="description" rows="4" required>${parsedDescription}</textarea>
                                <div class="invalid-feedback">Vui lòng nhập mô tả chi tiết.</div>
                            </div>

                            <div class="mb-4">
                                <label for="attachments" class="form-label fw-medium text-dark">Thêm ảnh đính kèm (Không bắt buộc)</label>
                                <input class="form-control" type="file" id="attachments" name="attachments" accept="image/*" multiple>
                                <div class="form-text text-muted">Chọn thêm ảnh sẽ tải lên cùng với các ảnh đã có.</div>
                                <div id="imagePreviewContainer" class="d-flex flex-wrap gap-2 mt-3">
                                    <!-- Hiển thị ảnh cũ -->
                                    <c:forEach var="img" items="${requestObj.images}">
                                        <div class="position-relative border rounded p-1 bg-white shadow-sm" style="width: 100px; height: 100px;">
                                            <img src="${pageContext.request.contextPath}${img}" class="w-100 h-100 object-fit-cover rounded" alt="Current Image">
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>

                            <div class="d-grid gap-2 d-md-flex justify-content-md-end mt-4">
                                <a href="${pageContext.request.contextPath}/operator/incidents/my-reports" class="btn-mintlify-secondary text-center text-decoration-none">Hủy</a>
                                <button type="submit" class="btn-mintlify-primary text-center">Lưu thay đổi</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>


<script>
document.addEventListener("DOMContentLoaded", function() {
    // Image preview logic for new files
    const fileInput = document.getElementById('attachments');
    const previewContainer = document.getElementById('imagePreviewContainer');

    fileInput.addEventListener('change', function() {
        // We keep old images, but remove previously selected "new" previews if they select again.
        // For simplicity, we just append new previews.
        if (this.files && this.files.length > 0) {
            Array.from(this.files).forEach((file, index) => {
                if (file.type.startsWith('image/')) {
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        const imgWrapper = document.createElement('div');
                        imgWrapper.className = 'position-relative border rounded p-1 bg-white shadow-sm border-success';
                        imgWrapper.style.width = '100px';
                        imgWrapper.style.height = '100px';

                        const img = document.createElement('img');
                        img.src = e.target.result;
                        img.className = 'w-100 h-100 object-fit-cover rounded opacity-75'; // distinct opacity for new

                        imgWrapper.appendChild(img);
                        previewContainer.appendChild(imgWrapper);
                    }
                    reader.readAsDataURL(file);
                }
            });
        }
    });

    // Bootstrap validation
    var forms = document.querySelectorAll('.needs-validation')
    Array.prototype.slice.call(forms).forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                event.preventDefault()
                event.stopPropagation()
            }
            form.classList.add('was-validated')
        }, false)
    });
});
</script>

            </main>
            <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
        </div>
    </div>
</body>
</html>
