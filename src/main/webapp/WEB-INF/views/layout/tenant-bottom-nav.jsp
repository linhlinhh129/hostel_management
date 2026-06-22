<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="menu" value="${activeMenu}"/>

<nav class="bottom-nav">
    <a href="${ctx}/tenant/dashboard" class="bottom-nav-item${menu == 'dashboard' ? ' active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/></svg>
        <span>Trang chủ</span>
    </a>
    <a href="${ctx}/tenant/notifications" class="bottom-nav-item${menu == 'notifications' ? ' active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/></svg>
        <span>Thông báo</span>
    </a>
    <a href="${ctx}/tenant/tickets" class="bottom-nav-item${menu == 'tickets' ? ' active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/></svg>
        <span>Yêu cầu</span>
    </a>
    <a href="${ctx}/tenant/invoices" class="bottom-nav-item${menu == 'invoices' ? ' active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="1" y="4" width="22" height="16" rx="2"/><line x1="1" y1="10" x2="23" y2="10"/></svg>
        <span>Hóa đơn</span>
    </a>
    <a href="${ctx}/tenant/payments" class="bottom-nav-item${menu == 'payments' ? ' active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
        <span>Giao dịch</span>
    </a>
    <a href="${ctx}/tenant/profile" class="bottom-nav-item${menu == 'profile' ? ' active' : ''}">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
        <span>Hồ sơ</span>
    </a>
</nav>
