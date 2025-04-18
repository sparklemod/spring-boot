package com.userinfo.controller;

import com.userinfo.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
class LoginController {
    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/index")
    public String home() {
        return "redirect:/index.html";
    }

    @GetMapping("/login")
    String login() {
        return "redirect:/login.html";
    }
}