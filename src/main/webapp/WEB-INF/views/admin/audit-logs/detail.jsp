<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx"       value="${pageContext.request.contextPath}"/>
<c:set var="pageTitle" value="Chi tiết Audit Log - Admin"/>
<c:set var="pageRole"  value="ADMIN"/>
<c:set var="activeMenu" value="audit-logs"/>
<jsp:include page="/WEB-INF/views/layout/head.jsp"/>
<body>
<div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp"/>
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
        <jsp:include page="/WEB-INF/views/layout/topbar.jsp"/>
        <main class="page-content">
            <jsp:include page="/WEB-INF/views/layout/alerts.jsp"/>

            <%-- Page header --%>
            <div class="page-header d-flex flex-wrap justify-content-between align-items-center gap-3">
                <div>
                    <h1>Nhật ký
                        <span style="font-family:var(--hms-font-mono);color:var(--hms-accent-deep)">
                            #<c:out value="${auditLog.id}"/>
                        </span>
                    </h1>
                    <p>Chi tiết hoạt động thao tác trong hệ thống</p>
                </div>
                <a href="javascript:history.back()" class="quick-action-btn">← Danh sách</a>
            </div>

            <%-- ─── Content grid ─────────────────────────────────────────── --%>
            <div class="row g-3" style="max-width:960px">

                <%-- ── Cột trái: Thông tin chung ──────────────────────────── --%>
                <div class="col-lg-5">
                    <div class="widget-surface h-100">
                        <div class="widget-surface-header">
                            <h3 style="display:flex;align-items:center;gap:8px">
                                <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
                                     stroke="var(--hms-accent-deep)" stroke-width="2">
                                    <circle cx="12" cy="12" r="10"/>
                                    <line x1="12" y1="8" x2="12" y2="12"/>
                                    <line x1="12" y1="16" x2="12.01" y2="16"/>
                                </svg>
                                Thông tin chung
                            </h3>
                        </div>
                        <div class="widget-surface-body" style="padding:0">

                            <%-- Mỗi dòng dùng cấu trúc flex nhất quán --%>
                            <dl style="margin:0">

                                <%-- Log ID --%>
                                <div style="display:flex;align-items:center;padding:12px 20px;
                                            border-bottom:1px solid var(--hms-border-soft);gap:12px">
                                    <dt style="width:44%;flex-shrink:0;font-size:0.8125rem;
                                               color:var(--hms-text-muted);font-weight:500">Log ID</dt>
                                    <dd style="margin:0;font-family:var(--hms-font-mono);
                                               font-weight:700;color:var(--hms-accent-deep);font-size:0.875rem">
                                        #<c:out value="${auditLog.id}"/>
                                    </dd>
                                </div>

                                <%-- Thời gian — format đẹp hơn bằng JS --%>
                                <div style="display:flex;align-items:center;padding:12px 20px;
                                            border-bottom:1px solid var(--hms-border-soft);gap:12px">
                                    <dt style="width:44%;flex-shrink:0;font-size:0.8125rem;
                                               color:var(--hms-text-muted);font-weight:500">Thời gian</dt>
                                    <dd style="margin:0;font-size:0.8125rem;color:var(--hms-text-secondary)">
                                        <span id="log-time" title="<c:out value='${auditLog.createdAt}'/>">
                                            <c:out value="${auditLog.createdAt}"/>
                                        </span>
                                        <script>
                                        (function(){
                                            var raw = '<c:out value="${auditLog.createdAt}"/>';
                                            try {
                                                var d = new Date(raw);
                                                var fmt = d.toLocaleDateString('vi-VN',{day:'2-digit',month:'2-digit',year:'numeric'})
                                                    + ' lúc '
                                                    + d.toLocaleTimeString('vi-VN',{hour:'2-digit',minute:'2-digit',second:'2-digit'});
                                                document.getElementById('log-time').textContent = fmt;
                                            } catch(e){}
                                        })();
                                        </script>
                                    </dd>
                                </div>

                                <%-- Người thực hiện --%>
                                <div style="display:flex;align-items:center;padding:12px 20px;
                                            border-bottom:1px solid var(--hms-border-soft);gap:12px">
                                    <dt style="width:44%;flex-shrink:0;font-size:0.8125rem;
                                               color:var(--hms-text-muted);font-weight:500">Người thực hiện</dt>
                                    <dd style="margin:0;display:flex;align-items:center;gap:8px">
                                        <span style="font-size:0.875rem;font-weight:600;color:var(--hms-text)">
                                            <c:out value="${auditLog.createdByName}"/>
                                        </span>
                                    </dd>
                                </div>

                                <%-- Chức năng + tên entity --%>
                                <div style="display:flex;align-items:flex-start;padding:12px 20px;
                                            border-bottom:1px solid var(--hms-border-soft);gap:12px">
                                    <dt style="width:44%;flex-shrink:0;font-size:0.8125rem;padding-top:2px;
                                               color:var(--hms-text-muted);font-weight:500">Đối tượng</dt>
                                    <dd style="margin:0;display:flex;flex-direction:column;gap:4px">
                                        
                                        <span style="font-size:0.875rem;font-weight:600;color:var(--hms-text);line-height:1.4">
                                            <c:choose>
                                                <c:when test="${auditLog.entityType == 'facilities'}">Cơ sở</c:when>
                                                <c:when test="${auditLog.entityType == 'rooms'}">Phòng</c:when>
                                                <c:when test="${auditLog.entityType == 'users'}">Nhân sự</c:when>
                                                <c:when test="${auditLog.entityType == 'notifications'}">Thông báo</c:when>
                                                <c:when test="${auditLog.entityType == 'invoices'}">Hóa đơn</c:when>
                                                <c:when test="${auditLog.entityType == 'payments'}">Thanh toán</c:when>
                                                <c:when test="${auditLog.entityType == 'requests'}">Yêu cầu</c:when>
                                                <c:when test="${auditLog.entityType == 'meter_readings'}">Số điện nước</c:when>
                                                <c:otherwise>Hệ thống</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </dd>
                                </div>

                                <%-- Hành động --%>
                                <div style="display:flex;align-items:center;padding:12px 20px;
                                            border-bottom:1px solid var(--hms-border-soft);gap:12px">
                                    <dt style="width:44%;flex-shrink:0;font-size:0.8125rem;
                                               color:var(--hms-text-muted);font-weight:500">Hành động</dt>
                                    <dd style="margin:0">
                                        <c:choose>
                                            <c:when test="${auditLog.action == 'CREATE' or auditLog.action == 'CREATE_EMPLOYEE'}">
                                                <span class="badge-hms badge-success">
                                                    <c:choose>
                                                        <c:when test="${auditLog.action == 'CREATE'}">Tạo mới</c:when>
                                                        <c:otherwise>Tạo nhân sự</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </c:when>
                                            <c:when test="${auditLog.action == 'DELETE' or auditLog.action == 'LOCK_EMPLOYEE'
                                                            or auditLog.action == 'DEACTIVATE' or auditLog.action == 'DELETE_EMPLOYEE'}">
                                                <span class="badge-hms badge-danger">
                                                    <c:choose>
                                                        <c:when test="${auditLog.action == 'DELETE'}">Xóa</c:when>
                                                        <c:when test="${auditLog.action == 'DELETE_EMPLOYEE'}">Xóa nhân sự</c:when>
                                                        <c:when test="${auditLog.action == 'LOCK_EMPLOYEE'}">Khóa tài khoản</c:when>
                                                        <c:otherwise>Vô hiệu hóa</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </c:when>
                                            <c:when test="${auditLog.action == 'UPDATE' or auditLog.action == 'UPDATE_STATUS'
                                                            or auditLog.action == 'UPDATE_AREA' or auditLog.action == 'UPDATE_EMPLOYEE'
                                                            or auditLog.action == 'UPDATE_ELECTRICITY' or auditLog.action == 'UPDATE_WATER'}">
                                                <span class="badge-hms badge-info">
                                                    <c:choose>
                                                        <c:when test="${auditLog.action == 'UPDATE'}">Cập nhật</c:when>
                                                        <c:when test="${auditLog.action == 'UPDATE_STATUS'}">Đổi trạng thái</c:when>
                                                        <c:when test="${auditLog.action == 'UPDATE_AREA'}">Cập nhật diện tích</c:when>
                                                        <c:when test="${auditLog.action == 'UPDATE_ELECTRICITY'}">Cập nhật số điện</c:when>
                                                        <c:when test="${auditLog.action == 'UPDATE_WATER'}">Cập nhật số nước</c:when>
                                                        <c:otherwise>Sửa nhân sự</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </c:when>
                                            <c:when test="${auditLog.action == 'ACTIVATE' or auditLog.action == 'UNLOCK_EMPLOYEE'}">
                                                <span class="badge-hms badge-accent">
                                                    <c:choose>
                                                        <c:when test="${auditLog.action == 'ACTIVATE'}">Kích hoạt</c:when>
                                                        <c:otherwise>Mở khóa</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-hms badge-neutral">
                                                    <c:out value="${auditLog.action}"/>
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </dd>
                                </div>

                                <%-- IP --%>
                                <c:if test="${not empty auditLog.ipAddress}">
                                    <div style="display:flex;align-items:center;padding:12px 20px;
                                                border-bottom:1px solid var(--hms-border-soft);gap:12px">
                                        <dt style="width:44%;flex-shrink:0;font-size:0.8125rem;
                                                   color:var(--hms-text-muted);font-weight:500">Địa chỉ IP</dt>
                                        <dd style="margin:0;font-family:var(--hms-font-mono);
                                                   font-size:0.8125rem;color:var(--hms-text-secondary)">
                                            <c:out value="${auditLog.ipAddress}"/>
                                        </dd>
                                    </div>
                                </c:if>

                                <%-- Ghi chú --%>
                                <c:if test="${not empty auditLog.comment}">
                                    <div style="display:flex;align-items:flex-start;padding:12px 20px;gap:12px">
                                        <dt style="width:44%;flex-shrink:0;font-size:0.8125rem;padding-top:1px;
                                                   color:var(--hms-text-muted);font-weight:500">Ghi chú</dt>
                                        <dd style="margin:0;font-size:0.8125rem;color:var(--hms-text-secondary);
                                                   line-height:1.5">
                                            <c:out value="${auditLog.comment}"/>
                                        </dd>
                                    </div>
                                </c:if>

                            </dl>
                        </div>
                    </div>
                </div>

                <%-- ── Cột phải: Thay đổi dữ liệu ─────────────────────────── --%>
                <div class="col-lg-7">
                    <div class="widget-surface h-100">
                        <div class="widget-surface-header">
                            <h3 style="display:flex;align-items:center;gap:8px">
                                <svg width="15" height="15" viewBox="0 0 24 24" fill="none"
                                     stroke="var(--hms-accent-deep)" stroke-width="2">
                                    <polyline points="16 3 21 3 21 8"/>
                                    <line x1="4" y1="20" x2="21" y2="3"/>
                                    <polyline points="21 16 21 21 16 21"/>
                                    <line x1="15" y1="15" x2="21" y2="21"/>
                                </svg>
                                Thay đổi dữ liệu
                            </h3>
                            <%-- Badge tóm tắt --%>
                            <c:choose>
                                <c:when test="${not empty auditLog.oldValue and not empty auditLog.newValue}">
                                    <span class="badge-hms badge-info" style="font-size:0.6875rem">Trước &amp; Sau</span>
                                </c:when>
                                <c:when test="${empty auditLog.oldValue and not empty auditLog.newValue}">
                                    <span class="badge-hms badge-success" style="font-size:0.6875rem">Tạo mới</span>
                                </c:when>
                                <c:when test="${not empty auditLog.oldValue and empty auditLog.newValue}">
                                    <span class="badge-hms badge-danger" style="font-size:0.6875rem">Đã xóa</span>
                                </c:when>
                            </c:choose>
                        </div>
                        <div class="widget-surface-body">

                            <%-- Một script JS duy nhất xử lý tất cả các case --%>
                            <div id="audit-diff-container"></div>
                            <script>
                            (function() {
                                var action = '<c:out value="${auditLog.action}"/>';
                                var oldRaw = '<c:out value="${auditLog.oldValue}" escapeXml="false"/>';
                                var newRaw = '<c:out value="${auditLog.newValue}" escapeXml="false"/>';

                                // ── Bảng dịch nhãn trường ──────────────────────────────────
                                var FIELD_LABELS = {
                                    'fullName':   'Họ tên',
                                    'role':       'Vai trò',
                                    'facilityId': 'Cơ sở',
                                    'status':     'Trạng thái',
                                    'email':      'Email',
                                    'phone':      'Điện thoại',
                                    'name':       'Tên cơ sở',
                                    'code':       'Mã',
                                    'title':      'Tiêu đề'
                                };
                                // ── Bảng dịch giá trị ──────────────────────────────────────
                                var VALUE_LABELS = {
                                    'MANAGER':  'Ban Quản lý',
                                    'OPERATOR': 'Nhân viên vận hành',
                                    'ACTIVE':   'Hoạt động',
                                    'INACTIVE': 'Không hoạt động',
                                    'INACTIVE (End Rental)': 'Không hoạt động (Kết thúc hợp đồng)',
                                    'INACTIVE (Manual)':     'Không hoạt động (Thủ công)',
                                    'INACTIVE (Locked)':     'Không hoạt động (Bị khóa)',
                                    'LOCKED':   'Bị khóa',
                                    'DRAFT':    'Nháp',
                                    'SENT':     'Đã gửi',
                                    'PENDING':  'Chờ xử lý',
                                    'APPROVED': 'Đã duyệt',
                                    'REJECTED': 'Từ chối',
                                    'IN_PROGRESS': 'Đang xử lý',
                                    'COMPLETED': 'Hoàn thành',
                                    'SCHEDULED': 'Đã lên lịch',
                                    'CANCELLED': 'Đã hủy',
                                    'DONE':      'Hoàn thành',
                                    'ASSIGNED':  'Đã phân công',
                                    'AVAILABLE': 'Trống',
                                    'OCCUPIED':  'Đang thuê',
                                    'MAINTENANCE': 'Đang bảo trì',
                                    'RESERVED':  'Đã đặt cọc',
                                    'UNPAID':    'Chưa thanh toán',
                                    'PAID':      'Đã thanh toán',
                                    'OVERDUE':   'Quá hạn',
                                    'null':     '—',
                                    'NA':       '—'
                                };

                                function fieldLabel(k)  { return FIELD_LABELS[k] || k; }
                                function valueLabel(v)  {
                                    if (!v || v === 'null' || v === 'undefined' || v === 'NA') return '—';
                                    var vStr = String(v).trim();
                                    if (vStr.indexOf('Old Total: ') === 0) return vStr.substring(11).trim();
                                    if (vStr.indexOf('New Total: ') === 0) return vStr.substring(11).trim();
                                    if (VALUE_LABELS[vStr]) return VALUE_LABELS[vStr];
                                    // Dịch dạng "INACTIVE (xxx)" chưa có trong bảng
                                    var m = vStr.match(/^INACTIVE\s*\((.+)\)$/);
                                    if (m) return 'Không hoạt động (' + m[1] + ')';
                                    return vStr;
                                }

                                // ── Parse chuỗi "key=value | key=value" thành map ──────────
                                function parseKV(str) {
                                    var map = {};
                                    if (!str) return map;
                                    str.split(' | ').forEach(function(p) {
                                        var idx = p.indexOf('=');
                                        if (idx > -1) {
                                            map[p.substring(0, idx).trim()] = p.substring(idx + 1).trim();
                                        } else if (Object.keys(map).length === 0) {
                                            // Giá trị plain text đầu tiên → fullName
                                            map['fullName'] = p.trim();
                                        }
                                    });
                                    return map;
                                }

                                // ── Xác định nhãn trường cho giá trị đơn ──────────────────
                                function singleFieldLabel(action) {
                                    var statusActions = ['ACTIVATE','DEACTIVATE','LOCK_EMPLOYEE',
                                                         'UNLOCK_EMPLOYEE','UPDATE_STATUS'];
                                    if (statusActions.indexOf(action) > -1) return 'Trạng thái';
                                    if (action === 'UPDATE_AREA')  return 'Diện tích';
                                    
                                    var knownStatuses = ['PENDING', 'IN_PROGRESS', 'COMPLETED', 'SCHEDULED', 'REJECTED', 'APPROVED', 'CANCELLED', 'DONE', 'ASSIGNED', 'AVAILABLE', 'OCCUPIED', 'MAINTENANCE', 'RESERVED', 'UNPAID', 'PAID', 'OVERDUE', 'DRAFT', 'SENT'];
                                    if (action === 'UPDATE' && (knownStatuses.indexOf(oldRaw.trim()) > -1 || knownStatuses.indexOf(newRaw.trim()) > -1)) {
                                        return 'Trạng thái';
                                    }
                                    if (oldRaw.trim().indexOf('Old Total: ') === 0 || newRaw.trim().indexOf('New Total: ') === 0) {
                                        return 'Tổng tiền';
                                    }
                                    
                                    if (action === 'UPDATE')       return 'Tên cơ sở';
                                    return 'Giá trị';
                                }

                                // ── Tạo plain-text row map cho action chỉ lưu plain text ───
                                // CREATE: newVal = code/username → hiển thị dạng 1 dòng "Mã / Tên tài khoản"
                                // UPDATE facility: old=code, new=name → 2 dòng khác nhau loại
                                // DELETE_EMPLOYEE: oldVal = username
                                function makeSingleMap(val, label) {
                                    var m = {};
                                    m[label] = val;
                                    return m;
                                }

                                // ── Render HTML bảng diff ───────────────────────────────────
                                function renderTable(rows, hasOldCol, hasNewCol) {
                                    // rows = [{label, old, new}]
                                    var changedCount = rows.filter(function(r){ return r.hasOwnProperty('old') && r.old !== r.new; }).length;

                                    var html = '';
                                    if (changedCount > 0) {
                                        html += '<div style="display:flex;align-items:center;gap:8px;padding:8px 12px;margin-bottom:12px;'
                                             + 'border-radius:8px;background:rgba(255,200,0,0.08);border:1px solid rgba(255,200,0,0.25)">'
                                             + '<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#c37d0d" stroke-width="2">'
                                             + '<circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>'
                                             + '<span style="font-size:0.8rem;color:#c37d0d;font-weight:600">'
                                             + changedCount + ' trường có thay đổi</span></div>';
                                    }

                                    var TH = 'padding:8px 10px;text-align:left;font-size:0.75rem;font-weight:700;text-transform:uppercase;letter-spacing:.04em;border-bottom:1px solid var(--hms-border)';
                                    html += '<table style="width:100%;font-size:0.8125rem;border-collapse:collapse">'
                                         + '<thead><tr style="background:var(--hms-surface-soft)">'
                                         + '<th style="' + TH + ';color:var(--hms-text-muted);width:30%">Trường</th>';
                                    if (hasOldCol) html += '<th style="' + TH + ';color:var(--hms-danger)">Trước</th>';
                                    if (hasNewCol) html += '<th style="' + TH + ';color:var(--hms-success)">Sau</th>';
                                    html += '</tr></thead><tbody>';

                                    rows.forEach(function(r) {
                                        var changed = r.hasOwnProperty('old') && r.old !== r.new;
                                        var oldDisp = hasOldCol ? valueLabel(r.old || '—') : '';
                                        var newDisp = hasNewCol ? valueLabel(r.new || '—') : '';
                                        html += '<tr style="' + (changed ? 'background:rgba(255,200,0,0.05)' : '') + '">'
                                            + '<td style="padding:9px 10px;color:var(--hms-text-muted);border-bottom:1px solid var(--hms-border-soft);font-weight:500">' + r.label + '</td>';
                                        if (hasOldCol) {
                                            html += '<td style="padding:9px 10px;border-bottom:1px solid var(--hms-border-soft);'
                                                 + (changed ? 'color:var(--hms-danger);text-decoration:line-through;opacity:.75' : 'color:var(--hms-text-secondary)')
                                                 + '">' + oldDisp + '</td>';
                                        }
                                        if (hasNewCol) {
                                            html += '<td style="padding:9px 10px;border-bottom:1px solid var(--hms-border-soft);'
                                                 + (changed ? 'color:var(--hms-success);font-weight:600' : 'color:var(--hms-text-secondary)')
                                                 + '">' + newDisp + '</td>';
                                        }
                                        html += '</tr>';
                                    });
                                    html += '</tbody></table>';
                                    return html;
                                }

                                // ── Phân loại và render ────────────────────────────────────
                                var html = '';
                                var hasOld = oldRaw && oldRaw.trim() !== '';
                                var hasNew = newRaw && newRaw.trim() !== '';

                                if (hasOld && hasNew && oldRaw.indexOf('|') > -1 && newRaw.indexOf('|') > -1) {
                                    // Case 1: UPDATE_EMPLOYEE — multi-field diff
                                    var oldMap = parseKV(oldRaw);
                                    var newMap = parseKV(newRaw);
                                    var allKeys = [];
                                    [oldMap, newMap].forEach(function(m){
                                        Object.keys(m).forEach(function(k){ if(allKeys.indexOf(k)<0) allKeys.push(k); });
                                    });
                                    var rows = allKeys.map(function(k){
                                        return { label: fieldLabel(k), old: oldMap[k], new: newMap[k] };
                                    });
                                    html = renderTable(rows, true, true);

                                } else if (hasOld && hasNew) {
                                    // Case 2: LOCK/UNLOCK/ACTIVATE/DEACTIVATE/UPDATE — single-field diff
                                    var label = singleFieldLabel(action);
                                    html = renderTable([{ label: label, old: oldRaw.trim(), new: newRaw.trim() }], true, true);

                                } else if (!hasOld && hasNew) {
                                    // Case 3: CREATE — chỉ có "Sau"
                                    var label = (action === 'CREATE_EMPLOYEE') ? 'Tên tài khoản' : 'Mã';
                                    html = renderTable([{ label: label, new: newRaw.trim() }], false, true);

                                } else if (hasOld && !hasNew) {
                                    // Case 4: DELETE — chỉ có "Trước"
                                    var label = (action === 'DELETE_EMPLOYEE') ? 'Tên tài khoản' : 'Mã';
                                    html = renderTable([{ label: label, old: oldRaw.trim() }], true, false);

                                } else {
                                    // Case 5: Không có gì
                                    html = '<div style="text-align:center;padding:2.5rem 1rem">'
                                         + '<div style="font-size:2rem;margin-bottom:.5rem">📋</div>'
                                         + '<p style="color:var(--hms-text-muted);font-size:.875rem;margin:0">Không có dữ liệu thay đổi.</p>'
                                         + '</div>';
                                }

                                document.getElementById('audit-diff-container').innerHTML = html;
                            })();
                            </script>
                        </div>
                    </div>
                </div>

            </div><%-- /row --%>
        </main>
    </div>
</div>
<jsp:include page="/WEB-INF/views/layout/footer.jsp"/>
</body>
</html>
