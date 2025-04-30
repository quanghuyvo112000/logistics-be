package com.cntt2.logistics.controller;

import com.cntt2.logistics.dto.request.*;
import com.cntt2.logistics.dto.response.DriverWorkScheduleResponse;
import com.cntt2.logistics.dto.response.DriverWorkScheduleStatusResponse;
import com.cntt2.logistics.service.DriverWorkScheduleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cntt2.logistics.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/work-schedules")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DriverWorkScheduleController {
    DriverWorkScheduleService driverWorkScheduleService;

    @PostMapping
    public ResponseEntity<ApiResponse<DriverWorkScheduleResponse>> registerWorkSchedule(@RequestBody DriverWorkScheduleRequest request) {
        try {

            DriverWorkScheduleResponse response = driverWorkScheduleService.registerWorkSchedule(request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Work schedule registered successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to register work schedule", null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<DriverWorkScheduleResponse>>> getMySchedules() {
        try {
            List<DriverWorkScheduleResponse> responses = driverWorkScheduleService.getSchedulesByCurrentDriver();
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Driver schedules fetched", responses));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error", null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DriverWorkScheduleResponse>>> getAllSchedulesByManager() {
        try {
            List<DriverWorkScheduleResponse> responses = driverWorkScheduleService.getAllSchedulesByManager();
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "All driver schedules for warehouse", responses));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error", null));
        }
    }

    @GetMapping("/manager-status")
    public ResponseEntity<ApiResponse<List<DriverWorkScheduleStatusResponse>>> getAllSchedulesByManagerWithApprovedStatus() {
        try {
            List<DriverWorkScheduleStatusResponse> responses = driverWorkScheduleService.getAllSchedulesByManagerWithApprovedStatus();
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "All driver schedules for warehouse", responses));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(HttpStatus.FORBIDDEN.value(), e.getMessage(), null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error", null));
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse<DriverWorkScheduleResponse>> updateScheduleStatus(
            @RequestBody UpdateDriverWorkScheduleRequest request) {
        try {
            DriverWorkScheduleResponse response = driverWorkScheduleService.updateScheduleStatus(request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Schedule status updated", response));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Error updating schedule", null));
        }
    }

}
