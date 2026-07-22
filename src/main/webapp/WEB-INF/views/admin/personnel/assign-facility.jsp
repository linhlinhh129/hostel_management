<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx"        value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle"  value="Gán cơ sở - Admin"/>
<c:set var="pageRole"   value="ADMIN"/>
<c:set var="activeMenu" value="personnel"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <div class="page-header hero-sky-gradient"
                 style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                <div style="display:flex;justify-content:space-between;align-items:flex-end;
                            flex-wrap:wrap;gap:1rem;position:relative;z-index:1">
                    <div>
                        <h1>Gán cơ sở quản lý</h1>
                        <p><c:out value="${person.fullName}"/> (<c:out value="${person.code}"/>)</p>
                    </div>
                    <a href="${ctx}/admin/personnel/${person.id}"
                       class="btn-mintlify-secondary text-decoration-none"
                       style="position:relative;z-index:1">&#8592; Quay lại</a>
                </div>
            </div>

            <div class="data-surface" style="max-width:600px">
                <form method="post"
                      action="${ctx}/admin/personnel/${person.id}/assign-facility"
                      class="p-4">
                    <input type="hidden" name="csrfToken" value="${csrfToken}"/>

                    <p class="text-muted small mb-3">Chọn cơ sở để gán cho nhân sự này:</p>

                    <c:choose>
                        <c:when test="${not empty facilities}">
                            <c:forEach var="facility" items="${facilities}">
                                <div class="form-check mb-2">
                                    <input type="checkbox" class="form-check-input"
                                           id="facility_${facility.id}"
                                           name="facilityIds" value="${facility.id}"
                                           ${facility.assigned ? 'checked' : ''}>
                                    <label class="form-check-label" for="facility_${facility.id}">
                                        <c:out value="${facility.code}"/> - <c:out value="${facility.name}"/>
                                    </label>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <p class="text-muted">Chưa có cơ sở nào</p>
                        </c:otherwise>
                    </c:choose>

                    <div class="d-flex gap-2 mt-3">
                        <button type="submit" class="btn btn-mintlify-primary" style="width:auto">Lưu</button>
                        <a href="${ctx}/admin/personnel/${person.id}"
                           class="btn-mintlify-secondary text-decoration-none">Hủy</a>
                    </div>
                </form>
            </div>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
