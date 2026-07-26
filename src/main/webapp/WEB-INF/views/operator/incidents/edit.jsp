<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Báo cáo sự cố - Kỹ thuật"/>
<c:set var="pageRole" value="OPERATOR"/>
<c:set var="activeMenu" value="incident-report"/>

<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
  <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
  <div class="sidebar-overlay"></div>
  <div class="main-wrapper">
    <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
    
    <main class="page-content">
      <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

      <div class="page-header hero-sky-gradient" style="border-radius: var(--hms-radius-lg, 12px); margin-bottom: 1.75rem;">
        <h1>Báo cáo sự cố hiện trường</h1>
        <p>Ghi nhận nhanh sự cố phát sinh để Quản lý nắm thông tin</p>
      </div>

      <!-- System Alerts handled by layout/alerts.jsp -->
      <div style="max-width:800px;margin:0 auto">
      <div class="mintlify-card-base">
          <form id="incidentForm" action="${ctx}/operator/incidents/edit" method="post" enctype="multipart/form-data" novalidate>
              <input type="hidden" name="csrfToken" value="${csrfToken}">
              <input type="hidden" name="id" value="${incident.id}">
              
              <div class="row mb-4">
                  <div class="col-md-6 mb-3 mb-md-0">
                      <label for="facility" class="hms-form-label hms-form-label-required">Cơ sở / Tòa nhà</label>
                      <select class="form-select mintlify-text-input" id="facility" name="facility" required onchange="updateRoomDropdown()">
                          <option value="" disabled ${empty parsedFacility ? 'selected' : ''}>-- Chọn cơ sở --</option>
                          <c:forEach var="f" items="${facilities}">
                              <option value="${f.name} (${f.code})" data-id="${f.id}" ${parsedFacility == (f.name += ' (' += f.code += ')') ? 'selected' : ''}><c:out value="${f.name}"/> (<c:out value="${f.code}"/>)</option>
                          </c:forEach>
                      </select>
                      <div class="invalid-feedback hms-invalid-feedback">Vui lòng chọn cơ sở/tòa nhà.</div>
                  </div>
                  <div class="col-md-6">
                      <label for="category" class="hms-form-label hms-form-label-required">Phân loại sự cố</label>
                      <select class="form-select mintlify-text-input" id="category" name="category" required>
                          <option value="" disabled ${empty incident.category ? 'selected' : ''}>-- Chọn loại sự cố --</option>
                          <option value="Điện" ${incident.category == 'Điện' ? 'selected' : ''}>Hệ thống Điện</option>
                          <option value="Nước" ${incident.category == 'Nước' ? 'selected' : ''}>Hệ thống Nước</option>
                          <option value="Nội thất" ${incident.category == 'Nội thất' ? 'selected' : ''}>Nội thất / Cơ sở vật chất</option>
                          <option value="An ninh" ${incident.category == 'An ninh' ? 'selected' : ''}>An ninh / Khóa cửa</option>
                          <option value="Vệ sinh" ${incident.category == 'Vệ sinh' ? 'selected' : ''}>Vệ sinh / Rác thải</option>
                          <option value="Khác" ${incident.category == 'Khác' ? 'selected' : ''}>Khác</option>
                      </select>
                      <div class="invalid-feedback hms-invalid-feedback">Vui lòng chọn loại sự cố.</div>
                  </div>
              </div>

              <div class="row mb-4">
                  <div class="col-md-6 mb-3 mb-md-0">
                      <label class="hms-form-label hms-form-label-required">Vị trí sự cố</label>
                      <div class="d-flex gap-4 mt-2">
                          <div class="form-check">
                              <input class="form-check-input" type="radio" name="locationType" id="locChung" value="Khu vực chung" ${parsedLocationType != 'Phòng' ? 'checked' : ''}>
                              <label class="form-check-label" for="locChung">Khu vực chung</label>
                          </div>
                          <div class="form-check">
                              <input class="form-check-input" type="radio" name="locationType" id="locPhong" value="Phòng" ${parsedLocationType == 'Phòng' ? 'checked' : ''}>
                              <label class="form-check-label" for="locPhong">Phòng cụ thể</label>
                          </div>
                      </div>
                  </div>
                  <div class="col-md-6">
                      <label class="hms-form-label hms-form-label-required">Chi tiết vị trí</label>
                      
                      <!-- Khu vực chung -->
                      <input type="text" class="mintlify-text-input" id="locationDetailCommon" placeholder="VD: Hành lang tầng 2, Cổng chính..." required maxlength="50" value="${parsedLocationType != 'Phòng' ? parsedLocationDetail : ''}">
                      <div class="invalid-feedback hms-invalid-feedback" id="feedbackCommon">Vui lòng nhập vị trí cụ thể (tối đa 50 ký tự).</div>
                      
                      <!-- Phòng cụ thể -->
                      <select class="form-select mintlify-text-input" id="locationDetailRoom" style="display: none;" data-selected="${parsedLocationType == 'Phòng' ? parsedLocationDetail : ''}">
                          <option value="" selected disabled>-- Chọn mã phòng --</option>
                      </select>
                      <div class="invalid-feedback hms-invalid-feedback" id="feedbackRoom" style="display: none;">Vui lòng chọn mã phòng.</div>
                      
                      <input type="hidden" id="locationDetail" name="locationDetail" value="">
                  </div>
              </div>

              <div class="mb-4">
                  <label for="incidentName" class="hms-form-label hms-form-label-required">Tiêu đề ngắn gọn</label>
                  <input type="text" class="mintlify-text-input" id="incidentName" name="incidentName" placeholder="VD: Cháy bóng đèn, Rò rỉ ống nước..." required maxlength="50" value="${parsedIncidentName}">
                  <div class="invalid-feedback hms-invalid-feedback" id="feedbackIncidentName">Vui lòng nhập tiêu đề sự cố (tối đa 50 ký tự).</div>
              </div>

              <div class="mb-4">
                  <label for="priority" class="hms-form-label hms-form-label-required">Mức độ ưu tiên</label>
                  <select class="form-select mintlify-text-input" id="priority" name="priority" required>
                      <option value="Bình thường" ${parsedPriority == 'Bình thường' ? 'selected' : ''}>Bình thường (Xử lý trong 24h-48h)</option>
                      <option value="Khẩn cấp" ${parsedPriority == 'Khẩn cấp' ? 'selected' : ''}>Khẩn cấp (Cần xử lý ngay)</option>
                  </select>
              </div>

              <div class="mb-4">
                  <label for="content" class="hms-form-label hms-form-label-required">Mô tả chi tiết</label>
                  <textarea class="mintlify-text-input" id="content" name="content" rows="4" placeholder="Mô tả cụ thể tình trạng sự cố đang diễn ra..." required maxlength="1000">${parsedContent}</textarea>
                  <div class="invalid-feedback hms-invalid-feedback" id="feedbackContent">Vui lòng nhập mô tả chi tiết (tối đa 1000 ký tự).</div>
              </div>

              <div class="mb-4">
                  <label for="images" class="hms-form-label">Hình ảnh minh chứng (Tối đa 3 ảnh)</label>
                  <div class="d-flex align-items-center gap-3">
                      <button type="button" class="mintlify-btn-secondary" onclick="document.getElementById('images').click()" style="padding: 8px 16px;">
                          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" class="me-2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>
                          Tải ảnh lên
                      </button>
                      <span id="uploadHint" class="hms-text-sm-steel">Chưa có ảnh nào được chọn</span>
                  </div>
                  <input class="d-none" type="file" id="images" name="images" accept="image/jpeg, image/png, image/jpg" multiple>
                  <div id="imagePreview" style="display: flex; flex-wrap: wrap; gap: 12px; margin-top: 16px;">
                      <c:if test="${not empty incident.attachmentUrls1}">
                          <c:forEach var="url" items="${incident.attachmentUrls1.split(',')}">
                              <div class="hms-image-preview-wrapper existing-image">
                                  <img src="${ctx}${url}">
                                  <input type="hidden" name="existingImages" value="${url}">
                              </div>
                          </c:forEach>
                      </c:if>
                  </div>
                  <div class="form-text mt-2">Chọn lại ảnh nếu bạn muốn thay đổi. Nếu để trống, ảnh cũ sẽ được giữ nguyên.</div>
              </div>

              <div class="mt-4 pt-3 border-top" style="border-color: var(--color-hairline-soft) !important;">
                  <div class="d-grid gap-2 d-md-flex justify-content-md-end mt-4">
                      <a href="${ctx}/operator/incidents/my-reports" class="mintlify-btn-secondary text-center text-decoration-none">Hủy bỏ</a>
                      <button type="submit" class="mintlify-btn-primary text-center border-0" id="submitBtn">
                          <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" class="me-2"><path d="M22 2L11 13M22 2l-7 20-4-9-9-4 20-7z"/></svg>
                          Gửi báo cáo
                      </button>
                  </div>
              </div>
          </form>
      </div><%-- /mintlify-card-base --%>
      </div><%-- /max-width wrapper --%>

    </main>
  </div>
