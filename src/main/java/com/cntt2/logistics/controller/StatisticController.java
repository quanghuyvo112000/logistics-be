package com.cntt2.logistics.controller;

import com.cntt2.logistics.dto.request.IncomeRequest;
import com.cntt2.logistics.dto.response.*;
import com.cntt2.logistics.service.StatisticService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.NoSuchElementException;


@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticController {
    StatisticService statisticService;

    @PostMapping("/{trackingCode}")
    public ResponseEntity<ApiResponse<IncomeResponse>> createIncomeByTrackingCode(@PathVariable String trackingCode) {
        try {
            // Gửi trackingCode vào request DTO
            IncomeRequest request = new IncomeRequest();
            request.setTrackingCode(trackingCode);

            IncomeResponse response = statisticService.createIncomeFromTrackingCode(request);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "Income created successfully", response)
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create income", null));
        }
    }

    // Lấy thống kê doanh thu theo tháng (cho DRIVER hoặc WAREHOUSE_MANAGER)
    @GetMapping("/monthly/{year}")
    public ResponseEntity<ApiResponse<List<TimeAmountResponse>>> getMonthlyStats(@PathVariable int year) {
        try {
            List<TimeAmountResponse> stats = statisticService.getMonthlyStats(year);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "Lấy thống kê doanh thu theo tháng thành công", stats)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi khi lấy thống kê theo tháng", null));
        }
    }

    // Lấy thống kê doanh thu theo quý (cho DRIVER hoặc WAREHOUSE_MANAGER)
    @GetMapping("/quarterly/{year}")
    public ResponseEntity<ApiResponse<List<TimeAmountResponse>>> getQuarterlyStats(@PathVariable int year) {
        try {
            List<TimeAmountResponse> stats = statisticService.getQuarterlyStats(year);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "Lấy thống kê doanh thu theo quý thành công", stats)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi khi lấy thống kê theo quý", null));
        }
    }

    // Lấy thống kê doanh thu theo tháng (cho CUSTOMER)
    @GetMapping("/customer/monthly/{year}")
    public ResponseEntity<ApiResponse<List<TimeAmountResponse>>> getMonthlyOrderStatsByCustomer(@PathVariable int year) {
        try {
            List<TimeAmountResponse> stats = statisticService.getMonthlyOrderStatsByCustomer(year);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "Lấy thống kê doanh thu theo tháng thành công", stats)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi khi lấy thống kê theo tháng", null));
        }
    }

    // Lấy thống kê doanh thu theo quý (cho CUSTOMER)
    @GetMapping("/customer/quarterly/{year}")
    public ResponseEntity<ApiResponse<List<TimeAmountResponse>>> getQuarterlyOrderStatsByCustomer(@PathVariable int year) {
        try {
            List<TimeAmountResponse> stats = statisticService.getQuarterlyOrderStatsByCustomer(year);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "Lấy thống kê doanh thu theo quý thành công", stats)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi khi lấy thống kê theo quý", null));
        }
    }

    //Lấy tổng doanh thu từng kho hàng cho admin
    @GetMapping("/admin/warehouse-amount/{warehouseId}/{year}")
    public ResponseEntity<ApiResponse<List<TimeAmountResponse>>> getWarehouseRevenueById(
            @PathVariable String warehouseId,
            @PathVariable int year) {
        try {
            List<TimeAmountResponse> response = statisticService.getMonthlyStatsByWarehouseId(warehouseId, year);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Thống kê doanh thu kho thành công", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Đã xảy ra lỗi", null));
        }
    }

    // CUSTOMER
    @GetMapping("order/customer/monthly/{year}")
    public ResponseEntity<ApiResponse<MonthlyOrderStatusGroupResponse>> getCustomerMonthlyStats(@PathVariable int year) {
        try {
            MonthlyOrderStatusGroupResponse response = statisticService.countCreatedAndDeliveredOrdersByMonth(year);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy thống kê theo tháng cho khách hàng thành công", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi khi lấy thống kê khách hàng", null));
        }
    }

    // WAREHOUSE_MANAGER
    @GetMapping("order/warehouse/monthly/{year}")
    public ResponseEntity<ApiResponse<MonthlyOrderStatusGroupResponse>> getWarehouseMonthlyStats(@PathVariable int year) {
        try {
            MonthlyOrderStatusGroupResponse response = statisticService.getMonthlyOrdersByWarehouse(year);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy thống kê theo tháng cho kho thành công", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi khi lấy thống kê kho", null));
        }
    }

    // ADMIN
    @GetMapping("order/admin/monthly/{warehouseId}/{year}")
    public ResponseEntity<ApiResponse<MonthlyOrderStatusGroupResponse>> getAdminMonthlyStats(
            @PathVariable String warehouseId,
            @PathVariable int year) {
        try {
            MonthlyOrderStatusGroupResponse response = statisticService.getMonthlyOrdersByAdmin(warehouseId, year);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Lấy thống kê theo tháng cho admin thành công", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Đã xảy ra lỗi", null));
        }
    }

}
