# Quickstart: Logout & Anti-Caching Validation

This guide explains how to validate the logout functionality and ensure the anti-caching headers are working correctly to prevent "Back" button usage after logout.

## Prerequisites
- The application is running (Tomcat/Jetty server).
- You have valid user credentials (e.g. `tenant_user` / `password123!`).

## Validation Steps

### 1. Test standard logout
1. Navigate to `/login` and log in with your credentials.
2. Verify you are redirected to a protected page (e.g. `/tenant/dashboard`).
3. Click the **Đăng xuất** (Logout) button in the user dropdown menu at the top right.
4. **Expected Outcome:** You are immediately redirected back to the `/login` screen.

### 2. Test "Back" button prevention (Anti-Caching)
1. Ensure you just completed step 1.
2. Click the browser's **Back** button.
3. **Expected Outcome:** 
   - The browser should *not* show the cached dashboard page.
   - The browser should attempt to reload the dashboard page from the server.
   - The server (`AuthFilter`) should intercept the request, realize the session is dead, and redirect you to `/login` again.

### 3. Verify Session Destruction (Optional but recommended)
1. Log in again.
2. Open another tab and go to a protected page.
3. In the first tab, click **Đăng xuất**.
4. In the second tab, try to refresh the page.
5. **Expected Outcome:** The refresh in the second tab should redirect you to `/login`, proving the session was destroyed globally on the server.
