# ASKS: Phân chia Chi tiết Đầu Việc - Vận hành Cơ sở được phân công

**Total Story Points:** ~32 points  
**Sprint Duration:** 2 weeks × 2.5 sprints = 5 weeks  
**Velocity:** ~12.8 points/sprint

---

## Epic 1: Backend Services (8 points)

### Task 1.1: Facility Access Validation Service (2 points)
**Duration:** 1 day  
**Description:**
- Implement checkFacilityAccess(managerId, facilityId) service
- Query EmployeeFacility table
- Return true/false

---

### Task 1.2: Get Assigned Facilities Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getFacilities(managerId) service
- Return list of facilities assigned to manager
- Include facility details (code, name, floors, address)
- Support pagination

---

### Task 1.3: Get Facility Detail Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getFacilityDetail(facilityId, managerId) service
- Check facility access permission
- Return full facility info
- Return room count by status

---

## Epic 2: Room Management Services (10 points)

### Task 2.1: Get Rooms by Facility Service (4 points)
**Duration:** 2 days  
**Description:**
- Implement getRooms(facilityId, managerId, filters) service
- Check facility access permission
- Support pagination
- Support filter by status
- Support search by room code
- Include tenant info if occupied

---

### Task 2.2: Get Room Detail Service (3 points)
**Duration:** 1-2 days  
**Description:**
- Implement getRoomDetail(roomId, managerId) service
- Check facility access permission
- Return full room info
- Include tenant details if occupied
- Include historical tenant info

---

### Task 2.3: Room Availability View Service (3 points)
**Duration:** 1 day  
**Description:**
- Implement getRoomStatistics(facilityId, managerId) service
- Check facility access permission
- Return count of rooms by status (AVAILABLE, OCCUPIED, MAINTENANCE, RESERVED)
- Return occupancy rate

---

## Epic 3: API Controllers (8 points)

### Task 3.1: Facility API Endpoints (4 points)
**Duration:** 2 days  
**Description:**
- GET /api/v1/manager/facilities (list)
- GET /api/v1/manager/facilities/{id} (detail)
- GET /api/v1/manager/facilities/{id}/statistics
- Implement DTOs
- Add authorization headers validation

---

### Task 3.2: Room API Endpoints (4 points)
**Duration:** 2 days  
**Description:**
- GET /api/v1/manager/facilities/{facilityId}/rooms (list)
- GET /api/v1/manager/rooms/{id} (detail)
- Support filters: status, search
- Implement DTOs

---

## Epic 4: Frontend - Dashboard (10 points)

### Task 4.1: Facility Dashboard Page (3 points)
**Duration:** 1-2 days  
**Description:**
- Create facility list/grid view
- Show: facility code, name, floor count, room count
- Click to view detail
- Load assigned facilities on page load

---

### Task 4.2: Facility Detail Page (3 points)
**Duration:** 1-2 days  
**Description:**
- Display facility information (read-only)
- Show statistics: total rooms, available, occupied, maintenance
- Show floor plan (if available)
- Room list tab

---

### Task 4.3: Room List & Detail (3 points)
**Duration:** 1-2 days  
**Description:**
- Display rooms in facility
- Table: room code, floor, status, tenant (if occupied)
- Click row to view detail
- Show room detail (read-only): code, floor, area, status, tenant info

---

### Task 4.4: Search & Filter (1 point)
**Duration:** 1 day  
**Description:**
- Search rooms by code
- Filter rooms by status

---

## Epic 5: Authorization & Security (4 points)

### Task 5.1: Permission Validation (2 points)
**Duration:** 1 day  
**Description:**
- Implement authorization interceptor
- Check manager has facility access
- Return 403 for unauthorized access

---

### Task 5.2: Data Access Control (2 points)
**Duration:** 1 day  
**Description:**
- Ensure manager only sees assigned facilities
- Ensure manager only sees rooms in assigned facilities
- No cross-facility access

---

## Epic 6: Testing (2 points)

### Task 6.1: Unit Tests (1 point)
**Duration:** 1 day  
**Description:**
- Test facility access validation
- Test service methods
- Test permission checks

---

### Task 6.2: Integration Tests (1 point)
**Duration:** 1 day  
**Description:**
- Test API endpoints
- Test permission enforcement
- Test E2E flows

---

## Summary by Sprint

| Sprint | Points | Focus |
|--------|--------|-------|
| Sprint 1 | 13 | Facility access, get facilities, API |
| Sprint 2 | 13 | Room services, API, frontend |
| Sprint 3 | 6 | Testing, authorization, deployment |

---

## Critical Dependencies

- Task 1.1 → Task 1.2 (access validation needed for list)
- Task 1.2 → Task 2.1 (facilities needed for room list)
- Task 1.1, 1.3, 2.1, 2.2 → Task 3.1, 3.2 (services needed for API)
- Task 3.1, 3.2 → Task 4.1-4.4 (API needed for frontend)

---

## Test Scenarios

1. ✓ Manager views assigned facilities
2. ✓ Manager views rooms in assigned facility
3. ✓ Manager views facility detail (read-only)
4. ✓ Manager views room detail (read-only)
5. ✗ Manager cannot view non-assigned facility (403)
6. ✗ No edit/delete buttons visible
7. ✓ Search works by room code
8. ✓ Filter works by status
9. ✓ Statistics display correctly
