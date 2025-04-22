package com.userinfo.controller;

import com.userinfo.model.Role;
import com.userinfo.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles")
    public List<Role> listUsers() {
        return roleService.getAllRoles();
    }
}