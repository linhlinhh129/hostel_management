<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/assets/js/hostel-app.js"></script>
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
</body>
</html>
