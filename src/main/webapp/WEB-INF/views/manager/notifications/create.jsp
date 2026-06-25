<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Tạo thông báo - BQL"/>
<c:set var="pageRole" value="MANAGER"/>
<c:set var="activeMenu" value="notifications"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
  <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
  <div class="sidebar-overlay"></div>
  <div class="main-wrapper">
    <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
    <main class="page-content">
      <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

      <div class="page-header hero-sky-gradient" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
        <h1>Tạo thông báo</h1>
        <p>Gửi thông báo đến cư dân trong phạm vi cơ sở được phân công</p>
      </div>

      <div class="data-surface" style="max-width:680px">
        <form method="post" action="${ctx}/manager/notifications/create" class="p-4">
          <input type="hidden" name="csrfToken" value="${csrfToken}"/>



          <%-- Warning box: không được gửi ALL --%>
          <div style="background:#fff1f1;border:1px solid #fca5a5;
                      border-radius:var(--hms-radius);padding:0.75rem 1rem;
                      font-size:0.8125rem;color:#7f1d1d;margin-bottom:1.25rem">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                 stroke="#dc2626" stroke-width="2"
                 style="margin-right:6px;vertical-align:-2px">
              <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
              <line x1="12" y1="9" x2="12" y2="13"/>
              <line x1="12" y1="17" x2="12.01" y2="17"/>
            </svg>
            <strong>Lưu ý:</strong> Manager <strong>không được gửi thông báo đến toàn bộ hệ thống</strong>.
            Chỉ có thể gửi trong phạm vi cơ sở hoặc phòng được phân công.
          </div>

          <%-- Tiêu đề --%>
          <div class="mb-3">
            <label for="title" class="form-label">Tiêu đề <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="title" name="title"
                   required maxlength="250"
                   placeholder="VD: Thông báo lịch vệ sinh hành lang..."
                   value="<c:out value='${dto.title}'/>">
          </div>

          <%-- Nội dung --%>
          <div class="mb-3">
            <label for="content" class="form-label">Nội dung <span class="text-danger">*</span></label>
            <textarea class="form-control" id="content" name="content"
                      rows="7" required maxlength="5000"
                      placeholder="Nội dung thông báo chi tiết..."><c:out value="${dto.content}"/></textarea>
            <div class="form-text">Tối đa 5000 ký tự</div>
          </div>

          <%-- Loại đối tượng — KHÔNG có ALL --%>
          <div class="mb-3">
            <label for="recipientType" class="form-label">Đối tượng nhận <span class="text-danger">*</span></label>
            <select class="form-select" id="recipientType" name="recipientType"
                    required onchange="toggleRecipientInput(this.value)">
              <option value="">-- Chọn loại --</option>
              <option value="FACILITY" ${dto.recipientType == 'FACILITY' ? 'selected' : ''}>Theo cơ sở</option>
              <option value="ROOM"     ${dto.recipientType == 'ROOM'     ? 'selected' : ''}>Theo phòng</option>
            </select>
          </div>

          <%-- recipientId: ẩn/hiện theo loại --%>
          <div class="mb-3" id="facilityGroup" style="display:none">
            <label for="facilityRecipient" class="form-label">Cơ sở <span class="text-danger">*</span></label>
            <select class="form-select" id="facilityRecipient" name="recipientId_facility">
              <option value="">-- Chọn cơ sở --</option>
              <c:forEach var="facility" items="${assignedFacilities}">
                <option value="${facility.id}" ${dto.recipientId == facility.id or dto.facilityId == facility.id ? 'selected' : ''}>
                  <c:out value="${facility.code}"/> — <c:out value="${facility.name}"/>
                </option>
              </c:forEach>
            </select>
          </div>

          <div class="mb-3" id="roomGroup" style="display:none">
            <label for="facilityForRoom" class="form-label">Chọn cơ sở của phòng <span class="text-danger">*</span></label>
            <select class="form-select mb-2" id="facilityForRoom" name="facilityId_for_room" onchange="filterRoomsByFacility(this.value)">
              <option value="">-- Chọn cơ sở --</option>
              <c:forEach var="facility" items="${assignedFacilities}">
                <option value="${facility.id}" ${dto.facilityId == facility.id ? 'selected' : ''}>
                  <c:out value="${facility.code}"/> — <c:out value="${facility.name}"/>
                </option>
              </c:forEach>
            </select>

            <label for="roomRecipient" class="form-label">Chọn phòng <span class="text-danger">*</span></label>
            <select class="form-select" id="roomRecipient" name="recipientId_room">
              <option value="">-- Chọn phòng --</option>
              <c:forEach var="room" items="${assignedRooms}">
                <option value="${room.id}" data-facility="${room.facilityId}" ${dto.recipientId == room.id ? 'selected' : ''}>
                  <c:out value="${room.code}"/>
                </option>
              </c:forEach>
            </select>
          </div>

          <%-- Hidden field để submit recipientId đúng giá trị --%>
          <input type="hidden" id="recipientIdField" name="recipientId" value="${dto.recipientId}"/>

          <div class="d-flex gap-2 mt-3">
            <button type="submit" class="quick-action-btn primary">
              <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                   stroke="currentColor" stroke-width="2.5" style="margin-right:4px">
                <line x1="22" y1="2" x2="11" y2="13"/>
                <polygon points="22 2 15 22 11 13 2 9 22 2"/>
              </svg>
              Gửi thông báo
            </button>
            <a href="${ctx}/manager/notifications" class="btn-mintlify-secondary text-decoration-none">Hủy</a>
          </div>
        </form>
      </div>

    </main>
  </div>
