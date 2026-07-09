<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Đăng bài viết mới"/>
<c:set var="pageRole" value="TENANT"/>
<c:set var="activeMenu" value="create-post"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="d-flex align-items-center mb-4">
                <a href="${ctx}/tenant/news-feed" class="btn btn-icon btn-light me-3 shadow-sm">
                    <i class="bi bi-arrow-left"></i>
                </a>
                <h2 class="mb-0">Đăng bài viết mới</h2>
            </div>

            <div class="card shadow-sm col-md-8 col-lg-6 mx-auto border-0">
                <div class="card-body p-4">
                    <form action="${ctx}/tenant/post/create" method="POST" enctype="multipart/form-data">
                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                        <div class="mb-3">
                            <label for="title" class="form-label fw-bold">Tiêu đề <span class="text-danger">*</span></label>
                            <input type="text" class="form-control form-control-lg bg-light" id="title" name="title" required placeholder="Nhập tiêu đề bài viết..." maxlength="250">
                        </div>
                        <div class="mb-3">
                            <label for="content" class="form-label fw-bold">Nội dung <span class="text-danger">*</span></label>
                            <textarea class="form-control bg-light" id="content" name="content" rows="6" required placeholder="Bạn muốn chia sẻ điều gì?"></textarea>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold">Hình ảnh đính kèm</label>
                            <div class="d-flex flex-wrap gap-2 mb-2">
                                <!-- Chụp ảnh từ Camera (Mobile) -->
                                <div class="position-relative">
                                    <input type="file" id="cameraInput" name="images" accept="image/*" capture="environment" class="d-none">
                                    <label for="cameraInput" class="btn btn-outline-primary fw-medium">
                                        <i class="bi bi-phone me-1"></i> Camera điện thoại
                                    </label>
                                </div>
                                <!-- Chụp ảnh từ Webcam (PC) -->
                                <button type="button" class="btn btn-outline-info fw-medium" id="btnOpenWebcam">
                                    <i class="bi bi-webcam me-1"></i> Webcam máy tính
                                </button>
                                <!-- Chọn từ thư viện -->
                                <div class="position-relative">
                                    <input type="file" id="fileInput" name="images" accept="image/png, image/jpeg, image/webp" class="d-none">
                                    <label for="fileInput" class="btn btn-outline-secondary fw-medium">
                                        <i class="bi bi-images me-1"></i> Tải ảnh lên
                                    </label>
                                </div>
                            </div>
                            <div class="text-muted small">Hỗ trợ JPG, PNG, WEBP. Tối đa 10MB/ảnh.</div>
                            <div id="imagePreviewContainer" class="d-flex flex-wrap gap-2 mt-3"></div>
                        </div>

                        <div class="d-grid mt-5">
                            <button type="submit" class="btn btn-primary btn-lg fw-bold shadow-sm">
                                <i class="bi bi-send-fill me-2"></i> Gửi bài viết
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </main>
    </div>
</div>

<!-- Webcam Modal -->
<div class="modal fade" id="webcamModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header border-0">
        <h5 class="modal-title fw-bold">Chụp ảnh từ Webcam</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body text-center p-0">
        <video id="webcamVideo" autoplay playsinline style="width: 100%; max-height: 400px; background: #000;"></video>
        <canvas id="webcamCanvas" class="d-none"></canvas>
      </div>
      <div class="modal-footer border-0 justify-content-center">
        <button type="button" class="btn btn-primary fw-medium px-4" id="btnCaptureWebcam">
            <i class="bi bi-camera-fill me-1"></i> Chụp ngay
        </button>
      </div>
    </div>
  </div>
</div>

<script>
    function handleFileSelect(event) {
        var files = event.target.files;
        var container = document.getElementById('imagePreviewContainer');
        container.innerHTML = ''; // Clear old preview

        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            if (!file.type.match('image.*')) continue;

            var reader = new FileReader();
            reader.onload = (function(theFile) {
                return function(e) {
                    var imgContainer = document.createElement('div');
                    imgContainer.className = 'position-relative';
                    var img = document.createElement('img');
                    img.className = 'img-thumbnail rounded shadow-sm';
                    img.src = e.target.result;
                    img.style.cssText = 'height:100px;width:100px;object-fit:cover;';
                    imgContainer.appendChild(img);
                    container.appendChild(imgContainer);
                };
            })(file);
            reader.readAsDataURL(file);
        }
    }

    document.getElementById('cameraInput').addEventListener('change', handleFileSelect);
    document.getElementById('fileInput').addEventListener('change', handleFileSelect);

    // Webcam Logic
    document.addEventListener('DOMContentLoaded', function() {
        var webcamModalElement = document.getElementById('webcamModal');
        var webcamModal = new bootstrap.Modal(webcamModalElement);
        var video = document.getElementById('webcamVideo');
        var canvas = document.getElementById('webcamCanvas');
        var stream = null;

        document.getElementById('btnOpenWebcam').addEventListener('click', function() {
            if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
                navigator.mediaDevices.getUserMedia({ video: true })
                    .then(function(s) {
                        stream = s;
                        video.srcObject = stream;
                        webcamModal.show();
                    })
                    .catch(function(error) {
                        console.error('Lỗi truy cập Webcam:', error);
                        alert('Không thể truy cập Webcam. Vui lòng kiểm tra quyền truy cập trong trình duyệt.');
                    });
            } else {
                alert('Trình duyệt của bạn không hỗ trợ tính năng Webcam.');
            }
        });

        webcamModalElement.addEventListener('hidden.bs.modal', function () {
            if (stream) {
                stream.getTracks().forEach(track => track.stop());
                stream = null;
            }
        });

        document.getElementById('btnCaptureWebcam').addEventListener('click', function() {
            if (stream) {
                canvas.width = video.videoWidth;
                canvas.height = video.videoHeight;
                canvas.getContext('2d').drawImage(video, 0, 0, canvas.width, canvas.height);
                
                canvas.toBlob(function(blob) {
                    var file = new File([blob], "webcam_capture_" + Date.now() + ".jpg", { type: "image/jpeg" });
                    var dataTransfer = new DataTransfer();
                    dataTransfer.items.add(file);
                    
                    var fileInput = document.getElementById('fileInput');
                    fileInput.files = dataTransfer.files;
                    
                    // Trigger change event to update preview
                    var event = new Event('change');
                    fileInput.dispatchEvent(event);
                    
                    webcamModal.hide();
                }, 'image/jpeg');
            }
        });
    });
</script>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
