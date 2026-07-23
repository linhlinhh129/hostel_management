<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
            <c:set var="ctx" value="${pageContext.request.contextPath}" />
            <c:set var="pageTitle" value="Danh sách điện nước" />
            <c:set var="pageRole" value="OPERATOR" />
            <c:set var="activeMenu" value="meter-readings" />
            <jsp:include page="/WEB-INF/views/layout/head.jsp" />

            <body>
                <div class="app-shell">
                    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
                    <div class="sidebar-overlay"></div>
                    <div class="main-wrapper">
                        <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
                        <main class="page-content">
                            <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

                            <div class="page-header hero-sky-gradient"
                                style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                                <h1>Danh sách điện nước</h1>
                            </div>

                            <div class="data-surface">
                                <%-- Filter bar đồng bộ với admin --%>
                                    <form method="get" action="${ctx}/operator/meter-readings"
                                        style="background:#fff; border:1px solid var(--hms-border-soft); border-radius:8px; padding:20px; margin-bottom:20px; box-shadow:0 1px 3px rgba(0,0,0,0.02)">
                                        <div style="display:flex; flex-wrap:wrap; gap:20px; margin-bottom:20px;">
                                            <div style="flex:2; min-width:200px;">
                                                <label
                                                    style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Mã
                                                    phòng</label>
                                                <input type="text" class="form-control" name="roomCode"
                                                    placeholder="Nhập mã phòng..."
                                                    value="<c:out value='${searchRoomCode}'/>" style="width:100%">
                                            </div>
                                            <c:if test="${not empty facilities and facilities.size() > 0}">
                                                <input type="hidden" name="facility"
                                                    value="${facilities[0].name} (${facilities[0].code})" />
                                                <div style="flex:1; min-width:150px;">
                                                    <label
                                                        style="display:block; font-size:13px; font-weight:600; color:var(--hms-text-muted); margin-bottom:8px;">Cơ
                                                        sở</label>
                                                    <div class="form-control"
                                                        style="background:var(--hms-bg-soft); color:var(--hms-stone); pointer-events:none;">
                                                        <c:out value="${facilities[0].name} (${facilities[0].code})" />
                                                    </div>
                                                </div>
                                            </c:if>
                                        </div>
                                        <div
                                            style="display:flex; justify-content:flex-end; gap:12px; border-top:1px dashed var(--hms-border-soft); padding-top:16px;">
                                            <a href="${ctx}/operator/meter-readings"
                                                style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); text-decoration:none; font-size:14px; font-weight:500;">Xóa
                                                bộ lọc</a>
                                            <button type="submit"
                                                style="display:inline-flex; align-items:center; background:#fff; border:1px solid var(--hms-border); border-radius:20px; padding:6px 20px; color:var(--hms-text); font-size:14px; font-weight:500; cursor:pointer;">Lọc
                                                danh sách</button>
                                        </div>
                                    </form>

                                    <div class="table-responsive">
                                        <table class="table-mintlify">
                                            <thead>
                                                <tr>
                                                    <th>Mã phòng</th>
                                                    <th>Số điện</th>
                                                    <th>Số nước</th>
                                                    <th class="d-none d-md-table-cell">Cập nhật lúc</th>
                                                    <th>Trạng thái</th>
                                                    <th>Thao tác</th>
                                                </tr>
                                            </thead>
                                            <tbody id="meterTbody">
                                                <c:choose>
                                                    <c:when test="${empty meterList}">
                                                        <tr>
                                                            <td colspan="6" class="text-center py-4"
                                                                style="color:var(--hms-stone)">Không có dữ liệu hiển
                                                                thị.</td>
                                                        </tr>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <c:forEach var="item" items="${meterList}">
                                                            <tr>
                                                                <td style="font-weight:500; color:var(--hms-ink)">
                                                                    <c:out value="${item.roomCode}" />
                                                                </td>
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when
                                                                            test="${not empty item.currentElectricReading}">
                                                                            <span
                                                                                style="font-weight:600; color:var(--hms-ink)">
                                                                                <c:out
                                                                                    value="${item.currentElectricReading}" />
                                                                            </span>
                                                                        </c:when>
                                                                        <c:otherwise>-</c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when
                                                                            test="${not empty item.currentWaterReading}">
                                                                            <span
                                                                                style="font-weight:600; color:var(--hms-ink)">
                                                                                <c:out
                                                                                    value="${item.currentWaterReading}" />
                                                                            </span>
                                                                        </c:when>
                                                                        <c:otherwise>-</c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td class="d-none d-md-table-cell"
                                                                    style="font-size:0.8125rem; color:var(--hms-stone)">
                                                                    <c:choose>
                                                                        <c:when test="${not empty item.updatedAt}">
                                                                            <fmt:formatDate value="${item.updatedAt}"
                                                                                pattern="dd/MM/yyyy HH:mm" />
                                                                        </c:when>
                                                                        <c:otherwise>-</c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${item.status == 'DA_CAP_NHAT'}">
                                                                            <span class="badge-hms badge-success">ĐÃ CẬP
                                                                                NHẬT</span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="badge-hms badge-warning">CHƯA
                                                                                CẬP NHẬT</span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </td>
                                                                <td>
                                                                    <%-- Thống nhất dùng btn-mintlify-secondary cho tất
                                                                        cả thao tác trong bảng --%>
                                                                        <a href="${ctx}/operator/meter-readings/update?roomCode=${item.roomCode}"
                                                                            class="btn-mintlify-secondary text-decoration-none"
                                                                            style="padding:4px 12px; font-size:12px;">
                                                                            <c:choose>
                                                                                <c:when
                                                                                    test="${item.status != 'DA_CAP_NHAT'}">
                                                                                    Cập nhật</c:when>
                                                                                <c:otherwise>Sửa</c:otherwise>
                                                                            </c:choose>
                                                                        </a>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </c:otherwise>
                                                </c:choose>
                                            </tbody>
                                        </table>
                                    </div>
                                    <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2"
                                        id="meterFooter">
                                        <span class="text-muted" style="font-size:0.875rem">
                                            Tổng <strong id="meterTotal"></strong> phòng
                                            · Trang <span id="meterPage">1</span> / <span id="meterTotalPages">1</span>
                                        </span>
                                        <div class="d-flex gap-1" id="meterBtns"></div>
                                    </div>
                            </div>

                        </main>
                        <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
                    </div>
                </div>
                <script>clientPaginate('meterTbody', 'meterTotal', 'meterPage', 'meterTotalPages', 'meterBtns');</script>
            </body>

            </html>