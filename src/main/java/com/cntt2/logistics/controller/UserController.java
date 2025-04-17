package com.cntt2.logistics.controller;

import com.cntt2.logistics.dto.request.DriverRequest;
import com.cntt2.logistics.dto.request.UserRequest;
import com.cntt2.logistics.dto.request.UserUpdateRequest;
import com.cntt2.logistics.dto.request.WarehouseManagerRequest;
import com.cntt2.logistics.dto.response.ApiResponse;
import com.cntt2.logistics.dto.response.UserProfileResponse;
import com.cntt2.logistics.dto.response.UserResponse;
import com.cntt2.logistics.entity.Driver;
import com.cntt2.logistics.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping("/customer")
    public ResponseEntity<ApiResponse<Void>> createCustomer(@RequestBody UserRequest request) {
        return handleUserCreation(() -> userService.createCustomer(request));
    }

    @PostMapping("/manager")
    public ResponseEntity<ApiResponse<Void>> createWarehouseManager(@RequestBody WarehouseManagerRequest request) {
        return handleUserCreation(() -> userService.createWarehouseManager(request));
    }

    @PostMapping("/driver")
    public ResponseEntity<ApiResponse<Void>> createDriver(@RequestBody DriverRequest request) {
        return handleUserCreation(() -> userService.createDriver(request));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsers() {
        List<UserResponse> users = userService.getUsers();
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "User list retrieved successfully", users));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMyInfor() {
        try {
            UserProfileResponse userProfileResponse = userService.getMyInfor();
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "User info retrieved successfully", userProfileResponse));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserUpdateRequest>> updateCurrentUser(
            @RequestBody UserUpdateRequest request) {
        Optional<UserUpdateRequest> updatedUser = userService.updateCurrentUser(request);

        return updatedUser.map(userDto -> ResponseEntity.ok(
                        new ApiResponse<>(HttpStatus.OK.value(), "User updated successfully", userDto)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null)));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String id) {
        boolean isDeleted = userService.deleteUser(id);

        if (isDeleted) {
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "User deleted successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null));
        }
    }

    private ResponseEntity<ApiResponse<Void>> handleUserCreation(Runnable createUser) {
        try {
            createUser.run();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(HttpStatus.CREATED.value(), "User created successfully", null));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(HttpStatus.CONFLICT.value(), "Email already exists", null));
        }
    }
}
