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

/**
 * Client-side pagination dùng chung cho các trang load toàn bộ data.
 * @param {string} tbodyId   - id của <tbody>
 * @param {string} totalId   - id của <strong> hiển thị tổng số
 * @param {string} pageId    - id của <span> hiển thị trang hiện tại
 * @param {string} totalPagesId - id của <span> hiển thị tổng trang
 * @param {string} btnsId    - id của <div> chứa nút Trước/Sau
 * @param {number} [pageSize=10]
 */
function clientPaginate(tbodyId, totalId, pageId, totalPagesId, btnsId, pageSize) {
    pageSize = pageSize || 10;
    var tbody = document.getElementById(tbodyId);
    if (!tbody) return;
    var rows = Array.from(tbody.querySelectorAll('tr'));
    var total = rows.length;
    var totalPages = Math.max(1, Math.ceil(total / pageSize));
    var current = 1;

    var elTotal      = document.getElementById(totalId);
    var elPage       = document.getElementById(pageId);
    var elTotalPages = document.getElementById(totalPagesId);
    var elBtns       = document.getElementById(btnsId);

    if (elTotal)      elTotal.textContent      = total;
    if (elTotalPages) elTotalPages.textContent = totalPages;

    function render(page) {
        current = page;
        var start = (page - 1) * pageSize;
        rows.forEach(function(row, i) {
            row.style.display = (i >= start && i < start + pageSize) ? '' : 'none';
        });
        if (elPage) elPage.textContent = page;
        if (!elBtns) return;
        elBtns.innerHTML = '';
        if (page > 1) {
            var prev = document.createElement('a');
            prev.href = '#'; prev.className = 'btn-mintlify-secondary text-decoration-none';
            prev.style.padding = '6px 14px'; prev.textContent = 'Trước';
            prev.onclick = function(e) { e.preventDefault(); render(page - 1); };
            elBtns.appendChild(prev);
        }
        if (page < totalPages) {
            var next = document.createElement('a');
            next.href = '#'; next.className = 'btn-mintlify-secondary text-decoration-none';
            next.style.padding = '6px 14px'; next.textContent = 'Sau';
            next.onclick = function(e) { e.preventDefault(); render(page + 1); };
            elBtns.appendChild(next);
        }
    }

    render(1);
}
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
<%-- PWA: Service Worker Registration --%>
<script>
if ('serviceWorker' in navigator) {
    window.addEventListener('load', function () {
        // Tự động detect context path: /hostel-management (local) hoặc / (Azure ROOT.war)
        var ctx = window.location.pathname.startsWith('/hostel-management') ? '/hostel-management' : '';
        var swPath = ctx + '/sw.js';
        var swScope = ctx + '/';

        navigator.serviceWorker.register(swPath, { scope: swScope })
            .then(function (reg) {
                console.log('[PWA] Service Worker registered. Scope:', reg.scope);

                reg.addEventListener('updatefound', function () {
                    var newWorker = reg.installing;
                    newWorker.addEventListener('statechange', function () {
                        if (newWorker.state === 'installed' && navigator.serviceWorker.controller) {
                            console.log('[PWA] Phien ban moi san sang. Hay tai lai trang de cap nhat.');
                        }
                    });
                });
            })
            .catch(function (err) {
                console.warn('[PWA] Service Worker registration failed:', err);
            });
    });
}
</script>


</html>
