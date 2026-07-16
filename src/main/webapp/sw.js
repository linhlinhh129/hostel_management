const CACHE_NAME = 'hostel-pwa-v2';

// Context path: '/hostel-management' khi test local, '' khi deploy Azure (ROOT.war)
const CTX = self.location.pathname.startsWith('/hostel-management') ? '/hostel-management' : '';

// Static assets được cache khi Service Worker install
const STATIC_ASSETS = [
  `${CTX}/assets/css/hostel-design.css`,
  `${CTX}/assets/css/mintlify.css`,
  `${CTX}/assets/js/hostel-app.js`,
  `${CTX}/assets/img/login-illustration.png`,
  // Bootstrap offline fallback
  'https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css',
  'https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js',
];

// ── INSTALL ─────────────────────────────────────────────────────────────────
// Chạy khi SW được cài lần đầu: pre-cache toàn bộ static assets
self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then((cache) => cache.addAll(STATIC_ASSETS))
      .catch((err) => console.warn('[SW] Pre-cache failed (có thể do offline):', err))
  );
  // Kích hoạt ngay, không chờ tab cũ đóng
  self.skipWaiting();
});

// ── ACTIVATE ────────────────────────────────────────────────────────────────
// Dọn dẹp cache cũ khi có version SW mới
self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((cacheNames) =>
      Promise.all(
        cacheNames
          .filter((name) => name !== CACHE_NAME)
          .map((name) => {
            console.log('[SW] Deleting old cache:', name);
            return caches.delete(name);
          })
      )
    )
  );
  // Kiểm soát tất cả tabs ngay lập tức
  self.clients.claim();
});

// ── FETCH ────────────────────────────────────────────────────────────────────
// Interceptor cho tất cả network requests
self.addEventListener('fetch', (event) => {
  const { request } = event;
  const url = new URL(request.url);

  // Chỉ xử lý GET requests
  if (request.method !== 'GET') return;

  // Bỏ qua requests tới domain khác (ngoại trừ CDN đã liệt kê)
  const allowedExternalHosts = ['cdn.jsdelivr.net', 'fonts.googleapis.com', 'fonts.gstatic.com'];
  if (url.origin !== self.location.origin && !allowedExternalHosts.includes(url.hostname)) {
    return;
  }

  // Bỏ qua API endpoints – không bao giờ cache dữ liệu động
  if (url.pathname.startsWith('/api/')) return;

  // Static assets (CSS, JS, images, fonts) → Cache First
  // Lý do: ít thay đổi, cần load nhanh
  if (/\.(css|js|png|jpg|jpeg|gif|svg|woff2?|ttf|ico|webp)$/.test(url.pathname)) {
    event.respondWith(cacheFirst(request));
    return;
  }

  // HTML pages (JSP/Servlet) → Network First
  // Lý do: cần data mới nhất từ server (session, dữ liệu DB)
  event.respondWith(networkFirst(request));
});

/**
 * Cache First: Trả về từ cache nếu có, ngược lại fetch từ network rồi lưu cache.
 * Phù hợp với: CSS, JS, ảnh, fonts (tài nguyên ít thay đổi)
 */
async function cacheFirst(request) {
  const cachedResponse = await caches.match(request);
  if (cachedResponse) {
    return cachedResponse;
  }

  try {
    const networkResponse = await fetch(request);
    if (networkResponse.ok) {
      const cache = await caches.open(CACHE_NAME);
      cache.put(request, networkResponse.clone());
    }
    return networkResponse;
  } catch (err) {
    console.warn('[SW] Cache First – fetch failed:', err);
    return new Response('Resource not available offline', {
      status: 503,
      headers: { 'Content-Type': 'text/plain' }
    });
  }
}

/**
 * Network First: Thử fetch từ network trước, nếu offline thì dùng cache.
 * Phù hợp với: HTML pages (JSP) – cần data mới nhất
 */
async function networkFirst(request) {
  try {
    const networkResponse = await fetch(request);
    if (networkResponse.ok) {
      const cache = await caches.open(CACHE_NAME);
      cache.put(request, networkResponse.clone());
    }
    return networkResponse;
  } catch (err) {
    // Offline: thử dùng cache
    const cachedResponse = await caches.match(request);
    if (cachedResponse) {
      return cachedResponse;
    }
    // Không có cache: hiển thị trang offline
    return offlineFallback();
  }
}

/**
 * Trang offline hiển thị khi không có mạng và không có cache.
 */
function offlineFallback() {
  return new Response(
    `<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Offline – Quản lý Nhà Trọ</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
      font-family: Inter, -apple-system, BlinkMacSystemFont, sans-serif;
      display: flex; align-items: center; justify-content: center;
      min-height: 100vh;
      background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%);
      color: #e2e8f0;
    }
    .card {
      text-align: center; padding: 3rem 2rem;
      background: rgba(30, 41, 59, 0.8);
      border: 1px solid rgba(99, 102, 241, 0.3);
      border-radius: 20px;
      backdrop-filter: blur(12px);
      max-width: 420px; width: 90%;
      box-shadow: 0 25px 50px rgba(0,0,0,0.5);
    }
    .icon {
      font-size: 4rem; margin-bottom: 1.5rem;
      animation: pulse 2s ease-in-out infinite;
    }
    @keyframes pulse {
      0%, 100% { transform: scale(1); opacity: 1; }
      50% { transform: scale(1.05); opacity: 0.8; }
    }
    h1 { font-size: 1.4rem; font-weight: 600; margin-bottom: .75rem; color: #f1f5f9; }
    p  { color: #94a3b8; line-height: 1.6; margin-bottom: 2rem; font-size: .95rem; }
    button {
      padding: .8rem 2.5rem;
      background: linear-gradient(135deg, #6366f1, #818cf8);
      color: #fff; border: none; border-radius: 10px;
      cursor: pointer; font-size: 1rem; font-weight: 500;
      transition: all .2s ease;
      box-shadow: 0 4px 15px rgba(99, 102, 241, 0.4);
    }
    button:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 25px rgba(99, 102, 241, 0.5);
    }
    button:active { transform: translateY(0); }
  </style>
</head>
<body>
  <div class="card">
    <div class="icon">📡</div>
    <h1>Không có kết nối mạng</h1>
    <p>Vui lòng kiểm tra kết nối Internet của bạn và thử lại.</p>
    <button onclick="location.reload()">Thử lại</button>
  </div>
</body>
</html>`,
    {
      status: 200,
      headers: { 'Content-Type': 'text/html;charset=UTF-8' }
    }
  );
}
