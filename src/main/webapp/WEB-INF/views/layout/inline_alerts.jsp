<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    Consume flash messages từ session.
    errorMessage / successMessage / warningMessage là request-scope.
    flashMessage / flashType là session-scope.
--%>
<%
    String _flashMsg  = (String) session.getAttribute("flashMessage");
    String _flashType = (String) session.getAttribute("flashType");
    if (_flashMsg != null) {
        session.removeAttribute("flashMessage");
        session.removeAttribute("flashType");
        request.setAttribute("flashMessage", _flashMsg);
        request.setAttribute("flashType",    _flashType);
    }
%>

<c:set var="isError" value="${not empty errorMessage or (not empty flashMessage and flashType != 'success')}"/>
<c:set var="isSuccess" value="${not empty successMessage or (not empty flashMessage and flashType == 'success')}"/>
<c:set var="warningMsg" value="${warningMessage}"/>

<c:if test="${isError}">
    <div style="background-color: #fee2e2; color: #991b1b; border-left: 4px solid #ef4444; border-radius: 8px; padding: 12px 16px; font-size: 0.875rem; display: flex; align-items: flex-start; gap: 8px; margin-bottom: 1.5rem;">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="flex-shrink:0; margin-top: 2px;"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>
        <span style="flex:1; line-height: 1.4;">
            <c:out value="${not empty errorMessage ? errorMessage : flashMessage}"/>
        </span>
    </div>
</c:if>

<c:if test="${isSuccess}">
    <div style="background-color: #d1fae5; color: #065f46; border-left: 4px solid #10b981; border-radius: 8px; padding: 12px 16px; font-size: 0.875rem; display: flex; align-items: flex-start; gap: 8px; margin-bottom: 1.5rem;">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="flex-shrink:0; margin-top: 2px;"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
        <span style="flex:1; line-height: 1.4;">
            <c:out value="${not empty successMessage ? successMessage : flashMessage}"/>
        </span>
    </div>
</c:if>

<c:if test="${not empty warningMsg}">
    <div style="background-color: #fef9c3; color: #854d0e; border-left: 4px solid #f59e0b; border-radius: 8px; padding: 12px 16px; font-size: 0.875rem; display: flex; align-items: flex-start; gap: 8px; margin-bottom: 1.5rem;">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="flex-shrink:0; margin-top: 2px;"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
        <span style="flex:1; line-height: 1.4;">
            <c:out value="${warningMsg}"/>
        </span>
    </div>
</c:if>
