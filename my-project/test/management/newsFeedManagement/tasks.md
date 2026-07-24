# Tasks: News Feed Management (Unit Test Only)

**Input**: Design documents from `my-project/test/management/newsFeedManagement/`
**Prerequisites**: spec.md, plan.md

## Phase 1: Setup

- [ ] T001 Initialize Unit Test base structure for `NewsFeedApiServletTest` with Mockito extensions.

## Phase 2: Happy Path 🎯 MVP

- [ ] T002 Test `GET /api/v1/news-feed` returns valid posts.
- [ ] T003 Test `GET /api/v1/news-feed/top` returns Top 5 posts.
- [ ] T004 Test `POST /api/v1/posts/{postId}/reactions` successfully toggles Like.
- [ ] T005 Test `POST /api/v1/posts/{postId}/comments` successfully adds a comment.
- [ ] T006 Test `DELETE /api/v1/comments/{commentId}` successfully deletes a comment.

## Phase 3: Error Cases (Unwanted)

- [ ] T007 Test `POST` comment fails gracefully on empty content (400).
- [ ] T008 Test API fails gracefully when Unauthenticated (401).
- [ ] T009 Test API fails gracefully when Post ID is invalid (404).

## Phase 4: Boundary Values

- [ ] T010 Test `GET` news-feed respects the exact 24h boundary.
- [ ] T011 Test `POST` comment respects the 1000 characters limit.

## Phase 5: Concurrent Scenarios

- [ ] T012 Setup `ExecutorService` to verify Rate Limiting (100 req/min).
- [ ] T013 Setup `ExecutorService` to verify 50 concurrent likes on the same post.