</div>

<script>
    // Inject facilityRoomsMap from server
    const facilityRooms = {};
    <c:forEach var="entry" items="${facilityRoomsMap}">
        facilityRooms[${entry.key}] = [
            <c:forEach var="r" items="${entry.value}" varStatus="status">
                { code: '${r.code}' }<c:if test="${!status.last}">,</c:if>
            </c:forEach>
        ];
    </c:forEach>

    function updateRoomDropdown() {
        const facilitySelect = document.getElementById('facility');
        const roomSelect = document.getElementById('locationDetailRoom');
        const selectedOption = facilitySelect.options[facilitySelect.selectedIndex];
        
        // Reset room options
        roomSelect.innerHTML = '<option value="" selected disabled>-- Chọn mã phòng --</option>';
        
        if (selectedOption && selectedOption.dataset.id) {
            const facilityId = selectedOption.dataset.id;
            const rooms = facilityRooms[facilityId];
            if (rooms && rooms.length > 0) {
                rooms.forEach(r => {
                    const option = document.createElement('option');
                    option.value = r.code;
                    option.textContent = 'Phòng ' + r.code;
                    
                    // Pre-select if matches data-selected
                    const preSelectedRoom = roomSelect.getAttribute('data-selected');
                    if (preSelectedRoom === r.code) {
                        option.selected = true;
                    }
                    
                    roomSelect.appendChild(option);
                });
            } else {
                const option = document.createElement('option');
                option.value = "";
                option.textContent = "Chưa có phòng nào";
                option.disabled = true;
                roomSelect.appendChild(option);
            }
        }
    }
