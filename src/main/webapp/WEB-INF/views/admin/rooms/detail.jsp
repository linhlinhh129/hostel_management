<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx"       value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết phòng - Admin"/>
<c:set var="pageRole"  value="ADMIN"/>
<c:set var="activeMenu" value="hostels"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
  <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
  <div class="sidebar-overlay"></div>
  <div class="main-wrapper">
    <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
    <main class="page-content">
      <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

      <%-- ── Breadcrumb + Header ─────────────────────────────────── --%>
      <div class="page-header d-flex flex-wrap justify-content-between align-items-start gap-3">
        <div>

          <h1>Phòng <c:out value="${room.code}"/></h1>

          <div class="d-flex align-items-center gap-2 mt-1">
            <c:choose>
              <c:when test="${room.status == 'OCCUPIED'}">
                <span class="badge-hms badge-info">Đang thuê</span>
              </c:when>
              <c:when test="${room.status == 'AVAILABLE' or room.status == 'ACTIVE'}">
                <span class="badge-hms badge-success">Phòng trống</span>
              </c:when>
              <c:when test="${room.status == 'MAINTENANCE'}">
                <span class="badge-hms badge-warning">Bảo trì</span>
              </c:when>
              <c:otherwise>
                <span class="badge-hms badge-neutral"><c:out value="${room.status}"/></span>
              </c:otherwise>
            </c:choose>
          </div>
        </div>

        <a href="${ctx}/admin/facilities/${room.facilityId}"
           class="btn-mintlify-secondary text-decoration-none">
          ← Danh sách phòng
        </a>
      </div>

      <div class="row g-3">

        <%-- ── Cột trái: Thông tin phòng + sửa diện tích ──────────── --%>
        <div class="col-lg-5">

          <%-- Thông tin căn hộ --%>
          <div class="widget-surface mb-3">
            <div class="widget-surface-header"><h3>Thông tin căn hộ</h3></div>
            <div class="widget-surface-body p-0">
              <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:10px 16px;color:var(--hms-text-muted);width:44%;white-space:nowrap">Mã phòng</td>
                  <td style="padding:10px 16px;font-weight:600"><c:out value="${room.code}"/></td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:10px 16px;color:var(--hms-text-muted)">Cơ sở</td>
                  <td style="padding:10px 16px">
                    <a href="${ctx}/admin/facilities/${room.facilityId}">
                      <c:out value="${room.facilityCode}"/> — <c:out value="${room.facilityName}"/>
                    </a>
                  </td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:10px 16px;color:var(--hms-text-muted)">Tầng</td>
                  <td style="padding:10px 16px"><c:out value="${room.floor}"/></td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:10px 16px;color:var(--hms-text-muted)">Số phòng</td>
                  <td style="padding:10px 16px"><c:out value="${room.roomNumber}"/></td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:10px 16px;color:var(--hms-text-muted)">Diện tích</td>
                  <td style="padding:10px 16px">
                    <c:choose>
                      <c:when test="${not empty room.areaRaw}">
                        <strong><fmt:formatNumber value="${room.area}" maxFractionDigits="1"/></strong> m²
                      </c:when>
                      <c:otherwise><em class="text-muted">Chưa cập nhật</em></c:otherwise>
                    </c:choose>
                  </td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:10px 16px;color:var(--hms-text-muted)">Trạng thái</td>
                  <td style="padding:10px 16px">
                    <c:choose>
                      <c:when test="${room.status == 'OCCUPIED'}">
                        <span class="badge-hms badge-info">Đang thuê</span>
                      </c:when>
                      <c:when test="${room.status == 'AVAILABLE' or room.status == 'ACTIVE'}">
                        <span class="badge-hms badge-success">Trống</span>
                      </c:when>
                      <c:when test="${room.status == 'MAINTENANCE'}">
                        <span class="badge-hms badge-warning">Bảo trì</span>
                      </c:when>
                      <c:otherwise>
                        <span class="badge-hms badge-neutral"><c:out value="${room.status}"/></span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                </tr>
                <tr style="border-bottom:1px solid var(--hms-border)">
                  <td style="padding:10px 16px;color:var(--hms-text-muted)">Ngày tạo</td>
                  <td style="padding:10px 16px;font-size:0.8125rem"><c:out value="${room.createdAt}"/></td>
                </tr>
                <tr>
                  <td style="padding:10px 16px;color:var(--hms-text-muted)">Cập nhật lúc</td>
                  <td style="padding:10px 16px;font-size:0.8125rem"><c:out value="${room.updatedAt}"/></td>
                </tr>
              </table>
            </div>
          </div>

          <%-- Sửa diện tích (chỉ admin) --%>
          <div class="widget-surface">
            <div class="widget-surface-header"><h3>Chỉnh sửa diện tích</h3></div>
            <div class="widget-surface-body">
              <form method="post"
                    action="${ctx}/admin/facilities/${room.facilityId}/rooms/${room.id}/area"
                    class="d-flex gap-2 align-items-center">
                <input type="hidden" name="csrfToken" value="${csrfToken}"/>
                <input type="number"
                       name="area"
                       class="form-control"
                       style="max-width:160px"
                       step="0.1" min="0" max="9999"
                       placeholder="VD: 25.5"
                       value="${not empty room.areaRaw ? room.area : ''}"/>
                <button type="submit" class="btn btn-mintlify-primary" style="width:auto">
                  Lưu diện tích
                </button>
              </form>
              <div class="form-text text-muted mt-2" style="font-size:0.8rem">
                Đơn vị: m². Để trống để xóa giá trị diện tích.
              </div>
            </div>
          </div>

        </div>

        <%-- ── Cột phải: Người thuê (chỉ xem) ─────────────────────── --%>
        <div class="col-lg-7">
          <div class="widget-surface">
            <div class="widget-surface-header">
              <h3>Người thuê hiện tại</h3>
              <c:if test="${not empty room.tenantId}">
                <span class="badge-hms badge-success">Có người thuê</span>
              </c:if>
            </div>
            <div class="widget-surface-body">
              <c:choose>

                <%-- Có người thuê → chỉ hiển thị thông tin, không có nút thao tác --%>
                <c:when test="${not empty room.tenantId}">
                  <div style="border:1px solid var(--hms-border-accent);
                              border-radius:var(--hms-radius);
                              background:var(--hms-accent-bg);
                              padding:1.25rem">
                    <div class="d-flex align-items-start gap-3 flex-wrap">

                      <%-- Avatar placeholder --%>
                      <div style="width:48px;height:48px;border-radius:50%;
                                  background:var(--hms-surface-2);
                                  display:flex;align-items:center;justify-content:center;
                                  flex-shrink:0;border:1px solid var(--hms-border)">
                        <svg width="22" height="22" viewBox="0 0 24 24" fill="none"
                             stroke="var(--hms-text-muted)" stroke-width="1.5">
                          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                          <circle cx="12" cy="7" r="4"/>
                        </svg>
                      </div>

                      <div style="flex:1;min-width:0">
                        <div style="font-size:1.0625rem;font-weight:700;margin-bottom:4px;
                                    color:var(--hms-ink)">
                          <c:out value="${room.tenantName}"/>
                        </div>

                        <table style="font-size:0.8125rem;border-collapse:collapse;width:100%">
                          <c:if test="${not empty room.tenantCode}">
                            <tr>
                              <td style="padding:3px 0;color:var(--hms-text-muted);width:36%">Mã tài khoản</td>
                              <td style="padding:3px 0;font-weight:500">
                                <c:out value="${room.tenantCode}"/>
                              </td>
                            </tr>
                          </c:if>
                          <c:if test="${not empty room.tenantPhone}">
                            <tr>
                              <td style="padding:3px 0;color:var(--hms-text-muted)">Số điện thoại</td>
                              <td style="padding:3px 0"><c:out value="${room.tenantPhone}"/></td>
                            </tr>
                          </c:if>
                          <c:if test="${not empty room.tenantEmail}">
                            <tr>
                              <td style="padding:3px 0;color:var(--hms-text-muted)">Email</td>
                              <td style="padding:3px 0"><c:out value="${room.tenantEmail}"/></td>
                            </tr>
                          </c:if>
                        </table>

                        <div class="mt-2">
                          <span class="badge-hms badge-success">Đang thuê</span>
                        </div>
                      </div>
                    </div>
                  </div>

                  <%-- Note: admin chỉ xem, không thao tác --%>
                  <div style="margin-top:12px;padding:10px 14px;
                              background:var(--hms-surface-2);
                              border:1px solid var(--hms-border);
                              border-radius:var(--hms-radius);
                              font-size:0.8125rem;color:var(--hms-text-muted)">
                    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                         stroke-width="2" style="margin-right:5px;vertical-align:-2px">
                      <circle cx="12" cy="12" r="10"/>
                      <line x1="12" y1="8"  x2="12"    y2="12"/>
                      <line x1="12" y1="16" x2="12.01" y2="16"/>
                    </svg>
                    Admin chỉ có quyền xem thông tin người thuê.
                    Việc thêm / xóa người thuê do <strong>Manager</strong> quản lý.
                  </div>
                </c:when>

                <%-- Phòng trống --%>
                <c:otherwise>
                  <div class="text-center py-4">
                    <svg width="52" height="52" viewBox="0 0 24 24" fill="none"
                         stroke="var(--hms-text-muted)" stroke-width="1.2"
                         style="margin-bottom:12px">
                      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                      <circle cx="12" cy="7" r="4"/>
                      <line x1="4" y1="4" x2="20" y2="20"
                            stroke="var(--hms-text-muted)" stroke-width="1.5"/>
                    </svg>
                    <div>
                      <span class="badge-hms badge-neutral"
                            style="font-size:0.9375rem;padding:8px 16px">
                        Phòng đang trống
                      </span>
                    </div>
                    <p class="text-muted mt-3 mb-0" style="font-size:0.875rem">
                      Hiện chưa có người thuê nào trong phòng này.
                    </p>
                  </div>
                </c:otherwise>

              </c:choose>
            </div>
          </div>
        </div>

      </div>
    </main>
  </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
