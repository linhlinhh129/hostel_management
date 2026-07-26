# Quickstart: Validation Guide for Post Interactions

## Prerequisites

- Project must be built and running locally (Tomcat server).
- You must have at least one active community post in the database.
- You must be logged in as a Manager or Tenant (authenticated user).

## Validation Scenarios

### Scenario 1: Liking and Unliking a Post

1. Navigate to the Community Posts feed (e.g., `/manager/articles`).
2. Locate a post in the feed.
3. Click the "Like" button (thumbs up icon).
4. **Expected Outcome**: The like count should increment by 1 immediately.
5. Click the "Like" button again.
6. **Expected Outcome**: The like count should decrement by 1 immediately.

### Scenario 2: Adding a Comment

1. Navigate to a specific post's detail page (e.g., `/manager/articles/detail?id=1`).
2. Locate the comment input field at the bottom.
3. Type a message: `This is a test comment from Quickstart!`
4. Click the "Submit" or "Post" button.
5. **Expected Outcome**: The comment should appear at the bottom of the comment thread immediately, showing your name and the current timestamp.

### Scenario 3: Validating Empty Comments

1. On the same post detail page, leave the comment input field empty.
2. Click the "Submit" or "Post" button.
3. **Expected Outcome**: An error message or validation alert should prevent submission. No blank comment should be added to the thread.

### Scenario 4: Deleting a Comment

1. Locate the comment you just created in Scenario 2.
2. Click the "Delete" icon/button next to it.
3. Confirm the deletion prompt (if any).
4. **Expected Outcome**: The comment should disappear from the thread, and the total comment count should decrease.
