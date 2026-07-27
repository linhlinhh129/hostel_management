# Research: Logout Anti-Caching

## Decision: Use standard HTTP Cache-Control Headers in Filter
**Rationale:** To prevent the browser's back button from rendering cached pages after a user logs out, the most reliable and standard approach is to instruct the browser not to cache protected pages at all. We use a combination of `Cache-Control: no-cache, no-store, must-revalidate`, `Pragma: no-cache`, and `Expires: 0` to cover HTTP 1.1, HTTP 1.0, and proxy servers. Placing this logic in a global `AuthFilter` ensures all authenticated routes are protected without modifying individual Servlets or JSPs.

**Alternatives considered:** 
- JavaScript `history.replaceState` or `window.onpageshow`: Less reliable as it requires client-side execution and doesn't prevent the initial cache load.
- Disabling cache for *all* pages: Inefficient as it prevents caching of static assets (CSS, JS, images). Applying it only to non-public paths is optimal.
