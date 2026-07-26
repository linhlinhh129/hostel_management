# Tasks: Post Interactions (Comments and Likes)

**Input**: Design documents from `/my-project/sdd/specs/manager/postManagement/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 Verify `post_reactions` and `post_comments` tables exist in database

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

- [x] T002 Implement BaseDAO enhancements for counting (if required)

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Like and Unlike a Post (Priority: P1) 🎯 MVP

**Goal**: Users can express appreciation by liking a post, or remove their like.

**Independent Test**: Can be fully tested by clicking a like button on a post and verifying the like count changes via AJAX.

### Implementation for User Story 1

- [x] T003 [P] [US1] Create PostReaction model in `src/main/java/com/quanlyphongtro/model/PostReaction.java`
- [x] T004 [P] [US1] Create PostReactionDTO in `src/main/java/com/quanlyphongtro/dto/PostReactionDTO.java`
- [x] T005 [US1] Implement PostReactionDAO in `src/main/java/com/quanlyphongtro/dao/PostReactionDAO.java`
- [x] T006 [US1] Implement PostInteractionService in `src/main/java/com/quanlyphongtro/service/PostInteractionService.java`
- [x] T007 [US1] Implement PostInteractionServiceImpl in `src/main/java/com/quanlyphongtro/service/impl/PostInteractionServiceImpl.java`
- [x] T008 [US1] Implement PostReactionServlet in `src/main/java/com/quanlyphongtro/controller/manager/PostReactionServlet.java`
- [x] T009 [US1] Update detail.jsp to add Like button and AJAX fetch logic in `src/main/webapp/WEB-INF/views/manager/postManagement/detail.jsp`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently.

---

## Phase 4: User Story 2 - Add a Comment to a Post (Priority: P1)

**Goal**: Users can write text comments on community posts.

**Independent Test**: Can be fully tested by entering text in a comment box and verifying the new comment appears.

### Implementation for User Story 2

- [x] T010 [P] [US2] Create PostComment model in `src/main/java/com/quanlyphongtro/model/PostComment.java`
- [x] T011 [P] [US2] Create PostCommentDTO in `src/main/java/com/quanlyphongtro/dto/PostCommentDTO.java`
- [x] T012 [US2] Implement add comment logic in PostCommentDAO in `src/main/java/com/quanlyphongtro/dao/PostCommentDAO.java`
- [x] T013 [US2] Add comment method to PostInteractionServiceImpl in `src/main/java/com/quanlyphongtro/service/impl/PostInteractionServiceImpl.java`
- [x] T014 [US2] Implement PostCommentServlet in `src/main/java/com/quanlyphongtro/controller/manager/PostCommentServlet.java`
- [x] T015 [US2] Update detail.jsp to add Comment input and AJAX post logic in `src/main/webapp/WEB-INF/views/manager/postManagement/detail.jsp`

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently.

---

## Phase 5: User Story 3 - View Comments and Interactions (Priority: P2)

**Goal**: Show total interaction counts on feeds and load comment threads on detail view.

**Independent Test**: Can be verified by viewing the post feed and post detail page to see counts and comment lists.

### Implementation for User Story 3

- [x] T016 [US3] Update CommunityPostDAO to fetch comment and like counts in `src/main/java/com/quanlyphongtro/dao/CommunityPostDAO.java`
- [x] T017 [US3] Update PostCommentDAO to fetch list of comments by postId in `src/main/java/com/quanlyphongtro/dao/PostCommentDAO.java`
- [x] T018 [US3] Update CommunityPostServlet to load counts and comment lists in `src/main/java/com/quanlyphongtro/controller/manager/CommunityPostServlet.java`
- [x] T019 [US3] Update list-pending.jsp to display counts in `src/main/webapp/WEB-INF/views/manager/postManagement/list-pending.jsp`
- [x] T020 [US3] Update detail.jsp to display comment thread in `src/main/webapp/WEB-INF/views/manager/postManagement/detail.jsp`

**Checkpoint**: At this point, all core view flows should work. Manager sees interactions visually.

---

## Phase 6: User Story 4 - Delete Own Comment (Priority: P3)

**Goal**: Users can remove their own comments and Managers can moderate.

**Independent Test**: Can be verified by clicking delete on your own comment.

### Implementation for User Story 4

- [x] T021 [US4] Implement delete logic in PostCommentDAO in `src/main/java/com/quanlyphongtro/dao/PostCommentDAO.java`
- [x] T022 [US4] Add delete method with auth check in PostInteractionServiceImpl in `src/main/java/com/quanlyphongtro/service/impl/PostInteractionServiceImpl.java`
- [x] T023 [US4] Update PostCommentServlet to handle DELETE method in `src/main/java/com/quanlyphongtro/controller/manager/PostCommentServlet.java`
- [x] T024 [US4] Update detail.jsp to include delete button and AJAX delete logic in `src/main/webapp/WEB-INF/views/manager/postManagement/detail.jsp`

---

### Phase 4: Validation and Quality Assurance (Required)

**Purpose**: Improvements that affect multiple user stories

- [x] T025 [P] End-to-End manual testing of US1
- [x] T026 [P] End-to-End manual testing of US2
- [x] T027 [P] End-to-End manual testing of US3 & US4

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2)
- **User Story 2 (P1)**: Can start after Foundational (Phase 2)
- **User Story 3 (P2)**: Depends heavily on US1 and US2 for generating data
- **User Story 4 (P3)**: Depends heavily on US2 to have comments to delete

### Parallel Opportunities

- T003 and T004 (Model and DTO) can be created in parallel.
- T010 and T011 (Model and DTO) can be created in parallel.
- US1 and US2 backend implementation can be done largely in parallel.