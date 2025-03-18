package com.userinfo.controller;

import com.userinfo.model.Role;
import com.userinfo.model.User;
import com.userinfo.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import java.util.Optional;

@Controller
class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    String login() {
        return "login";
    }

    @GetMapping("/")
    String index() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.findUserByUsername(auth.getName());
        if (user.isEmpty()) {

            return "redirect:/login";
        }

        if (user.get().getRoles().stream()
                .anyMatch(role -> role.getName().equals(Role.ADMIN))) {
            return "redirect:/admin/list";
        }

        return "redirect:/user";
    }
}