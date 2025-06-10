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

    @GetMapping("/customer/monthly/created/{year}")
    public ResponseEntity<ApiResponse<List<MonthlyOrderStatusResponse>>> getMonthlyCreatedOrdersByCustomer(@PathVariable int year) {
        try {
            List<MonthlyOrderStatusResponse> stats = statisticService.countCreatedOrdersByMonth(year);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "Lấy thống kê đơn hàng CREATED theo tháng thành công", stats)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi khi lấy thống kê CREATED theo tháng", null));
        }
    }

    @GetMapping("/customer/monthly/delivered/{year}")
    public ResponseEntity<ApiResponse<List<MonthlyOrderStatusResponse>>> getMonthlyDeliveredOrdersByCustomer(@PathVariable int year) {
        try {
            List<MonthlyOrderStatusResponse> stats = statisticService.countDeliveredSuccessfullyOrdersByMonth(year);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "Lấy thống kê đơn hàng DELIVERED theo tháng thành công", stats)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi khi lấy thống kê DELIVERED theo tháng", null));
        }
    }

    @GetMapping("/warehouse/monthly/delivered/{year}")
    public ResponseEntity<ApiResponse<List<MonthlyOrderStatusResponse>>> getMonthlyDeliveredOrdersByWarehouse(@PathVariable int year) {
        try {
            List<MonthlyOrderStatusResponse> stats = statisticService.getMonthlyDeliveredSuccessfullyOrdersByWarehouse(year);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "Lấy thống kê đơn hàng DELIVERED theo tháng của kho thành công", stats)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi khi lấy thống kê DELIVERED của kho", null));
        }
    }

    @GetMapping("/warehouse/monthly/created/{year}")
    public ResponseEntity<ApiResponse<List<MonthlyOrderStatusResponse>>> getMonthlyCreatedOrdersByWarehouse(@PathVariable int year) {
        try {
            List<MonthlyOrderStatusResponse> stats = statisticService.getMonthlyCreatedOrdersByWarehouse(year);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "Lấy thống kê đơn hàng CREATED theo tháng của kho thành công", stats)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi khi lấy thống kê CREATED của kho", null));
        }
    }

    //Admin
    @GetMapping("/admin/monthly/created/{warehouseId}/{year}")
    public ResponseEntity<ApiResponse<List<MonthlyOrderStatusResponse>>> getCreatedOrdersRevenueById(
            @PathVariable String warehouseId,
            @PathVariable int year) {
        try {
            List<MonthlyOrderStatusResponse> response = statisticService.getMonthlyCreatedOrdersByAdmin(warehouseId, year);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), " Lấy thống kê thành công", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Đã xảy ra lỗi", null));
        }
    }

    @GetMapping("/admin/monthly/delivered/{warehouseId}/{year}")
    public ResponseEntity<ApiResponse<List<MonthlyOrderStatusResponse>>> getDeliveredOrdersRevenueById(
            @PathVariable String warehouseId,
            @PathVariable int year) {
        try {
            List<MonthlyOrderStatusResponse> response = statisticService.getMonthlyDeliveredSuccessfullyOrdersByAdmin(warehouseId, year);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), " Lấy thống kê thành công", response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Đã xảy ra lỗi", null));
        }
    }
}
