<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết phòng - BQL"/>
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

      <%-- Breadcrumb + Header --%>
      <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3" style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
        <div>
          <%-- Breadcrumb --%>
          <div style="font-size:0.8125rem;color:var(--hms-text-muted);margin-bottom:6px">
            <a href="${ctx}/manager/rooms?showGrid=true" style="color:var(--hms-text-muted);text-decoration:none">
              Căn hộ / Phòng
            </a>
            <span style="margin:0 6px">›</span>
            <a href="${ctx}/manager/facilities/${room.facilityId}/rooms"
               style="color:var(--hms-text-muted);text-decoration:none">
              <c:out value="${room.facilityName}"/>
            </a>
            <span style="margin:0 6px">›</span>
            <strong style="color:var(--hms-ink)"><c:out value="${room.code}"/></strong>
          </div>
          <h1>Phòng <c:out value="${room.code}"/></h1>
          <div class="d-flex align-items-center gap-2 mt-1">
            <%-- Trạng thái badge --%>
            <c:choose>
              <c:when test="${room.status == 'OCCUPIED'}">
                <span class="badge-hms badge-info">Đang thuê</span>
              </c:when>
              <c:otherwise>
                <span class="badge-hms badge-success">Trống</span>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
        <a href="${ctx}/manager/facilities/${room.facilityId}/rooms"
           class="btn-mintlify-secondary text-decoration-none">
          ← Danh sách phòng
        </a>
      </div>

      <div class="row g-3">

        <%-- ── Cột trái: Thông tin phòng ──────────────────────────── --%>
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
                    <a href="${ctx}/manager/facilities/${room.facilityId}/rooms">
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
                      <c:when test="${not empty room.area}">
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
                      <c:otherwise>
                        <span class="badge-hms badge-success">Trống</span>
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

          <%-- Note: chỉ Admin sửa được --%>
          <div style="background:var(--hms-surface-2);border:1px solid var(--hms-border);
                      border-radius:var(--hms-radius);padding:0.75rem 1rem;
                      font-size:0.8125rem;color:var(--hms-text-muted)">
            <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                 style="margin-right:5px;vertical-align:-2px">
              <circle cx="12" cy="12" r="10"/>
              <line x1="12" y1="8" x2="12" y2="12"/>
              <line x1="12" y1="16" x2="12.01" y2="16"/>
            </svg>
            Chỉ <strong>Admin</strong> mới có thể thay đổi thông tin phòng (diện tích, trạng thái).
          </div>

        </div>

        <%-- ── Cột phải: Người thuê hiện tại ─────────────────────── --%>
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

                <%-- Có người thuê → Clickable card theo Manager.md §5 --%>
                <c:when test="${not empty room.tenantId}">
                  <div style="border:1px solid var(--hms-border-accent);
                              border-radius:var(--hms-radius);
                              background:var(--hms-accent-bg);
                              padding:1.25rem">
                    <div class="d-flex align-items-start justify-content-between gap-3 flex-wrap">
                      <div>
                        <%-- Tên PHẢI là hyperlink theo spec --%>
                        <div style="font-size:1.125rem;font-weight:700;margin-bottom:4px">
                          <a href="${ctx}/manager/tenants/${room.tenantId}"
                             style="color:var(--hms-accent-deep);text-decoration:none">
                            <c:out value="${room.tenantName}"/>
                          </a>
                        </div>
                        <c:if test="${not empty room.tenantCode}">
                          <div style="font-size:0.8125rem;color:var(--hms-text-muted);margin-bottom:4px">
                            Mã: <strong><c:out value="${room.tenantCode}"/></strong>
                          </div>
                        </c:if>
                        <c:if test="${not empty room.tenantPhone}">
                          <div style="font-size:0.875rem;color:var(--hms-ink)">
                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                 stroke-width="2" style="vertical-align:-1px;margin-right:3px">
                              <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.07 14a19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 2.98 3h3a2 2 0 0 1 2 1.72c.127.96.361 1.903.7 2.81a2 2 0 0 1-.45 2.11L7.09 10.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45c.907.339 1.85.573 2.81.7A2 2 0 0 1 21 18z"/>
                            </svg>
                            <c:out value="${room.tenantPhone}"/>
                          </div>
                        </c:if>
                        <div style="margin-top:10px">
                          <span class="badge-hms badge-success">Đang thuê</span>
                        </div>
                      </div>

                      <%-- Button "Xem hồ sơ" bắt buộc theo Manager.md §5 --%>
                      <a href="${ctx}/manager/tenants/${room.tenantId}"
                         class="quick-action-btn primary"
                         style="white-space:nowrap;align-self:flex-start">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                             stroke-width="2" style="margin-right:5px">
                          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                          <circle cx="12" cy="7" r="4"/>
                        </svg>
                        Xem hồ sơ người thuê
                      </a>
                    </div>
                  </div>

                  <%-- Quick links --%>
                  <div class="d-flex gap-2 mt-3 flex-wrap">
                    <a href="${ctx}/manager/tenants/${room.tenantId}"
                       class="btn-mintlify-secondary text-decoration-none"
                       style="padding:6px 14px;font-size:0.8125rem">
                      Thông tin chi tiết
                    </a>
                    <a href="${ctx}/manager/contracts?tenantId=${room.tenantId}"
                       class="btn-mintlify-secondary text-decoration-none"
                       style="padding:6px 14px;font-size:0.8125rem">
                      Xem hợp đồng
                    </a>
                  </div>
                </c:when>

                <c:otherwise>
                  <div class="text-center py-4">
                    <c:choose>
                      <c:when test="${room.status == 'OCCUPIED' && not empty activeContractId}">
                        <svg width="52" height="52" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)"
                             stroke-width="1.2" style="margin-bottom:12px">
                          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                          <polyline points="14 2 14 8 20 8"></polyline>
                        </svg>
                        <div>
                          <span class="badge-hms badge-warning" style="font-size:0.9375rem;padding:8px 16px">
                            Đang chờ tạo tài khoản người thuê
                          </span>
                        </div>
                        <p class="text-muted mt-3 mb-3" style="font-size:0.875rem">
                          Hợp đồng phòng này đã được ký nhưng chưa được tạo tài khoản người thuê đại diện.
                        </p>
                        <a href="${ctx}/manager/contracts/detail?id=${activeContractId}"
                           class="quick-action-btn primary" style="display:inline-flex">
                          Xem chi tiết Hợp đồng để tạo tài khoản
                        </a>
                      </c:when>
                      <c:otherwise>
                        <svg width="52" height="52" viewBox="0 0 24 24" fill="none" stroke="var(--hms-text-muted)"
                             stroke-width="1.2" style="margin-bottom:12px">
                          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                          <circle cx="12" cy="7" r="4"/>
                          <line x1="4" y1="4" x2="20" y2="20" stroke="var(--hms-text-muted)" stroke-width="1.5"/>
                        </svg>
                        <div>
                          <span class="badge-hms badge-neutral" style="font-size:0.9375rem;padding:8px 16px">
                            Phòng đang trống
                          </span>
                        </div>
                        <p class="text-muted mt-3 mb-3" style="font-size:0.875rem">
                          Hiện chưa có người thuê nào trong phòng này.
                        </p>
                        <a href="${ctx}/manager/contracts/create?roomId=${room.id}"
                           class="quick-action-btn primary" style="display:inline-flex">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                               stroke-width="2" style="margin-right:5px">
                            <line x1="12" y1="5" x2="12" y2="19"/>
                            <line x1="5" y1="12" x2="19" y2="12"/>
                          </svg>
                          Tạo hợp đồng thuê phòng này
                        </a>
                      </c:otherwise>
                    </c:choose>
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
