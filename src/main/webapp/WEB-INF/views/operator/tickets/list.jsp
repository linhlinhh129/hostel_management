<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"  %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Yêu cầu - Kỹ thuật"/>
<c:set var="pageRole"   value="OPERATOR"/>
<c:set var="activeMenu" value="tickets"/>
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
                <div><h1>Yêu cầu được phân công</h1><p>Danh sách yêu cầu kỹ thuật cần xử lý</p></div>
            </div>

            <div class="data-surface">
                <form class="filter-bar" method="get" action="${ctx}/operator/tickets">
                    <select class="form-select" name="status" style="max-width:200px">
                        <option value="">Tất cả trạng thái</option>
                        <option value="PENDING"     ${filterStatus == 'PENDING'     ? 'selected':''}>Mới tạo</option>
                        <option value="IN_PROGRESS" ${filterStatus == 'IN_PROGRESS' ? 'selected':''}>Đang xử lý</option>
                        <option value="DONE"        ${filterStatus == 'DONE'        ? 'selected':''}>Hoàn thành</option>
                        <option value="REJECTED"    ${filterStatus == 'REJECTED'    ? 'selected':''}>Từ chối</option>
                    </select>
                    <button type="submit" class="btn-mintlify-secondary">Lọc</button>
                    <a href="${ctx}/operator/tickets" class="btn-mintlify-secondary text-decoration-none">Xóa</a>
                </form>

                <c:choose>
                    <c:when test="${not empty tickets}">
                        <div class="table-responsive">
                            <table class="table-mintlify">
                                <thead><tr>
                                    <th>Mã yêu cầu</th><th>Loại</th><th>Tiêu đề</th>
                                    <th>Ngày gửi</th><th>Trạng thái</th><th></th>
                                </tr></thead>
                                <tbody>
                                <c:forEach var="ticket" items="${tickets}">
                                    <tr data-href="${ctx}/operator/tickets/${ticket.id}">
                                        <td><a href="${ctx}/operator/tickets/${ticket.id}"
                                               style="font-family:var(--hms-font-mono);font-weight:700">
                                            <c:out value="${ticket.code}"/></a></td>
                                        <td>
                                            <span class="badge-hms badge-neutral" style="font-size:0.6875rem">
                                                <c:out value="${ticket.category}"/>
                                            </span>
                                        </td>
                                        <td style="max-width:260px;font-weight:500">
                                            <c:out value="${ticket.title}"/>
                                        </td>
                                        <td style="font-size:0.8125rem;color:var(--hms-stone)">
                                            <c:out value="${ticket.createdAt}"/>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${ticket.status == 'PENDING'}">
                                                    <span class="badge-hms badge-info">Mới tạo</span>
                                                </c:when>
                                                <c:when test="${ticket.status == 'IN_PROGRESS'}">
                                                    <span class="badge-hms badge-warning">Đang xử lý</span>
                                                </c:when>
                                                <c:when test="${ticket.status == 'DONE'}">
                                                    <span class="badge-hms badge-success">Hoàn thành</span>
                                                </c:when>
                                                <c:when test="${ticket.status == 'REJECTED'}">
                                                    <span class="badge-hms badge-danger">Từ chối</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge-hms badge-neutral"><c:out value="${ticket.status}"/></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <a href="${ctx}/operator/tickets/${ticket.id}"
                                               class="quick-action-btn" style="font-size:0.75rem;padding:5px 10px">
                                                Xử lý
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div style="font-size:2rem;margin-bottom:0.5rem">🔧</div>
                            <h4>Không có yêu cầu nào</h4>
                            <p>Chưa có yêu cầu kỹ thuật được phân công cho bạn.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
