<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--
    Consume flash messages từ session ngay lập tức để chúng không bị hiển thị lại ở màn khác.
    errorMessage / successMessage / warningMessage là request-scope → tự mất sau request.
    flashMessage / flashType là session-scope → phải xóa thủ công ở đây.
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

<c:set var="isInactive" value="${currentFacilityStatus == 'INACTIVE'}"/>
<c:set var="hasAlert" value="${isInactive or not empty errorMessage or not empty successMessage or not empty warningMessage or not empty flashMessage}"/>
<c:if test="${hasAlert}">
<div id="hms-toast-container" aria-live="polite" aria-atomic="true" class="hms-toast-container">

    <c:if test="${isInactive}">
        <div class="hms-toast hms-toast--warning hms-toast--persistent" role="alert">
            <span class="hms-toast__icon">⚠</span>
            <span class="hms-toast__msg">
                Cơ sở đã bị <strong>vô hiệu hoá</strong>.<br>
                Chế độ <strong>CHỈ XEM</strong>.
            </span>
        </div>
    </c:if>

    <c:if test="${not empty successMessage}">
        <div class="hms-toast hms-toast--success" role="alert">
            <span class="hms-toast__icon">✓</span>
            <span class="hms-toast__msg"><c:out value="${successMessage}"/></span>
            <button class="hms-toast__close" onclick="this.closest('.hms-toast').remove()" aria-label="Đóng">×</button>
        </div>
    </c:if>

    <c:if test="${not empty errorMessage}">
        <div class="hms-toast hms-toast--error" role="alert">
            <span class="hms-toast__icon">✕</span>
            <span class="hms-toast__msg"><c:out value="${errorMessage}"/></span>
            <button class="hms-toast__close" onclick="this.closest('.hms-toast').remove()" aria-label="Đóng">×</button>
        </div>
    </c:if>

    <c:if test="${not empty warningMessage}">
        <div class="hms-toast hms-toast--warning" role="alert">
            <span class="hms-toast__icon">⚠</span>
            <span class="hms-toast__msg"><c:out value="${warningMessage}"/></span>
            <button class="hms-toast__close" onclick="this.closest('.hms-toast').remove()" aria-label="Đóng">×</button>
        </div>
    </c:if>

    <c:if test="${not empty flashMessage}">
        <c:choose>
            <c:when test="${flashType == 'success'}">
                <div class="hms-toast hms-toast--success" role="alert">
                    <span class="hms-toast__icon">✓</span>
                    <span class="hms-toast__msg"><c:out value="${flashMessage}"/></span>
                    <button class="hms-toast__close" onclick="this.closest('.hms-toast').remove()" aria-label="Đóng">×</button>
                </div>
            </c:when>
            <c:otherwise>
                <div class="hms-toast hms-toast--error" role="alert">
                    <span class="hms-toast__icon">✕</span>
                    <span class="hms-toast__msg"><c:out value="${flashMessage}"/></span>
                    <button class="hms-toast__close" onclick="this.closest('.hms-toast').remove()" aria-label="Đóng">×</button>
                </div>
            </c:otherwise>
        </c:choose>
    </c:if>

</div>



<script>
(function () {
    var DELAY = 5000;
    var FADE  = 400;
    document.querySelectorAll('#hms-toast-container .hms-toast:not(.hms-toast--persistent)').forEach(function (el) {
        setTimeout(function () {
            el.classList.add('hms-toast--fade-out');
            setTimeout(function () { el.remove(); }, FADE);
        }, DELAY);
    });
})();
</script>
</c:if>
