package com.cntt2.logistics.controller;

import com.cntt2.logistics.dto.request.AuthenticationRequest;
import com.cntt2.logistics.dto.request.IntrospectRequest;
import com.cntt2.logistics.dto.request.LogoutRequest;
import com.cntt2.logistics.dto.request.RefreshTokenRequest;
import com.cntt2.logistics.dto.response.ApiResponse;
import com.cntt2.logistics.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.ApplicationContextException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> authenticate(@RequestBody AuthenticationRequest request) {
        try {
            // Gọi hàm authenticate và nhận về dữ liệu chứa token và id
            Map<String, Object> data = authenticationService.authenticate(request);

            // Trả về ApiResponse với data chứa cả token và id
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Login successful", data));
        } catch (ApplicationContextException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Email does not exist or incorrect password", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong", null));
        }
    }

    @PostMapping("/introspect")
    public ResponseEntity<ApiResponse<Boolean>> introspect(@RequestBody IntrospectRequest request) {
        try {
            Boolean result = authenticationService.introspect(request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Token is valid", result));
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid token format", false));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired token", false));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody LogoutRequest request) {
        try {
            authenticationService.logout(request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Logout successful", null));
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid token format", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Logout failed", null));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<String>> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String newToken = authenticationService.refreshToken(request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Token refreshed successfully", newToken));
        } catch (ParseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid token format", null));
        } catch (ApplicationContextException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), "Token is invalid or expired", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Token refresh failed", null));
        }
    }



}
