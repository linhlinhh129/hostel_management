# Tasks: Post Interactions (Comments and Likes)

**Input**: Design documents from `/my-project/sdd/specs/manager/postManagement/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- Paths shown below assume standard Maven Java web application structure.

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

*(Project structure already exists. No setup tasks required.)*

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

- [x] T001 [P] Create `PostReactionDTO` in `src/main/java/com/quanlyphongtro/dto/PostReactionDTO.java`
- [x] T002 [P] Create `PostCommentDTO` in `src/main/java/com/quanlyphongtro/dto/PostCommentDTO.java`

**Checkpoint**: Foundation ready - user story implementation can now begin.

---

## Phase 3: User Story 1 - Like and Unlike a Post (Priority: P1) 🎯 MVP

**Goal**: Users can express appreciation by liking a post and remove their like.

**Independent Test**: Click Like on a post, verify count updates immediately via AJAX without reload.

### Implementation for User Story 1

- [x] T003 [P] [US1] Create `PostReaction` model in `src/main/java/com/quanlyphongtro/model/PostReaction.java`
- [x] T004 [P] [US1] Create `PostReactionDAO` with insert/delete methods in `src/main/java/com/quanlyphongtro/dao/PostReactionDAO.java`
- [x] T005 [US1] Create `PostInteractionService` interface in `src/main/java/com/quanlyphongtro/service/PostInteractionService.java`
- [x] T006 [US1] Implement `likePost` and `unlikePost` in `src/main/java/com/quanlyphongtro/service/impl/PostInteractionServiceImpl.java` (depends on T004)
- [x] T007 [US1] Create `PostReactionServlet` in `src/main/java/com/quanlyphongtro/controller/manager/PostReactionServlet.java` to handle AJAX toggle requests
- [x] T008 [US1] Update `src/main/webapp/WEB-INF/views/manager/postManagement/detail.jsp` to include Like button and JavaScript for AJAX

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently.

---

## Phase 4: User Story 2 - Add a Comment to a Post (Priority: P1)

**Goal**: Users can write text comments on community posts.

**Independent Test**: Enter text in comment box, submit, verify it appears dynamically via AJAX.

### Implementation for User Story 2

- [x] T009 [P] [US2] Create `PostComment` model in `src/main/java/com/quanlyphongtro/model/PostComment.java`
- [x] T010 [P] [US2] Create `PostCommentDAO` with insert method in `src/main/java/com/quanlyphongtro/dao/PostCommentDAO.java`
- [x] T011 [US2] Implement `addComment` in `src/main/java/com/quanlyphongtro/service/impl/PostInteractionServiceImpl.java` (depends on T010)
- [x] T012 [US2] Create `PostCommentServlet` in `src/main/java/com/quanlyphongtro/controller/manager/PostCommentServlet.java` to handle adding comments
- [x] T013 [US2] Update `src/main/webapp/WEB-INF/views/manager/postManagement/detail.jsp` to include Comment input form and JavaScript for AJAX submission

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently.

---

## Phase 5: User Story 5 - Post Content Constraints (Priority: P1)

**Goal**: Ensure post titles are max 50 chars and content is max 1000 chars.

**Independent Test**: Attempt to save a post with title > 50 chars or content > 1000 chars and verify validation prevents it.

### Implementation for User Story 5

- [x] T014 [US5] Implement server-side validation for title and content length in `src/main/java/com/quanlyphongtro/service/impl/CommunityPostServiceImpl.java` or relevant servlet
- [x] T015 [US5] Update create/edit UI (e.g., `src/main/webapp/WEB-INF/views/manager/postManagement/form.jsp` if exists, or relevant UI file) to include HTML5 `maxlength` attributes

**Checkpoint**: Post creation/editing is fully validated.

---

## Phase 6: User Story 3 - View Comments and Interactions (Priority: P2)

**Goal**: Display aggregate counts of likes and comments on post feed, and list all comments on detail view.

**Independent Test**: View post feed to see accurate counts; view detail page to see chronological comment list.

### Implementation for User Story 3

- [x] T016 [US3] Update `src/main/java/com/quanlyphongtro/dao/CommunityPostDAO.java` to aggregate likes and comments counts when fetching posts
- [x] T017 [US3] Implement `getCommentsByPostId` in `src/main/java/com/quanlyphongtro/dao/PostCommentDAO.java`
- [x] T018 [US3] Implement `getComments` in `src/main/java/com/quanlyphongtro/service/impl/PostInteractionServiceImpl.java`
- [x] T019 [US3] Update `src/main/webapp/WEB-INF/views/manager/postManagement/detail.jsp` to display list of comments on page load
- [x] T020 [US3] Update `src/main/webapp/WEB-INF/views/manager/postManagement/list-pending.jsp` and other post list views to display aggregate counts

**Checkpoint**: Interactions are fully visible and quantifiable.

---

## Phase 7: User Story 4 - Delete Own Comment (Priority: P3)

**Goal**: Allow users to remove their own comments and managers to delete any comment.

**Independent Test**: User deletes their own comment, verifies it disappears. User cannot see delete button on others' comments.

### Implementation for User Story 4

- [x] T021 [US4] Implement `deleteComment` in `src/main/java/com/quanlyphongtro/dao/PostCommentDAO.java`
- [x] T022 [US4] Implement `deleteComment` in `src/main/java/com/quanlyphongtro/service/impl/PostInteractionServiceImpl.java` (with role and ownership check)
- [x] T023 [US4] Update `PostCommentServlet` in `src/main/java/com/quanlyphongtro/controller/manager/PostCommentServlet.java` to handle delete action via AJAX
- [x] T024 [US4] Update `src/main/webapp/WEB-INF/views/manager/postManagement/detail.jsp` to show Delete button conditionally and handle AJAX deletion

**Checkpoint**: All user stories should now be independently functional.

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T025 [P] Run quickstart.md validation manually
- [x] T026 Code cleanup and refactoring (if needed)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Foundational (Phase 2)**: BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - P1 stories (US1, US2, US5) can proceed in parallel
  - US3 (P2) depends on US1 and US2 models
  - US4 (P3) depends on US2 (comments existing)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### Parallel Opportunities

- DTO creation (T001, T002) can run in parallel
- Model and DAO creation for US1 and US2 can run in parallel
- US1 (Likes), US2 (Comments), and US5 (Validation) can be worked on in parallel by different team members

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 2: Foundational (CRITICAL)
2. Complete Phase 3: User Story 1 (Likes)
3. **STOP and VALIDATE**: Test User Story 1 independently

### Incremental Delivery

1. Complete Foundational
2. Add User Story 1 (Likes) → Test independently → MVP
3. Add User Story 2 (Comments) → Test independently
4. Add User Story 5 (Validation) → Test independently
5. Add User Story 3 (View Interactions) → Test independently
6. Add User Story 4 (Delete Comment) → Test independently
