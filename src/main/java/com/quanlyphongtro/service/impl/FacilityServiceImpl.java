package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.FacilityDAO;
import com.quanlyphongtro.dao.RoomDAO;
import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.service.FacilityService;
import com.quanlyphongtro.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

public class FacilityServiceImpl implements FacilityService {

    private static final Logger logger = LoggerFactory.getLogger(FacilityServiceImpl.class);

    private final FacilityDAO facilityDAO = new FacilityDAO();
    private final RoomDAO     roomDAO     = new RoomDAO();

    // ── List / read ───────────────────────────────────────────────────────

    @Override
    public PageDTO<Facility> list(String keyword, String status, int page, int pageSize) {
        int total = facilityDAO.count(keyword, status);
        List<Facility> items = facilityDAO.findAll(keyword, status, page, pageSize);
        return new PageDTO<>(items, page, pageSize, total);
    }

    @Override
    public Facility getById(int id) throws NotFoundException {
        return facilityDAO.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public List<Room> getRooms(int facilityId) {
        return facilityDAO.findRoomsByFacilityId(facilityId);
    }

    // ── Create ────────────────────────────────────────────────────────────

    @Override
    public void create(String code, String name, String address,
                       String floorCountStr, String roomsPerFloorStr)
            throws ValidationException {

        code    = trim(code);
        name    = trim(name);
        address = trim(address);

        if (code.isEmpty())    throw new ValidationException("Mã cơ sở không được để trống.");
        if (name.isEmpty())    throw new ValidationException("Tên cơ sở không được để trống.");
        if (address.isEmpty()) throw new ValidationException("Địa chỉ không được để trống.");

        if (!code.matches("[A-Za-z]{2,10}"))
            throw new ValidationException("Mã cơ sở chỉ gồm 2-10 chữ cái A-Z.");

        code = code.toUpperCase();

        int floorCount    = parseFloorCount(floorCountStr);
        int roomsPerFloor = parseRoomsPerFloor(roomsPerFloorStr);

        if (facilityDAO.countByCode(code, null) > 0)
            throw new ValidationException("Mã cơ sở '" + code + "' đã tồn tại.");

        Facility f = new Facility();
        f.setCode(code);
        f.setName(name);
        f.setAddress(address);
        f.setFloorCount(floorCount);
        f.setRoomsPerFloor(roomsPerFloor);
        f.setStatus("DRAFT");
        f.setElectricityPrice(BigDecimal.ZERO);
        f.setWaterPrice(BigDecimal.ZERO);
        f.setInternetFee(BigDecimal.ZERO);
        f.setServiceFee(BigDecimal.ZERO);

        facilityDAO.insert(f);
    }

    // ── Update ────────────────────────────────────────────────────────────

    @Override
    public void update(int id,
                       String code, String name, String address,
                       String floorCountStr, String roomsPerFloorStr)
            throws NotFoundException, ValidationException {

        Facility existing = facilityDAO.findById(id).orElseThrow(NotFoundException::new);

        if ("INACTIVE".equals(existing.getStatus())) {
            throw new ValidationException("Cơ sở đã bị vô hiệu hóa. Không thể chỉnh sửa.");
        }

        name    = trim(name);
        address = trim(address);

        if (name.isEmpty())    throw new ValidationException("Tên cơ sở không được để trống.");
        if (name.length() > 255) throw new ValidationException("Tên cơ sở tối đa 255 ký tự.");

        existing.setName(name);

        // Khi DRAFT thì cho sửa thêm code/floors/rooms/address
        if ("DRAFT".equals(existing.getStatus())) {
            if (address.isEmpty()) throw new ValidationException("Địa chỉ không được để trống.");
            if (address.length() > 500) throw new ValidationException("Địa chỉ tối đa 500 ký tự.");
            existing.setAddress(address);

            code = trim(code).toUpperCase();
            if (code.isEmpty()) throw new ValidationException("Mã cơ sở không được để trống.");
            if (!code.matches("[A-Za-z]{2,10}"))
                throw new ValidationException("Mã cơ sở chỉ gồm 2-10 chữ cái A-Z.");

            int floorCount    = parseFloorCount(floorCountStr);
            int roomsPerFloor = parseRoomsPerFloor(roomsPerFloorStr);

            if (!existing.getCode().equalsIgnoreCase(code) && facilityDAO.countByCode(code, id) > 0)
                throw new ValidationException("Mã cơ sở '" + code + "' đã tồn tại.");

            existing.setCode(code);
            existing.setFloorCount(floorCount);
            existing.setRoomsPerFloor(roomsPerFloor);
        }

        facilityDAO.update(existing);
    }

    // ── Status transitions ────────────────────────────────────────────────

    @Override
    public void activate(int id) throws NotFoundException, ValidationException {
        Facility facility = facilityDAO.findById(id).orElseThrow(NotFoundException::new);

        if (!"DRAFT".equals(facility.getStatus()))
            throw new ValidationException("Chỉ có thể kích hoạt cơ sở ở trạng thái DRAFT.");

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            facilityDAO.updateStatus(id, "ACTIVE", conn);
            facilityDAO.generateRooms(id, facility.getCode(),
                    facility.getFloorCount(), facility.getRoomsPerFloor(), conn);
            conn.commit();
        } catch (Exception e) {
            DatabaseUtil.rollbackQuietly(conn);
            logger.error("FacilityService.activate failed for id={}", id, e);
            throw new ValidationException("Kích hoạt cơ sở thất bại. Vui lòng thử lại.");
        } finally {
            DatabaseUtil.closeQuietly(conn);
        }
    }

