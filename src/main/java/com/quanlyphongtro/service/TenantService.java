package com.quanlyphongtro.service;

import com.quanlyphongtro.model.Dependent;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Invoice;
import com.quanlyphongtro.model.Notification;
import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TenantService {
    Optional<Room> getTenantRoom(int tenantId);
    Optional<Facility> getFacilityByRoomId(int roomId);
    Optional<User> getTenantProfile(int tenantId);
}
