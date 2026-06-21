# Implementation Plan

## Exploration Phase (BEFORE Fix)

- [ ] 1. Write bug condition exploration test
  - **Property 1: Bug Condition** - SMTP Authentication Failure with Malformed App Password
  - **CRITICAL**: This test MUST FAIL on unfixed code - failure confirms the bug exists
  - **DO NOT attempt to fix the test or the code when it fails**
  - **NOTE**: This test encodes the expected behavior - it will validate the fix when it passes after implementation
  - **GOAL**: Surface counterexamples that demonstrate the bug exists (authentication failures, no email delivery)
  - **Scoped PBT Approach**: Scope the property to concrete failing case(s) - valid registered emails like "tenant@example.com", "manager@example.com"
  - Test that `ForgotPasswordServlet.doPost()` with valid registered email fails to deliver email due to SMTP authentication failure
  - Verify `EmailService.SENDER_PASSWORD` contains spaces (root cause validation)
  - Capture console output and assert it contains `AuthenticationFailedException` with "535-5.7.8 Username and Password not accepted"
  - Check Gmail inbox programmatically via IMAP and assert no email received after 60 seconds
  - Run test on UNFIXED code (with spaces in App Password: "ywgq bjng ymfo bpol")
  - **EXPECTED OUTCOME**: Test FAILS (this is correct - it proves the bug exists)
  - Document counterexamples found:
    - Console shows authentication error
    - No emails delivered to inbox/spam
    - Pattern consistent across multiple valid email addresses
  - Mark task complete when test is written, run, and failure is documented
  - _Requirements: 1.1, 1.2, 1.3, 2.1, 2.2, 2.3_

- [ ] 2. Write preservation property tests (BEFORE implementing fix)
  - **Property 2: Preservation** - Non-Buggy Input Behavior Unchanged
  - **IMPORTANT**: Follow observation-first methodology
  - Observe behavior on UNFIXED code for non-buggy inputs (cases where email is invalid, non-existent, rate-limited, or empty)
  - **Anti-Enumeration Preservation Test**:
    - Observe: Submit "nonexistent@example.com" on unfixed code → "Link khôi phục đã được gửi" displayed
    - Write property-based test: For all non-existent emails, system displays generic success message without revealing existence
    - Generate 100 random non-existent email addresses and verify all show same message
  - **Validation Preservation Test**:
    - Observe: Submit empty/null email on unfixed code → "Vui lòng nhập địa chỉ email hợp lệ" error displayed
    - Write property-based test: For all invalid inputs (null, empty, whitespace-only), validation error is shown
    - Generate various invalid email inputs and verify validation error displayed
  - **Rate Limiting Preservation Test**:
    - Observe: Submit same email 4 times rapidly on unfixed code → "Bạn đã vượt quá số lần yêu cầu" on 4th attempt
    - Write property-based test: For all emails exceeding 3 requests/hour, rate limit error is enforced
    - Generate random valid emails and submit each 4 times, verify rate limit enforced consistently
  - **Token TTL Preservation Test**:
    - Observe: Generate token on unfixed code, wait 16 minutes, verify "Token hết hạn" error
    - Write unit test: Verify `ResetTokenManager.TOKEN_TTL_MS` remains exactly `15 * 60 * 1000` (15 minutes)
  - **Session Revocation Preservation Test**:
    - Observe: Login on two browsers, reset password on unfixed code, verify both sessions invalidated via `SessionRegistry`
    - Write integration test: Verify `SessionRegistry` is still called after password reset and invalidates all sessions
  - Verify all preservation tests PASS on UNFIXED code
  - **EXPECTED OUTCOME**: All tests PASS (this confirms baseline behavior to preserve)
  - Mark task complete when tests are written, run, and passing on unfixed code
  - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6_

## Implementation Phase

