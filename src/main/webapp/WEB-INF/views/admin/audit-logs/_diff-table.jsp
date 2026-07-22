<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ taglib prefix="c"  uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%--
  Fragment: _diff-table.jsp
  Parse và render bảng diff Audit Log hoàn toàn trong JSP.
  Dùng scriptlet nhỏ để xử lý string phức tạp mà JSTL EL không làm được.
--%>
<%
    /* ── Lấy dữ liệu từ request scope ─────────────────────────── */
    com.quanlyphongtro.model.AuditLog _log =
        (com.quanlyphongtro.model.AuditLog) request.getAttribute("auditLog");

    String _oldRaw = _log != null && _log.getOldValue() != null ? _log.getOldValue().trim() : "";
    String _newRaw = _log != null && _log.getNewValue() != null ? _log.getNewValue().trim() : "";
    String _action = _log != null && _log.getAction()   != null ? _log.getAction()           : "";

    boolean _hasOld  = !_oldRaw.isEmpty();
    boolean _hasNew  = !_newRaw.isEmpty();
    boolean _isMulti = _hasOld && _hasNew && _oldRaw.contains(" | ") && _newRaw.contains(" | ");

    /* ── Bảng dịch tên field ───────────────────────────────────── */
    Map<String,String> _fldLbl = new LinkedHashMap<>();
    _fldLbl.put("fullName","Họ tên"); _fldLbl.put("role","Vai trò");
    _fldLbl.put("facilityId","Cơ sở"); _fldLbl.put("status","Trạng thái");
    _fldLbl.put("email","Email"); _fldLbl.put("phone","Điện thoại");
    _fldLbl.put("name","Tên cơ sở"); _fldLbl.put("code","Mã");
    _fldLbl.put("title","Tiêu đề");

    /* ── Bảng dịch giá trị ────────────────────────────────────── */
    Map<String,String> _valLbl = new LinkedHashMap<>();
    _valLbl.put("MANAGER","Ban Quản lý"); _valLbl.put("OPERATOR","Nhân viên vận hành");
    _valLbl.put("ACTIVE","Hoạt động"); _valLbl.put("INACTIVE","Không hoạt động");
    _valLbl.put("LOCKED","Bị khóa"); _valLbl.put("DRAFT","Nháp");
    _valLbl.put("SENT","Đã gửi"); _valLbl.put("PENDING","Chờ xử lý");
    _valLbl.put("APPROVED","Đã duyệt"); _valLbl.put("REJECTED","Từ chối");
    _valLbl.put("IN_PROGRESS","Đang xử lý"); _valLbl.put("COMPLETED","Hoàn thành");
    _valLbl.put("SCHEDULED","Đã lên lịch"); _valLbl.put("CANCELLED","Đã hủy");
    _valLbl.put("DONE","Hoàn thành"); _valLbl.put("ASSIGNED","Đã phân công");
    _valLbl.put("AVAILABLE","Trống"); _valLbl.put("OCCUPIED","Đang thuê");
    _valLbl.put("MAINTENANCE","Đang bảo trì"); _valLbl.put("RESERVED","Đã đặt cọc");
    _valLbl.put("UNPAID","Chưa thanh toán"); _valLbl.put("PAID","Đã thanh toán");
    _valLbl.put("OVERDUE","Quá hạn");

    /* ── Helper: dịch giá trị ─────────────────────────────────── */
    // (dùng inner interface pattern thông qua anonymous class)

    /* ── Parse KV: "k=v | k=v" → LinkedHashMap ───────────────── */
    java.util.function.Function<String,Map<String,String>> parseKV = raw -> {
        Map<String,String> m = new LinkedHashMap<>();
        if (raw == null || raw.isEmpty()) return m;
        for (String p : raw.split(" \\| ")) {
            int idx = p.indexOf('=');
            if (idx > 0) m.put(p.substring(0,idx).trim(), p.substring(idx+1).trim());
            else if (m.isEmpty()) m.put("fullName", p.trim());
        }
        return m;
    };

    /* ── Helper: dịch giá trị ─────────────────────────────────── */
    java.util.function.Function<String,String> xlate = v -> {
        if (v == null || v.isEmpty() || v.equals("null") || v.equals("NA")) return "—";
        v = v.trim();
        if (v.startsWith("Old Total: ")) return v.substring(11).trim();
        if (v.startsWith("New Total: ")) return v.substring(11).trim();
        String t = _valLbl.get(v);
        if (t != null) return t;
        if (v.startsWith("INACTIVE (") && v.endsWith(")"))
            return "Không hoạt động (" + v.substring(10, v.length()-1) + ")";
        return v;
    };

    /* ── Xác định nhãn field cho single-value ─────────────────── */
    Set<String> _statusActs = new HashSet<>(Arrays.asList(
        "ACTIVATE","DEACTIVATE","LOCK_EMPLOYEE","UNLOCK_EMPLOYEE","UPDATE_STATUS"));
    Set<String> _knownSt = new HashSet<>(Arrays.asList(
        "PENDING","IN_PROGRESS","COMPLETED","SCHEDULED","REJECTED","APPROVED",
        "CANCELLED","DONE","ASSIGNED","AVAILABLE","OCCUPIED","MAINTENANCE",
        "RESERVED","UNPAID","PAID","OVERDUE","DRAFT","SENT"));
    String _singleLabel = "Giá trị";
    if (_statusActs.contains(_action)) _singleLabel = "Trạng thái";
    else if ("UPDATE_AREA".equals(_action)) _singleLabel = "Diện tích";
    else if ("UPDATE".equals(_action) && (_oldRaw.startsWith("Old Total: ") || _newRaw.startsWith("New Total: "))) _singleLabel = "Tổng tiền";
    else if ("UPDATE".equals(_action) && (_knownSt.contains(_oldRaw) || _knownSt.contains(_newRaw))) _singleLabel = "Trạng thái";
    else if ("UPDATE".equals(_action)) _singleLabel = "Tên cơ sở";
    else if ("CREATE_EMPLOYEE".equals(_action) || "DELETE_EMPLOYEE".equals(_action)) _singleLabel = "Tên tài khoản";
    else if ("CREATE".equals(_action)) _singleLabel = "Mã";

    /* ── Build rows ───────────────────────────────────────────── */
    // row: String[]{label, oldDisplay, newDisplay, "1"=changed/"0"}
    // oldDisplay/newDisplay = null nghĩa là không có cột đó
    List<String[]> _rows = new ArrayList<>();
    boolean _showOldCol, _showNewCol;

    if (_isMulti) {
        Map<String,String> _om = parseKV.apply(_oldRaw);
        Map<String,String> _nm = parseKV.apply(_newRaw);
        Set<String> _keys = new LinkedHashSet<>(_om.keySet());
        _keys.addAll(_nm.keySet());
        _showOldCol = true; _showNewCol = true;
        for (String k : _keys) {
            String ov = _om.getOrDefault(k,"");
            String nv = _nm.getOrDefault(k,"");
            String lbl = _fldLbl.getOrDefault(k, k);
            _rows.add(new String[]{lbl, xlate.apply(ov), xlate.apply(nv), ov.equals(nv)?"0":"1"});
        }
    } else {
        _showOldCol = _hasOld;
        _showNewCol = _hasNew;
        String ov = _hasOld ? xlate.apply(_oldRaw) : null;
        String nv = _hasNew ? xlate.apply(_newRaw) : null;
        String chg = (_hasOld && _hasNew && !_oldRaw.equals(_newRaw)) ? "1" : "0";
        _rows.add(new String[]{_singleLabel, ov, nv, chg});
    }

    int _changedCount = 0;
    for (String[] r : _rows) if ("1".equals(r[3])) _changedCount++;

    request.setAttribute("_diffRows",      _rows);
    request.setAttribute("_diffShowOld",   _showOldCol);
    request.setAttribute("_diffShowNew",   _showNewCol);
    request.setAttribute("_diffHasData",   _hasOld || _hasNew);
    request.setAttribute("_diffChanged",   _changedCount);
