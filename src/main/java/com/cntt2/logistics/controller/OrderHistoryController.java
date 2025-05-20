package com.cntt2.logistics.controller;

import com.cntt2.logistics.dto.response.ApiResponse;
import com.cntt2.logistics.dto.response.HistoryOrderResponse;
import com.cntt2.logistics.service.HistoryOrderService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderHistoryController {
    HistoryOrderService historyOrderService;

    @GetMapping("/{trackingCode}")
    public ResponseEntity<ApiResponse<HistoryOrderResponse>> getHistoryByTrackingCode(@PathVariable String trackingCode) {
        try {
            HistoryOrderResponse response = historyOrderService.getHistoryByTrackingCode(trackingCode);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK.value(), "History fetched successfully", response)
            );
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch history", null));
        }
    }

}
