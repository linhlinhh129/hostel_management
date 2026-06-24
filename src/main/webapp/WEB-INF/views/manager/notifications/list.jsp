<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Thông báo - BQL"/>
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

      <div class="page-header d-flex flex-wrap justify-content-between align-items-start gap-3">
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
            <a class="nav-link ${empty tab or tab == 'general' ? 'active' : ''}" 
               href="${ctx}/manager/notifications?tab=general" 
               style="font-weight: 600; color: ${(empty tab or tab == 'general') ? 'var(--hms-accent-deep)' : 'var(--hms-text-muted)'}">
              Thông báo chung
            </a>
          </li>
          <li class="nav-item" role="presentation">
            <a class="nav-link ${tab == 'incorrect-utility' ? 'active' : ''}" 
               href="${ctx}/manager/notifications?tab=incorrect-utility" 
               style="font-weight: 600; color: ${tab == 'incorrect-utility' ? 'var(--hms-accent-deep)' : 'var(--hms-text-muted)'}">
              Báo lỗi điện nước
            </a>
          </li>
        </ul>

        <c:choose>
          <c:when test="${param.tab == 'incorrect-utility'}">
            <%-- Tab Báo lỗi điện nước --%>
            <form class="filter-bar mb-3" method="get" action="${ctx}/manager/notifications">
              <input type="hidden" name="tab" value="incorrect-utility"/>
              <select class="form-select" name="facilityId" style="max-width:240px">
                <option value="">Tất cả cơ sở</option>
                <c:forEach var="facility" items="${assignedFacilities}">
                  <option value="${facility.id}" ${filterFacilityId == facility.id ? 'selected' : ''}>
                    <c:out value="${facility.name}"/> (<c:out value="${facility.code}"/>)
                  </option>
                </c:forEach>
              </select>
              <input type="text" class="form-control" name="keyword"
                     placeholder="Mã hóa đơn / phòng / cơ sở..."
                     value="<c:out value='${keyword}'/>">
              <button type="submit" class="btn-mintlify-secondary">Tìm kiếm</button>
              <a href="${ctx}/manager/notifications?tab=incorrect-utility" class="btn-mintlify-secondary text-decoration-none">Xóa bộ lọc</a>
            </form>

            <c:choose>
              <c:when test="${not empty incorrectInvoices}">
                <div class="table-responsive">
                  <table class="table-mintlify">
                    <thead>
                      <tr>
                        <th>Cơ sở</th>
                        <th>Phòng</th>
                        <th>Kỳ hạn</th>
                        <th>Số điện nước báo sai</th>
                        <th>Tổng tiền</th>
                        <th>Trạng thái chỉ số</th>
                        <th>Thao tác</th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:forEach var="item" items="${incorrectInvoices}">
                        <tr>
                          <td><c:out value="${item.facilityName}"/> (<c:out value="${item.facilityCode}"/>)</td>
                          <td>
                            <a href="${ctx}/manager/rooms/${item.id}">
                              <strong><c:out value="${item.roomCode}"/></strong>
                            </a>
                          </td>
                          <td><c:out value="${item.billingPeriod}"/></td>
                          <td>
                            <div>Điện: <strong><c:out value="${item.electric}"/></strong> kWh</div>
                            <div>Nước: <strong><c:out value="${item.water}"/></strong> m³</div>
                          </td>
                          <td style="font-weight:600">
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
                          <td>
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
          <c:otherwise>
            <%-- Tab Thông báo chung (Gộp cả gửi đến tôi và gửi tới cư dân) --%>
            
            <%-- Sub-tabs/Pills for General tab --%>
            <ul class="nav nav-pills mb-3" id="notificationSubTabs" role="tablist" style="gap:8px">
              <li class="nav-item" role="presentation">
                <a class="nav-link ${type == 'received' ? 'active' : ''}" 
                   href="${ctx}/manager/notifications?tab=general&type=received"
                   style="font-weight: 600; font-size: 0.8125rem; padding: 8px 16px; border-radius: 6px; 
                          transition: all 0.2s;
                          ${type == 'received' ? 'background: var(--hms-accent-deep); color: #fff;' : 'background: var(--hms-surface); color: var(--hms-text-muted); border: 1px solid var(--hms-border);'}">
                  Gửi đến tôi (Từ Admin)
                </a>
              </li>
              <li class="nav-item" role="presentation">
                <a class="nav-link ${type == 'sent' ? 'active' : ''}" 
                   href="${ctx}/manager/notifications?tab=general&type=sent"
                   style="font-weight: 600; font-size: 0.8125rem; padding: 8px 16px; border-radius: 6px; 
                          transition: all 0.2s;
                          ${type == 'sent' ? 'background: var(--hms-accent-deep); color: #fff;' : 'background: var(--hms-surface); color: var(--hms-text-muted); border: 1px solid var(--hms-border);'}">
                  Gửi tới cư dân
                </a>
              </li>
            </ul>

            <c:choose>
              <c:when test="${type == 'received'}">
                <%-- Sub-tab Gửi đến tôi (Nhận từ Admin) --%>
                <form class="filter-bar mb-3" method="get" action="${ctx}/manager/notifications">
                  <input type="hidden" name="tab" value="general"/>
                  <input type="hidden" name="type" value="received"/>
                  <input type="text" class="form-control" name="keyword"
                         placeholder="Tiêu đề / nội dung..."
                         value="<c:out value='${keyword}'/>" style="max-width: 320px;">
                  <button type="submit" class="btn-mintlify-secondary">Tìm kiếm</button>
                  <a href="${ctx}/manager/notifications?tab=general&type=received" class="btn-mintlify-secondary text-decoration-none">Xóa bộ lọc</a>
                </form>

                <c:choose>
                  <c:when test="${not empty page.items}">
                    <div class="table-responsive">
                      <table class="table-mintlify">
                        <thead>
                          <tr>
                            <th>Mã</th>
                            <th>Tiêu đề</th>
                            <th>Người gửi</th>
                            <th>Ngày gửi</th>
                            <th>Thao tác</th>
                          </tr>
                        </thead>
                        <tbody>
                          <c:forEach var="notif" items="${page.items}">
                            <tr>
                              <td>
                                <a href="${ctx}/manager/notifications/${notif.id}">
                                  <c:out value="${notif.code}"/>
                                </a>
                              </td>
                              <td style="max-width:320px; font-weight: 500;"><c:out value="${notif.title}"/></td>
                              <td>
                                <c:choose>
                                  <c:when test="${notif.creatorRole == 'ADMIN'}">
                                    <span class="badge-hms badge-danger">Admin</span>
                                  </c:when>
                                  <c:otherwise>
                                    <c:out value="${notif.createdByName}"/>
                                  </c:otherwise>
                                </c:choose>
                              </td>
                              <td style="font-size:0.8125rem;color:var(--hms-text-muted)">
                                <c:out value="${notif.createdAt}"/>
                              </td>
                              <td>
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
                <%-- Sub-tab Gửi tới cư dân --%>
                <div style="background:var(--hms-accent-bg);border:1px solid var(--hms-border-accent);
                            border-radius:var(--hms-radius);padding:0.75rem 1rem;
                            font-size:0.8125rem;color:var(--hms-ink);margin-bottom:1rem">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none"
                       stroke="var(--hms-accent-deep)" stroke-width="2"
                       style="margin-right:6px;vertical-align:-2px">
                    <circle cx="12" cy="12" r="10"/>
                    <line x1="12" y1="8" x2="12" y2="12"/>
                    <line x1="12" y1="16" x2="12.01" y2="16"/>
                  </svg>
                  Manager chỉ gửi thông báo trong phạm vi <strong>cơ sở được phân công</strong>.
                  Không thể gửi thông báo toàn hệ thống.
                </div>

                <form class="filter-bar mb-3" method="get" action="${ctx}/manager/notifications">
                  <input type="hidden" name="tab" value="general"/>
                  <input type="hidden" name="type" value="sent"/>
                  <select class="form-select" name="facilityId" style="max-width:240px">
                    <option value="">Tất cả cơ sở</option>
                    <c:forEach var="facility" items="${assignedFacilities}">
                      <option value="${facility.id}" ${filterFacilityId == facility.id ? 'selected' : ''}>
                        <c:out value="${facility.name}"/> (<c:out value="${facility.code}"/>)
                      </option>
                    </c:forEach>
                  </select>
                  <input type="text" class="form-control" name="keyword"
                         placeholder="Tiêu đề / nội dung..."
                         value="<c:out value='${keyword}'/>" style="max-width: 320px;">
                  <button type="submit" class="btn-mintlify-secondary">Tìm kiếm</button>
                  <a href="${ctx}/manager/notifications?tab=general&type=sent" class="btn-mintlify-secondary text-decoration-none">Xóa bộ lọc</a>
                </form>

                <c:choose>
                  <c:when test="${not empty page.items}">
                    <div class="table-responsive">
                      <table class="table-mintlify">
                        <thead>
                          <tr>
                            <th>Mã</th>
                            <th>Tiêu đề</th>
                            <th>Đối tượng</th>
                            <th>Người tạo</th>
                            <th>Trạng thái</th>
                            <th>Ngày tạo</th>
                            <th>Thao tác</th>
                          </tr>
                        </thead>
                        <tbody>
                          <c:forEach var="notif" items="${page.items}">
                            <tr>
                              <td>
                                <a href="${ctx}/manager/notifications/${notif.id}">
                                  <c:out value="${notif.code}"/>
                                </a>
                              </td>
                              <td style="max-width:280px"><c:out value="${notif.title}"/></td>
                              <td>
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
                              <td><c:out value="${notif.createdByName}"/></td>
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
                              <td style="font-size:0.8125rem;color:var(--hms-text-muted)">
                                <c:out value="${notif.createdAt}"/>
                              </td>
                              <td>
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
      </div>

    </main>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