%>

<c:choose>
    <%-- Không có dữ liệu --%>
    <c:when test="${not _diffHasData}">
        <div class="diff-empty">
            <div class="diff-empty__icon">&#128203;</div>
            <p class="diff-empty__text">Không có dữ liệu thay đổi.</p>
        </div>
    </c:when>

    <c:otherwise>
        <%-- Banner --%>
        <c:if test="${_diffChanged gt 0}">
            <div class="diff-banner">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#c37d0d" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <line x1="12" y1="8" x2="12" y2="12"/>
                    <line x1="12" y1="16" x2="12.01" y2="16"/>
                </svg>
                <span class="diff-banner__text">${_diffChanged} trường có thay đổi</span>
            </div>
        </c:if>

        <%-- Bảng diff --%>
        <table class="diff-table">
            <thead>
                <tr>
                    <th class="diff-th diff-th--field">Trường</th>
                    <c:if test="${_diffShowOld}">
                        <th class="diff-th diff-th--old">Trước</th>
                    </c:if>
                    <c:if test="${_diffShowNew}">
                        <th class="diff-th diff-th--new">Sau</th>
                    </c:if>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="row" items="${_diffRows}">
                    <c:set var="isChanged" value="${row[3] == '1'}"/>
                    <c:choose>
                        <c:when test="${isChanged}">
                            <tr class="diff-row--changed">
                                <td class="diff-td--field"><c:out value="${row[0]}"/></td>
                                <c:if test="${_diffShowOld}">
                                    <td class="diff-td--old-changed"><c:out value="${row[1]}"/></td>
                                </c:if>
                                <c:if test="${_diffShowNew}">
                                    <td class="diff-td--new-changed"><c:out value="${row[2]}"/></td>
                                </c:if>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td class="diff-td--field"><c:out value="${row[0]}"/></td>
                                <c:if test="${_diffShowOld}">
                                    <td class="diff-td"><c:out value="${row[1]}"/></td>
                                </c:if>
                                <c:if test="${_diffShowNew}">
                                    <td class="diff-td"><c:out value="${row[2]}"/></td>
                                </c:if>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>
