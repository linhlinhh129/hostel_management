<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="activeMenu" value="profile"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp">
        <jsp:param name="title" value="Hồ sơ cá nhân" />
    </jsp:include>
    <link href="https://fonts.googleapis.com/css2?family=Geist+Mono:wght@400;500&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/mintlify.css" />
    <style>
        .avatar-preview {
            width: 120px;
            height: 120px;
            object-fit: cover;
            border-radius: 50%;
            border: 3px solid #fff;
            box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
        }
    </style>
</head>
<body id="page-top">
    <div class="app-shell">
        <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
        <div class="sidebar-overlay"></div>
        <div class="main-wrapper" style="background-color: var(--color-canvas);">
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />

            <main class="page-content" style="padding: 32px 48px; max-width: 1400px; margin: 0 auto;">
                <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />
                
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2 class="h3 mb-0" style="font-family: 'Inter', sans-serif; font-weight: 600; color: var(--color-ink);">Hồ sơ của tôi</h2>
                </div>

                <c:if test="${param.success == 'profile'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        Cập nhật thông tin hồ sơ thành công!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${param.success == 'password'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        Đổi mật khẩu thành công!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${param.error == 'password_mismatch'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        Xác nhận mật khẩu mới không khớp!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${param.error == 'invalid_password'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        Mật khẩu hiện tại không chính xác!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <div class="row">
                    <!-- Column 1: Chỉnh sửa hồ sơ -->
                    <div class="col-lg-8 mb-4">
                        <div class="card border rounded-4 shadow-sm" style="background-color: #ffffff;">
                            <div class="card-body p-4">
                                <h5 class="card-title fw-bold mb-4">Thông tin cá nhân</h5>
                                
                                <form action="${pageContext.request.contextPath}/profile" method="POST" enctype="multipart/form-data" class="needs-validation" novalidate>
                                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                                    <input type="hidden" name="action" value="update_profile">
                                    
                                    <div class="d-flex align-items-center gap-4 mb-4">
                                        <div class="position-relative">
                                            <c:choose>
                                                <c:when test="${not empty userProfile.avatarUrl}">
                                                    <img src="${pageContext.request.contextPath}${userProfile.avatarUrl}" class="avatar-preview" id="avatarPreview" alt="Avatar">
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="avatar-preview d-flex align-items-center justify-content-center bg-secondary text-white fs-1" id="avatarInitials">
                                                        ${sessionScope.currentUser.initials}
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <div>
                                            <label for="avatarInput" class="btn btn-outline-secondary rounded-pill px-4 mb-2">Đổi ảnh đại diện</label>
                                            <input type="file" id="avatarInput" name="avatar" class="d-none" accept="image/*">
                                            <div class="form-text text-muted">Định dạng JPEG, PNG. Tối đa 5MB.</div>
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label fw-medium text-dark">Tên đăng nhập</label>
                                            <input type="text" class="form-control" value="${userProfile.username}" readonly style="background-color: #f7f7f7;">
                                        </div>
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label fw-medium text-dark">Họ và tên <span class="text-danger">*</span></label>
                                            <input type="text" class="form-control" name="fullName" value="${userProfile.fullName}" required>
                                            <div class="invalid-feedback">Vui lòng nhập họ và tên.</div>
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label fw-medium text-dark">Email</label>
                                            <input type="email" class="form-control" value="${userProfile.email}" readonly style="background-color: #f7f7f7;">
                                            <div class="form-text text-muted">Vui lòng liên hệ Admin nếu muốn đổi Email.</div>
                                        </div>
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label fw-medium text-dark">Số điện thoại</label>
                                            <input type="text" class="form-control" name="phone" value="${userProfile.phone}">
                                        </div>
                                    </div>

                                    <div class="row">
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label fw-medium text-dark">Số CCCD / CMND</label>
                                            <input type="text" class="form-control" name="identityNumber" value="${userProfile.identityNumber}">
                                        </div>
                                        <div class="col-md-6 mb-3">
                                            <label class="form-label fw-medium text-dark">Ngày sinh</label>
                                            <input type="date" class="form-control" name="dob" value="${userProfile.dob}">
                                        </div>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label fw-medium text-dark">Giới tính</label>
                                        <select class="form-select" name="gender">
                                            <option value="" ${empty userProfile.gender ? 'selected' : ''}>-- Chọn giới tính --</option>
                                            <option value="Nam" ${userProfile.gender == 'Nam' ? 'selected' : ''}>Nam</option>
                                            <option value="Nữ" ${userProfile.gender == 'Nữ' ? 'selected' : ''}>Nữ</option>
                                            <option value="Khác" ${userProfile.gender == 'Khác' ? 'selected' : ''}>Khác</option>
                                        </select>
                                    </div>

                                    <div class="mb-4">
                                        <label class="form-label fw-medium text-dark">Địa chỉ thường trú</label>
                                        <textarea class="form-control" name="permanentAddress" rows="2">${userProfile.permanentAddress}</textarea>
                                    </div>

                                    <div class="text-end">
                                        <button type="submit" class="btn rounded-pill px-4 text-white" style="background-color: #00d4a4; color: #0a0a0a !important;">Lưu thay đổi</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                    <!-- Column 2: Đổi mật khẩu -->
                    <div class="col-lg-4">
                        <div class="card border rounded-4 shadow-sm" style="background-color: #ffffff;">
                            <div class="card-body p-4">
                                <h5 class="card-title fw-bold mb-4">Đổi mật khẩu</h5>
                                
                                <form action="${pageContext.request.contextPath}/profile" method="POST" class="needs-validation" novalidate>
                                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                                    <input type="hidden" name="action" value="change_password">
                                    
                                    <div class="mb-3">
                                        <label class="form-label fw-medium text-dark">Mật khẩu hiện tại <span class="text-danger">*</span></label>
                                        <input type="password" class="form-control" name="currentPassword" required>
                                        <div class="invalid-feedback">Vui lòng nhập mật khẩu hiện tại.</div>
                                    </div>
                                    
                                    <div class="mb-3">
                                        <label class="form-label fw-medium text-dark">Mật khẩu mới <span class="text-danger">*</span></label>
                                        <input type="password" class="form-control" name="newPassword" minlength="7" required>
                                        <div class="invalid-feedback">Mật khẩu mới phải có ít nhất 7 ký tự.</div>
                                    </div>
                                    
                                    <div class="mb-4">
                                        <label class="form-label fw-medium text-dark">Xác nhận mật khẩu mới <span class="text-danger">*</span></label>
                                        <input type="password" class="form-control" name="confirmPassword" minlength="7" required>
                                        <div class="invalid-feedback">Vui lòng xác nhận mật khẩu mới.</div>
                                    </div>
                                    
                                    <div class="d-grid">
                                        <button type="submit" class="btn rounded-pill text-white" style="background-color: #0a0a0a;">Cập nhật mật khẩu</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>

            </main>
            <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
        </div>
    </div>

<script>
document.addEventListener("DOMContentLoaded", function() {
    // Avatar preview logic
    const avatarInput = document.getElementById('avatarInput');
    const avatarPreview = document.getElementById('avatarPreview');
    const avatarInitials = document.getElementById('avatarInitials');

    avatarInput.addEventListener('change', function() {
        if (this.files && this.files.length > 0) {
            const file = this.files[0];
            if (file.type.startsWith('image/')) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    if (avatarPreview) {
                        avatarPreview.src = e.target.result;
                    } else if (avatarInitials) {
                        // Create image element if it doesn't exist
                        const img = document.createElement('img');
                        img.src = e.target.result;
                        img.className = 'avatar-preview';
                        img.id = 'avatarPreview';
                        avatarInitials.parentNode.replaceChild(img, avatarInitials);
                    }
                }
                reader.readAsDataURL(file);
            }
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
</body>
</html>
