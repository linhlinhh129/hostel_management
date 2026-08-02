<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
      <c:set var="ctx" value="${pageContext.request.contextPath}" />
      <c:set var="pageTitle" value="Gửi yêu cầu chỉnh sửa - BQL" />
      <c:set var="pageRole" value="MANAGER" />
      <c:set var="activeMenu" value="notifications" />
      <jsp:include page="/WEB-INF/views/layout/head.jsp" />

      <body>
        <div class="app-shell">
          <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
          <div class="sidebar-overlay"></div>
          <div class="main-wrapper">
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
            <main class="page-content">
              <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

              <div
                class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3"
                style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div>
                  <h1 class="mb-1">Gửi yêu cầu chỉnh sửa số điện nước</h1>
                  <p class="mb-0">Báo cáo chỉ số sai cho nhân viên vận hành (Operator) để xác minh và cập nhật lại</p>
                </div>
                <a href="${ctx}/manager/notifications?tab=incorrect-utility"
                  class="btn-mintlify-secondary text-decoration-none">← Danh sách</a>
              </div>

              <div class="row g-4">
                <%-- Cột trái: Thông tin đã gửi / Form nhập --%>
                  <div class="col-lg-7">
                    <div class="data-surface" style="padding:2rem">
                      <%-- Tiến độ xử lý hiện tại nếu đã gửi --%>
                        <c:if test="${not empty invoice.ticketId}">
                          <div class="card mb-4 border-warning"
                            style="border-radius:var(--hms-radius-md);box-shadow:none;background:#fffdf5">
                            <div class="card-header bg-warning bg-opacity-10 font-semibold"
                              style="border-bottom:1px solid #fde68a">
                              <div class="d-flex justify-content-between align-items-center">
                                <span class="fw-bold" style="color:#92400e;font-size:1rem">📋 Chi tiết yêu cầu đã gửi &
                                  Tiến độ xử lý</span>
                                <c:choose>
                                  <c:when test="${invoice.ticketStatus == 'PENDING'}">
                                    <span class="badge bg-warning text-dark px-3 py-2" style="font-size:0.875rem">🟡 Chờ
                                      Operator tiếp nhận</span>
                                  </c:when>
                                  <c:when test="${invoice.ticketStatus == 'IN_PROGRESS'}">
                                    <span class="badge bg-info text-white px-3 py-2" style="font-size:0.875rem">🔵
                                      Operator đang xử lý</span>
                                  </c:when>
                                  <c:when
                                    test="${invoice.ticketStatus == 'COMPLETED' or invoice.ticketStatus == 'DONE' or invoice.meterStatus == 'UPDATED'}">
                                    <span class="badge bg-success text-white px-3 py-2" style="font-size:0.875rem">🟢
                                      Operator đã xử lý xong</span>
                                  </c:when>
                                  <c:otherwise>
                                    <span class="badge bg-warning text-dark px-3 py-2">🟡 Đã gửi báo cáo</span>
                                  </c:otherwise>
                                </c:choose>
                              </div>
                            </div>
                            <div class="card-body" style="font-size:0.9rem">
                              <div class="row g-2 mb-3">
                                <div class="col-sm-6">
                                  <span class="text-muted d-block" style="font-size:0.8rem">Nhân viên phụ trách:</span>
                                  <strong>
                                    <c:out value="${invoice.operatorName}" default="Chưa phân công" />
                                  </strong>
                                </div>
                                <div class="col-sm-6">
                                  <span class="text-muted d-block" style="font-size:0.8rem">Thời gian gửi:</span>
                                  <strong>
                                    <fmt:formatDate value="${invoice.ticketCreatedAt}" pattern="dd/MM/yyyy HH:mm:ss" />
                                  </strong>
                                </div>
                              </div>

                              <div class="mb-3">
                                <span class="text-muted d-block" style="font-size:0.8rem">Tiêu đề yêu cầu:</span>
                                <div class="fw-bold text-dark">
                                  <c:out value="${invoice.ticketTitle}" default="${defaultTitle}" />
                                </div>
                              </div>

                              <div class="mb-3">
                                <span class="text-muted d-block" style="font-size:0.8rem">Nội dung chi tiết đã
                                  gửi:</span>
                                <div
                                  style="background:#f8fafc;padding:10px 14px;border-radius:6px;border:1px solid #e2e8f0;white-space:pre-wrap;font-size:0.875rem">
                                  <c:out value="${invoice.ticketContent}" default="${defaultContent}" />
                                </div>
                              </div>

                              <c:if test="${not empty invoice.ticketNotes}">
                                <div class="p-3 bg-light rounded border border-success">
                                  <span class="text-success font-semibold d-block" style="font-size:0.85rem">📝 Phản hồi
                                    / Kết quả từ Operator:</span>
                                  <div class="mt-1" style="white-space:pre-wrap">
                                    <c:out value="${invoice.ticketNotes}" />
                                  </div>
                                </div>
                              </c:if>
                            </div>
                          </div>
                        </c:if>

                        <details ${empty invoice.ticketId ? 'open' : '' }>
                          <summary class="fw-bold mb-3" style="cursor:pointer;color:var(--hms-primary)">
                            <c:choose>
                              <c:when test="${not empty invoice.ticketId}">
                                ✏️ Gửi bổ sung / Cập nhật yêu cầu cho Operator
                              </c:when>
                              <c:otherwise>
                                📝 Nhập thông tin yêu cầu gửi Operator
                              </c:otherwise>
                            </c:choose>
                          </summary>

                          <%-- Form nhập gửi yêu cầu --%>
                            <form method="post" action="${ctx}/manager/notifications/send-operator" class="mt-3">
                              <input type="hidden" name="csrfToken" value="${csrfToken}" />
                              <input type="hidden" name="invoiceId" value="${invoice.id}" />

                              <%-- Nhân viên vận hành phụ trách cơ sở (Tự động nạp) --%>
                                <div class="mb-3">
                                  <label for="operatorIdDisplay" class="form-label">Nhân viên vận hành phụ trách cơ sở
                                    <span class="text-danger">*</span></label>
                                  <c:choose>
                                    <c:when test="${not empty operators}">
                                      <input type="hidden" name="operatorId" value="${operators[0].id}" />
                                      <input type="text" class="form-control fw-bold text-dark" id="operatorIdDisplay"
                                        value="<c:out value='${operators[0].fullName}'/>" readonly
                                        style="background-color: #f1f5f9; cursor: not-allowed;">
                                    </c:when>
                                    <c:otherwise>
                                      <select class="form-select" id="operatorId" name="operatorId" required>
                                        <option value="">-- Chưa gán Operator cho cơ sở này --</option>
                                      </select>
                                    </c:otherwise>
                                  </c:choose>
                                  <div class="form-text">Yêu cầu sẽ được gửi thẳng tới Nhân viên vận hành đang phụ trách
                                    cơ sở này.</div>
                                </div>

                                <%-- Tiêu đề yêu cầu --%>
                                  <div class="mb-3">
                                    <label for="title" class="form-label">Tiêu đề yêu cầu <span
                                        class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="title" name="title" required
                                      maxlength="200" value="<c:out value='${defaultTitle}'/>">
                                  </div>

                                  <%-- Nội dung yêu cầu --%>
                                    <div class="mb-3">
                                      <label for="content" class="form-label">Nội dung chi tiết <span
                                          class="text-danger">*</span></label>
                                      <textarea class="form-control" id="content" name="content" rows="6" required
                                        maxlength="5000"><c:out value="${defaultContent}"/></textarea>
                                      <div class="form-text">Mô tả cụ thể lý do sai và yêu cầu đo đạc/chốt số lại.</div>
                                    </div>

                                    <div class="d-flex gap-2 mt-4">
                                      <button type="submit" class="quick-action-btn primary">
                                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                                          stroke="currentColor" stroke-width="2.5" style="margin-right:4px">
                                          <line x1="22" y1="2" x2="11" y2="13" />
                                          <polygon points="22 2 15 22 11 13 2 9 22 2" />
                                        </svg>
                                        Gửi Operator
                                      </button>
                                      <a href="${ctx}/manager/invoices"
                                        class="btn-mintlify-secondary text-decoration-none">Quay lại</a>
                                    </div>
                            </form>
                        </details>
                    </div>
                  </div>

                  <%-- Cột phải: Thông tin hóa đơn bị báo sai --%>
                    <div class="col-lg-5">
                      <div class="widget-surface">
                        <div class="widget-surface-header">
                          <h3>Thông tin hóa đơn báo lỗi</h3>
                        </div>
                        <div class="widget-surface-body p-0">
                          <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                            <tr style="border-bottom:1px solid var(--hms-border)">
                              <td style="padding:12px 16px;color:var(--hms-text-muted);width:38%">Mã hóa đơn</td>
                              <td style="padding:12px 16px;font-weight:600">
                                <c:out value="${invoice.code}" />
                              </td>
                            </tr>
                            <tr style="border-bottom:1px solid var(--hms-border)">
                              <td style="padding:12px 16px;color:var(--hms-text-muted)">Cơ sở</td>
                              <td style="padding:12px 16px">
                                <c:out value="${invoice.facilityName}" /> (
                                <c:out value="${invoice.facilityCode}" />)
                              </td>
                            </tr>
                            <tr style="border-bottom:1px solid var(--hms-border)">
                              <td style="padding:12px 16px;color:var(--hms-text-muted)">Phòng & Khách thuê</td>
                              <td style="padding:12px 16px">
                                <strong style="color:var(--hms-primary);font-size:0.9375rem">Phòng
                                  <c:out value="${invoice.roomCode}" />
                                </strong>
                                <div style="font-size:0.8125rem;color:var(--hms-text-secondary);margin-top:2px">
                                  <c:out value="${invoice.tenantName}" /> (
                                  <c:out value="${invoice.tenantPhone}" />)
                                </div>
                              </td>
                            </tr>
                            <tr style="border-bottom:1px solid var(--hms-border)">
                              <td style="padding:12px 16px;color:var(--hms-text-muted)">Kỳ hóa đơn</td>
                              <td style="padding:12px 16px;font-weight:600">Tháng
                                <c:out value="${invoice.billingPeriod}" />
                              </td>
                            </tr>
                            <tr style="border-bottom:1px solid var(--hms-border)">
                              <td style="padding:12px 16px;color:var(--hms-text-muted)">Chỉ số điện</td>
                              <td style="padding:12px 16px">
                                <div>Cũ: <strong>
                                    <c:out value="${invoice.oldElectric}" />
                                  </strong> &rarr; Mới: <strong>
                                    <c:out value="${invoice.newElectric}" />
                                  </strong> kWh</div>
                                <div style="font-size:0.8rem;color:var(--hms-text-muted);margin-top:2px">Sử dụng:
                                  <strong style="color:var(--hms-ink)">
                                    <c:out value="${invoice.electricUsage}" /> kWh
                                  </strong></div>
                              </td>
                            </tr>
                            <tr style="border-bottom:1px solid var(--hms-border)">
                              <td style="padding:12px 16px;color:var(--hms-text-muted)">Chỉ số nước</td>
                              <td style="padding:12px 16px">
                                <div>Cũ: <strong>
                                    <c:out value="${invoice.oldWater}" />
                                  </strong> &rarr; Mới: <strong>
                                    <c:out value="${invoice.newWater}" />
                                  </strong> m³</div>
                                <div style="font-size:0.8rem;color:var(--hms-text-muted);margin-top:2px">Sử dụng:
                                  <strong style="color:var(--hms-ink)">
                                    <c:out value="${invoice.waterUsage}" /> m³
                                  </strong></div>
                              </td>
                            </tr>
                            <tr style="background:var(--hms-accent-bg)">
                              <td style="padding:12px 16px;font-weight:700;color:var(--hms-ink)">Tổng số tiền HĐ</td>
                              <td style="padding:12px 16px;font-weight:800;color:var(--hms-accent-deep)">
                                <fmt:formatNumber value="${invoice.totalAmount}" pattern="#,##0" /> đ
                              </td>
                            </tr>
                          </table>
                        </div>
                      </div>

                    </div>
              </div>

            </main>
          </div>
        </div>
        <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
      </body>

      </html>