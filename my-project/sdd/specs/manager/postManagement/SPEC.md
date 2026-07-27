# Feature Specification: Post Interactions (Comments and Likes)

**Feature Branch**: `post-interactions`

**Created**: 2026-07-27

**Status**: Draft

**Input**: User description: "trang bài viết mình cần thêm cả comment và cả like cho mình đi"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Like and Unlike a Post (Priority: P1)

Users browsing the community posts can express their appreciation by liking a post. If they change their mind, they can remove their like (unlike).

**Why this priority**: Liking is the most basic form of community engagement. It provides immediate feedback to authors and encourages participation.

**Independent Test**: Can be fully tested by clicking a like button on a post and verifying the like count increases/decreases immediately, and the state persists after a page reload.

**Acceptance Scenarios**:

1. **Given** a user is viewing a post they haven't liked, **When** they click the Like button, **Then** the like count increases by 1, and the button visually indicates they have liked it.
2. **Given** a user is viewing a post they have already liked, **When** they click the Like button again, **Then** the like count decreases by 1, and the button returns to its default state.
3. **Given** multiple users view the same post, **When** one user likes it, **Then** the updated count is reflected for other users upon their next page load.

---

### User Story 2 - Add a Comment to a Post (Priority: P1)

Users can write text comments on community posts to discuss topics, ask questions, or provide detailed feedback.

**Why this priority**: Commenting allows for rich, meaningful interactions and discussions between community members, which is the core purpose of a community page.

**Independent Test**: Can be fully tested by entering text in a comment box, submitting it, and verifying the new comment appears at the bottom of the post's comment list with the correct author name and timestamp.

**Acceptance Scenarios**:

1. **Given** a user is viewing a post, **When** they type a valid message in the comment input and submit, **Then** their comment is saved and displayed immediately under the post.
2. **Given** a user attempts to submit an empty comment, **When** they click submit, **Then** the system prevents submission and shows a validation error.
3. **Given** a user submits a comment exceeding the maximum length, **When** they click submit, **Then** the system prevents submission and informs the user of the limit.

---

### User Story 3 - View Comments and Interactions (Priority: P2)

Users viewing the post feed can quickly see how much engagement a post has received (total likes and total comments) before opening the full details.

**Why this priority**: Summarized interaction metrics help users identify popular or active discussions quickly.

**Independent Test**: Can be fully tested by viewing the main post feed and ensuring each post card displays accurate aggregate counts for both likes and comments.

**Acceptance Scenarios**:

1. **Given** a post with existing interactions, **When** it appears in a feed, **Then** it clearly displays the total number of likes and comments.
2. **Given** a user opens a post's detailed view, **When** the page loads, **Then** they see the full list of comments ordered chronologically (oldest first or newest first, depending on standard conventions).

---

### User Story 4 - Delete Own Comment (Priority: P3)

Users have the ability to remove their own comments if they made a mistake or changed their mind. Managers can delete any comment for moderation purposes.

**Why this priority**: Self-correction and moderation are important for maintaining a healthy community environment, but less critical than the core interaction mechanics.

**Independent Test**: Can be fully tested by a user deleting their own comment and verifying it disappears from the UI and total count.

**Acceptance Scenarios**:

1. **Given** a user views a comment they authored, **When** they click delete and confirm, **Then** the comment is removed from the post.
2. **Given** a user views a comment authored by someone else, **When** they view the options, **Then** the delete option is not available (unless they are a Manager/Admin).

### User Story 5 - Post Content Constraints (Priority: P1)

Managers creating or editing a post must adhere to character limits to maintain a clean and consistent UI.

**Why this priority**: Prevents layout breaking and ensures content is concise and readable.

**Independent Test**: Attempting to save a post with a title longer than 50 characters or content longer than 1000 characters should display validation errors and prevent saving.

**Acceptance Scenarios**:

1. **Given** a manager is creating/editing a post, **When** they enter a title exceeding 50 characters, **Then** the system prevents submission and shows a validation error.
2. **Given** a manager is creating/editing a post, **When** they enter content exceeding 1000 characters, **Then** the system prevents submission and shows a validation error.

### Edge Cases

- What happens when a user tries to like a post that has just been deleted by a manager? (System should return a friendly error and refresh the view).
- How does system handle extremely long words without spaces in comments? (UI must wrap text properly to prevent layout breaking).
- What happens if a user submits multiple comments very quickly (spam)? (System should process them, but rate limiting could be applied in the future).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow authenticated users to add a Like to any active community post.
- **FR-002**: System MUST allow users to remove their own Like from a post.
- **FR-003**: System MUST prevent a single user from liking the same post multiple times (toggle behavior).
- **FR-004**: System MUST allow authenticated users to submit text comments (up to 1000 characters) on any active post.
- **FR-005**: System MUST NOT allow submission of empty or whitespace-only comments.
- **FR-006**: System MUST display the aggregate count of Likes and Comments on the post summary/feed view.
- **FR-007**: System MUST display a list of comments for a post, including the author's name, timestamp, and content.
- **FR-008**: System MUST allow users to delete their own comments.
- **FR-009**: System MUST allow users with the MANAGER or ADMIN role to delete any comment for moderation purposes.
- **FR-010**: System MUST validate that the Post Title does not exceed 50 characters upon creation or editing.
- **FR-011**: System MUST validate that the Post Content does not exceed 1000 characters upon creation or editing.

### Key Entities *(include if feature involves data)*

- **Post Reaction (Like)**: Represents a user's "like" on a specific post. Key attributes: Post ID, User ID, Created Timestamp.
- **Post Comment**: Represents a textual reply to a post. Key attributes: Post ID, User ID (Author), Content, Created Timestamp.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can successfully add a like to a post, with UI reflecting the change in under 500ms.
- **SC-002**: Users can successfully submit a comment, with the new comment appearing in the thread in under 1 second.
- **SC-003**: Total interaction counts (likes, comments) displayed on the UI are 100% accurate based on the underlying data.
- **SC-004**: 0% of unauthorized deletions occur (users cannot delete comments they did not author, excluding managers).
- **SC-005**: 100% of posts created or updated successfully adhere to the maximum character limits for title (50) and content (1000).

## Assumptions

- Users must be logged in to like or comment on posts (no guest interactions).
- Comments are flat; nested replies (threads) are out of scope for this version.
- Rich text or image attachments in comments are out of scope; comments are plain text only.
- The underlying database schema for `post_reactions` and `post_comments` already exists or matches the standard structure defined in the current architecture.
- "Likes" are binary (Like/Unlike), no reaction varieties (e.g., Love, Haha, Sad) are needed for v1.