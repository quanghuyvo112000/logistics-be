package com.cntt2.logistics.service;

import com.cntt2.logistics.dto.request.AssignManagerRequest;
import com.cntt2.logistics.dto.request.SearchWarehouseLocationRequest;
import com.cntt2.logistics.dto.request.WarehouseLocationRequest;
import com.cntt2.logistics.dto.response.SearchWarehouseLocationResponse;
import com.cntt2.logistics.dto.response.WarehouseLocationResponse;
import com.cntt2.logistics.entity.Role;
import com.cntt2.logistics.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.cntt2.logistics.entity.WarehouseLocations;
import com.cntt2.logistics.repository.UserRepository;
import com.cntt2.logistics.repository.WarehouseLocationsRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseLocationService {

    UserRepository userRepository;
    WarehouseLocationsRepository warehouseLocationsRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public void createWarehouse(WarehouseLocationRequest request) {
        WarehouseLocations warehouse = WarehouseLocations.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .province(request.getProvince())
                .district(request.getDistrict())
                .address(request.getAddress())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        warehouseLocationsRepository.save(warehouse);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<WarehouseLocationResponse> getAllWarehouseResponses() {
        return warehouseLocationsRepository.findAll().stream()
                .map(warehouse -> WarehouseLocationResponse.builder()
                        .id(warehouse.getId())
                        .code(warehouse.getCode())
                        .name(warehouse.getName())
                        .phone(warehouse.getPhone())
                        .province(warehouse.getProvince())
                        .district(warehouse.getDistrict())
                        .address(warehouse.getAddress())
                        .createdAt(warehouse.getCreatedAt())
                        .updatedAt(warehouse.getUpdatedAt())
                        .manager(warehouse.getManager() != null ? WarehouseLocationResponse.ManagerResponse.builder()
                                .fullName(warehouse.getManager().getFullName())
                                .email(warehouse.getManager().getEmail())
                                .birthday(warehouse.getManager().getBirthday())
                                .phone(warehouse.getManager().getPhone())
                                .province(warehouse.getManager().getProvince())
                                .district(warehouse.getManager().getDistrict())
                                .ward(warehouse.getManager().getWard())
                                .address(warehouse.getManager().getAddress())
                                .role(warehouse.getManager().getRole())
                                .createdBy(warehouse.getManager().getCreatedBy())
                                .createdAt(warehouse.getManager().getCreatedAt())
                                .updatedAt(warehouse.getManager().getUpdatedAt())
                                .build() : null)
                        .drivers(warehouse.getDrivers() != null ? warehouse.getDrivers().stream()
                                .map(driver -> WarehouseLocationResponse.DriverResponse.builder()
                                        .vehiclePlate(driver.getVehiclePlate())
                                        .vehicleType(driver.getVehicleType())
                                        .fullName(driver.getUser().getFullName())
                                        .email(driver.getUser().getEmail())
                                        .phone(driver.getUser().getPhone())
                                        .birthday(driver.getUser().getBirthday())
                                        .role(driver.getUser().getRole())
                                        .province(driver.getUser().getProvince())
                                        .district(driver.getUser().getDistrict())
                                        .ward(driver.getUser().getWard())
                                        .address(driver.getUser().getAddress())
                                        .createdBy(driver.getUser().getCreatedBy())
                                        .createdAt(driver.getUser().getCreatedAt())
                                        .updatedAt(driver.getUser().getUpdatedAt())
                                        .build())
                                .collect(Collectors.toList()) : null)
                        .build())
                .sorted(Comparator.comparing(WarehouseLocationResponse::getProvince)) // Sắp xếp A-Z theo tên kho
                .collect(Collectors.toList());
    }

    public WarehouseLocationResponse getWarehouseByLoggedInManager() {
        // Lấy thông tin người dùng từ SecurityContextHolder
        var context = SecurityContextHolder.getContext();
        var userEmail = context.getAuthentication().getName();

        // Tìm người quản lý trong cơ sở dữ liệu theo email (username)
        User manager = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        // Kiểm tra xem người dùng có phải là WAREHOUSE_MANAGER không
        if (manager.getRole() != Role.WAREHOUSE_MANAGER) {
            throw new IllegalArgumentException("User is not a WAREHOUSE_MANAGER");
        }

        // Tìm kho của người quản lý
        WarehouseLocations warehouse = warehouseLocationsRepository.findByManager(manager)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found for manager"));

        // Trả về WarehouseLocationResponse
        return WarehouseLocationResponse.builder()
                .id(warehouse.getId())  // Đảm bảo bao gồm ID của kho
                .name(warehouse.getName())
                .phone(warehouse.getPhone())
                .province(warehouse.getProvince())
                .district(warehouse.getDistrict())
                .address(warehouse.getAddress())
                .createdAt(warehouse.getCreatedAt())
                .updatedAt(warehouse.getUpdatedAt())
                .manager(WarehouseLocationResponse.ManagerResponse.builder()
                        .fullName(manager.getFullName())
                        .email(manager.getEmail())
                        .birthday(manager.getBirthday())
                        .phone(manager.getPhone())
                        .province(manager.getProvince())
                        .district(manager.getDistrict())
                        .ward(manager.getWard())
                        .address(manager.getAddress())
                        .role(manager.getRole())
                        .createdBy(manager.getCreatedBy())
                        .createdAt(manager.getCreatedAt())
                        .updatedAt(manager.getUpdatedAt())
                        .build())
                .drivers(warehouse.getDrivers() != null ? warehouse.getDrivers().stream()
                        .map(driver -> WarehouseLocationResponse.DriverResponse.builder()
                                .vehiclePlate(driver.getVehiclePlate())
                                .vehicleType(driver.getVehicleType())
                                .fullName(driver.getUser().getFullName())
                                .email(driver.getUser().getEmail())
                                .phone(driver.getUser().getPhone())
                                .birthday(driver.getUser().getBirthday())
                                .province(driver.getUser().getProvince())
                                .district(driver.getUser().getDistrict())
                                .ward(driver.getUser().getWard())
                                .address(driver.getUser().getAddress())
                                .createdBy(driver.getUser().getCreatedBy())
                                .createdAt(driver.getUser().getCreatedAt())
                                .updatedAt(driver.getUser().getUpdatedAt())
                                .role(driver.getUser().getRole())
                                .build())
                        .collect(Collectors.toList()) : null)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void assignManagerToWarehouse(AssignManagerRequest request) {
        User manager = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        if (manager.getRole() != Role.WAREHOUSE_MANAGER) {
            throw new IllegalArgumentException("User is not a WAREHOUSE_MANAGER");
        }

        WarehouseLocations warehouse = warehouseLocationsRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + request.getWarehouseId()));

        warehouse.setManager(manager);
        warehouse.setUpdatedAt(LocalDateTime.now());

        warehouseLocationsRepository.save(warehouse);
    }

    public List<SearchWarehouseLocationResponse> searchWarehouseByLocation(SearchWarehouseLocationRequest request) {
        List<WarehouseLocations> warehouses = warehouseLocationsRepository
                .findByProvinceAndDistrict(request.getProvince(), request.getDistrict());

        return warehouses.stream()
                .map(warehouse -> new SearchWarehouseLocationResponse(
                        warehouse.getId(),
                        warehouse.getName()))
                .collect(Collectors.toList());
    }
}
