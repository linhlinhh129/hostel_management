package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.DependentDAO;
import com.quanlyphongtro.model.Dependent;
import com.quanlyphongtro.service.DependentService;

import java.util.List;
import java.util.Optional;

public class DependentServiceImpl implements DependentService {

    private final DependentDAO dependentDAO = new DependentDAO();

    @Override
    public List<Dependent> getDependentsByTenantId(int tenantId) {
        return dependentDAO.findByTenantId(tenantId);
    }

    @Override
    public Optional<Dependent> getDependentById(int id, int tenantId) {
        return dependentDAO.findByIdAndTenantId(id, tenantId);
    }

    @Override
    public boolean addDependent(Dependent dependent) {
        return dependentDAO.insert(dependent);
    }

    @Override
    public boolean updateDependent(Dependent dependent) {
        return dependentDAO.update(dependent);
    }

    @Override
    public boolean removeDependent(int id, int tenantId) {
        return dependentDAO.softDelete(id, tenantId);
    }
}