</div>

<script>
  (function () {
    var originalRoomOptions = [];
    var roomSelect = document.getElementById('roomRecipient');
    if (roomSelect) {
      for (var i = 0; i < roomSelect.options.length; i++) {
        var opt = roomSelect.options[i];
        if (opt.value !== "") {
          originalRoomOptions.push({
            value: opt.value,
            text: opt.text,
            facilityId: opt.getAttribute('data-facility'),
            selected: opt.selected
          });
        }
      }
    }

    function filterRoomsByFacility(facilityId) {
      var roomSelect = document.getElementById('roomRecipient');
      if (!roomSelect) return;
      
      // Clear all except first option
      roomSelect.options.length = 1;
      document.getElementById('recipientIdField').value = "";

      for (var i = 0; i < originalRoomOptions.length; i++) {
        var optData = originalRoomOptions[i];
        if (optData.facilityId == facilityId) {
          var newOpt = new Option(optData.text, optData.value);
          newOpt.setAttribute('data-facility', optData.facilityId);
          roomSelect.add(newOpt);
        }
      }
    }

    function toggleRecipientInput(type) {
      var facilityGroup = document.getElementById('facilityGroup');
      var roomGroup = document.getElementById('roomGroup');
      var facilitySelect = document.getElementById('facilityRecipient');
      var facilityForRoom = document.getElementById('facilityForRoom');
      var roomSelect = document.getElementById('roomRecipient');
      var hiddenField = document.getElementById('recipientIdField');

      facilityGroup.style.display = 'none';
      roomGroup.style.display = 'none';
      facilitySelect.removeAttribute('required');
      facilityForRoom.removeAttribute('required');
      roomSelect.removeAttribute('required');

      if (type === 'FACILITY') {
        facilityGroup.style.display = '';
        facilitySelect.setAttribute('required', 'required');
        hiddenField.value = facilitySelect.value;
      } else if (type === 'ROOM') {
        roomGroup.style.display = '';
        facilityForRoom.setAttribute('required', 'required');
        roomSelect.setAttribute('required', 'required');
        hiddenField.value = roomSelect.value;
      }
    }

    // Sync hidden field on input changes
    document.getElementById('facilityRecipient').addEventListener('change', function () {
      document.getElementById('recipientIdField').value = this.value;
    });
    document.getElementById('roomRecipient').addEventListener('change', function () {
      document.getElementById('recipientIdField').value = this.value;
    });

    // Initialize on page load
    var sel = document.getElementById('recipientType');
    toggleRecipientInput(sel.value);
    
    // Trigger filtering if facility for room was already selected on page load
    var facForRoom = document.getElementById('facilityForRoom');
    if (facForRoom && facForRoom.value !== "") {
      filterRoomsByFacility(facForRoom.value);
      // Restore selected room value
      var prevRoomId = "${dto.recipientId}";
      if (prevRoomId) {
        roomSelect.value = prevRoomId;
        document.getElementById('recipientIdField').value = prevRoomId;
      }
    }

    // Expose functions
    window.toggleRecipientInput = toggleRecipientInput;
    window.filterRoomsByFacility = filterRoomsByFacility;
  })();
</script>

<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
