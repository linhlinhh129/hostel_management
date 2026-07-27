# Phase 0: Research

**Decision**: Pass `roomId` and `facilityId` from `invoice` to `Request` when creating UTILITY report.
**Rationale**: Operator's UI relies on `req.roomCode` and `req.facilityName` which are joined via `room_id` and `facility_id` in the `requests` table.
**Alternatives considered**: Modifying the UI to try parsing room numbers from the request title/content. Rejected because it's hacky, unreliable, and duplicates data unnecessarily when a proper relational approach is available.

**Decision**: Modify Operator's `list.jsp` to translate `UTILITY` to `ĐIỆN NƯỚC`.
**Rationale**: Brings consistency to the UI and helps operators quickly understand the request type.
**Alternatives considered**: Changing the database ENUM value from `UTILITY` to `DIEN_NUOC`. Rejected because it would require a DB migration and could break existing code relying on `UTILITY`.
