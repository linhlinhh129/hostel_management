# Research: fix-first-login-password-change

## Finding 1: Mismatched Password Validations (JS vs Java)
**Decision**: Unify the password validation logic between `first_login.jsp` (JavaScript) and `PasswordValidator.java` (Java).
**Rationale**:
1. Java uses `^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,50}$` which requires 1 uppercase, 1 digit, 1 special character (`@#$%^&+=!`), and length 8-50.
2. JS uses `[^A-Za-z0-9]` for special characters, which allows characters like `_` or `*` that Java rejects. This causes a scenario where JS allows submission but Java rejects it, causing a confusing server-side validation error.
3. JS checks for at least 1 lowercase letter (`/[a-z]/`), which Java does not require. A password like `ADMIN123!` would fail JS validation (submit button disabled) but would pass Java validation.

## Finding 2: Silent failure on Form Submit (JS)
**Decision**: Show a clear error message or alert when the form submission is blocked by JavaScript.
**Rationale**: In `first_login.jsp`, the form submission is prevented silently if the passwords do not match:
```javascript
    document.querySelector('form').addEventListener('submit', function (e) {
        if (!allPassed(pwInput.value) || cfInput.value !== pwInput.value) {
            e.preventDefault();
            pwInput.focus(); // Silent failure
        }
    });
```
When a user clicks "Cập nhật mật khẩu", if the confirm password doesn't match, the form simply doesn't submit. No pop-up or clear error message is shown (only a small text under the input field which might be missed), leading the user to wonder "sao ấn submit không được?". We need to add an `alert()` or a visible error message before `e.preventDefault()`.

## Finding 3: Server-side redirect and AuthFilter
**Decision**: The existing redirect logic in `FirstLoginServlet.java` and `AuthFilter.java` is correct.
**Rationale**: `currentUser.setFirstLogin(false)` is correctly called before redirecting to the dashboard. Since the `currentUser` object is modified in the session, `AuthFilter` will correctly read `isFirstLogin() == false` on the subsequent request and allow the user to access the dashboard.
