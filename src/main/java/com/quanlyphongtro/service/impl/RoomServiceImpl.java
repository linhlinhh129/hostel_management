package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.RoomDAO;
import com.quanlyphongtro.dto.PageResult;
import com.quanlyphongtro.dto.RoomDTO;
import com.quanlyphongtro.dto.RoomDetailDTO;
import com.quanlyphongtro.service.RoomService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomServiceImpl implements RoomService {

    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    public List<Map<String, Object>> getFacilitiesByManager(int managerId) {
        return roomDAO.getFacilitiesByManager(managerId);
    }

    @Override
    public Integer getDefaultFacilityId(int managerId) {
        return roomDAO.getDefaultFacilityId(managerId);
    }

    @Override
    public Map<String, Object> verifyFacilityManager(int facilityId, int managerId) {
        return roomDAO.verifyFacilityManager(facilityId, managerId);
    }

    @Override
    public PageResult<RoomDTO> getFacilityRoomsPage(int facilityId, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        int totalRooms = roomDAO.countFacilityRooms(facilityId, status);
        List<Map<String, Object>> roomsMap = roomDAO.findFacilityRooms(facilityId, status, offset, pageSize);
        
        List<RoomDTO> rooms = new ArrayList<>();
        for (Map<String, Object> map : roomsMap) {
            RoomDTO dto = new RoomDTO();
            dto.setId((Integer) map.get("id"));
            dto.setCode((String) map.get("code"));
            dto.setArea((Double) map.get("area"));
            dto.setStatus((String) map.get("status"));
            dto.setTenantId((Integer) map.get("tenantId"));
            dto.setTenantName((String) map.get("tenantName"));
            dto.setFloor((String) map.get("floor"));
            dto.setRoomNumber((String) map.get("roomNumber"));
            rooms.add(dto);
        }
        
        int totalPages = totalRooms > 0 ? (int) Math.ceil((double) totalRooms / pageSize) : 1;
        return new PageResult<>(rooms, totalRooms, page, totalPages);
    }

    @Override
    public RoomDetailDTO getRoomDetail(int roomId, int managerId) throws Exception {
        Map<String, Object> map = roomDAO.findRoomDetail(roomId);
        if (map == null) {
            return null;
        }

        // Verify manager controls the facility containing the room
        int roomManagerId = (int) map.get("managerId");
        if (roomManagerId != managerId) {
            throw new java.nio.file.AccessDeniedException("Bạn không quản lý cơ sở chứa phòng này.");
        }

        RoomDetailDTO dto = new RoomDetailDTO();
        dto.setId((Integer) map.get("id"));
        dto.setFacilityId((Integer) map.get("facilityId"));
        dto.setFacilityCode((String) map.get("facilityCode"));
        dto.setFacilityName((String) map.get("facilityName"));
        dto.setManagerId(roomManagerId);
        dto.setCode((String) map.get("code"));
        dto.setArea((Double) map.get("area"));
        dto.setStatus((String) map.get("status"));
        dto.setCreatedAt((String) map.get("createdAt"));
        dto.setUpdatedAt((String) map.get("updatedAt"));
        dto.setTenantId((Integer) map.get("tenantId"));
        dto.setTenantName((String) map.get("tenantName"));
        dto.setTenantCode((String) map.get("tenantCode"));
        dto.setTenantPhone((String) map.get("tenantPhone"));
        dto.setFloor((String) map.get("floor"));
        dto.setRoomNumber((String) map.get("roomNumber"));
        dto.setRoomFee((java.math.BigDecimal) map.get("roomFee"));
        dto.setCreatedAtAsDate((java.util.Date) map.get("createdAtAsDate"));
        dto.setUpdatedAtAsDate((java.util.Date) map.get("updatedAtAsDate"));

        // Load activeContractId if room is occupied and tenantId is null
        if (dto.getTenantId() == null && "OCCUPIED".equals(dto.getStatus())) {
            Integer activeContractId = roomDAO.findActiveContractId(roomId);
            dto.setActiveContractId(activeContractId);
        }

        return dto;
    }
}
