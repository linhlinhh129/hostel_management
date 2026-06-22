<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activeMenu" value="tickets"/>
<!DOCTYPE html>
<html lang="vi">
<head>
    <jsp:include page="/WEB-INF/views/layout/head.jsp">
        <jsp:param name="title" value="Danh sách yêu cầu sửa chữa" />
    </jsp:include>
    <link href="https://fonts.googleapis.com/css2?family=Geist+Mono:wght@400;500&family=Inter:wght@400;500;600&display=swap" rel="stylesheet">
    <style>
        .page-content { max-width: 1400px; margin: 0 auto; }
        .table-responsive { overflow-x: auto; }
        .custom-table th { 
            font-weight: 600; 
            color: var(--color-ink); 
            background-color: var(--color-surface-soft); 
            border-bottom: 1px solid var(--color-hairline-soft);
            padding: 12px 16px;
            font-size: 14px;
        }
        .custom-table td { 
            color: var(--color-steel); 
            vertical-align: middle; 
            padding: 12px 16px;
            font-size: 14px;
        }
        .filter-card {
            background-color: var(--color-canvas);
            border: 1px solid var(--color-hairline-soft);
            border-radius: 8px;
            padding: 16px;
            margin-bottom: 24px;
        }
    </style>
</head>
<body id="page-top">
    <div class="app-shell">
        <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
        <div class="sidebar-overlay"></div>
        <div class="main-wrapper" style="background-color: var(--color-canvas);">
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />

            <main class="page-content">
                <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

                <!-- Header -->
                <div class="page-header hero-sky-gradient" style="border-radius: var(--hms-radius-lg, 12px); margin-bottom: 1.75rem;">
                    <h1>Yêu cầu sửa chữa</h1>
                    <p>Danh sách các yêu cầu sửa chữa từ khách thuê</p>
                </div>

                <!-- Filters -->
                <div class="filter-card mb-4">
                    <form action="${pageContext.request.contextPath}/operator/requests" method="GET" class="row g-3 align-items-end mb-3">
                        <div class="col-12 col-sm-6 col-md-4 col-lg-3">
                            <label style="font-size: 12px; font-weight: 600; color: var(--color-steel); margin-bottom: 4px; display: block;">TRẠNG THÁI</label>
                            <select name="status" class="form-select mintlify-text-input w-100 shadow-sm" style="padding: 8px 12px; font-size: 14px;" onchange="this.form.submit()">
                                <option value="">Tất cả</option>
                                <option value="PENDING" ${paramStatus == 'PENDING' ? 'selected' : ''}>Chờ xử lý</option>
                                <option value="IN_PROGRESS" ${paramStatus == 'IN_PROGRESS' ? 'selected' : ''}>Đang xử lý</option>
                                <option value="COMPLETED" ${paramStatus == 'COMPLETED' ? 'selected' : ''}>Đã hoàn thành</option>
                                <option value="REJECTED" ${paramStatus == 'REJECTED' ? 'selected' : ''}>Đã từ chối</option>
                            </select>
                        </div>
                        <div class="col-12 col-sm-6 col-md-4 col-lg-3">
                            <label style="font-size: 12px; font-weight: 600; color: var(--color-steel); margin-bottom: 4px; display: block;">THỂ LOẠI</label>
                            <select name="category" class="form-select mintlify-text-input w-100 shadow-sm" style="padding: 8px 12px; font-size: 14px;" onchange="this.form.submit()">
                                <option value="">Tất cả</option>
                                <option value="Điện lạnh" ${paramCategory == 'Điện lạnh' ? 'selected' : ''}>Điện lạnh</option>
                                <option value="Điện nước" ${paramCategory == 'Điện nước' ? 'selected' : ''}>Điện nước</option>
                                <option value="Mộc" ${paramCategory == 'Mộc' ? 'selected' : ''}>Mộc</option>
                                <option value="Khác" ${paramCategory == 'Khác' ? 'selected' : ''}>Khác</option>
                            </select>
                        </div>
                        <c:if test="${not empty paramStatus or not empty paramCategory}">
                            <div class="col-12 col-md-auto pb-2">
                                <a href="${pageContext.request.contextPath}/operator/requests" class="text-decoration-none" style="font-size: 13px; color: var(--color-brand-tag);">Xóa bộ lọc</a>
                            </div>
                        </c:if>
                    </form>
                    <div class="border-top pt-3" style="border-color: var(--color-hairline-soft) !important; font-size: 13px; color: var(--color-steel); margin-top: 12px;">
                        Hiển thị ${requestListSize} / ${totalRecords} yêu cầu
                    </div>
                </div>

                <!-- Main Content -->
                <div class="data-surface mb-4">
                    <div class="table-responsive">
                        <table class="table table-hover custom-table m-0">
                            <thead>
                                <tr>
                                    <th class="d-none d-md-table-cell">Mã YC</th>
                                    <th>Tiêu đề</th>
                                    <th class="d-none d-md-table-cell">Phòng</th>
                                    <th class="d-none d-lg-table-cell">Thể loại</th>
                                    <th class="d-none d-md-table-cell">Ngày tạo</th>
                                    <th>Trạng thái</th>
                                    <th class="text-end">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty requestList}">
                                        <tr>
                                            <td colspan="7" class="text-center py-5" style="color: var(--color-steel);">
                                                <div style="font-size: 24px; margin-bottom: 8px;">🎉</div>
                                                Không có yêu cầu nào phù hợp.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="req" items="${requestList}">
                                            <tr>
                                                <td class="d-none d-md-table-cell" style="font-family: 'Geist Mono', monospace; font-size: 13px;">${req.code}</td>
                                                <td style="font-weight: 500; color: var(--color-ink); max-width: 200px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                                    ${req.title}
                                                </td>
                                                <td class="d-none d-md-table-cell">${req.displayLocation}</td>
                                                <td class="d-none d-lg-table-cell"><span class="mintlify-badge-type" style="text-transform: uppercase;">${req.category}</span></td>
                                                <td class="d-none d-md-table-cell"><fmt:formatDate value="${req.createdAt}" pattern="dd/MM/yyyy"/></td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${req.status == 'PENDING'}">
                                                            <span class="mintlify-badge-status-pending">CHỜ XỬ LÝ</span>
                                                        </c:when>
                                                        <c:when test="${req.status == 'IN_PROGRESS'}">
                                                            <span class="mintlify-badge-status-inprogress">ĐANG XỬ LÝ</span>
                                                        </c:when>
                                                        <c:when test="${req.status == 'COMPLETED'}">
                                                            <span class="mintlify-badge-status-resolved" style="background-color: var(--color-surface-soft); color: var(--color-brand-annotate); padding: 2px 8px; border-radius: 4px; font-weight: 600; font-size: 12px;">HOÀN THÀNH</span>
                                                        </c:when>
                                                        <c:when test="${req.status == 'REJECTED'}">
                                                            <span class="mintlify-badge-status-rejected">TỪ CHỐI</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="mintlify-badge-type">${req.status}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="text-end">
                                                    <div class="d-flex justify-content-end align-items-center" style="gap: 8px;">
                                                        <c:if test="${req.status == 'PENDING'}">
                                                            <a href="${pageContext.request.contextPath}/operator/incident-report/edit?id=${req.requestId}" class="mintlify-btn-secondary text-decoration-none" style="padding: 4px 12px; font-size: 12px;">Sửa</a>
                                                        </c:if>
                                                        <a href="${pageContext.request.contextPath}/operator/requests/detail?id=${req.requestId}" class="mintlify-btn-secondary text-decoration-none" style="padding: 4px 12px; font-size: 12px;">Chi tiết</a>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <nav class="d-flex justify-content-center mt-4">
                        <ul class="pagination pagination-sm m-0" style="gap: 4px;">
                            <c:if test="${currentPage > 1}">
                                <li class="page-item">
                                    <a class="page-link rounded" style="color: var(--color-steel); border-color: var(--color-hairline-soft);" href="?page=${currentPage - 1}&status=${paramStatus}&category=${paramCategory}">&laquo;</a>
                                </li>
                            </c:if>
                            
                            <c:forEach begin="1" end="${totalPages}" var="i">
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                    <c:choose>
                                        <c:when test="${i == currentPage}">
                                            <span class="page-link rounded" style="background-color: var(--color-brand-green); border-color: var(--color-brand-green); color: white;">${i}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <a class="page-link rounded" style="color: var(--color-ink); border-color: var(--color-hairline-soft);" href="?page=${i}&status=${paramStatus}&category=${paramCategory}">${i}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </li>
                            </c:forEach>

                            <c:if test="${currentPage < totalPages}">
                                <li class="page-item">
                                    <a class="page-link rounded" style="color: var(--color-steel); border-color: var(--color-hairline-soft);" href="?page=${currentPage + 1}&status=${paramStatus}&category=${paramCategory}">&raquo;</a>
                                </li>
                            </c:if>
                        </ul>
                    </nav>
                </c:if>

            </main>
            <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
        </div>
    </div>
</body>
</html>
