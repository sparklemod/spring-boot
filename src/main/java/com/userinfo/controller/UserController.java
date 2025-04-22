package com.userinfo.controller;

import com.userinfo.model.Role;
import com.userinfo.security.CustomUserDetails;
import com.userinfo.model.Dto.UserDto;
import com.userinfo.service.RoleService;
import com.userinfo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Operation(summary = "Get list of all roles")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of roles")
    @GetMapping("/get-roles")
    public List<Role> getRoles() {
        return roleService.getAllRoles();
    }

    @Operation(summary = "Get list of all users")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    @GetMapping("/get-users")
    public List<UserDto> listUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Get current user info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/get-current-user-info")
    public ResponseEntity<?> getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Map<String, Object> response = Map.of(
                "id", userDetails.getId(),
                "name", userDetails.getName(),
                "surname", userDetails.getSurname(),
                "username", userDetails.getUsername(),
                "roles", userDetails.getAuthorities().toString()
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/add-user")
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto userDTO) {
        userService.addUser(userDTO);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "Get a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/get-user/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userService.getUserDto(id);
    }

    @Operation(summary = "Update a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "User successfully updated"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PutMapping("/edit-user/{id}")
    public ResponseEntity<UserDto> editUser(@PathVariable Long id, @RequestBody @Valid UserDto userDTO) {
        userDTO.setId(id);
        userService.editUser(userDTO);
        return ResponseEntity.accepted().body(userDTO);
    }

    @Operation(summary = "Delete a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User successfully deleted"));
    }
}
