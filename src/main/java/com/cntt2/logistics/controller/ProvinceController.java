package com.cntt2.logistics.controller;

import com.cntt2.logistics.dto.response.ApiResponse;
import com.cntt2.logistics.dto.response.ProvincesResponse;
import com.cntt2.logistics.service.ProvincesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/provinces")
@RequiredArgsConstructor
public class ProvinceController {
    private final ProvincesService provincesService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProvincesResponse>>> getAllWarehouseAddresses() {
        List<ProvincesResponse> addresses;
        try {
            // Gọi service để lấy danh sách địa chỉ kho hàng
            addresses = provincesService.getAllWarehouseAddresses();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong", null));
        }

        // Trả về ApiResponse với danh sách địa chỉ kho hàng
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Addresses fetched successfully", addresses));
    }
}
