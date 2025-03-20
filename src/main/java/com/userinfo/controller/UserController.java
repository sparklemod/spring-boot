package com.userinfo.controller;

import com.userinfo.model.Role;
import com.userinfo.model.User;
import com.userinfo.service.RoleService;
import com.userinfo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping()
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/user")
    public String listUser(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.findUserByUsername(auth.getName());
        if (user.isEmpty()) {
            return "redirect:/login";
        }

        model.addAttribute("users", List.of(user.get()));
        return "list";
    }

    @GetMapping("/admin/list")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "list";
    }

    @GetMapping(value = "/admin/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("isEdit", 0);
        return "form";
    }

    @PostMapping("/admin/save")
    public String saveUser(@Valid @ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "form";
        }
//
//        Set<Role> roles = new HashSet<>(roleService.getRolesByIds(roleIds));
//        user.setRoles(roles);
        userService.saveUser(user);
        return "redirect:/main";
    }

    @GetMapping("/admin/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUser(id));
        model.addAttribute("isEdit", 1);
        model.addAttribute("roles", roleService.getAllRoles());
        return "form";
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/main";
    }
}
