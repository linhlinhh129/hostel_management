package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.RequestDAO;
import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.service.RequestService;

import java.util.List;
import java.util.Optional;

public class RequestServiceImpl implements RequestService {

    private final RequestDAO requestDAO = new RequestDAO();

    @Override
    public List<Request> getRequestsBySenderId(int senderId) {
        return requestDAO.findBySenderId(senderId);
    }

    @Override
    public Optional<Request> getRequestById(int id, int senderId) {
        return requestDAO.findByIdAndSenderId(id, senderId);
    }

    @Override
    public boolean createRequest(Request request) {
        return requestDAO.insert(request);
    }

    @Override
    public int countPendingRequests(int senderId) {
        return requestDAO.countPendingBySenderId(senderId);
    }
}
