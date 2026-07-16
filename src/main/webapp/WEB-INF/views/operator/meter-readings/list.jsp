<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chỉ số điện nước"/>
<c:set var="pageRole" value="OPERATOR"/>
<c:set var="activeMenu" value="meter-readings"/>
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
                <h1>Chỉ số điện nước</h1>
                <p>Kỳ <c:out value="${billingPeriodLabel}"/></p>
            </div>

            <div class="data-surface">
                <%-- Filter bar đồng bộ với admin --%>
                <form method="get" action="${ctx}/operator/meter-readings"
                      style="background:#fff; border:1px solid var(--hms-border-soft); border-radius:8px; padding:20px; margin-bottom:20px; box-shadow:0 1px 3px rgba(0,0,0,0.02)">
                    <div style="display:flex; flex-wrap:wrap; gap:20px; margin-bottom:20px;">
                        <div style="flex:2; min-width:200px;">
                            <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Mã phòng</label>
                            <input type="text" class="form-control" name="roomCode"
                                   placeholder="Nhập mã phòng..."
                                   value="<c:out value='${filterRoomCode}'/>" style="width:100%">
                        </div>
                        <div style="flex:1; min-width:150px;">
                            <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Trạng thái</label>
                            <select class="form-select" name="status" style="width:100%">
                                <option value="">Tất cả</option>
                                <option value="PENDING" ${filterStatus == 'PENDING' ? 'selected' : ''}>Chưa cập nhật</option>
                                <option value="DONE"    ${filterStatus == 'DONE'    ? 'selected' : ''}>Đã cập nhật</option>
                            </select>
                        </div>
                        <%-- Hiển thị cơ sở chỉ đọc --%>
                        <c:if test="${not empty facilities and facilities.size() > 0}">
                            <input type="hidden" name="facility" value="${facilities[0].name} (${facilities[0].code})"/>
                            <div style="flex:1; min-width:150px;">
                                <label style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Cơ sở</label>
                                <div class="form-control" style="background:var(--hms-bg-soft); color:var(--hms-stone); pointer-events:none;">
                                    <c:out value="${facilities[0].name} (${facilities[0].code})"/>
                                </div>
                            </div>
                        </c:if>
                    </div>
                    <div style="display:flex; justify-content:flex-end; gap:12px; border-top:1px dashed var(--hms-border-soft); padding-top:16px;">
                        <a href="${ctx}/operator/meter-readings"
                           style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); text-decoration:none; font-size:14px; font-weight:500;">Xóa bộ lọc</a>
                        <button type="submit"
                                style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); font-size:14px; font-weight:500; cursor:pointer;">Tìm kiếm</button>
                    </div>
                </form>

                <c:choose>
                    <c:when test="${not empty readings}">
                        <div class="table-responsive">
                            <table class="table-mintlify">
                                <thead>
                                    <tr>
                                        <th>Phòng</th>
                                        <th>Điện kỳ trước</th>
                                        <th>Nước kỳ trước</th>
                                        <th>Trạng thái</th>
                                        <th class="d-none d-md-table-cell">Cập nhật lúc</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="r" items="${readings}">
                                        <tr data-href="${ctx}/operator/meter-readings/${r.roomId}/edit">
                                            <td style="font-weight:500; color:var(--hms-ink)"><c:out value="${r.roomCode}"/></td>
                                            <td><c:out value="${r.previousElectric}"/> kWh</td>
                                            <td><c:out value="${r.previousWater}"/> m³</td>
                                            <td>
                                                <span class="badge-hms <c:out value='${r.statusBadgeClass}'/>">
                                                    <c:out value="${r.statusLabel}"/>
                                                </span>
                                            </td>
                                            <td class="d-none d-md-table-cell" style="font-size:0.8125rem; color:var(--hms-stone)">
                                                <c:out value="${r.updatedAtLabel}"/>
                                            </td>
                                            <td>
                                                <%-- Dùng btn-mintlify-secondary thống nhất cho tất cả thao tác --%>
                                                <a href="${ctx}/operator/meter-readings/${r.roomId}/edit"
                                                   class="btn-mintlify-secondary text-decoration-none"
                                                   style="padding:4px 12px; font-size:12px;">Cập nhật</a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state p-5 text-center">
                            <h4>Tất cả phòng đã được cập nhật</h4>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
