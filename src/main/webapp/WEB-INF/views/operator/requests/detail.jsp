<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
      <%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
        <c:set var="ctx" value="${pageContext.request.contextPath}" />
        <c:set var="pageTitle" value="Chi tiết yêu cầu sửa chữa" />
        <c:set var="pageRole" value="OPERATOR" />
        <c:set var="activeMenu" value="tickets" />
        <jsp:include page="/WEB-INF/views/layout/head.jsp" />

        <style>
          .page-content,
          .page-content th,
          .page-content td,
          .page-content input,
          .page-content select,
          .page-content button,
          .page-content label {
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
          }

          .req-detail-container {
            padding: 0.5rem 0 2rem 0;
          }

          .detail-card {
            background: var(--hms-canvas);
            border: 1px solid var(--hms-border-soft);
            border-radius: var(--hms-radius-lg);
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
            overflow: hidden;
            height: 100%;
            display: flex;
            flex-direction: column;
          }

          .detail-card-header {
            padding: 1.25rem 1.5rem;
            background: var(--hms-surface-soft);
            border-bottom: 1px solid var(--hms-border-soft);
            display: flex;
            align-items: center;
            justify-content: space-between;
          }

          .detail-card-header h3 {
            font-size: 0.9375rem;
            font-weight: 700;
            color: var(--hms-ink);
            margin: 0;
            display: flex;
            align-items: center;
            gap: 0.5rem;
          }

          .detail-card-body {
            padding: 0;
            flex-grow: 1;
          }

          .metadata-row {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            padding: 1rem 1.5rem;
            border-bottom: 1px solid var(--hms-border-soft);
            transition: background-color 0.15s ease;
          }

          .metadata-row:hover {
            background-color: var(--hms-surface-soft);
          }

          .metadata-row:last-child {
            border-bottom: none;
          }

          .meta-label {
            display: flex;
            align-items: center;
            gap: 0.6rem;
            color: var(--hms-stone);
            font-size: 0.875rem;
            font-weight: 500;
            flex-shrink: 0;
          }

          .meta-label svg {
            color: var(--hms-muted);
          }

          .meta-value {
            color: var(--hms-ink);
            font-size: 0.875rem;
            font-weight: 600;
            text-align: right;
            max-width: 60%;
            word-break: break-word;
          }

          .req-btn-outline {
            background: var(--hms-canvas);
            color: var(--hms-text-secondary);
            font-family: 'Inter', sans-serif;
            font-size: 0.875rem;
            font-weight: 600;
            padding: 0.5rem 1.25rem;
            height: 40px;
            border-radius: var(--hms-radius);
            border: 1px solid var(--hms-border);
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 0.4rem;
            cursor: pointer;
            text-decoration: none;
            transition: background 0.15s, border-color 0.15s;
          }

          .req-btn-outline:hover {
            background: var(--hms-surface);
            border-color: var(--hms-stone);
          }

          .req-btn-primary {
            font-family: 'Inter', sans-serif;
            font-size: 0.875rem;
            font-weight: 600;
            padding: 0.5rem 1.25rem;
            height: 40px;
            border-radius: var(--hms-radius);
            border: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 0.4rem;
            cursor: pointer;
            transition: opacity 0.15s, transform 0.15s;
            width: 100%;
          }

          .req-btn-primary:hover {
            opacity: 0.88;
            transform: translateY(-1px);
          }

          .req-btn-green {
            background: var(--hms-success);
            color: #fff;
          }

          .req-btn-sky {
            background: #0ea5e9;
            color: #fff;
          }

          .req-btn-red {
            background: var(--hms-danger);
            color: #fff;
          }
        </style>

        <body>
          <div class="app-shell">
            <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
            <div class="sidebar-overlay"></div>
            <div class="main-wrapper">
              <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
              <main class="page-content">
                <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

                <c:if test="${not empty error}">
                  <div class="alert alert-danger border-0"
                    style="border-radius:var(--hms-radius);background:var(--hms-danger-bg);color:var(--hms-danger);margin-bottom:16px;">
                    <c:out value="${error}" />
                  </div>
                </c:if>
                <c:if test="${not empty sessionScope.successMessage}">
                  <div class="alert alert-success border-0"
                    style="border-radius:var(--hms-radius);background:var(--hms-success-bg);color:var(--hms-success);margin-bottom:16px;">
                    <c:out value="${sessionScope.successMessage}" />
                  </div>
                  <c:remove var="successMessage" scope="session" />
                </c:if>

                <div class="req-detail-container">

                  <%-- ── Page Header (design system chuẩn) ──────────────────── --%>
                    <div class="page-header hero-sky-gradient d-flex justify-content-between align-items-center"
                      style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem;">
                      <div>
                        <div class="d-flex align-items-center gap-2 flex-wrap" style="margin-bottom:6px;">
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
                            <c:when
                              test="${reqDetail.status == 'COMPLETED' || reqDetail.status == 'DONE' || reqDetail.status == 'RESOLVED'}">
                              <span class="badge-hms badge-success">HOÀN THÀNH</span>
                            </c:when>
                            <c:when test="${reqDetail.status == 'REJECTED' || reqDetail.status == 'CANCELLED'}">
                              <span class="badge-hms badge-danger">TỪ CHỐI</span>
                            </c:when>
                            <c:otherwise>
                              <span class="badge-hms badge-neutral">
                                <c:out value="${reqDetail.status}" />
                              </span>
                            </c:otherwise>
                          </c:choose>
                        </div>
                        <h1>
                          <c:out value="${reqDetail.title}" />
                        </h1>
                        <p style="margin:0;">
                          Mã yêu cầu: <span
                            style="font-family:'Geist Mono','JetBrains Mono',monospace;font-weight:600;">
                            <c:out value="${reqDetail.code}" />
                          </span>
                        </p>
                      </div>
                      <a href="${ctx}/operator/requests" class="req-btn-outline">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                          stroke-width="2">
                          <line x1="19" y1="12" x2="5" y2="12" />
                          <polyline points="12 19 5 12 12 5" />
                        </svg>
                        Quay lại
                      </a>
                    </div>

                    <%-- ── Grid ──────────────────────────────────────────────────── --%>
                      <div class="row g-4">

                        <%-- Cột trái: nội dung yêu cầu + ảnh --%>
                          <div class="col-lg-7">
                            <div class="detail-card">
                              <div class="detail-card-header">
                                <h3>
                                  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                    stroke-width="2.5" style="color:var(--hms-info)">
                                    <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                                    <polyline points="14 2 14 8 20 8" />
                                    <line x1="16" y1="13" x2="8" y2="13" />
                                    <line x1="16" y1="17" x2="8" y2="17" />
                                  </svg>
                                  Nội dung yêu cầu
                                </h3>
                              </div>
                              <div class="detail-card-body">
                                <div style="padding:1.5rem;font-size:15px;color:#374151;line-height:1.7;">
                                  ${reqDetail.content}
                                </div>

                                <c:set var="images" value="${reqDetail.images}" />
                                <c:if test="${not empty images}">
                                  <div style="padding:0 1.5rem 1.5rem;">
                                    <div style="font-size:0.8125rem;font-weight:600;color:#6b7280;text-transform:uppercase;
                                letter-spacing:0.5px;margin-bottom:12px;">
                                      Ảnh đính kèm (${images.size()})
                                    </div>
                                    <div class="d-flex flex-wrap gap-3">
                                      <c:forEach var="img" items="${images}">
                                        <c:set var="finalImg">
                                          <c:choose>
                                            <c:when test="${fn:startsWith(img, ctx)}">${img}</c:when>
                                            <c:otherwise>${ctx}${img}</c:otherwise>
                                          </c:choose>
                                        </c:set>
                                        <img src="${finalImg}" alt="Attachment" style="width:110px;height:110px;object-fit:cover;cursor:pointer;
                                    border-radius:10px;border:1px solid var(--hms-border-soft);"
                                          onclick="openLightbox('${finalImg}')" />
                                      </c:forEach>
                                    </div>
                                  </div>
                                </c:if>
                              </div>
                            </div>
                          </div>

                          <%-- Cột phải: thông tin + hành động --%>
                            <div class="col-lg-5">
                              <div class="detail-card">
                                <div class="detail-card-header">
                                  <h3>
                                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                      stroke-width="2.5" style="color:var(--hms-success)">
                                      <circle cx="12" cy="12" r="10" />
                                      <line x1="12" y1="8" x2="12" y2="12" />
                                      <line x1="12" y1="16" x2="12.01" y2="16" />
                                    </svg>
                                    Thông tin chi tiết
                                  </h3>
                                </div>
                                <div class="detail-card-body">

                                  <div class="metadata-row">
                                    <span class="meta-label">
                                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                        stroke-width="2">
                                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                                        <circle cx="12" cy="7" r="4" />
                                      </svg>
                                      Người gửi
                                    </span>
                                    <span class="meta-value">
                                      <c:out value="${reqDetail.senderName}" />
                                    </span>
                                  </div>

                                  <div class="metadata-row">
                                    <span class="meta-label">
                                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                        stroke-width="2">
                                        <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
                                        <polyline points="9 22 9 12 15 12 15 22" />
                                      </svg>
                                      Phòng / Cơ sở
                                    </span>
                                    <span class="meta-value">
                                      P.
                                      <c:out value="${reqDetail.roomCode}" /> —
                                      <c:out value="${reqDetail.facilityName}" />
                                    </span>
                                  </div>

                                  <div class="metadata-row">
                                    <span class="meta-label">
                                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                        stroke-width="2">
                                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                                        <line x1="16" y1="2" x2="16" y2="6" />
                                        <line x1="8" y1="2" x2="8" y2="6" />
                                        <line x1="3" y1="10" x2="21" y2="10" />
                                      </svg>
                                      Ngày tạo
                                    </span>
                                    <span class="meta-value">
                                      <fmt:formatDate value="${reqDetail.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm" />
                                    </span>
                                  </div>

                                  <c:if
                                    test="${reqDetail.status == 'IN_PROGRESS' && not empty reqDetail.appointSchedule}">
                                    <div class="metadata-row">
                                      <span class="meta-label" style="color:var(--hms-info);">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                                          stroke="currentColor" stroke-width="2">
                                          <circle cx="12" cy="12" r="10" />
                                          <polyline points="12 6 12 12 16 14" />
                                        </svg>
                                        Lịch hẹn xử lý
                                      </span>
                                      <span class="meta-value" style="color:var(--hms-info);">
                                        <c:out value="${reqDetail.formattedAppointmentDate}" />
                                      </span>
                                    </div>
                                  </c:if>

                                  <c:if test="${reqDetail.status == 'REJECTED' && not empty reqDetail.rejectionReason}">
                                    <div class="metadata-row">
                                      <span class="meta-label" style="color:var(--hms-danger);">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                                          stroke="currentColor" stroke-width="2">
                                          <circle cx="12" cy="12" r="10" />
                                          <line x1="15" y1="9" x2="9" y2="15" />
                                          <line x1="9" y1="9" x2="15" y2="15" />
                                        </svg>
                                        Lý do từ chối
                                      </span>
                                      <span class="meta-value" style="color:var(--hms-danger);font-weight:400;">
                                        <c:out value="${reqDetail.rejectionReason}" />
                                      </span>
                                    </div>
                                  </c:if>

                                  <c:if
                                    test="${(reqDetail.status == 'COMPLETED' || reqDetail.status == 'DONE') && not empty reqDetail.rejectionReason}">
                                    <div class="metadata-row">
                                      <span class="meta-label" style="color:var(--hms-success);">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                                          stroke="currentColor" stroke-width="2">
                                          <polyline points="20 6 9 17 4 12" />
                                        </svg>
                                        Ghi chú hoàn thành
                                      </span>
                                      <span class="meta-value" style="font-weight:400;color:var(--hms-text-secondary);">
                                        <c:out value="${reqDetail.rejectionReason}" />
                                      </span>
                                    </div>
                                  </c:if>

                                </div><%-- /detail-card-body --%>

                                  <%-- ── Action footer ──────────────────────────────────── --%>
                                    <div
                                      style="padding:1.25rem 1.5rem;border-top:1px solid var(--hms-border-soft);background:var(--hms-surface-soft);">
                                      <div
                                        style="font-size:0.8125rem;font-weight:600;color:var(--hms-stone);text-transform:uppercase;letter-spacing:0.4px;margin-bottom:0.75rem;">
                                        Hành động
                                      </div>

                                      <c:choose>
                                        <%-- PENDING: tiếp nhận hoặc từ chối --%>
                                          <c:when test="${reqDetail.status == 'PENDING'}">
                                            <form action="${ctx}/operator/requests/detail" method="POST"
                                              class="m-0 mb-2">
                                              <input type="hidden" name="csrfToken" value="${csrfToken}" />
                                              <input type="hidden" name="id" value="${reqDetail.requestId}" />
                                              <input type="hidden" name="action" value="accept" />
                                              <button type="submit" class="req-btn-primary req-btn-green"
                                                onclick="return confirm('Bạn có chắc chắn muốn nhận xử lý yêu cầu này?')">
                                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                                                  stroke="currentColor" stroke-width="2.5">
                                                  <polyline points="20 6 9 17 4 12" />
                                                </svg>
                                                Xác nhận tiếp nhận
                                              </button>
                                            </form>
                                            <button type="button" onclick="openRejectModal()" class="req-btn-primary"
                                              style="background:var(--hms-canvas);color:var(--hms-text-secondary);border:1px solid var(--hms-border);">
                                              <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                                                stroke="currentColor" stroke-width="2">
                                                <line x1="18" y1="6" x2="6" y2="18" />
                                                <line x1="6" y1="6" x2="18" y2="18" />
                                              </svg>
                                              Từ chối
                                            </button>
                                          </c:when>

                                          <%-- ASSIGNED + là người được phân công: lên lịch --%>
                                            <c:when
                                              test="${reqDetail.status == 'ASSIGNED' && reqDetail.assignedStaffId == sessionScope.currentUser.id}">
                                              <c:if test="${not empty reqDetail.appointSchedule}">
                                                <div class="badge-hms badge-info"
                                                  style="display:block;margin-bottom:10px;font-size:12px;font-weight:400;border-radius:var(--hms-radius-sm);padding:8px 10px;">
                                                  Đã có lịch hẹn. Bạn có thể thay đổi bên dưới.
                                                </div>
                                              </c:if>
                                              <form action="${ctx}/operator/requests/detail" method="POST" class="m-0"
                                                onsubmit="return validateAppointment(this)">
                                                <input type="hidden" name="csrfToken" value="${csrfToken}" />
                                                <input type="hidden" name="id" value="${reqDetail.requestId}" />
                                                <input type="hidden" name="action" value="schedule" />
                                                <input type="datetime-local" id="appointmentDateInput"
                                                  name="appointmentDate" value="${reqDetail.appointScheduleForInput}"
                                                  class="form-control mb-2"
                                                  style="border-radius:var(--hms-radius);font-size:14px;" required />
                                                <button type="submit" class="req-btn-primary req-btn-sky">
                                                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                                                    stroke="currentColor" stroke-width="2">
                                                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                                                    <line x1="16" y1="2" x2="16" y2="6" />
                                                    <line x1="8" y1="2" x2="8" y2="6" />
                                                    <line x1="3" y1="10" x2="21" y2="10" />
                                                  </svg>
                                                  Xác nhận lịch hẹn
                                                </button>
                                              </form>
                                            </c:when>

                                            <%-- IN_PROGRESS + là người được phân công: báo cáo hoàn thành --%>
                                              <c:when
                                                test="${reqDetail.status == 'IN_PROGRESS' && reqDetail.assignedStaffId == sessionScope.currentUser.id}">
                                                <button type="button" onclick="openCompleteModal()"
                                                  class="req-btn-primary req-btn-green">
                                                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none"
                                                    stroke="currentColor" stroke-width="2.5">
                                                    <polyline points="20 6 9 17 4 12" />
                                                  </svg>
                                                  Báo cáo hoàn thành
                                                </button>
                                              </c:when>

                                              <%-- Các trạng thái còn lại --%>
                                                <c:otherwise>
                                                  <div style="text-align:center;padding:12px;background:var(--hms-surface);
                                border:1px solid var(--hms-border-soft);border-radius:var(--hms-radius);
                                color:var(--hms-stone);font-size:13px;">
                                                    <c:choose>
                                                      <c:when test="${reqDetail.status == 'IN_PROGRESS'}">Yêu cầu đang
                                                        được xử lý bởi nhân sự khác.</c:when>
                                                      <c:when
                                                        test="${reqDetail.status == 'COMPLETED' || reqDetail.status == 'DONE'}">
                                                        Yêu cầu đã được xử lý thành công.</c:when>
                                                      <c:when test="${reqDetail.status == 'REJECTED'}">Yêu cầu đã bị từ
                                                        chối.</c:when>
                                                      <c:otherwise>Không có hành động khả dụng.</c:otherwise>
                                                    </c:choose>
                                                  </div>
                                                </c:otherwise>
                                      </c:choose>
                                    </div><%-- /action footer --%>

                              </div><%-- /detail-card right --%>
                            </div><%-- /col-lg-5 --%>

                      </div><%-- /row --%>
                </div><%-- /req-detail-container --%>

              </main>
              <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
            </div>
          </div>

          <%-- ── Modal: Từ chối ─────────────────────────────────────────────── --%>
            <div id="rejectModal" class="custom-modal-backdrop" style="z-index:9999;">
              <div class="custom-modal-dialog">
                <h5 style="font-weight:600;margin-bottom:20px;">Từ chối yêu cầu</h5>
                <form action="${ctx}/operator/requests/detail" method="POST">
                  <input type="hidden" name="csrfToken" value="${csrfToken}" />
                  <input type="hidden" name="id" value="${reqDetail.requestId}" />
                  <input type="hidden" name="action" value="reject" />
                  <div class="mb-3">
                    <label class="d-block mb-2" style="font-size:14px;font-weight:500;color:#111827;">
                      Lý do từ chối <span style="color:var(--hms-danger)">*</span>
                    </label>
                    <textarea name="rejectReason" class="form-control" rows="3" required
                      placeholder="Vui lòng nhập lý do cụ thể..." style="border-radius:8px;font-size:14px;"></textarea>
                  </div>
                  <div class="d-flex justify-content-end mt-4" style="gap:12px;">
                    <button type="button" onclick="closeRejectModal()" class="req-btn-outline">Hủy</button>
                    <button type="submit" class="req-btn-primary req-btn-red" style="width:auto;">Xác nhận từ
                      chối</button>
                  </div>
                </form>
              </div>
            </div>

            <%-- ── Modal: Báo cáo hoàn thành ────────────────────────────────────── --%>
              <div id="completeModal" class="custom-modal-backdrop" style="z-index:9999;">
                <div class="custom-modal-dialog">
                  <h5 style="font-weight:600;margin-bottom:20px;">Báo cáo hoàn thành sửa chữa</h5>
                  <form action="${ctx}/operator/requests/detail" method="POST" enctype="multipart/form-data">
                    <input type="hidden" name="csrfToken" value="${csrfToken}" />
                    <input type="hidden" name="id" value="${reqDetail.requestId}" />
                    <input type="hidden" name="action" value="complete" />
                    <div class="mb-3">
                      <label class="d-block mb-2" style="font-size:14px;font-weight:500;color:#111827;">
                        Ghi chú kết quả <span style="color:var(--hms-danger)">*</span>
                      </label>
                      <textarea name="notes" class="form-control" rows="3" required
                        placeholder="Nhập ghi chú hoặc kết quả xử lý..."
                        style="border-radius:8px;font-size:14px;"></textarea>
                    </div>
                    <div class="mb-3">
                      <label class="d-flex align-items-center gap-2 mb-2"
                        style="font-size:14px;font-weight:500;color:#111827;cursor:pointer;">
                        <input type="checkbox" id="no_image_checkbox" name="no_image_checkbox"
                          onchange="toggleImageRequired()" style="width:16px;height:16px;">
                        Lỗi đơn giản (chỉ cần ghi chú, không đính kèm ảnh)
                      </label>
                    </div>
                    <div class="mb-3" id="image_upload_section">
                      <label class="d-block mb-2" style="font-size:14px;font-weight:500;color:#111827;">
                        Ảnh minh chứng (tối đa 5 ảnh) <span style="color:var(--hms-danger)"
                          id="image_required_star">*</span>
                      </label>
                      <input type="file" name="after_images" id="after_images_input" class="form-control"
                        style="border-radius:8px;" multiple accept="image/jpeg,image/png,image/jpg" required>
                      <div style="font-size:12px;color:#6b7280;margin-top:4px;">Định dạng: JPG, PNG. Tối đa 5MB/ảnh.
                      </div>
                    </div>
                    <div class="d-flex justify-content-end mt-4" style="gap:12px;">
                      <button type="button" onclick="closeCompleteModal()" class="req-btn-outline">Hủy</button>
                      <button type="submit" class="req-btn-primary req-btn-green" style="width:auto;">Xác nhận
                        lưu</button>
                    </div>
                  </form>
                </div>
              </div>

              <%-- ── Lightbox ────────────────────────────────────────────────────── --%>
                <div id="lightboxModal" class="lightbox-modal">
                  <span class="lightbox-close" onclick="closeLightbox()">&times;</span>
                  <img class="lightbox-content" id="lightboxImage">
                </div>

                <script>
                  function openLightbox(src) {
                    document.getElementById('lightboxModal').style.display = 'block';
                    document.getElementById('lightboxImage').src = src;
                    var sb = document.querySelector('.sidebar');
                    if (sb) sb.style.display = 'none';
                  }
                  function closeLightbox() {
                    document.getElementById('lightboxModal').style.display = 'none';
                    var sb = document.querySelector('.sidebar');
                    if (sb) sb.style.display = '';
                  }
                  function openRejectModal() { document.getElementById('rejectModal').style.display = 'block'; }
                  function closeRejectModal() { document.getElementById('rejectModal').style.display = 'none'; }
                  function openCompleteModal() { document.getElementById('completeModal').style.display = 'block'; }
                  function closeCompleteModal() { document.getElementById('completeModal').style.display = 'none'; }

                  function toggleImageRequired() {
                    var cb = document.getElementById('no_image_checkbox');
                    var input = document.getElementById('after_images_input');
                    var star = document.getElementById('image_required_star');
                    input.required = !cb.checked;
                    star.style.display = cb.checked ? 'none' : 'inline';
                  }

                  window.onclick = function (e) {
                    ['lightboxModal', 'rejectModal', 'completeModal'].forEach(function (id) {
                      var el = document.getElementById(id);
                      if (e.target === el) {
                        el.style.display = 'none';
                        if (id === 'lightboxModal') {
                          var sb = document.querySelector('.sidebar');
                          if (sb) sb.style.display = '';
                        }
                      }
                    });
                  };

                  document.addEventListener('DOMContentLoaded', function () {
                    var appt = document.getElementById('appointmentDateInput');
                    if (appt) {
                      var now = new Date();
                      now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
                      appt.min = now.toISOString().slice(0, 16);
                    }
                  });

                  function validateAppointment(form) {
                    var appt = document.getElementById('appointmentDateInput');
                    if (appt && appt.value && new Date(appt.value) < new Date()) {
                      alert('Không thể chọn lịch hẹn trong quá khứ. Vui lòng chọn thời gian từ hiện tại trở đi.');
                      return false;
                    }
                    return true;
                  }
                </script>
        </body>

        </html>