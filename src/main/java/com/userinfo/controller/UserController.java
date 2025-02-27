package com.userinfo.controller;

import com.userinfo.model.User;
import com.userinfo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "list";
    }

    @GetMapping(value = "/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        return "form";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable(value = "id") long id, Model model,
                                RedirectAttributes attributes) {
        User user = userService.getUser(id);

        if (null == user) {
            attributes.addFlashAttribute("flashMessage", "User are not exists!");
            return "redirect:/users";
        }

        model.addAttribute("user", user);
        return "form";
    }

    @PostMapping()
    public String saveUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                           RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            return "form";
        }

        boolean isNew = (user.getId() == null);
        userService.createOrUpdateUser(user);

        attributes.addFlashAttribute("flashMessage",
                isNew ? "User successfully created!" : "User successfully updated!");
        return "redirect:/users";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam(value = "id") long id, RedirectAttributes attributes) {
        attributes.addFlashAttribute(
                "flashMessage",
                userService.deleteUser(id)
                        ? "User successfully deleted!"
                        : "User does not exist!");

        return "redirect:/users";
    }
}
