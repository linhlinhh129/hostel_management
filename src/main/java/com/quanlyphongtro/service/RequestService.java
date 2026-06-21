package com.quanlyphongtro.service;

import com.quanlyphongtro.model.Request;
import java.util.List;
import java.util.Optional;

public interface RequestService {
    List<Request> getRequestsBySenderId(int senderId);
    Optional<Request> getRequestById(int id, int senderId);
    boolean createRequest(Request request);
    int countPendingRequests(int senderId);
}
