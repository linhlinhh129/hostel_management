<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/assets/js/hostel-app.js?v=3"></script>
<script>
/* Row-click: click vào bất kỳ chỗ nào trên row để vào detail
   - Chỉ kích hoạt khi <tr> có data-href
   - Bỏ qua nếu người dùng đang click vào <a>, <button>, <input>, <select>
*/
document.addEventListener('click', function (e) {
    var tr = e.target.closest('tr[data-href]');
    if (!tr) return;
    if (e.target.closest('a, button, input, select, textarea, label')) return;
    window.location.href = tr.dataset.href;
});
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('tr[data-href]').forEach(function (tr) {
        tr.style.cursor = 'pointer';
    });
});
</script>

<c:if test="${currentFacilityStatus == 'INACTIVE'}">
<script>
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('form[method="post"] input, form[method="post"] select, form[method="post"] textarea').forEach(function(el) {
        if (el.type !== 'hidden') {
            el.setAttribute('disabled', 'disabled');
            el.setAttribute('readonly', 'readonly');
            el.style.backgroundColor = '#f3f4f6';
        }
    });

    document.querySelectorAll('form[method="post"] button[type="submit"], form[method="post"] input[type="submit"]').forEach(function(btn) {
        btn.setAttribute('disabled', 'disabled');
        btn.style.opacity = '0.5';
        btn.style.cursor = 'not-allowed';
        btn.title = 'Cơ sở đã bị vô hiệu hoá';
    });

    document.querySelectorAll('.btn-add, .btn-edit, .quick-action-btn:not(.btn-secondary), a.btn-primary, button.btn-primary, a.btn-mintlify-primary, button.btn-mintlify-primary, a.btn-mintlify-accent, button.btn-mintlify-accent').forEach(function(el) {
        if (!el.closest('.topbar') && !el.closest('.sidebar') && !el.closest('.filter-bar')) {
            el.style.display = 'none';
        }
    });
});
</script>
</c:if>
</body>
</html>
