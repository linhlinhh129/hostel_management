package com.quanlyphongtro.service;

import java.util.Map;

public interface DashboardService {
    Map<String, Object> getManagerDashboardStats(int managerId);
}