</script>

<script>
    // Xử lý logic chọn Vị trí (Khu vực chung / Phòng)
    const locChung = document.getElementById('locChung');
    const locPhong = document.getElementById('locPhong');
    const locationDetailCommon = document.getElementById('locationDetailCommon');
    const locationDetailRoom = document.getElementById('locationDetailRoom');
    const feedbackCommon = document.getElementById('feedbackCommon');
    const feedbackRoom = document.getElementById('feedbackRoom');
    const locationDetailHidden = document.getElementById('locationDetail');

    function toggleLocationInput() {
        if(locChung.checked) {
            locationDetailCommon.style.display = 'block';
            locationDetailCommon.required = true;
            feedbackCommon.style.display = 'none';
            
            locationDetailRoom.style.display = 'none';
            locationDetailRoom.required = false;
            feedbackRoom.style.display = 'none';
        } else {
            locationDetailCommon.style.display = 'none';
            locationDetailCommon.required = false;
            feedbackCommon.style.display = 'none';
            
            locationDetailRoom.style.display = 'block';
            locationDetailRoom.required = true;
            feedbackRoom.style.display = 'none';
        }
    }

    locChung.addEventListener('change', toggleLocationInput);
    locPhong.addEventListener('change', toggleLocationInput);

    // Initial setup
    toggleLocationInput();
    document.addEventListener("DOMContentLoaded", function() {
        if (document.getElementById('facility').value) {
            updateRoomDropdown();
        }
    });

    // Xử lý upload ảnh, preview, xóa ảnh và nén (mô phỏng)
    const imagesInput = document.getElementById('images');
    const previewContainer = document.getElementById('imagePreview');
    const uploadHint = document.getElementById('uploadHint');
    let selectedFiles = [];

    function updateFileInput() {
        const dt = new DataTransfer();
        selectedFiles.forEach(file => dt.items.add(file));
        imagesInput.files = dt.files;
        
        if (selectedFiles.length > 0) {
            uploadHint.textContent = `Đã chọn ${selectedFiles.length}/3 ảnh`;
        } else {
            uploadHint.textContent = 'Chưa có ảnh nào được chọn';
        }
    }

    function removeImage(index) {
        selectedFiles.splice(index, 1);
        renderPreview();
        updateFileInput();
    }

    function renderPreview() {
        previewContainer.innerHTML = '';
        selectedFiles.forEach((file, index) => {
            const reader = new FileReader();
            reader.onload = function(e) {
                const wrapper = document.createElement('div');
                wrapper.className = 'hms-image-preview-wrapper';
                
                const img = document.createElement('img');
                img.src = e.target.result;
                
                const removeBtn = document.createElement('button');
                removeBtn.type = 'button';
                removeBtn.innerHTML = '&times;';
                removeBtn.className = 'hms-image-preview-remove';
                removeBtn.onclick = () => removeImage(index);
                
                wrapper.appendChild(img);
                wrapper.appendChild(removeBtn);
                previewContainer.appendChild(wrapper);
            }
            reader.readAsDataURL(file);
        });
    }

    imagesInput.addEventListener('change', function(e) {
        const newFiles = Array.from(e.target.files);
        if(selectedFiles.length + newFiles.length > 3) {
            alert('Chỉ được phép tải lên tối đa 3 ảnh.');
            // Khôi phục lại input files
            updateFileInput();
            return;
        }

        // Mô phỏng nén ảnh (Compress) như Spec yêu cầu
        // Thực tế cần dùng canvas để nén, ở đây lưu thẳng vào mảng
        selectedFiles = selectedFiles.concat(newFiles);
        renderPreview();
        updateFileInput();
    });

    // Validate Form cơ bản
    const form = document.getElementById('incidentForm');
    form.addEventListener('submit', function(e) {
        let isValid = true;
        
        // Kiểm tra các trường có thuộc tính required và maxlength
        const elementsToValidate = form.querySelectorAll('input, select, textarea');
        elementsToValidate.forEach(el => {
            let invalid = false;
            if (el.hasAttribute('required') && (!el.value || el.value.trim() === '')) {
                invalid = true;
            }
            if (el.hasAttribute('maxlength') && el.value && el.value.length > parseInt(el.getAttribute('maxlength'))) {
                invalid = true;
            }
            
            if (invalid) {
                isValid = false;
                el.classList.add('is-invalid');
                if (el.id === 'locationDetailCommon') {
                    feedbackCommon.style.display = 'block';
                } else if (el.id === 'locationDetailRoom') {
                    feedbackRoom.style.display = 'block';
                }
            } else {
                el.classList.remove('is-invalid');
                if (el.id === 'locationDetailCommon') {
                    feedbackCommon.style.display = 'none';
                } else if (el.id === 'locationDetailRoom') {
                    feedbackRoom.style.display = 'none';
                }
            }
        });
        
        // Xóa style viền đỏ khi người dùng bắt đầu nhập/chọn
        elementsToValidate.forEach(el => {
            el.addEventListener('input', function() {
                this.classList.remove('is-invalid');
                if (this.id === 'locationDetailCommon') {
                    feedbackCommon.style.display = 'none';
                } else if (this.id === 'locationDetailRoom') {
                    feedbackRoom.style.display = 'none';
                }
            }, {once: true});
        });

        if(!isValid) {
            e.preventDefault();
            e.stopPropagation();
        } else {
            // Cập nhật giá trị ẩn trước khi submit
            if (locChung.checked) {
                locationDetailHidden.value = locationDetailCommon.value;
            } else {
                locationDetailHidden.value = locationDetailRoom.value;
            }
        }
    });
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
