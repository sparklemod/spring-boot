package com.userinfo.repository;

import com.userinfo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
    Set<Role> findByIdIn(List<Long> ids);
}