# Phase 0: Outline & Research

## Note length validation
- **Decision**: Add `maxlength="1000"` to the `textarea` for the Note field in `create.jsp`. Add a backend check in `InvoiceServiceImpl.createInvoice` to throw `IllegalArgumentException` if `note != null && note.length() > 1000`. Add Bootstrap 5 `form-text` class for the helper message.
- **Rationale**: This guarantees validation at both frontend and backend layers, conforming to typical web standards without introducing new dependencies or complexity.
- **Alternatives considered**: Javascript-based character counter. Rejected as it introduces unnecessary JS for a simple requirement when `maxlength` and CSS provide sufficient UX.