- [ ] 3. Fix SMTP authentication and enhance error logging

  - [ ] 3.1 Implement the primary fix in EmailService.java
    - Remove spaces from `SENDER_PASSWORD` constant on line 17
    - Change from: `private static final String SENDER_PASSWORD = "ywgq bjng ymfo bpol";`
    - Change to: `private static final String SENDER_PASSWORD = "ywgqbjngymfobpol";`
    - Add static initializer block to validate App Password format at class loading time:
      - Check SENDER_PASSWORD is exactly 16 characters
      - Check SENDER_PASSWORD contains no spaces
      - Throw IllegalStateException if validation fails
      - Log configuration success on valid setup
    - Enhance error handling in `sendResetLink()` method:
      - Add pre-send debug logging (SMTP host, port, sender, recipient)
      - Add specific catch blocks for `AuthenticationFailedException`, `MessagingException`, and generic `Exception`
      - Log detailed error messages with troubleshooting hints for each exception type
      - Move "Gửi email thành công!" inside try block after `Transport.send()` to ensure it only prints on actual success
    - Add post-send confirmation logging with checkmark symbol (✓)
    - _Bug_Condition: isBugCondition(input) where input.email is valid registered email AND EmailService.SENDER_PASSWORD contains spaces causing SMTP authentication failure_
    - _Expected_Behavior: After fix, for all valid registered emails, `Transport.send()` succeeds without exceptions, email is delivered to inbox within 60 seconds, console shows "✓ Gửi email thành công đến: [email]"_
    - _Preservation: Anti-enumeration (non-existent emails show generic success), rate limiting (3 requests/hour enforced), validation (empty/null emails show error), token TTL (15 minutes), session revocation (SessionRegistry invalidates all sessions after password reset)_
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 3.4, 3.5, 3.6_

  - [ ] 3.2 Verify bug condition exploration test now passes
    - **Property 1: Expected Behavior** - Email Delivery via Authenticated SMTP
    - **IMPORTANT**: Re-run the SAME test from task 1 - do NOT write a new test
    - The test from task 1 encodes the expected behavior (successful SMTP authentication and email delivery)
    - Run bug condition exploration test from step 1 on FIXED code (App Password without spaces)
    - **EXPECTED OUTCOME**: Test PASSES (confirms bug is fixed)
    - Verify assertions succeed:
      - No `AuthenticationFailedException` in console output
      - Console shows "✓ Gửi email thành công đến: [email]"
      - Email received in Gmail inbox within 60 seconds
      - Email subject is "Khôi phục mật khẩu - Quản lý Nhà trọ"
      - Email body contains reset link with pattern `/reset-password?token=`
    - _Requirements: 2.1, 2.2, 2.3_

  - [ ] 3.3 Verify preservation tests still pass
    - **Property 2: Preservation** - Non-Buggy Input Behavior Unchanged
    - **IMPORTANT**: Re-run the SAME tests from task 2 - do NOT write new tests
    - Run all preservation property tests from step 2 on FIXED code
    - **EXPECTED OUTCOME**: All tests PASS (confirms no regressions)
    - Verify each preservation test result:
      - Anti-enumeration test: 100 non-existent emails still show generic success message
      - Validation test: Invalid inputs (null, empty, whitespace) still show validation error
      - Rate limiting test: 4th request within hour still shows rate limit error
      - Token TTL test: Tokens still expire after exactly 15 minutes
      - Session revocation test: Password reset still invalidates all active sessions
    - Confirm no behavioral changes for non-buggy inputs
    - _Requirements: 3.1, 3.2, 3.3, 3.4, 3.5, 3.6_

## Validation Phase

- [ ] 4. Checkpoint - Ensure all tests pass and perform manual verification
  - Run complete test suite (exploration test + preservation tests)
  - Verify all automated tests pass without failures
  - Perform manual end-to-end verification:
    - Start application server
    - Navigate to `/forgot-password` form
    - Test valid registered email: Submit and verify email received in inbox
    - Test non-existent email: Submit and verify generic success message shown
    - Test empty email: Submit and verify validation error shown
    - Test rate limiting: Submit same email 4 times and verify rate limit enforced
    - Click reset link from received email and verify password reset form displayed
    - Complete password reset and verify old sessions invalidated
  - Review console logs for proper error handling (no stack traces on success, detailed errors with troubleshooting hints on failure)
  - Check that App Password validation runs at application startup and logs success
  - Ensure all tests pass and ask user if questions arise
  - _Requirements: All (1.1-3.6)_
