package com.quanlyphongtro.service;

import com.quanlyphongtro.model.Dependent;
import java.util.List;
import java.util.Optional;

public interface DependentService {
    List<Dependent> getDependentsByTenantId(int tenantId);
    Optional<Dependent> getDependentById(int id, int tenantId);
    boolean addDependent(Dependent dependent);
    boolean updateDependent(Dependent dependent);
    boolean removeDependent(int id, int tenantId);
}
