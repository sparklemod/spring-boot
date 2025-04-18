package com.userinfo.controller;

import com.userinfo.model.Dto.UserDto;
import com.userinfo.model.User;
import com.userinfo.service.RoleService;
import com.userinfo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //    GET /users (список)
    //    GET /user (текущий пользователь)
    //    GET /users/{id} (карточка юзера)
    //    POST /user (создание)
    //    PUT /user/{id} (обновление / создание)
    //    GET /roles (список ролей)

    @GetMapping("/users")
    public List<User> listUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/user")
    public User listUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userService.findUserByUsername(auth.getName());

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User " + auth.getName() + " not found");
        }

        return user.get();
    }

    @GetMapping("/users/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUserDto(id);
    }

    @PostMapping("/user")
    public ResponseEntity<String> addUser(@Valid @ModelAttribute("user") UserDto userDTO, Model model) {
        try {
            userService.addUser(userDTO);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        return new ResponseEntity<>("User saved", HttpStatus.CREATED);
    }


    @PutMapping(value = "/user/{id}")
    public ResponseEntity<String> editUser(@Valid @ModelAttribute("user") UserDto userDTO, Model model) {
        try {
            userService.editUser(userDTO);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }

        return new ResponseEntity<>("User saved", HttpStatus.ACCEPTED);
    }

    @GetMapping("/admin/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
