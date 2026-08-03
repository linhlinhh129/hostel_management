# Research: remove-js-validation-first-login

## Decision 1: Remove all dynamic JS password validation

- **Decision**: Completely strip out the `allPassed`, `updateChecklist`, and password strength bar scripts.
- **Rationale**: The user explicitly desires to rely on JSP server-side validation. Maintaining duplicate logic (JS frontend + Java backend) often leads to discrepancies (like the lowercase bug previously encountered). Removing the JS centralizes the source of truth to the Java backend.
- **Alternatives considered**: Keeping a simplified JS validation. This was rejected because the user specifically requested to avoid JS here due to the system's architecture.

## Decision 2: Maintain Native HTML5 Validation

- **Decision**: Keep the `pattern="(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,}"` in the HTML form.
- **Rationale**: HTML5 native validation does not count as custom JavaScript. It is a standard browser feature that provides a first line of defense without maintaining custom JS scripts. It aligns perfectly with standard server-rendered forms.
- **Alternatives considered**: Removing `pattern` entirely. This was rejected because removing native browser protections degrades UX significantly when it costs zero JS maintenance.

## Decision 3: Remove Dynamic Checklist UI

- **Decision**: Convert the dynamic checklist (`id="pwChecklist"`) to a static tooltip or simple descriptive text block. Remove the strength bar entirely.
- **Rationale**: Without JS, we cannot dynamically update the checklist ticks/crosses or the strength bar width/color. A static text explaining the password policy is sufficient for user guidance.
- **Alternatives considered**: None, as dynamic UI requires JS.
