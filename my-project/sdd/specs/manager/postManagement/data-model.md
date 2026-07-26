# Data Model: Post Interactions (Comments and Likes)

## Entities

### PostReaction
Represents a user liking a specific community post.

- `postId` (Integer): The ID of the post being liked (Primary Key part 1, Foreign Key to `community_posts`).
- `userId` (Integer): The ID of the user who liked the post (Primary Key part 2, Foreign Key to `users`).
- `createdAt` (LocalDateTime): Timestamp when the like was added.

**Database Table**: `post_reactions`

### PostComment
Represents a text comment made by a user on a community post.

- `commentId` (Integer): Unique identifier for the comment (Primary Key, Identity).
- `postId` (Integer): The ID of the post being commented on (Foreign Key to `community_posts`).
- `userId` (Integer): The ID of the user who authored the comment (Foreign Key to `users`).
- `content` (String): The text content of the comment (Max 1000 chars).
- `createdAt` (LocalDateTime): Timestamp when the comment was created.
- `updatedAt` (LocalDateTime): Timestamp of last edit (optional for future use).
- `deletedAt` (LocalDateTime): Timestamp of deletion for soft-delete (if applicable).

**Database Table**: `post_comments`

## DTOs

### PostReactionDTO
- `postId`
- `userId`
- `createdAt`

### PostCommentDTO
- `commentId`
- `postId`
- `userId`
- `authorName` (String): Joined from `users` table.
- `content`
- `createdAt`
- `isAuthor` (Boolean): Calculated field for UI to determine if current user can delete.
