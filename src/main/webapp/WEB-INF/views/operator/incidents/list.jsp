<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Lịch sử Báo cáo Sự cố - Kỹ thuật"/>
<c:set var="pageRole" value="OPERATOR"/>
<c:set var="activeMenu" value="my-incidents"/>
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
                <h1>Sự cố tôi đã báo cáo</h1>
                <p>Danh sách các sự cố bạn đã ghi nhận tại hiện trường</p>
            </div>

            <div class="data-surface">
                <%-- Filter bar đồng bộ với admin --%>
                <form method="GET" action="${ctx}/operator/incidents/my-reports" class="hms-filter-form">
                    <div style="display:flex; flex-wrap:wrap; gap:20px; margin-bottom:20px;">
                        <div style="flex:2; min-width:160px;">
                            <label class="hms-form-label hms-text-sm-steel mb-2" style="margin-top:0;">Trạng thái</label>
                            <select name="status" class="form-select" style="width:100%">
                                <option value="">Tất cả</option>
                                <option value="PENDING"     ${paramStatus == 'PENDING'     ? 'selected' : ''}>Chờ quản lý duyệt</option>
                                <option value="RECEIVED"    ${paramStatus == 'RECEIVED'    ? 'selected' : ''}>Đã tiếp nhận</option>
                                <option value="ASSIGNED"    ${paramStatus == 'ASSIGNED'    ? 'selected' : ''}>Đã phân công</option>
                                <option value="IN_PROGRESS" ${paramStatus == 'IN_PROGRESS' ? 'selected' : ''}>Đang xử lý</option>
                                <option value="DONE"        ${paramStatus == 'DONE'        ? 'selected' : ''}>Đã xử lý</option>
                                <option value="COMPLETED"   ${paramStatus == 'COMPLETED'   ? 'selected' : ''}>Hoàn thành</option>
                                <option value="REJECTED"    ${paramStatus == 'REJECTED'    ? 'selected' : ''}>Đã hủy</option>
                            </select>
                        </div>
                        <div style="flex:2; min-width:160px;">
                            <label class="hms-form-label hms-text-sm-steel mb-2" style="margin-top:0;">Thể loại</label>
                            <select name="category" class="form-select" style="width:100%">
                                <option value="">Tất cả</option>
                                <c:forEach var="cat" items="${availableCategories}">
                                    <c:if test="${cat != 'Khác'}">
                                        <option value="${cat}" ${paramCategory == cat ? 'selected' : ''}>
                                            <c:choose>
                                                <c:when test="${cat == 'ELECTRIC'}">Điện</c:when>
                                                <c:when test="${cat == 'WATER'}">Nước</c:when>
                                                <c:when test="${cat == 'INTERNET'}">Internet</c:when>
                                                <c:when test="${cat == 'INFRASTRUCTURE'}">Cơ sở vật chất</c:when>
                                                <c:when test="${cat == 'MAINTENANCE'}">Bảo trì</c:when>
                                                <c:when test="${cat == 'CLEANING'}">Vệ sinh</c:when>
                                                <c:when test="${cat == 'COMPLAINT'}">Khiếu nại</c:when>
                                                <c:when test="${cat == 'OTHER'}">Khác</c:when>
                                                <c:otherwise><c:out value="${cat}"/></c:otherwise>
                                            </c:choose>
                                        </option>
                                    </c:if>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="d-flex justify-content-end gap-2 border-top pt-3 mt-4" style="border-top-style: dashed !important; border-color: var(--hms-border-soft) !important;">
                        <a href="${ctx}/operator/incidents/my-reports" class="btn-mintlify-secondary text-decoration-none" style="border-radius: 20px;">Xóa bộ lọc</a>
                        <button type="submit" class="btn-mintlify-secondary" style="border-radius: 20px;">Tìm kiếm</button>
                    </div>
                </form>

                <c:choose>
                    <c:when test="${not empty items}">
                        <div class="table-responsive">
                            <table class="table-mintlify">
                                <thead>
                                    <tr>
                                        <th class="d-none d-md-table-cell">Mã SC</th>
                                        <th class="d-none d-md-table-cell">Loại</th>
                                        <th>Tiêu đề / Vị trí</th>
                                        <th class="d-none d-md-table-cell">Ngày gửi</th>
                                        <th>Trạng thái</th>
                                        <th>Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="item" items="${items}">
                                        <tr>
                                            <td class="d-none d-md-table-cell" style="font-family:'Geist Mono',monospace; font-size:13px;">
                                                <c:out value="${item.code}"/>
                                            </td>
                                            <td class="d-none d-md-table-cell">
                                                <span class="badge-hms badge-neutral"><c:out value="${item.category}"/></span>
                                            </td>
                                            <td style="max-width:200px;">
                                                <div style="font-weight:500; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;" title="${item.title}">
                                                    <c:out value="${item.title}"/>
                                                </div>
                                            </td>
                                            <td class="d-none d-md-table-cell" style="font-size:0.8125rem; color:var(--hms-stone);">
                                                <fmt:formatDate value="${item.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${item.status == 'PENDING'}">   <span class="badge-hms badge-info">Chờ duyệt</span></c:when>
                                                    <c:when test="${item.status == 'RECEIVED'}">  <span class="badge-hms badge-info">Đã tiếp nhận</span></c:when>
                                                    <c:when test="${item.status == 'ASSIGNED'}">  <span class="badge-hms badge-neutral">Đã phân công</span></c:when>
                                                    <c:when test="${item.status == 'IN_PROGRESS'}"><span class="badge-hms badge-warning">Đang xử lý</span></c:when>
                                                    <c:when test="${item.status == 'COMPLETED' or item.status == 'RESOLVED' or item.status == 'DONE'}">
                                                        <span class="badge-hms badge-success">Hoàn thành</span></c:when>
                                                    <c:when test="${item.status == 'CANCELLED' or item.status == 'REJECTED'}">
                                                        <span class="badge-hms badge-danger">Đã hủy</span></c:when>
                                                    <c:otherwise><span class="badge-hms badge-neutral"><c:out value="${item.status}"/></span></c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <div class="d-flex gap-2">
                                                    <button class="btn-mintlify-secondary" style="padding:4px 12px; font-size:12px; cursor:pointer;"
                                                            data-title="<c:out value='${item.title}' escapeXml='true'/>"
                                                            data-content="<c:out value='${item.content}' escapeXml='true'/>"
                                                            data-status="${item.status}"
                                                            data-reason="<c:out value='${item.rejectionReason}' escapeXml='true'/>"
                                                            data-img="${item.attachmentUrls1}"
                                                            onclick="event.stopPropagation(); openIncidentDetail(this)">
                                                        Chi tiết
                                                    </button>
                                                    <c:if test="${item.status == 'PENDING'}">
                                                        <a href="${ctx}/operator/incidents/edit?id=${item.requestId}"
                                                           class="btn-mintlify-secondary text-decoration-none"
                                                           style="padding:4px 12px; font-size:12px;"
                                                           onclick="event.stopPropagation()">Sửa</a>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <div class="table-footer d-flex justify-content-between align-items-center px-3 py-2 flex-wrap gap-2">
                            <span class="text-muted" style="font-size:0.875rem">
                                Tổng <fmt:formatNumber value="${totalItems}" groupingUsed="true"/> sự cố
                                · Trang ${currentPage} / ${totalPages}
                            </span>
                            <div class="d-flex gap-1">
                                <c:if test="${currentPage > 1}">
                                    <a href="${ctx}/operator/incidents/my-reports?page=${currentPage-1}&status=${paramStatus}&category=${paramCategory}"
                                       class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Trước</a>
                                </c:if>
                                <c:if test="${currentPage < totalPages}">
                                    <a href="${ctx}/operator/incidents/my-reports?page=${currentPage+1}&status=${paramStatus}&category=${paramCategory}"
                                       class="btn-mintlify-secondary text-decoration-none" style="padding:6px 14px">Sau</a>
                                </c:if>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state p-5 text-center">
                            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="var(--hms-stone)" stroke-width="1.5" style="margin-bottom:12px">
                                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                                <polyline points="14 2 14 8 20 8"/>
                                <line x1="16" y1="13" x2="8" y2="13"/>
                                <line x1="16" y1="17" x2="8" y2="17"/>
                            </svg>
                            <h4>Bạn chưa báo cáo sự cố nào</h4>
                            <p class="text-muted mb-4">Các sự cố phát sinh tại hiện trường do bạn báo cáo sẽ hiển thị tại đây.</p>
                            <a href="${ctx}/operator/incidents/create" class="btn-mintlify-primary text-decoration-none">Báo cáo ngay</a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </main>

        <%-- Detail Modal --%>
        <div id="incidentDetailModal" class="custom-modal-backdrop">
            <div class="custom-modal">
                <div class="custom-modal-header d-flex justify-content-between align-items-center">
                    <h5 class="m-0" id="detailTitle" style="font-weight:600">Chi tiết sự cố</h5>
                    <span class="custom-modal-close" onclick="closeIncidentDetail()">&times;</span>
                </div>
                <div class="custom-modal-body" style="text-align:left; max-height:70vh; overflow-y:auto;">
                    <div class="mb-3">
                        <label class="form-label" style="font-weight:600; font-size:13px; color:var(--hms-stone);">Mô tả sự cố:</label>
                        <div id="detailContent" style="padding:12px; background:var(--hms-bg-soft); border-radius:6px; font-size:14px; white-space:pre-wrap; line-height:1.5;"></div>
                    </div>
                    <div class="mb-3" id="reasonBlock" style="display:none">
                        <label class="form-label text-danger" style="font-weight:600; font-size:13px;">Lý do (nếu có):</label>
                        <div id="detailReason" style="padding:12px; background:#fff0f0; border-radius:6px; font-size:14px; color:#dc3545; white-space:pre-wrap;"></div>
                    </div>
                    <div class="mb-3" id="imgBlock" style="display:none">
                        <label class="form-label" style="font-weight:600; font-size:13px; color:var(--hms-stone);">Hình ảnh đính kèm:</label>
                        <div class="mt-2 text-center">
                            <img id="detailImg" src="" style="max-width:100%; border-radius:8px; border:1px solid var(--hms-border);" alt="Attachment"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <script>
            function openIncidentDetail(btn) {
                document.getElementById('detailTitle').innerText   = btn.getAttribute('data-title');
                document.getElementById('detailContent').innerText = btn.getAttribute('data-content');
                var reason = btn.getAttribute('data-reason');
                var reasonBlock = document.getElementById('reasonBlock');
                if (reason && reason.trim() !== '' && reason !== 'null') {
                    reasonBlock.style.display = 'block';
                    document.getElementById('detailReason').innerText = reason;
                } else {
                    reasonBlock.style.display = 'none';
                }
                var imgUrl   = btn.getAttribute('data-img');
                var imgBlock = document.getElementById('imgBlock');
                if (imgUrl && imgUrl.trim() !== '' && imgUrl !== 'null') {
                    imgBlock.style.display = 'block';
                    var fullUrl = imgUrl.startsWith('http') ? imgUrl : ('${ctx}' + (imgUrl.startsWith('/') ? imgUrl : '/' + imgUrl));
                    document.getElementById('detailImg').src = fullUrl;
                } else {
                    imgBlock.style.display = 'none';
                }
                document.getElementById('incidentDetailModal').style.display = 'block';
            }
            function closeIncidentDetail() {
                document.getElementById('incidentDetailModal').style.display = 'none';
            }
            window.addEventListener('click', function(e) {
                var modal = document.getElementById('incidentDetailModal');
                if (e.target === modal) modal.style.display = 'none';
            });
        </script>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
