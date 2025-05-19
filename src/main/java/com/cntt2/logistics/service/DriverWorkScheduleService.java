package com.cntt2.logistics.service;

import com.cntt2.logistics.dto.request.DriverWorkScheduleRequest;
import com.cntt2.logistics.dto.request.UpdateDriverWorkScheduleRequest;
import com.cntt2.logistics.dto.response.DriverWorkScheduleResponse;
import com.cntt2.logistics.dto.response.DriverWorkScheduleStatusResponse;
import com.cntt2.logistics.entity.*;
import com.cntt2.logistics.repository.DriverRepository;
import com.cntt2.logistics.repository.DriverWorkScheduleRepository;
import com.cntt2.logistics.repository.UserRepository;
import com.cntt2.logistics.repository.WarehouseLocationsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DriverWorkScheduleService {
    DriverWorkScheduleRepository scheduleRepository;
    DriverRepository driverRepository;
    UserRepository userRepository;
    WarehouseLocationsRepository warehouseLocationsRepository;

    public DriverWorkScheduleResponse registerWorkSchedule(DriverWorkScheduleRequest request) {
        var context = SecurityContextHolder.getContext();
        String userEmail = context.getAuthentication().getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + userEmail));

        Driver driver = driverRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found for user ID: " + user.getId()));

        DriverWorkSchedule schedule = DriverWorkSchedule.builder()
                .driver(driver)
                .warehouse(driver.getWarehouse())
                .workDate(request.getWorkDate())
                .shift(request.getShift())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(ScheduleStatus.PENDING)
                .note(request.getNote())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        DriverWorkSchedule savedSchedule = scheduleRepository.save(schedule);


        return DriverWorkScheduleResponse.builder()
                .id(savedSchedule.getId())
                .nameDriver(savedSchedule.getDriver().getUser().getFullName())
                .workDate(savedSchedule.getWorkDate())
                .startTime(savedSchedule.getStartTime())
                .endTime(savedSchedule.getEndTime())
                .shift(savedSchedule.getShift())
                .status(savedSchedule.getStatus())
                .note(savedSchedule.getNote())
                .createdAt(savedSchedule.getCreatedAt())
                .updatedAt(savedSchedule.getUpdatedAt())
                .build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public DriverWorkScheduleResponse updateScheduleStatus(UpdateDriverWorkScheduleRequest request) {
        DriverWorkSchedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));

        schedule.setStatus(request.getStatus());
        schedule.setUpdatedAt(LocalDateTime.now());

        DriverWorkSchedule updated = scheduleRepository.save(schedule);
        return toResponse(updated);
    }

    public List<DriverWorkScheduleResponse> getSchedulesByCurrentDriver() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Driver driver = driverRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found"));

        return scheduleRepository.findByDriverId(driver.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<DriverWorkScheduleResponse> getAllSchedulesByManager() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User manager = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        if (!manager.getRole().name().equals("WAREHOUSE_MANAGER")) {
            throw new SecurityException("User is not a warehouse manager");
        }

        // Tìm warehouse mà user này đang quản lý
        WarehouseLocations warehouse = warehouseLocationsRepository.findByManager(manager)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found for manager"));

        // Lấy tất cả lịch làm việc của các tài xế thuộc warehouse này
        List<DriverWorkSchedule> schedules = scheduleRepository.findByWarehouseId(warehouse.getId());

        return schedules.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<DriverWorkScheduleStatusResponse> getAllSchedulesByManagerWithApprovedStatus() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User manager = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        if (!manager.getRole().name().equals("WAREHOUSE_MANAGER")) {
            throw new SecurityException("User is not a warehouse manager");
        }

        // Tìm warehouse mà user này đang quản lý
        WarehouseLocations warehouse = warehouseLocationsRepository.findByManager(manager)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found for manager"));

        // Lấy ngày hôm sau
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // Lấy tất cả lịch làm việc của các tài xế thuộc warehouse này có trạng thái APPROVED
        List<DriverWorkSchedule> schedules = scheduleRepository.findByWarehouseIdAndStatusAndWorkDate(warehouse.getId(), ScheduleStatus.APPROVED, tomorrow);

        // Chuyển đổi danh sách DriverWorkSchedule thành DriverWorkScheduleStatusResponse
        return schedules.stream()
                .map(this::toStatusResponse)
                .collect(Collectors.toList());
    }

    private DriverWorkScheduleStatusResponse toStatusResponse(DriverWorkSchedule schedule) {
        return DriverWorkScheduleStatusResponse.builder()
                .driverId(schedule.getDriver().getId())
                .nameDriver(schedule.getDriver().getUser().getFullName())
                .vehicleType(String.valueOf(schedule.getDriver().getVehicleType()))
                .build();
    }

    private DriverWorkScheduleResponse toResponse(DriverWorkSchedule schedule) {
        String driverName = schedule.getDriver().getUser().getFullName();
        return DriverWorkScheduleResponse.builder()
                .id(schedule.getId())
                .nameDriver(driverName)
                .workDate(schedule.getWorkDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .shift(schedule.getShift())
                .status(schedule.getStatus())
                .note(schedule.getNote())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }

}
