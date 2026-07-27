# Phase 1: Quickstart Validation Guide

**Prerequisites**: Manager account logged in.

**Testing Scenario**:
1. Go to the Manager Dashboard and navigate to the "Quản lý hóa đơn" (Invoice Management) page.
2. Click on the button to Create a new invoice.
3. Check the "Ghi chú" (Note) input field. Verify there is a hint indicating "Tối đa 1000 ký tự".
4. Try to paste a string longer than 1000 characters. Verify that the UI prevents pasting past 1000 characters.
5. If frontend validation is bypassed (e.g. using browser developer tools), submitting the form should return an error message: "Ghi chú không được vượt quá 1000 ký tự."