    @Override
    public void deactivate(int id) throws NotFoundException, ValidationException {
        Facility facility = facilityDAO.findById(id).orElseThrow(NotFoundException::new);

        if (!"ACTIVE".equals(facility.getStatus()))
            throw new ValidationException("Chỉ có thể vô hiệu hóa cơ sở đang ACTIVE.");

        int occupied = facilityDAO.countOccupiedRooms(id);
        if (occupied > 0)
            throw new ValidationException(
                "Không thể vô hiệu hóa cơ sở vì hiện có " + occupied +
                " phòng đang được thuê. Vui lòng kết thúc tất cả hợp đồng trước.");

        facilityDAO.updateStatus(id, "INACTIVE");
        facilityDAO.deactivateAllRooms(id);
    }

    // ── Lookup helpers ────────────────────────────────────────────────────

    @Override
    public Facility findByManagerId(int managerId) {
        return facilityDAO.findByManagerId(managerId).orElse(null);
    }

    @Override
    public Facility findByOperatorId(int operatorId) {
        return facilityDAO.findByOperatorId(operatorId).orElse(null);
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private int parseFloorCount(String floorCountStr) throws ValidationException {
        try {
            int floorCount = Integer.parseInt(trim(floorCountStr));
            if (floorCount < 1 || floorCount > 10)
                throw new ValidationException("Số tầng phải từ 1 đến 10.");
            return floorCount;
        } catch (NumberFormatException e) {
            throw new ValidationException("Số tầng không hợp lệ.");
        }
    }

    private int parseRoomsPerFloor(String roomsPerFloorStr) throws ValidationException {
        try {
            int roomsPerFloor = Integer.parseInt(trim(roomsPerFloorStr));
            if (roomsPerFloor < 1 || roomsPerFloor > 30)
                throw new ValidationException("Số phòng mỗi tầng phải từ 1 đến 30.");
            return roomsPerFloor;
        } catch (NumberFormatException e) {
            throw new ValidationException("Số phòng mỗi tầng không hợp lệ.");
        }
    }

    private BigDecimal parseBigDecimal(String s) {
        if (s == null || s.isBlank()) return BigDecimal.ZERO;
        try { return new BigDecimal(s.replace(",", ".")); }
        catch (Exception e) { return BigDecimal.ZERO; }
    }
}
