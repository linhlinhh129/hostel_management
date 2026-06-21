package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.RoomDAO;
import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.service.TenantService;

import java.util.Optional;

public class TenantServiceImpl implements TenantService {

    private final RoomDAO roomDAO = new RoomDAO();
    private final UserDAO userDAO = new UserDAO();

    @Override
    public Optional<Room> getTenantRoom(int tenantId) {
        return roomDAO.findByTenantId(tenantId);
    }

    @Override
    public Optional<Facility> getFacilityByRoomId(int roomId) {
        return roomDAO.findFacilityByRoomId(roomId);
    }

    @Override
    public Optional<User> getTenantProfile(int tenantId) {
        return userDAO.findById(tenantId);
    }
}
