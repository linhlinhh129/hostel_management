package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;

import java.util.List;

public interface FacilityService {

    // Tìm kiếm danh sách cơ sở
    PageDTO<Facility> list(String keyword, String status, int page, int pageSize);

    // Tìm theo ID
    Facility getById(int id) throws NotFoundException;

    // Danh sách phòng
    List<Room> getRooms(int facilityId);

    // Tạo cơ sở    
    void create(String code, String name, String address,
                String floorCountStr, String roomsPerFloorStr)
            throws ValidationException;

    // Sửa cơ sở                        
    void update(int id,
                String code, String name, String address,
                String floorCountStr, String roomsPerFloorStr)
            throws NotFoundException, ValidationException;

    // Kích hoạt cơ sở                      
    void activate(int id) throws NotFoundException, ValidationException;

    // Vô hiệu hóa cơ sở                      
    void deactivate(int id) throws NotFoundException, ValidationException;

    //  Tìm cơ sở theo quản lý                      
    Facility findByManagerId(int managerId);

    // Tìm cơ sở theo người vận hành                      
    Facility findByOperatorId(int operatorId);
}
