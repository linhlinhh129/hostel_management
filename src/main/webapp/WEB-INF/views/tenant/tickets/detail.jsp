<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
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
                                    <c:set var="img1" value="${ticket.attachmentUrls1.trim()}" />
                                    <c:choose>
                                        <c:when test="${fn:startsWith(img1, 'http')}">
                                            <c:set var="finalImg1" value="${img1}" />
                                        </c:when>
                                        <c:when test="${fn:startsWith(img1, ctx)}">
                                            <c:set var="finalImg1" value="${img1}" />
                                        </c:when>
                                        <c:when test="${fn:startsWith(img1, '/')}">
                                            <c:set var="finalImg1" value="${ctx}${img1}" />
                                        </c:when>
                                        <c:otherwise>
                                            <c:set var="finalImg1" value="${ctx}/${img1}" />
                                        </c:otherwise>
                                    </c:choose>
                                    <img src="${finalImg1}" alt="Đính kèm" style="max-width: 100%; border-radius: var(--hms-radius-md); box-shadow: var(--hms-shadow-sm);">
                                </div>
                            </c:if>
                        </div>
                    </div>
                    
                    <c:if test="${ticket.status == 'DONE' && not empty ticket.attachmentUrls2}">
                        <div class="widget-surface mb-3">
                            <div class="widget-surface-header">
                                <h3>Kết quả xử lý bằng hình ảnh</h3>
                            </div>
                            <div class="widget-surface-body">
                                <div class="d-flex flex-wrap gap-2 mt-2">
                                    <c:forTokens items="${ticket.attachmentUrls2}" delims="," var="imgUrl">
                                        <c:if test="${not empty imgUrl.trim()}">
                                            <c:set var="trimmedUrl" value="${imgUrl.trim()}" />
                                            <c:choose>
                                                <c:when test="${fn:startsWith(trimmedUrl, 'http')}">
                                                    <c:set var="finalImg2" value="${trimmedUrl}" />
                                                </c:when>
                                                <c:when test="${fn:startsWith(trimmedUrl, ctx)}">
                                                    <c:set var="finalImg2" value="${trimmedUrl}" />
                                                </c:when>
                                                <c:when test="${fn:startsWith(trimmedUrl, '/')}">
                                                    <c:set var="finalImg2" value="${ctx}${trimmedUrl}" />
                                                </c:when>
                                                <c:otherwise>
                                                    <c:set var="finalImg2" value="${ctx}/${trimmedUrl}" />
                                                </c:otherwise>
                                            </c:choose>
                                            <img src="${finalImg2}" alt="Kết quả xử lý" style="max-width: 200px; height: auto; border-radius: var(--hms-radius-md); box-shadow: var(--hms-shadow-sm);">
                                        </c:if>
                                    </c:forTokens>
                                </div>
                            </div>
                        </div>
                    </c:if>
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
                                    <c:if test="${ticket.status == 'REJECTED' && not empty ticket.rejectionReason}">
                                        <tr>
                                            <td style="padding: 12px 1.25rem; color: var(--hms-danger);">Lý do từ chối</td>
                                            <td style="padding: 12px 1.25rem; text-align: right; color: var(--hms-danger); font-weight: 500;"><c:out value="${ticket.rejectionReason}"/></td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${ticket.status == 'IN_PROGRESS' && not empty ticket.appointSchedule}">
                                        <tr>
                                            <td style="padding: 12px 1.25rem; color: var(--hms-info);">Lịch hẹn xử lý</td>
                                            <td style="padding: 12px 1.25rem; text-align: right; color: var(--hms-info); font-weight: 600;"><c:out value="${ticket.formattedAppointmentDate}"/></td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${ticket.status == 'DONE' && not empty ticket.rejectionReason}">
                                        <tr>
                                            <td style="padding: 12px 1.25rem; color: var(--hms-success);">Ghi chú hoàn thành</td>
                                            <td style="padding: 12px 1.25rem; text-align: right; color: var(--hms-success); font-weight: 500;"><c:out value="${ticket.rejectionReason}"/></td>
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
