<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    Consume flash messages từ session.
    errorMessage / successMessage / warningMessage là request-scope.
    flashMessage / flashType là session-scope.
--%>
<%
    String _flashMsg = (String) request.getAttribute("flashMessage");
    if (_flashMsg == null) {
        _flashMsg = (String) session.getAttribute("flashMessage");
        if (_flashMsg != null) {
            session.removeAttribute("flashMessage");
            request.setAttribute("flashMessage", _flashMsg);
            
            String _flashType = (String) session.getAttribute("flashType");
            session.removeAttribute("flashType");
            request.setAttribute("flashType", _flashType);
        }
    }

    String _sessionError = (String) request.getAttribute("errorMessage");
    if (_sessionError == null) {
        _sessionError = (String) session.getAttribute("errorMessage");
        if (_sessionError != null) {
            session.removeAttribute("errorMessage");
            request.setAttribute("errorMessage", _sessionError);
        }
    }
%>

<c:set var="isError" value="${not empty errorMessage or (not empty flashMessage and flashType != 'success')}"/>
<c:set var="isSuccess" value="${not empty successMessage or (not empty flashMessage and flashType == 'success')}"/>
<c:set var="warningMsg" value="${warningMessage}"/>

<c:if test="${isError}">
    <div class="hms-inline-alert hms-inline-alert--error">
        <svg class="hms-inline-alert__icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>
        <span class="hms-inline-alert__msg">
            <c:out value="${not empty errorMessage ? errorMessage : flashMessage}"/>
        </span>
    </div>
</c:if>

<c:if test="${isSuccess}">
    <div class="hms-inline-alert hms-inline-alert--success">
        <svg class="hms-inline-alert__icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
        <span class="hms-inline-alert__msg">
            <c:out value="${not empty successMessage ? successMessage : flashMessage}"/>
        </span>
    </div>
</c:if>

<c:if test="${not empty warningMsg}">
    <div class="hms-inline-alert hms-inline-alert--warning">
        <svg class="hms-inline-alert__icon" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"></path><line x1="12" y1="9" x2="12" y2="13"></line><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
        <span class="hms-inline-alert__msg">
            <c:out value="${warningMsg}"/>
        </span>
    </div>
</c:if>
