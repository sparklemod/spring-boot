package com.userinfo.service;

import com.userinfo.model.Role;
import com.userinfo.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public List<Role> getRolesByIds(List<Long> ids) {
        return roleRepository.findAllById(ids);
    }
}