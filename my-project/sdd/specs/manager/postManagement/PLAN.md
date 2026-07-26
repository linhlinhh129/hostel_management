# Implementation Plan: Post Interactions (Comments and Likes)

**Branch**: `post-interactions` | **Date**: 2026-07-27 | **Spec**: [spec.md](file:///f:/SU26/New%20folder/hostel_management/my-project/sdd/specs/manager/postManagement/spec.md)

**Input**: Feature specification from `/my-project/sdd/specs/manager/postManagement/spec.md`

## Summary

Add comment and like functionality to the community posts page. This includes allowing authenticated users to like/unlike posts, add text comments, and displaying aggregated counts for likes and comments. The database schema already has `post_reactions` and `post_comments` tables, so the focus will be on backend APIs and frontend UI integration.

## Technical Context

**Language/Version**: Java 17, Servlet API, JSP

**Primary Dependencies**: JDBC, Bootstrap 5, Mintlify design system

**Storage**: SQL Server (tables `post_reactions`, `post_comments` already exist)

**Testing**: JUnit, Mockito (if applicable)

**Target Platform**: Web application deployed on Tomcat

**Project Type**: Web application (Frontend + Backend in same repo)

**Performance Goals**: UI updates under 500ms for likes, 1s for comments

**Constraints**: Role-based access control (must be logged in), no nested comments, plain text only for comments.

**Scale/Scope**: Extension of existing Community Post feature.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- [x] **Layered Architecture (MVC)**: Yes, using Servlets, Services, DAOs, and JSPs.
- [x] **Consistent UI Design**: Yes, will use `hostel-design.css` and Bootstrap 5.
- [x] **Role-Based Access Control (RBAC)**: Yes, restricted to authenticated users.
- [x] **Safe Database Operations**: Yes, using JDBC PreparedStatement and transactions where necessary.
- [x] **Test-Driven and Code Quality**: Yes, logic placed in services.

## Project Structure

### Documentation (this feature)

```text
my-project/sdd/specs/manager/postManagement/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (empty for internal app)
└── tasks.md             # To be created later
```

### Source Code (repository root)

```text
src/main/
├── java/com/quanlyphongtro/
│   ├── model/
│   │   ├── PostComment.java
│   │   └── PostReaction.java
│   ├── dto/
│   │   ├── PostCommentDTO.java
│   │   └── PostReactionDTO.java
│   ├── dao/
│   │   ├── PostCommentDAO.java
│   │   └── PostReactionDAO.java
│   ├── service/
│   │   ├── PostInteractionService.java
│   │   └── impl/PostInteractionServiceImpl.java
│   └── controller/
│       ├── PostCommentServlet.java
│       └── PostReactionServlet.java
└── webapp/WEB-INF/views/
    └── manager/postManagement/
        ├── list-pending.jsp (update to show counts)
        └── detail.jsp (update to show comments thread and like button)
```

**Structure Decision**: Standard MVC structure following existing project conventions. Added DAOs, Services, and Controllers for Post Comments and Post Reactions. UI changes will be made to existing post management JSPs.
