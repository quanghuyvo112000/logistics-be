package com.cntt2.logistics.controller;

import com.cntt2.logistics.dto.request.*;
import com.cntt2.logistics.dto.response.ApiResponse;
import com.cntt2.logistics.dto.response.SearchWarehouseLocationResponse;
import com.cntt2.logistics.dto.response.ShippingInfoResponse;
import com.cntt2.logistics.dto.response.WarehouseLocationResponse;
import com.cntt2.logistics.service.WarehouseLocationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouse-locations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseLocationController {

    WarehouseLocationService warehouseLocationService;

    // ✅ Tạo mới một warehouse location
    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseLocationResponse>> createWarehouseLocation(
            @RequestBody WarehouseLocationRequest request
    ) {
        try {
            // Tạo kho mới
            warehouseLocationService.createWarehouse(request);

            // Trả về thông báo thành công khi kho được tạo
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(201, "Warehouse created successfully", null));
        } catch (Exception e) {
            // Trả về lỗi nếu có vấn đề khi tạo kho
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Failed to create warehouse", null));
        }
    }


    // ✅ Lấy tất cả warehouse
    @GetMapping
    public ResponseEntity<ApiResponse<List<WarehouseLocationResponse>>> getAllWarehouses() {
        try {
            List<WarehouseLocationResponse> warehouses = warehouseLocationService.getAllWarehouseResponses();
            return ResponseEntity.ok(new ApiResponse<>(200, "Fetched successfully", warehouses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Something went wrong", null));
        }
    }

    // ✅ Lấy warehouse của manager hiện tại
    @GetMapping("/manager")
    public ResponseEntity<ApiResponse<WarehouseLocationResponse>> getMyWarehouseLocation() {
        try {
            WarehouseLocationResponse warehouse = warehouseLocationService.getWarehouseByLoggedInManager();
            return ResponseEntity.ok(new ApiResponse<>(200, "Fetched successfully", warehouse));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage(), null));
        }
    }

    // ✅ Bổ nhiệm user thành manager của warehouse
    @PutMapping("/{warehouseId}/assign-manager")
    public ResponseEntity<ApiResponse<String>> assignManagerToWarehouse(
            @PathVariable String warehouseId,
            @RequestBody AssignManagerRequest request
    ) {
        try {
            warehouseLocationService.assignManagerToWarehouse(request);
            return ResponseEntity.ok(new ApiResponse<>(200, "Manager assigned successfully", "Success"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Failed to assign manager", null));
        }
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<SearchWarehouseLocationResponse>>> searchWarehouseByLocation(
            @RequestBody SearchWarehouseLocationRequest request
    ) {
        try {
            List<SearchWarehouseLocationResponse> warehouses = warehouseLocationService.searchWarehouseByLocation(request);
            return ResponseEntity.ok(new ApiResponse<>(200, "Search results fetched successfully", warehouses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Failed to fetch search results", null));
        }
    }

    @PostMapping("shipping/calculate")
    public ResponseEntity<ApiResponse<ShippingInfoResponse>> calculateShippingInfo(
            @RequestBody ShippingInfoRequest request
    ) {
        try {
            ShippingInfoResponse shippingInfo = warehouseLocationService.calculateShippingInfo(
                    request.getFromWarehouseId(),
                    request.getToWarehouseId()
            );
            return ResponseEntity.ok(new ApiResponse<>(200, "Shipping info calculated successfully", shippingInfo));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Failed to calculate shipping info", null));
        }
    }

    @PostMapping("shipping/lookup")
    public ResponseEntity<ApiResponse<ShippingInfoResponse>> calculateShippingInfoByLocation(
            @RequestBody ShippingLookUpInfoRequest request
    ) {
        try {
            ShippingInfoResponse shippingInfo = warehouseLocationService.calculateShippingInfoByLocation(request);
            return ResponseEntity.ok(new ApiResponse<>(200, "Shipping info calculated successfully", shippingInfo));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Failed to calculate shipping info", null));
        }
    }
}
