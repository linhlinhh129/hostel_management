package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.ContractDAO;
import com.quanlyphongtro.dao.FacilityDAO;
import com.quanlyphongtro.dao.RoomDAO;
import com.quanlyphongtro.model.Contract;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.service.ContractService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ContractServiceImpl implements ContractService {

    private final ContractDAO contractDAO = new ContractDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final FacilityDAO facilityDAO = new FacilityDAO();

    @Override
    public List<Contract> getContractsByManager(int managerId, String searchName) {
        List<Contract> contracts = contractDAO.findAllByManagerId(managerId, searchName);
        // Map room details if necessary
        for (Contract c : contracts) {
            Optional<Room> r = roomDAO.findById(c.getRoomId());
            r.ifPresent(c::setRoom);
        }
        return contracts;
    }

    @Override
    public Contract getContractDetail(int contractId, int managerId) {
        Optional<Contract> opt = contractDAO.findByIdAndManagerId(contractId, managerId);
        if (opt.isPresent()) {
            Contract c = opt.get();
            Optional<Room> r = roomDAO.findById(c.getRoomId());
            r.ifPresent(c::setRoom);
            return c;
        }
        return null;
    }

    @Override
    public void createContract(Contract contract, int managerId) throws Exception {
        // Validate room
        Optional<Room> roomOpt = roomDAO.findById(contract.getRoomId());
        if (roomOpt.isEmpty()) {
            throw new Exception("Phòng không tồn tại.");
        }
        Room room = roomOpt.get();

        // Validate manager has access to this room
        Optional<Facility> facilityOpt = roomDAO.findFacilityByRoomId(room.getId() > 0 ? room.getId() : contract.getRoomId());
        // Since RoomDAO.findById might set roomId or id, just use contract.getRoomId() directly
        facilityOpt = roomDAO.findFacilityByRoomId(contract.getRoomId());
        if (facilityOpt.isEmpty() || facilityOpt.get().getManagerId() == null || facilityOpt.get().getManagerId() != managerId) {
            throw new Exception("Bạn không có quyền tạo hợp đồng cho phòng này.");
        }

        // Check active contract
        Optional<Contract> activeOpt = contractDAO.findActiveContractByRoomId(contract.getRoomId());
        if (activeOpt.isPresent()) {
            throw new Exception("Phòng này đang có hợp đồng ACTIVE.");
        }

        // Generate Code: HD-RoomCode-YearMonthDay-Sequence
        String code = "HD-" + room.getCode() + "-" + LocalDate.now().toString().replace("-", "") + "-" + System.currentTimeMillis() % 10000;
        contract.setCode(code);
        contract.setStatus("ACTIVE");
        contract.setCreatedBy(managerId);

        // Required fields check
        if (contract.getTenantFullName() == null || contract.getTenantFullName().trim().isEmpty()) {
            throw new Exception("Tên người thuê không được để trống.");
        }
        if (contract.getTenantIdentityNumber() == null || contract.getTenantIdentityNumber().trim().isEmpty()) {
            throw new Exception("CCCD không được để trống.");
        }
        if (contract.getSignedDate() == null || contract.getStartDate() == null || contract.getEndDate() == null) {
            throw new Exception("Ngày tháng ký/bắt đầu/kết thúc không được để trống.");
        }

        int id = contractDAO.create(contract);
        if (id <= 0) {
            throw new Exception("Lỗi hệ thống, không thể tạo hợp đồng trong cơ sở dữ liệu.");
        }
        contract.setContractId(id);

        // Cập nhật trạng thái phòng sang OCCUPIED
        room.setStatus("OCCUPIED");
        room.setContractStartDate(contract.getStartDate());
        room.setContractEndDate(contract.getEndDate());
        if (contract.getTenantId() != null) {
            room.setTenantId(contract.getTenantId());
        }
        roomDAO.update(room);
    }
}
