package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.RoomDAO;
import com.quanlyphongtro.dto.PageResult;
import com.quanlyphongtro.dto.RoomDTO;
import com.quanlyphongtro.dto.RoomDetailDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.service.RoomService;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoomServiceImpl implements RoomService {

    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    public Map<String, Object> getDetailForAdmin(int roomId) throws NotFoundException {
        return roomDAO.findDetailForAdmin(roomId).orElseThrow(NotFoundException::new);
    }

    @Override
    public void updateAreaAndFee(int roomId, String areaStr, String feeStr)
            throws NotFoundException, ValidationException {

        Map<String, Object> room = roomDAO.findDetailForAdmin(roomId)
                .orElseThrow(NotFoundException::new);

        if ("INACTIVE".equals(room.get("facilityStatus")))
            throw new ValidationException("Cơ sở đã bị vô hiệu hóa. Không thể chỉnh sửa thông tin phòng.");

        BigDecimal area = parsePositiveDecimal(areaStr, "Diện tích");
        BigDecimal fee  = parsePositiveDecimal(feeStr,  "Giá phòng");

        boolean updated = roomDAO.updateAreaAndFee(roomId, area, fee);
        if (!updated) throw new NotFoundException();
    }

    @Override
    public List<Map<String, Object>> getFacilitiesByManager(Integer managerId) {
        return roomDAO.getFacilitiesByManager(managerId);
    }

    @Override
    public Integer getDefaultFacilityId(Integer managerId) {
        return roomDAO.getDefaultFacilityId(managerId);
    }

    @Override
    public Map<String, Object> verifyFacilityManager(int facilityId, Integer managerId) {
        return roomDAO.verifyFacilityManager(facilityId, managerId);
    }

    @Override
    public PageResult<RoomDTO> getFacilityRoomsPage(int facilityId, String status, int page, int pageSize) {
        int total = roomDAO.countFacilityRooms(facilityId, status);
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> rows = roomDAO.findFacilityRooms(facilityId, status, offset, pageSize);

        List<RoomDTO> items = rows.stream().map(r -> {
            RoomDTO dto = new RoomDTO();
            dto.setId((int) r.get("id"));
            dto.setCode((String) r.get("code"));
            Object area = r.get("area");
            dto.setArea(area != null ? ((Number) area).doubleValue() : 0.0);
            dto.setStatus((String) r.get("status"));
            dto.setTenantId((Integer) r.get("tenantId"));
            dto.setTenantName((String) r.get("tenantName"));
            dto.setFloor((String) r.get("floor"));
            dto.setRoomNumber((String) r.get("roomNumber"));
            return dto;
        }).collect(Collectors.toList());

        int totalPages = (total == 0) ? 1 : (int) Math.ceil((double) total / pageSize);
        return new PageResult<>(items, total, page, totalPages);
    }

    @Override
    public RoomDetailDTO getRoomDetail(int roomId, Integer managerId) throws AccessDeniedException {
        Map<String, Object> raw = roomDAO.findRoomDetail(roomId);
        if (raw == null) return null;

        // Kiểm tra quyền: manager phải phụ trách cơ sở chứa phòng này
        Object facilityManagerId = raw.get("managerId");
        if (facilityManagerId != null && !facilityManagerId.equals(managerId)) {
            throw new AccessDeniedException("Bạn không có quyền xem phòng này.");
        }

        RoomDetailDTO dto = new RoomDetailDTO();
        dto.setId((int) raw.get("id"));
        dto.setFacilityId((int) raw.get("facilityId"));
        dto.setFacilityCode((String) raw.get("facilityCode"));
        dto.setFacilityName((String) raw.get("facilityName"));
        Object mgr = raw.get("managerId");
        dto.setManagerId(mgr != null ? (int) mgr : 0);
        dto.setCode((String) raw.get("code"));
        Object area = raw.get("area");
        dto.setArea(area != null ? ((Number) area).doubleValue() : 0.0);
        dto.setStatus((String) raw.get("status"));
        dto.setCreatedAt((String) raw.get("createdAt"));
        dto.setUpdatedAt((String) raw.get("updatedAt"));
        dto.setCreatedAtAsDate(toJavaDate(raw.get("createdAtAsDate")));
        dto.setUpdatedAtAsDate(toJavaDate(raw.get("updatedAtAsDate")));
        dto.setTenantId((Integer) raw.get("tenantId"));
        dto.setTenantName((String) raw.get("tenantName"));
        dto.setTenantCode((String) raw.get("tenantCode"));
        dto.setTenantPhone((String) raw.get("tenantPhone"));
        dto.setFloor((String) raw.get("floor"));
        dto.setRoomNumber((String) raw.get("roomNumber"));
        dto.setRoomFee((BigDecimal) raw.get("roomFee"));

        // Lookup active contract
        Integer activeContractId = roomDAO.findActiveContractId(roomId);
        dto.setActiveContractId(activeContractId);

        return dto;
    }

    // ── Private helpers ───────────────────────────────────────────────────

    /**
     * Trả về null nếu chuỗi trống (xóa giá trị).
     * Ném ValidationException nếu không phải số hoặc âm.
     */
    private BigDecimal parsePositiveDecimal(String raw, String fieldLabel) throws ValidationException {
        if (raw == null || raw.isBlank()) return null;
        try {
            BigDecimal val = new BigDecimal(raw.replace(",", ".").trim());
            if (val.compareTo(BigDecimal.ZERO) < 0)
                throw new ValidationException(fieldLabel + " không được âm.");
            return val;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldLabel + " không hợp lệ.");
        }
    }

    /**
     * Chuyển Timestamp (từ Map DAO) sang java.util.Date cho JSP fmt:formatDate.
     */
    private java.util.Date toJavaDate(Object obj) {
        if (obj instanceof Timestamp) return (Timestamp) obj;
        return null;
    }
}
