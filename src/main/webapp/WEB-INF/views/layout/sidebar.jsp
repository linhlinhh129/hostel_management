<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <c:set var="ctx" value="${pageContext.request.contextPath}" />
        <c:set var="role"
            value="${not empty sessionScope.currentUser.role ? sessionScope.currentUser.role : pageRole}" />
        <c:set var="menu" value="${activeMenu}" />

        <aside class="sidebar">
            <div class="sidebar-brand">
                <div class="sidebar-brand-icon">HT</div>
                <div class="sidebar-brand-text">
                    <c:choose>
                        <c:when test="${role == 'TENANT'}">Cổng cư dân</c:when>
                        <c:when test="${role == 'OPERATOR'}">Vận hành cơ sở</c:when>
                        <c:otherwise>Quản lý Nhà trọ</c:otherwise>
                    </c:choose>
                    <small>
                        <c:choose>
                            <c:when test="${role == 'ADMIN'}">Admin Panel</c:when>
                            <c:when test="${role == 'MANAGER'}">Ban Quản lý<c:if
                                    test="${not empty sessionScope.currentUser.facilityCode}"> ·
                                    <c:out value="${sessionScope.currentUser.facilityCode}" />
                                </c:if>
                            </c:when>
                            <c:when test="${role == 'TENANT'}">
                                <c:out
                                    value="${empty sessionScope.currentUser.roomCode ? 'Cư dân' : sessionScope.currentUser.roomCode}" />
                            </c:when>
                            <c:when test="${role == 'OPERATOR'}">Kỹ thuật</c:when>
                        </c:choose>
                    </small>
                </div>
            </div>

            <nav class="sidebar-nav">
                <c:if test="${role == 'ADMIN'}">
                    <a href="${ctx}/admin/dashboard" class="sidebar-link${menu == 'dashboard' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="3" width="7" height="7" />
                            <rect x="14" y="3" width="7" height="7" />
                            <rect x="3" y="14" width="7" height="7" />
                            <rect x="14" y="14" width="7" height="7" />
                        </svg>
                        Dashboard
                    </a>
                    <div class="sidebar-section">Quản lý</div>
                    <a href="${ctx}/admin/facilities"
                        class="sidebar-link${menu == 'hostels' or menu == 'facilities' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M3 21h18M5 21V7l8-4v18M19 21V11l-6-4" />
                        </svg>
                        Quản lý cơ sở
                    </a>
                    <a href="${ctx}/admin/revenue" class="sidebar-link${menu == 'revenue' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="12" y1="1" x2="12" y2="23" />
                            <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
                        </svg>
                        Báo cáo doanh thu
                    </a>
                    <a href="${ctx}/admin/personnel" class="sidebar-link${menu == 'personnel' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
                            <path d="M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z" />
                        </svg>
                        Quản lý nhân sự
                    </a>
                    <a href="${ctx}/admin/notifications"
                        class="sidebar-link${menu == 'notifications' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
                        </svg>
                        Thông báo
                    </a>
                    <div class="sidebar-section">Hệ thống</div>
                    <a href="${ctx}/admin/audit-logs" class="sidebar-link${menu == 'audit-logs' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                        </svg>
                        Nhật ký kiểm tra
                    </a>
                </c:if>

                <c:if test="${role == 'MANAGER'}">
                    <a href="${ctx}/manager/dashboard" class="sidebar-link${menu == 'dashboard' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="3" width="7" height="7" />
                            <rect x="14" y="3" width="7" height="7" />
                            <rect x="3" y="14" width="7" height="7" />
                            <rect x="14" y="14" width="7" height="7" />
                        </svg>
                        Dashboard
                    </a>
                    <div class="sidebar-section">Vận hành</div>
                    <a href="${ctx}/manager/tenants" class="sidebar-link${menu == 'tenants' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" />
                            <circle cx="9" cy="7" r="4" />
                        </svg>
                        Người thuê
                    </a>
                    <a href="${ctx}/manager/rooms" class="sidebar-link${menu == 'rooms' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
                            <polyline points="9 22 9 12 15 12 15 22" />
                        </svg>
                        Căn hộ / Phòng
                    </a>
                    <a href="${ctx}/manager/tickets" class="sidebar-link${menu == 'tickets' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                            <polyline points="9 11 12 14 22 4" />
                        </svg>
                        Yêu cầu
                    </a>
                    <a href="${ctx}/manager/contracts" class="sidebar-link${menu == 'contracts' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                            <polyline points="14 2 14 8 20 8" />
                            <line x1="16" y1="13" x2="8" y2="13" />
                            <line x1="16" y1="17" x2="8" y2="17" />
                        </svg>
                        Hợp đồng
                    </a>
                    <a href="${ctx}/manager/notifications"
                        class="sidebar-link${menu == 'notifications' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
                            <path d="M13.73 21a2 2 0 0 1-3.46 0" />
                        </svg>
                        Thông báo
                    </a>
                    <div class="sidebar-section">Tài chính</div>
                    <a href="${ctx}/manager/invoices" class="sidebar-link${menu == 'invoices' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="1" y="4" width="22" height="16" rx="2" ry="2" />
                            <line x1="1" y1="10" x2="23" y2="10" />
                        </svg>
                        Hóa đơn
                    </a>
                    <a href="${ctx}/manager/payments" class="sidebar-link${menu == 'payments' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6" />
                        </svg>
                        Giao dịch
                    </a>
                    <a href="${ctx}/manager/service-prices" class="sidebar-link${menu == 'service-prices' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"></path>
                            <line x1="7" y1="7" x2="7.01" y2="7"></line>
                        </svg>
                        Giá dịch vụ
                    </a>
                    <a href="${ctx}/manager/debts" class="sidebar-link${menu == 'debts' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M22 12h-4l-3 9L9 3l-3 9H2"></path>
                        </svg>
                        Công nợ
                    </a>
                </c:if>

                <c:if test="${role == 'TENANT'}">
                    <a href="${ctx}/tenant/dashboard"
                        class="sidebar-link${menu == 'dashboard' ? ' active' : ''}">Dashboard</a>
                    <a href="${ctx}/tenant/notifications"
                        class="sidebar-link${menu == 'notifications' ? ' active' : ''}">Thông báo</a>
                    <a href="${ctx}/tenant/tickets" class="sidebar-link${menu == 'tickets' ? ' active' : ''}">Yêu
                        cầu</a>
                    <a href="${ctx}/tenant/invoices" class="sidebar-link${menu == 'invoices' ? ' active' : ''}">Hóa
                        đơn</a>
                    <a href="${ctx}/tenant/payments" class="sidebar-link${menu == 'payments' ? ' active' : ''}">Giao
                        dịch</a>
                    <a href="${ctx}/tenant/profile" class="sidebar-link${menu == 'profile' ? ' active' : ''}">Hồ sơ</a>
                </c:if>

                <c:if test="${role == 'OPERATOR'}">
                    <a href="${ctx}/operator/dashboard" class="sidebar-link${menu == 'dashboard' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="3" width="7" height="7" />
                            <rect x="14" y="3" width="7" height="7" />
                            <rect x="3" y="14" width="7" height="7" />
                            <rect x="14" y="14" width="7" height="7" />
                        </svg>
                        Dashboard
                    </a>
                    <div class="sidebar-section">Tác vụ</div>
                    <a href="${ctx}/operator/meter-readings" class="sidebar-link${menu == 'meter-readings' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/></svg>
                        Danh sách điện nước
                    </a>
                    <a href="${ctx}/operator/meter-readings/update" class="sidebar-link${menu == 'meter-readings-update' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                        Cập nhật điện nước
                    </a>
                    <a href="${ctx}/operator/meter-readings/update" class="sidebar-link${menu == 'meter-readings-update' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                        Cập nhật điện nước
                    </a>
                    <a href="${ctx}/operator/requests" class="sidebar-link${menu == 'requests' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/></svg>
                        Danh sách yêu cầu (Cũ)
                    </a>
                    <a href="${ctx}/operator/tickets" class="sidebar-link${menu == 'tickets' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/></svg>
                        Danh sách yêu cầu (Mới)
                    </a>
                    <a href="${ctx}/operator/incidents/create" class="sidebar-link${menu == 'incident-report' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
                        Báo cáo sự cố
                    </a>
                    <a href="${ctx}/operator/incidents/my-reports" class="sidebar-link${menu == 'my-incidents' ? ' active' : ''}">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="14 2 14 8 20 8"/><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><path d="M12 18v-6"/><path d="M9 15l3-3 3 3"/></svg>
                        Lịch sử báo cáo
                    </a>
                </c:if>
            </nav>

            <div class="sidebar-footer">v1.0 · Hostel Management</div>
        </aside>