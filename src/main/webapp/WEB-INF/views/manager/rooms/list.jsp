<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Căn hộ / Phòng - BQL"/>
<c:set var="pageRole" value="MANAGER"/>
<c:set var="activeMenu" value="rooms"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
  <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
  <div class="sidebar-overlay"></div>
  <div class="main-wrapper">
    <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
    <main class="page-content">
      <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

      <%-- ================================================================
           CHẾ ĐỘ 1: Chưa chọn cơ sở → hiển thị grid cơ sở
           Điều kiện: facilityId không được set bởi servlet
           ================================================================ --%>
      <c:choose>
        <c:when test="${empty facilityId}">

          <div class="page-header d-flex flex-wrap justify-content-between align-items-start gap-3">
            <div>
              <h1>Căn hộ / Phòng</h1>
              <p>Chọn cơ sở để xem danh sách phòng</p>
            </div>
          </div>

          <c:choose>
            <c:when test="${not empty facilities}">
              <div class="row g-3">
                <c:forEach var="f" items="${facilities}">
                  <div class="col-sm-6 col-lg-4">
                    <div class="widget-surface" style="height:100%">
                      <div class="widget-surface-header">
                        <h3 style="font-size:1rem">
                          <c:out value="${f.code}"/>
                        </h3>
                        <c:choose>
                          <c:when test="${f.status == 'ACTIVE'}">
                            <span class="badge-hms badge-success" style="font-size:0.75rem">Hoạt động</span>
                          </c:when>
                          <c:otherwise>
                            <span class="badge-hms badge-neutral" style="font-size:0.75rem">
                              <c:out value="${f.status}"/>
                            </span>
                          </c:otherwise>
                        </c:choose>
                      </div>
                      <div class="widget-surface-body">
                        <p style="font-weight:600;font-size:0.9375rem;margin-bottom:4px;color:var(--hms-ink)">
                          <c:out value="${f.name}"/>
                        </p>
                        <p style="font-size:0.8125rem;color:var(--hms-text-muted);margin-bottom:10px">
                          <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                               style="vertical-align:-1px;margin-right:3px">
                            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                            <circle cx="12" cy="10" r="3"/>
                          </svg>
                          <c:out value="${f.address}"/>
                        </p>
                        <div class="d-flex align-items-center gap-3 mb-3">
                          <div style="text-align:center">
                            <div style="font-size:1.25rem;font-weight:700;color:var(--hms-ink)">
                              <c:out value="${f.totalRooms}"/>
                            </div>
                            <div style="font-size:0.75rem;color:var(--hms-text-muted)">Tổng phòng</div>
                          </div>
                          <div style="text-align:center">
                            <div style="font-size:1.25rem;font-weight:700;color:var(--hms-ink)">
                              <c:out value="${f.floorCount}"/>
                            </div>
                            <div style="font-size:0.75rem;color:var(--hms-text-muted)">Tầng</div>
                          </div>
                        </div>
                        <a href="${ctx}/manager/facilities/${f.id}/rooms"
                           class="quick-action-btn primary"
                           style="display:inline-flex;align-items:center;gap:6px;width:100%;justify-content:center">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                            <polyline points="9 22 9 12 15 12 15 22"/>
                          </svg>
                          Xem danh sách phòng
                        </a>
                      </div>
                    </div>
                  </div>
                </c:forEach>
              </div>
            </c:when>
            <c:otherwise>
              <div class="empty-state p-5 text-center">
                <svg width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)"
                     stroke-width="1.2" style="margin-bottom:16px">
                  <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                  <polyline points="9 22 9 12 15 12 15 22"/>
                </svg>
                <h4>Chưa được phân công cơ sở nào</h4>
                <p class="text-muted" style="max-width:360px;margin:0 auto">
                  Liên hệ Admin để được phân công cơ sở quản lý.
                </p>
              </div>
            </c:otherwise>
          </c:choose>

        </c:when>

        <%-- ================================================================
             CHẾ ĐỘ 2: Đã chọn cơ sở → hiển thị danh sách phòng
             Điều kiện: facilityId được set bởi servlet từ /manager/facilities/{id}/rooms
             ================================================================ --%>
        <c:otherwise>

          <div class="page-header d-flex flex-wrap justify-content-between align-items-start gap-3">
            <div>
              <div class="d-flex align-items-center gap-2 mb-1">
                <span style="font-size:0.875rem;color:var(--hms-text-muted)">Cơ sở:</span>
                <c:choose>
                  <c:when test="${facilities.size() > 1}">
                    <select class="form-select form-select-sm" style="width: auto; font-size: 0.875rem; padding-top: 2px; padding-bottom: 2px;" onchange="window.location.href='${ctx}/manager/facilities/' + this.value + '/rooms'">
                      <c:forEach var="f" items="${facilities}">
                        <option value="${f.id}" ${f.id == currentFacility.id ? 'selected' : ''}>
                          <c:out value="${f.name}"/> (<c:out value="${f.code}"/>)
                        </option>
                      </c:forEach>
                    </select>
                  </c:when>
                  <c:otherwise>
                    <span style="font-size:0.875rem;color:var(--hms-ink);font-weight:500">
                      <c:out value="${currentFacility.name}"/>
                    </span>
                  </c:otherwise>
                </c:choose>
              </div>
              <h1>Danh sách phòng</h1>
              <p>
                Địa chỉ: <c:out value="${currentFacility.address}"/>
              </p>
            </div>
          </div>

          <div class="data-surface">
            <%-- Bộ lọc trạng thái --%>
            <form class="filter-bar" method="get" action="${ctx}/manager/facilities/${facilityId}/rooms">
              <select class="form-select" name="status" style="max-width:180px">
                <option value="">Tất cả trạng thái</option>
                <option value="AVAILABLE"   ${filterStatus == 'AVAILABLE'   ? 'selected' : ''}>Phòng trống</option>
                <option value="OCCUPIED"    ${filterStatus == 'OCCUPIED'    ? 'selected' : ''}>Đang thuê</option>
              </select>
              <button type="submit" class="btn-mintlify-secondary">Lọc</button>
              <a href="${ctx}/manager/facilities/${facilityId}/rooms"
                 class="btn-mintlify-secondary text-decoration-none">Xóa bộ lọc</a>
            </form>

            <c:choose>
              <c:when test="${not empty page.items}">
                <div class="table-responsive">
                  <table class="table-mintlify">
                    <thead>
                      <tr>
                        <th>Mã phòng</th>
                        <th>Tầng</th>
                        <th>Số phòng</th>
                        <th>Diện tích</th>
                        <th>Trạng thái</th>
                        <th>Chủ thuê</th>
                        <th>Thao tác</th>
                      </tr>
                    </thead>
                    <tbody>
                      <c:forEach var="room" items="${page.items}">
                        <tr data-href="${ctx}/manager/rooms/${room.id}">
                          <td>
                            <a href="${ctx}/manager/rooms/${room.id}" style="font-weight:600">
                              <c:out value="${room.code}"/>
                            </a>
                          </td>
                          <td><c:out value="${room.floor}"/></td>
                          <td><c:out value="${room.roomNumber}"/></td>
                          <td>
                            <c:choose>
                              <c:when test="${not empty room.area}">
                                <fmt:formatNumber value="${room.area}" maxFractionDigits="1"/> m²
                              </c:when>
                              <c:otherwise><span class="text-muted">—</span></c:otherwise>
                            </c:choose>
                          </td>
                          <td>
                            <c:choose>
                              <c:when test="${room.status == 'OCCUPIED'}">
                                <span class="badge-hms badge-info">Đang thuê</span>
                              </c:when>
                              <c:otherwise>
                                <span class="badge-hms badge-success">Trống</span>
                              </c:otherwise>
                            </c:choose>
                          </td>
                          <td>
                            <c:choose>
                              <c:when test="${not empty room.tenantId}">
                                <%-- Hyperlink bắt buộc theo Manager.md §5 --%>
                                <a href="${ctx}/manager/tenants/${room.tenantId}"
                                   style="font-weight:500">
                                  <c:out value="${room.tenantName}"/>
                                </a>
                              </c:when>
                              <c:otherwise>
                                <span class="text-muted" style="font-size:0.8125rem">Chưa có</span>
                              </c:otherwise>
                            </c:choose>
                          </td>
                          <td>
                            <a href="${ctx}/manager/rooms/${room.id}"
                               class="btn-mintlify-secondary text-decoration-none"
                               style="padding:4px 12px;font-size:0.8125rem">Xem</a>
                          </td>
                        </tr>
                      </c:forEach>
                    </tbody>
                  </table>
                </div>

                <%-- Phân trang --%>
                <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2">
                  <span class="text-muted" style="font-size:0.875rem">
                    Tổng <fmt:formatNumber value="${page.total}" groupingUsed="true"/> phòng
                    · Trang ${page.page} / ${page.totalPages}
                  </span>
                  <div class="d-flex gap-1">
                    <c:if test="${page.page > 1}">
                      <a href="${ctx}/manager/facilities/${facilityId}/rooms?page=${page.page - 1}&status=${filterStatus}"
                         class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                    </c:if>
                    <c:if test="${page.page < page.totalPages}">
                      <a href="${ctx}/manager/facilities/${facilityId}/rooms?page=${page.page + 1}&status=${filterStatus}"
                         class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                    </c:if>
                  </div>
                </div>

              </c:when>
              <c:otherwise>
                <div class="empty-state p-4 text-center">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)"
                       stroke-width="1.5" style="margin-bottom:12px">
                    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
                    <polyline points="9 22 9 12 15 12 15 22"/>
                  </svg>
                  <h4>Không có phòng nào</h4>
                  <p class="text-muted">
                    <c:choose>
                      <c:when test="${not empty filterStatus}">
                        Không tìm thấy phòng với trạng thái đã chọn.
                      </c:when>
                      <c:otherwise>Cơ sở này chưa có phòng nào được sinh.</c:otherwise>
                    </c:choose>
                  </p>
                </div>
              </c:otherwise>
            </c:choose>
          </div>

        </c:otherwise>
      </c:choose>

    </main>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
