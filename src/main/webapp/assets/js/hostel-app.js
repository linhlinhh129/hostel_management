/* Hostel Management System — App JS v2.0 */

document.addEventListener('DOMContentLoaded', function () {
  // ── Sidebar toggle (mobile) ──────────────────────────────
  const sidebar  = document.querySelector('.sidebar');
  const toggle   = document.querySelector('.topbar-toggle');
  const overlay  = document.querySelector('.sidebar-overlay');

  function openSidebar() {
    if (sidebar)  sidebar.classList.add('open');
    if (overlay)  overlay.classList.add('show');
    document.body.style.overflow = 'hidden';
  }
  function closeSidebar() {
    if (sidebar)  sidebar.classList.remove('open');
    if (overlay)  overlay.classList.remove('show');
    document.body.style.overflow = '';
  }

  if (toggle)  toggle.addEventListener('click', openSidebar);
  if (overlay) overlay.addEventListener('click', closeSidebar);

  // Close on Escape
  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') closeSidebar();
  });

  // Close sidebar when a nav link is clicked on mobile (< 1024px)
  if (sidebar) {
    sidebar.querySelectorAll('.sidebar-link').forEach(function (link) {
      link.addEventListener('click', function () {
        if (window.innerWidth < 1024) closeSidebar();
      });
    });
  }

  // ── Swipe-left to close sidebar on touch devices ─────────
  var touchStartX = 0;
  var touchStartY = 0;
  document.addEventListener('touchstart', function (e) {
    touchStartX = e.changedTouches[0].screenX;
    touchStartY = e.changedTouches[0].screenY;
  }, { passive: true });
  document.addEventListener('touchend', function (e) {
    var dx = e.changedTouches[0].screenX - touchStartX;
    var dy = e.changedTouches[0].screenY - touchStartY;
    // Swipe left ≥ 60px and mostly horizontal → close
    if (dx < -60 && Math.abs(dy) < 80 && sidebar && sidebar.classList.contains('open')) {
      closeSidebar();
    }
    // Swipe right ≥ 60px from left edge → open
    if (dx > 60 && Math.abs(dy) < 80 && touchStartX < 24 && sidebar && !sidebar.classList.contains('open')) {
      openSidebar();
    }
  }, { passive: true });

  // ── Flash message auto-dismiss ───────────────────────────
  document.querySelectorAll('.alert.alert-success, .alert.alert-info').forEach(function (el) {
    setTimeout(function () {
      el.style.transition = 'opacity 0.4s ease, transform 0.4s ease';
      el.style.opacity = '0';
      el.style.transform = 'translateY(-8px)';
      setTimeout(function () { el.remove(); }, 400);
    }, 4000);
  });

  // ── KPI counter animation ────────────────────────────────
  document.querySelectorAll('.kpi-value[data-count]').forEach(function (el) {
    var target = parseInt(el.getAttribute('data-count'), 10);
    var start  = 0;
    var duration = 800;
    var startTime = null;

    function step(timestamp) {
      if (!startTime) startTime = timestamp;
      var progress = Math.min((timestamp - startTime) / duration, 1);
      var eased = 1 - Math.pow(1 - progress, 3); // ease-out cubic
      el.textContent = Math.floor(eased * target).toLocaleString('vi-VN');
      if (progress < 1) requestAnimationFrame(step);
    }
    requestAnimationFrame(step);
  });

  // ── Table row ripple on click ────────────────────────────
  document.querySelectorAll('.table-mintlify tbody tr').forEach(function (row) {
    row.addEventListener('click', function (e) {
      // Only navigate if clicking on the row itself (not a nested button/link)
      var link = row.querySelector('a[href]:not(.badge-hms)');
      if (link && e.target === row) link.click();
    });
  });

  // ── Confirm dialogs for destructive actions ──────────────
  document.querySelectorAll('[data-confirm]').forEach(function (el) {
    el.addEventListener('click', function (e) {
      if (!confirm(el.getAttribute('data-confirm'))) {
        e.preventDefault();
        e.stopPropagation();
      }
    });
  });

  // ── Textarea char counter ────────────────────────────────
  document.querySelectorAll('textarea[maxlength]').forEach(function (ta) {
    var max = parseInt(ta.getAttribute('maxlength'), 10);
    var counter = document.createElement('div');
    counter.className = 'form-text text-end';
    counter.style.marginTop = '4px';

    function update() {
      var remaining = max - ta.value.length;
      counter.textContent = remaining + ' k\xFD t\u1EF1 c\xF2n l\u1EA1i';
      counter.style.color = remaining < 100 ? 'var(--hms-warning)' : 'var(--hms-text-muted)';
      if (remaining < 20) counter.style.color = 'var(--hms-danger)';
    }
    update();
    ta.addEventListener('input', update);
    ta.parentNode.insertBefore(counter, ta.nextSibling);
  });

  // ── Tooltip on data-tooltip elements ────────────────────
  document.querySelectorAll('[data-tooltip]').forEach(function (el) {
    var tip = document.createElement('div');
    tip.className = 'hms-tooltip';
    tip.textContent = el.getAttribute('data-tooltip');
    tip.style.cssText = [
      'position:absolute', 'bottom:calc(100% + 6px)', 'left:50%',
      'transform:translateX(-50%)', 'background:var(--hms-primary)',
      'color:#fff', 'padding:4px 10px', 'border-radius:6px',
      'font-size:0.75rem', 'white-space:nowrap', 'pointer-events:none',
      'opacity:0', 'transition:opacity 0.2s', 'z-index:9999'
    ].join(';');
    el.style.position = 'relative';
    el.appendChild(tip);
    el.addEventListener('mouseenter', function () { tip.style.opacity = '1'; });
    el.addEventListener('mouseleave', function () { tip.style.opacity = '0'; });
  });

  // ── Active sidebar link highlight from URL ───────────────
  var currentPath = window.location.pathname;
  document.querySelectorAll('.sidebar-link').forEach(function (link) {
    if (link.getAttribute('href') && currentPath.startsWith(link.getAttribute('href'))) {
      link.classList.add('active');
    }
  });

  // ── Form loading state on submit ─────────────────────────
  document.querySelectorAll('form').forEach(function (form) {
    form.addEventListener('submit', function () {
      var submitBtn = form.querySelector('[type="submit"]');
      if (submitBtn) {
        setTimeout(function () {
          submitBtn.disabled = true;
          submitBtn.style.opacity = '0.7';
          var orig = submitBtn.textContent;
          submitBtn.textContent = '\u0110ang x\u1EED l\xFD...';
          // Re-enable after 8s as safety net
          setTimeout(function () {
            submitBtn.disabled = false;
            submitBtn.style.opacity = '';
            submitBtn.textContent = orig;
          }, 8000);
        }, 10);
      }
    });
  });
});









/* ── Clickable table rows (.tr-clickable[data-href]) ───────────
   Click anywhere on the row → navigate to data-href.
   Elements with onclick / links / buttons inside stop propagation. */
document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('tr.tr-clickable[data-href]').forEach(function (row) {
    var href = row.getAttribute('data-href');
    if (!href) return;
    row.style.cursor = 'pointer';
    row.addEventListener('click', function (e) {
      // Ignore clicks directly on interactive children
      var tag = e.target.tagName.toUpperCase();
      if (tag === 'A' || tag === 'BUTTON' || tag === 'INPUT' || tag === 'SELECT') return;
      if (e.target.closest('a, button, input, select')) return;
      window.location.href = href;
    });
  });
});
