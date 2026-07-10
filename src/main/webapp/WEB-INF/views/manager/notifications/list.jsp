<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Thông báo - BQL"/>
<c:set var="pageRole" value="MANAGER"/>
<c:set var="activeMenu" value="notifications"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<style>
  .ntf-tab-link { font-weight: 600; color: var(--hms-text-muted); }
  .ntf-tab-link.ntf-tab-active { color: var(--hms-accent-deep); }
  .ntf-pill-link {
    font-weight: 600; font-size: 0.8125rem;
    padding: 8px 20px; border-radius: 6px;
    transition: all 0.2s;
    background: var(--hms-surface);
    color: var(--hms-text-muted);
    border: 1px solid var(--hms-border);
  }
  .ntf-pill-link.ntf-pill-active {
    background: var(--hms-accent-deep);
    color: #fff;
    border-color: var(--hms-accent-deep);
  }
</style>
<body>
<div class="app-shell">
  <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
  <div class="sidebar-overlay"></div>
  <div class="main-wrapper">
    <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
    <main class="page-content">
      <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

      <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
        <div>
          <h1>Thông báo</h1>
          <p>Quản lý thông báo trong phạm vi cơ sở được phân công</p>
        </div>
        <a href="${ctx}/manager/notifications/create" class="quick-action-btn primary">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
          </svg>
          Tạo thông báo
        </a>
      </div>

      <div class="data-surface">

        <%-- Tabs --%>
        <ul class="nav nav-tabs mb-3" id="notificationTabs" role="tablist">
          <li class="nav-item" role="presentation">
            <a class="nav-link ntf-tab-link ${empty tab or tab == 'general' ? 'active ntf-tab-active' : ''}"
               href="${ctx}/manager/notifications?tab=general">
              Thông báo chung
            </a>
          </li>
          <li class="nav-item" role="presentation">
            <a class="nav-link ntf-tab-link ${tab == 'payment-reminder' ? 'active ntf-tab-active' : ''}"
               href="${ctx}/manager/notifications?tab=payment-reminder">
              Nhắc nhở thanh toán
            </a>
          </li>
          <li class="nav-item" role="presentation">
            <a class="nav-link ntf-tab-link ${tab == 'incorrect-utility' ? 'active ntf-tab-active' : ''}"
               href="${ctx}/manager/notifications?tab=incorrect-utility">
              Báo lỗi điện nước
            </a>
          </li>
        </ul>

        <c:choose>
          <c:when test="${param.tab == 'incorrect-utility'}">
            <%-- Tab Báo lỗi điện nước --%>
            <form method="get" action="${ctx}/manager/notifications" id="filterFormIncorrect" class="mb-4 p-3 rounded" style="background-color: var(--hms-bg-surface); border: 1px solid var(--hms-border);">
              <input type="hidden" name="tab" value="incorrect-utility"/>
              <div class="row g-3 align-items-end">
                <div class="col-12 col-md-8">
                  <label class="form-label" style="font-size:0.875rem;font-weight:500;color:var(--hms-text-primary);margin-bottom:0.25rem;">Tìm kiếm</label>
                  <input type="text" class="form-control" name="keyword"
                         placeholder="Mã hóa đơn / phòng..."
                         value="<c:out value='${keyword}'/>">
                </div>
                <div class="col-12 col-md-4 d-flex justify-content-md-end gap-2">
                  <a href="${ctx}/manager/notifications?tab=incorrect-utility" class="btn btn-light border text-decoration-none" style="font-size:0.875rem;font-weight:500;padding:6px 16px;">Xóa lọc</a>
                  <button type="submit" class="btn-mintlify-secondary" style="padding:6px 20px;">Tìm kiếm</button>
                </div>
              </div>
            </form>

            <c:choose>
              <c:when test="${not empty incorrectInvoices}">
                <div class="table-responsive">
                  <table class="table-mintlify">
                    <thead>
                      <tr>
                        <th class="d-none d-md-table-cell">Cơ sở</th>
                        <th>Phòng</th>
                        <th class="d-none d-md-table-cell">Kỳ hạn</th>
                        <th>Số điện nước báo sai</th>
                        <th class="d-none d-md-table-cell">Tổng tiền</th>
                        <th>Trạng thái chỉ số</th>
                        <th class="d-none d-md-table-cell">Thao tác</th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:forEach var="item" items="${incorrectInvoices}">
                        <tr>
                          <td class="d-none d-md-table-cell"><c:out value="${item.facilityName}"/> (<c:out value="${item.facilityCode}"/>)</td>
                          <td>
                            <a href="${ctx}/manager/rooms/${item.roomId}">
                              <strong><c:out value="${item.roomCode}"/></strong>
                            </a>
                          </td>
                          <td class="d-none d-md-table-cell"><c:out value="${item.billingPeriod}"/></td>
                          <td>
                            <div>Điện: <strong><c:out value="${item.electric}"/></strong> kWh</div>
                            <div>Nước: <strong><c:out value="${item.water}"/></strong> m³</div>
                          </td>
                          <td class="d-none d-md-table-cell" style="font-weight:600">
                            <fmt:formatNumber value="${item.totalAmount}" pattern="#,##0"/> đ
                          </td>
                          <td>
                            <c:choose>
                              <c:when test="${item.meterStatus == 'INCORRECT'}">
                                <span class="badge-hms badge-danger">Chờ xử lý</span>
                              </c:when>
                              <c:when test="${item.meterStatus == 'REPORTED'}">
                                <span class="badge-hms badge-info">Đã gửi Operator</span>
                              </c:when>
                              <c:otherwise>
                                <span class="badge-hms badge-neutral"><c:out value="${item.meterStatus}"/></span>
                              </c:otherwise>
                            </c:choose>
                          </td>
                          <td class="d-none d-md-table-cell">
                            <c:choose>
                              <c:when test="${item.meterStatus == 'INCORRECT'}">
                                <a href="${ctx}/manager/notifications/send-operator?invoiceId=${item.id}"
                                   class="quick-action-btn primary"
                                   style="padding:6px 14px; font-size:0.8125rem; white-space:nowrap; text-decoration:none">
                                  Gửi Operator
                                </a>
                              </c:when>
                              <c:otherwise>
                                <a href="${ctx}/manager/notifications/send-operator?invoiceId=${item.id}"
                                   class="btn-mintlify-secondary text-decoration-none"
                                   style="padding:6px 14px; font-size:0.8125rem; white-space:nowrap">
                                  Gửi lại
                                </a>
                              </c:otherwise>
                            </c:choose>
                          </td>
                        </tr>
                      </c:forEach>
                    </tbody>
                  </table>
                </div>
                <div class="table-footer px-3 py-2 text-muted" style="font-size:0.875rem">
                  Tổng cộng: <strong>${incorrectInvoices.size()}</strong> hóa đơn lỗi chỉ số.
                </div>
              </c:when>
              <c:otherwise>
                <div class="empty-state p-5 text-center">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)" stroke-width="1.5" style="margin-bottom:12px">
                    <circle cx="12" cy="12" r="10"/>
                    <line x1="12" y1="8" x2="12" y2="12"/>
                    <line x1="12" y1="16" x2="12.01" y2="16"/>
                  </svg>
                  <h4>Không có hóa đơn báo lỗi</h4>
                  <p class="text-muted">Tất cả hóa đơn trong các cơ sở của bạn đều có chỉ số điện nước bình thường.</p>
                </div>
              </c:otherwise>
            </c:choose>
          </c:when>
          <c:when test="${param.tab == 'payment-reminder'}">
            <%-- Tab Nhắc nhở thanh toán --%>
            <form method="get" action="${ctx}/manager/notifications" id="filterFormReminder" class="mb-4 p-3 rounded" style="background-color: var(--hms-bg-surface); border: 1px solid var(--hms-border);">
              <input type="hidden" name="tab" value="payment-reminder"/>
              <div class="row g-3 align-items-end">
                <div class="col-12 col-md-8">
                  <label class="form-label" style="font-size:0.875rem;font-weight:500;color:var(--hms-text-primary);margin-bottom:0.25rem;">Tìm kiếm</label>
                  <input type="text" class="form-control" name="keyword"
                         placeholder="Tiêu đề / nội dung..."
                         value="<c:out value='${keyword}'/>">
                </div>
                <div class="col-12 col-md-4 d-flex justify-content-md-end gap-2">
                  <a href="${ctx}/manager/notifications?tab=payment-reminder" class="btn btn-light border text-decoration-none" style="font-size:0.875rem;font-weight:500;padding:6px 16px;">Xóa lọc</a>
                  <button type="submit" class="btn-mintlify-secondary" style="padding:6px 20px;">Tìm kiếm</button>
                </div>
              </div>
            </form>

            <c:choose>
              <c:when test="${not empty page.items}">
                <div class="table-responsive">
                  <table class="table-mintlify">
                    <thead>
                      <tr>
                        <th>Mã</th>
                        <th>Tiêu đề</th>
                        <th class="d-none d-md-table-cell">Đối tượng</th>
                        <th class="d-none d-md-table-cell">Người gửi</th>
                        <th>Trạng thái</th>
                        <th class="d-none d-md-table-cell">Ngày gửi</th>
                        <th class="d-none d-md-table-cell">Thao tác</th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:forEach var="notif" items="${page.items}">
                        <tr data-href="${ctx}/manager/notifications/${notif.id}">
                          <td>
                            <a href="${ctx}/manager/notifications/${notif.id}" class="code-badge">
                              <c:out value="${notif.code}"/>
                            </a>
                          </td>
                          <td style="max-width:280px"><c:out value="${notif.title}"/></td>
                          <td class="d-none d-md-table-cell">
                            <c:choose>
                              <c:when test="${notif.recipientType == 'ROOM'}">
                                <span class="badge-hms badge-neutral">Phòng</span>
                              </c:when>
                              <c:otherwise>
                                <span class="badge-hms badge-neutral"><c:out value="${notif.recipientType}"/></span>
                              </c:otherwise>
                            </c:choose>
                          </td>
                          <td class="d-none d-md-table-cell"><c:out value="${notif.createdByName}"/></td>
                          <td>
                            <span class="badge-hms badge-success">Đã gửi</span>
                          </td>
                          <td class="d-none d-md-table-cell" style="font-size:0.8125rem;color:var(--hms-text-muted)">
                            <c:out value="${notif.createdDateLabel}"/>
                          </td>
                          <td class="d-none d-md-table-cell">
                            <a href="${ctx}/manager/notifications/${notif.id}"
                               class="btn-mintlify-secondary text-decoration-none"
                               style="padding:4px 12px;font-size:0.8125rem">Xem</a>
                          </td>
                        </tr>
                      </c:forEach>
                    </tbody>
                  </table>
                </div>
                <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2">
                  <span class="text-muted" style="font-size:0.875rem">
                    Tổng <fmt:formatNumber value="${page.total}" groupingUsed="true"/> nhắc nhở
                    · Trang ${page.page} / ${page.totalPages}
                  </span>
                  <div class="d-flex gap-1">
                    <c:if test="${page.page > 1}">
                      <a href="${ctx}/manager/notifications?page=${page.page - 1}&keyword=${keyword}&facilityId=${filterFacilityId}&tab=payment-reminder"
                         class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                    </c:if>
                    <c:if test="${page.page < page.totalPages}">
                      <a href="${ctx}/manager/notifications?page=${page.page + 1}&keyword=${keyword}&facilityId=${filterFacilityId}&tab=payment-reminder"
                         class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                    </c:if>
                  </div>
                </div>
              </c:when>
              <c:otherwise>
                <div class="empty-state p-4 text-center">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)" stroke-width="1.5" style="margin-bottom:12px">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                    <line x1="16" y1="2" x2="16" y2="6"/>
                    <line x1="8" y1="2" x2="8" y2="6"/>
                    <line x1="3" y1="10" x2="21" y2="10"/>
                  </svg>
                  <h4>Chưa có nhắc nhở thanh toán nào</h4>
                  <p class="text-muted">Các nhắc nhở thanh toán gửi từ danh sách công nợ quá hạn sẽ hiển thị ở đây.</p>
                </div>
              </c:otherwise>
            </c:choose>
          </c:when>
          <c:otherwise>
            <%-- Tab Thông báo chung (Gộp cả gửi đến tôi và gửi tới cư dân) --%>
            
            <%-- Sub-tabs/Pills for General tab --%>
            <c:set var="pillStyleReceived" value="${type == 'received' ? 'background:var(--hms-accent-deep);color:#fff;' : 'background:var(--hms-surface);color:var(--hms-text-muted);border:1px solid var(--hms-border);'}"/>
            <c:set var="pillStyleSent"     value="${type == 'sent'     ? 'background:var(--hms-accent-deep);color:#fff;' : 'background:var(--hms-surface);color:var(--hms-text-muted);border:1px solid var(--hms-border);'}"/>
            <ul class="nav nav-pills mb-4 mt-3 ms-2" id="notificationSubTabs" role="tablist" style="gap:12px">
              <li class="nav-item" role="presentation">
                <a class="nav-link ntf-pill-link ${type == 'received' ? 'active ntf-pill-active' : ''}"
                   href="${ctx}/manager/notifications?tab=general&type=received">
                  Gửi đến tôi
                </a>
              </li>
              <li class="nav-item" role="presentation">
                <a class="nav-link ntf-pill-link ${type == 'sent' ? 'active ntf-pill-active' : ''}"
                   href="${ctx}/manager/notifications?tab=general&type=sent">
                  Gửi tới cư dân
                </a>
              </li>
            </ul>

            <c:choose>
              <c:when test="${type == 'received'}">
                <%-- Sub-tab Gửi đến tôi (Nhận từ Admin) --%>
                <form method="get" action="${ctx}/manager/notifications" id="filterFormReceived" class="mb-4 p-3 rounded" style="background-color: var(--hms-bg-surface); border: 1px solid var(--hms-border);">
                  <input type="hidden" name="tab" value="general"/>
                  <input type="hidden" name="type" value="received"/>
                  <div class="row g-3 align-items-end">
                    <div class="col-12 col-md-6">
                      <label class="form-label" style="font-size:0.875rem;font-weight:500;color:var(--hms-text-primary);margin-bottom:0.25rem;">Tìm kiếm</label>
                      <input type="text" class="form-control" name="keyword"
                             placeholder="Tiêu đề / nội dung..."
                             value="<c:out value='${keyword}'/>">
                    </div>
                    <div class="col-12 col-md-6 d-flex justify-content-md-end gap-2">
                      <a href="${ctx}/manager/notifications?tab=general&type=received" class="btn btn-light border text-decoration-none" style="font-size:0.875rem;font-weight:500;padding:6px 16px;">Xóa lọc</a>
                      <button type="submit" class="btn-mintlify-secondary" style="padding:6px 20px;">Tìm kiếm</button>
                    </div>
                  </div>
                </form>

                <c:choose>
                  <c:when test="${not empty page.items}">
                    <div class="table-responsive">
                      <table class="table-mintlify">
                        <thead>
                          <tr>
                            <th>Mã</th>
                            <th>Tiêu đề</th>
                            <th class="d-none d-md-table-cell">Người gửi</th>
                            <th class="d-none d-md-table-cell">Ngày gửi</th>
                            <th class="d-none d-md-table-cell">Thao tác</th>
                          </tr>
                        </thead>
                        <tbody>
                          <c:forEach var="notif" items="${page.items}">
                            <tr data-href="${ctx}/manager/notifications/${notif.id}">
                              <td>
                                <a href="${ctx}/manager/notifications/${notif.id}">
                                  <c:out value="${notif.code}"/>
                                </a>
                              </td>
                              <td style="max-width:320px; font-weight: 500;"><c:out value="${notif.title}"/></td>
                              <td class="d-none d-md-table-cell">
                                <c:out value="${notif.createdByName}"/>
                              </td>
                              <td class="d-none d-md-table-cell" style="font-size:0.8125rem;color:var(--hms-text-muted)">
                                <c:out value="${notif.createdDateLabel}"/>
                              </td>
                              <td class="d-none d-md-table-cell">
                                <a href="${ctx}/manager/notifications/${notif.id}"
                                   class="btn-mintlify-secondary text-decoration-none"
                                   style="padding:4px 12px;font-size:0.8125rem">Xem chi tiết</a>
                              </td>
                            </tr>
                          </c:forEach>
                        </tbody>
                      </table>
                    </div>
                    <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2">
                      <span class="text-muted" style="font-size:0.875rem">
                        Tổng <fmt:formatNumber value="${page.total}" groupingUsed="true"/> thông báo
                        · Trang ${page.page} / ${page.totalPages}
                      </span>
                      <div class="d-flex gap-1">
                        <c:if test="${page.page > 1}">
                          <a href="${ctx}/manager/notifications?page=${page.page - 1}&keyword=${keyword}&tab=general&type=received"
                             class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                        </c:if>
                        <c:if test="${page.page < page.totalPages}">
                          <a href="${ctx}/manager/notifications?page=${page.page + 1}&keyword=${keyword}&tab=general&type=received"
                             class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                        </c:if>
                      </div>
                    </div>
                  </c:when>
                  <c:otherwise>
                    <div class="empty-state p-5 text-center">
                      <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)" stroke-width="1.5" style="margin-bottom:12px">
                        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                        <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
                      </svg>
                      <h4>Không có thông báo chung nào</h4>
                      <p class="text-muted">Chưa có thông báo hệ thống nào được gửi từ Admin.</p>
                    </div>
                  </c:otherwise>
                </c:choose>
              </c:when>
              <c:otherwise>
          
                <form method="get" action="${ctx}/manager/notifications" id="filterFormSent" class="mb-4 p-3 rounded" style="background-color: var(--hms-bg-surface); border: 1px solid var(--hms-border);">
                  <input type="hidden" name="tab" value="general"/>
                  <input type="hidden" name="type" value="sent"/>
                  <div class="row g-3 align-items-end">
                    <div class="col-12 col-md-4">
                      <label class="form-label" style="font-size:0.875rem;font-weight:500;color:var(--hms-text-primary);margin-bottom:0.25rem;">Tìm kiếm</label>
                      <input type="text" class="form-control" name="keyword"
                             placeholder="Tiêu đề / nội dung..."
                             value="<c:out value='${keyword}'/>">
                    </div>
                    <div class="col-12 col-md-4">
                      <label class="form-label" style="font-size:0.875rem;font-weight:500;color:var(--hms-text-primary);margin-bottom:0.25rem;">Cơ sở</label>
                      <select class="form-select" name="facilityId">
                        <option value="">Tất cả cơ sở</option>
                        <c:forEach var="facility" items="${assignedFacilities}">
                          <option value="${facility.id}" ${filterFacilityId == facility.id ? 'selected' : ''}>
                            <c:out value="${facility.name}"/> (<c:out value="${facility.code}"/>)
                          </option>
                        </c:forEach>
                      </select>
                    </div>
                    <div class="col-12 col-md-4 d-flex justify-content-md-end gap-2">
                      <a href="${ctx}/manager/notifications?tab=general&type=sent" class="btn btn-light border text-decoration-none" style="font-size:0.875rem;font-weight:500;padding:6px 16px;">Xóa lọc</a>
                      <button type="submit" class="btn-mintlify-secondary" style="padding:6px 20px;">Tìm kiếm</button>
                    </div>
                  </div>
                </form>

                <c:choose>
                  <c:when test="${not empty page.items}">
                    <div class="table-responsive">
                      <table class="table-mintlify">
                        <thead>
                          <tr>
                            <th>Mã</th>
                            <th>Tiêu đề</th>
                            <th class="d-none d-md-table-cell">Đối tượng</th>
                            <th class="d-none d-md-table-cell">Người tạo</th>
                            <th>Trạng thái</th>
                            <th class="d-none d-md-table-cell">Ngày tạo</th>
                            <th class="d-none d-md-table-cell">Thao tác</th>
                          </tr>
                        </thead>
                        <tbody>
                          <c:forEach var="notif" items="${page.items}">
                            <tr data-href="${ctx}/manager/notifications/${notif.id}">
                              <td>
                                <a href="${ctx}/manager/notifications/${notif.id}">
                                  <c:out value="${notif.code}"/>
                                </a>
                              </td>
                              <td style="max-width:280px"><c:out value="${notif.title}"/></td>
                              <td class="d-none d-md-table-cell">
                                <c:choose>
                                  <c:when test="${notif.recipientType == 'FACILITY'}">
                                    <span class="badge-hms badge-info">Cơ sở</span>
                                  </c:when>
                                  <c:when test="${notif.recipientType == 'ROOM'}">
                                    <span class="badge-hms badge-neutral">Phòng</span>
                                  </c:when>
                                  <c:otherwise>
                                    <span class="badge-hms badge-neutral"><c:out value="${notif.recipientType}"/></span>
                                  </c:otherwise>
                                </c:choose>
                              </td>
                              <td class="d-none d-md-table-cell"><c:out value="${notif.createdByName}"/></td>
                              <td>
                                <c:choose>
                                  <c:when test="${notif.status == 'SENT'}">
                                    <span class="badge-hms badge-success">Đã gửi</span>
                                  </c:when>
                                  <c:otherwise>
                                    <span class="badge-hms badge-warning">Nháp</span>
                                  </c:otherwise>
                                </c:choose>
                              </td>
                              <td class="d-none d-md-table-cell" style="font-size:0.8125rem;color:var(--hms-text-muted)">
                                <c:out value="${notif.createdDateLabel}"/>
                              </td>
                              <td class="d-none d-md-table-cell">
                                <a href="${ctx}/manager/notifications/${notif.id}"
                                   class="btn-mintlify-secondary text-decoration-none"
                                   style="padding:4px 12px;font-size:0.8125rem">Xem</a>
                              </td>
                            </tr>
                          </c:forEach>
                        </tbody>
                      </table>
                    </div>
                    <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2">
                      <span class="text-muted" style="font-size:0.875rem">
                        Tổng <fmt:formatNumber value="${page.total}" groupingUsed="true"/> thông báo
                        · Trang ${page.page} / ${page.totalPages}
                      </span>
                      <div class="d-flex gap-1">
                        <c:if test="${page.page > 1}">
                          <a href="${ctx}/manager/notifications?page=${page.page - 1}&keyword=${keyword}&facilityId=${filterFacilityId}&tab=general&type=sent"
                             class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                        </c:if>
                        <c:if test="${page.page < page.totalPages}">
                          <a href="${ctx}/manager/notifications?page=${page.page + 1}&keyword=${keyword}&facilityId=${filterFacilityId}&tab=general&type=sent"
                             class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                        </c:if>
                      </div>
                    </div>
                  </c:when>
                  <c:otherwise>
                    <div class="empty-state p-4 text-center">
                      <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)" stroke-width="1.5" style="margin-bottom:12px">
                        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                        <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
                      </svg>
                      <h4>Chưa có thông báo nào</h4>
                      <p class="text-muted">Tạo thông báo đầu tiên để gửi đến cư dân.</p>
                      <a href="${ctx}/manager/notifications/create" class="quick-action-btn primary mt-2">Tạo thông báo</a>
                    </div>
                  </c:otherwise>
                </c:choose>
              </c:otherwise>
            </c:choose>
          </c:otherwise>
        </c:choose>
      </div>

    </main>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
