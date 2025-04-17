package com.cntt2.logistics.configuration;

import com.cntt2.logistics.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // Đặt mã phản hồi là 401 (Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Thiết lập kiểu nội dung là JSON
        response.setContentType("application/json");

        // Tạo đối tượng ApiResponse
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .message("Unauthorized: " + authException.getMessage())
                .data(null)
                .build();

        // Chuyển đổi đối tượng ApiResponse thành JSON
        String jsonResponse = new ObjectMapper().writeValueAsString(apiResponse);

        // Ghi thông điệp lỗi vào response
        response.getWriter().write(jsonResponse);
    }
}
