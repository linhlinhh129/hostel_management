<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%--
    Consume flash messages từ session ngay lập tức để chúng không bị hiển thị lại ở màn khác.
    errorMessage / successMessage / warningMessage là request-scope → tự mất sau request.
    flashMessage / flashType là session-scope → phải xóa thủ công ở đây.
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

<c:set var="hasAlert" value="${not empty errorMessage or not empty successMessage or not empty warningMessage or not empty flashMessage}"/>
<c:if test="${hasAlert}">
<div id="hms-toast-container" aria-live="polite" aria-atomic="true"
     style="position:fixed;bottom:1.5rem;left:1.25rem;z-index:9999;display:flex;flex-direction:column;gap:.5rem;min-width:280px;max-width:400px;">

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

<style>
.hms-toast {
    display: flex;
    align-items: flex-start;
    gap: .625rem;
    padding: .75rem 1rem;
    border-radius: .5rem;
    box-shadow: 0 4px 16px rgba(0,0,0,.14);
    font-size: .875rem;
    line-height: 1.4;
    opacity: 1;
    transition: opacity .4s ease, transform .4s ease;
    animation: hmsToastIn .25s ease;
}
@keyframes hmsToastIn {
    from { opacity: 0; transform: translateX(-40px); }
    to   { opacity: 1; transform: translateX(0); }
}
.hms-toast--success { background: #d1fae5; color: #065f46; border-left: 4px solid #10b981; }
.hms-toast--error   { background: #fee2e2; color: #991b1b; border-left: 4px solid #ef4444; }
.hms-toast--warning { background: #fef9c3; color: #854d0e; border-left: 4px solid #f59e0b; }
.hms-toast__icon    { font-size: 1rem; flex-shrink: 0; margin-top: .05rem; }
.hms-toast__msg     { flex: 1; }
.hms-toast__close   {
    background: none; border: none; cursor: pointer;
    font-size: 1.1rem; line-height: 1; padding: 0;
    color: inherit; opacity: .6; flex-shrink: 0;
}
.hms-toast__close:hover { opacity: 1; }
.hms-toast--fade-out { opacity: 0; transform: translateX(-40px); }
</style>

<script>
(function () {
    var DELAY = 5000;
    var FADE  = 400;
    document.querySelectorAll('#hms-toast-container .hms-toast').forEach(function (el) {
        setTimeout(function () {
            el.classList.add('hms-toast--fade-out');
            setTimeout(function () { el.remove(); }, FADE);
        }, DELAY);
    });
})();
</script>
</c:if>
