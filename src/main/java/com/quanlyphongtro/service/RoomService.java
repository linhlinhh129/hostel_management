package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.PageResult;
import com.quanlyphongtro.dto.RoomDTO;
import com.quanlyphongtro.dto.RoomDetailDTO;

import java.util.List;
import java.util.Map;

public interface RoomService {
    List<Map<String, Object>> getFacilitiesByManager(int managerId);
    
    Integer getDefaultFacilityId(int managerId);
    
    Map<String, Object> verifyFacilityManager(int facilityId, int managerId);
    
    PageResult<RoomDTO> getFacilityRoomsPage(int facilityId, String status, int page, int pageSize);
    
    RoomDetailDTO getRoomDetail(int roomId, int managerId) throws Exception;
}
