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

            <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="position:relative;z-index:1">
                    <h1>Đăng bài viết mới</h1>
                    <p>Chia sẻ thông tin, kinh nghiệm hoặc đóng góp ý kiến với cộng đồng</p>
                </div>
                <div style="display:flex;gap:8px;flex-wrap:wrap;position:relative;z-index:1">
                    <a href="${ctx}/tenant/news-feed" class="btn-accent">
                        Trở về bảng tin
                    </a>
                </div>
            </div>

            <style>
                .post-card {
                    background: #fff;
                    border-radius: 16px;
                    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
                    padding: 32px;
                    margin: 0 auto;
                    max-width: 850px;
                    border: 1px solid #e5e5e5;
                }
                .form-control-custom {
                    border-radius: 12px;
                    border: 1px solid #e5e5e5;
                    padding: 14px 16px;
                    background-color: #fcfcfc;
                    transition: all 0.2s ease;
                    font-size: 1rem;
                }
                .form-control-custom:focus {
                    border-color: #00d4a4;
                    box-shadow: 0 0 0 3px rgba(0, 212, 164, 0.1);
                    background-color: #fff;
                    outline: none;
                }
                .btn-submit-custom {
                    border-radius: 12px;
                    background-color: #0a0a0a;
                    color: #fff;
                    padding: 14px 24px;
                    font-weight: 600;
                    transition: all 0.2s ease;
                    border: none;
                    display: inline-flex;
                    align-items: center;
                    justify-content: center;
                }
                .btn-submit-custom:hover {
                    background-color: #1a1a1a;
                    color: #fff;
                    transform: translateY(-1px);
                }
                .upload-btn-outline {
                    border-radius: 10px;
                    border: 1px solid #e5e5e5;
                    background: #fff;
                    color: #0a0a0a;
                    transition: all 0.2s ease;
                    padding: 8px 16px;
                }
                .upload-btn-outline:hover {
                    background: #f7f7f7;
                    border-color: #d5d5d5;
                    color: #0a0a0a;
                }
            </style>

            <div class="post-card">
                <div class="p-2">
                    <form action="${ctx}/tenant/post/create" method="POST" enctype="multipart/form-data">
                        <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                        <div class="mb-4">
                            <label for="title" class="form-label fw-bold" style="color: #0a0a0a;">Tiêu đề <span class="text-danger">*</span></label>
                            <input type="text" class="form-control form-control-custom w-100" id="title" name="title" required placeholder="Nhập tiêu đề bài viết..." maxlength="250">
                        </div>
                        <div class="mb-4">
                            <label for="content" class="form-label fw-bold" style="color: #0a0a0a;">Nội dung <span class="text-danger">*</span></label>
                            <textarea class="form-control form-control-custom w-100" id="content" name="content" rows="6" required placeholder="Bạn muốn chia sẻ điều gì?"></textarea>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold" style="color: #0a0a0a;">Hình ảnh đính kèm</label>
                            <div class="d-flex flex-wrap gap-2 mb-2">
                                <!-- Chụp ảnh từ Camera (Mobile) -->
                                <div class="position-relative">
                                    <input type="file" id="cameraInput" accept="image/*" capture="environment" class="d-none">
                                    <label for="cameraInput" class="btn upload-btn-outline fw-medium mb-0">
                                        <i class="bi bi-phone me-1"></i> Camera điện thoại
                                    </label>
                                </div>
                                <!-- Chụp ảnh từ Webcam (PC) -->
                                <button type="button" class="btn upload-btn-outline fw-medium" id="btnOpenWebcam">
                                    <i class="bi bi-webcam me-1"></i> Webcam máy tính
                                </button>
                                <!-- Chọn từ thư viện -->
                                <div class="position-relative">
                                    <input type="file" id="fileInput" name="images" accept="image/png, image/jpeg, image/webp" class="d-none">
                                    <label for="fileInput" class="btn upload-btn-outline fw-medium mb-0">
                                        <i class="bi bi-images me-1"></i> Tải ảnh lên
                                    </label>
                                </div>
                            </div>
                            <div class="text-muted small">Hỗ trợ JPG, PNG, WEBP. Tối đa 10MB/ảnh.</div>
                            <div id="imagePreviewContainer" class="d-flex flex-wrap gap-2 mt-3"></div>
                        </div>

                        <div class="d-grid mt-5">
                            <button type="submit" class="btn-submit-custom shadow-sm w-100">
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

    document.getElementById('cameraInput').addEventListener('change', function(event) {
        var fileInput = document.getElementById('fileInput');
        fileInput.files = event.target.files;
        handleFileSelect(event);
    });
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
