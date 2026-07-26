# Research: Post Interactions (Comments and Likes)

## Unknowns Addressed

None. The technology stack and requirements are well understood and align with existing patterns in the project.

## Technology Choices & Patterns

- **AJAX for Interactions**:
  - **Decision**: Use Vanilla JavaScript `fetch` API for submitting likes and comments without page reloads.
  - **Rationale**: Provides the fast, responsive UX required (under 500ms for likes). Prevents the page from reloading, which would disrupt the user's reading experience.
  - **Alternatives considered**: Traditional form submission (rejected due to poor UX and page reloads).

- **Database Queries for Counts**:
  - **Decision**: Use `COUNT()` aggregate functions with `LEFT JOIN` in the `CommunityPostDAO` to fetch the total number of likes and comments when listing posts.
  - **Rationale**: Efficient and reliable. Avoids N+1 query problems.
  - **Alternatives considered**: Fetching all rows and counting in Java (rejected due to memory inefficiency).

- **Pagination for Comments**:
  - **Decision**: Not required for v1. We will load all comments or a fixed limit (e.g., 50 latest comments) when viewing a post detail.
  - **Rationale**: Keeps implementation simple for the first version.
  - **Alternatives considered**: Cursor-based pagination for comments (deferred to future enhancements).
