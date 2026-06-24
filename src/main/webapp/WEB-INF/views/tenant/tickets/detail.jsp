<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết yêu cầu - Cổng cư dân"/>
<c:set var="pageRole" value="TENANT"/>
<c:set var="activeMenu" value="tickets"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/><div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>
            
            <div class="page-header hero-sky-gradient">
                <div class="d-flex justify-content-between align-items-center flex-wrap">
                    <div>
                        <h1><c:out value="${ticket.title}"/></h1>
                        <p><c:out value="${ticket.typeLabel}"/> · <span class="badge-hms ${ticket.statusBadgeClass}"><c:out value="${ticket.statusLabel}"/></span></p>
                    </div>
                    <div>
                        <a href="${ctx}/tenant/tickets" class="btn-mintlify-secondary text-decoration-none">
                            ← Danh sách yêu cầu
                        </a>
                    </div>
                </div>
            </div>

            <div class="row g-3">
                <div class="col-lg-8">
                    <div class="widget-surface mb-3">
                        <div class="widget-surface-header">
                            <h3>Nội dung yêu cầu</h3>
                        </div>
                        <div class="widget-surface-body">
                            <p style="line-height: 1.6; color: var(--hms-ink);"><c:out value="${ticket.content}"/></p>
                            
                            <c:if test="${not empty ticket.attachmentUrls1}">
                                <div class="mt-4 pt-3" style="border-top: 1px solid var(--hms-border-soft);">
                                    <h4 style="font-size: 0.875rem; color: var(--hms-stone); margin-bottom: 0.75rem;">Đính kèm:</h4>
                                    <img src="${ctx}${ticket.attachmentUrls1}" alt="Đính kèm" style="max-width: 100%; border-radius: var(--hms-radius-md); box-shadow: var(--hms-shadow-sm);">
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
                
                <div class="col-lg-4">
                    <div class="widget-surface mb-3">
                        <div class="widget-surface-header">
                            <h3>Thông tin xử lý</h3>
                        </div>
                        <div class="widget-surface-body p-0">
                            <table class="table-mintlify" style="font-size: 0.875rem;">
                                <tbody>
                                    <tr>
                                        <td style="padding: 12px 1.25rem; color: var(--hms-stone);">Gửi lúc</td>
                                        <td style="padding: 12px 1.25rem; text-align: right; font-weight: 600;"><c:out value="${ticket.createdDateLabel}"/></td>
                                    </tr>
                                    <c:if test="${not empty ticket.assignedTo}">
                                        <tr>
                                            <td style="padding: 12px 1.25rem; color: var(--hms-stone);">Người phụ trách</td>
                                            <td style="padding: 12px 1.25rem; text-align: right; font-weight: 600;"><c:out value="${ticket.assignedTo}"/></td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
