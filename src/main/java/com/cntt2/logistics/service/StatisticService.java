package com.cntt2.logistics.service;

import com.cntt2.logistics.dto.request.IncomeRequest;
import com.cntt2.logistics.dto.response.IncomeResponse;
import com.cntt2.logistics.dto.response.TimeAmountResponse;
import com.cntt2.logistics.dto.response.WarehouseAmountAdminResponse;
import com.cntt2.logistics.entity.*;
import com.cntt2.logistics.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StatisticService {

    StatisticRepository statisticRepository;
    OrderRepository orderRepository;
    UserRepository userRepository;
    DriverRepository driverRepository;
    WarehouseLocationsRepository warehouseLocationsRepository;

    public IncomeResponse createIncomeFromTrackingCode(IncomeRequest request) {
        var context = SecurityContextHolder.getContext();
        var userEmail = context.getAuthentication().getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        Order order = orderRepository.findByTrackingCode(request.getTrackingCode());
        if (order == null) {
            throw new RuntimeException("Order not found with tracking code: " + request.getTrackingCode());
        }

        Role role = user.getRole();

        double shippingFee = order.getShippingFee();
        double warehouseShare = shippingFee * 0.7;
        double driverBonus = shippingFee * 0.3;
        double baseShipperFee = 10000.0;
        double amountShipper = baseShipperFee + driverBonus;

        // Nếu là trạng thái CREATED, xử lý cho warehouse manager
        if (order.getStatus() == OrderStatus.CREATED) {
            Statistics income = new Statistics();
            income.setOrder(order);
            income.setCreatedAt(LocalDateTime.now());
            income.setWarehouse(order.getSourceWarehouse());
            income.setAmountWarehouse(warehouseShare);
            income.setDriver(order.getPickupDriver());
            income.setAmountShipper(0.0);
            income.setType(IncomeType.COMMISSION);

            Statistics saved = statisticRepository.save(income);
            return IncomeResponse.builder()
                    .amountShipper(saved.getAmountShipper())
                    .amountWarehouse(saved.getAmountWarehouse())
                    .type(saved.getType())
                    .createdAt(saved.getCreatedAt())
                    .build();
        }

        // Nếu là DRIVER
        if (role == Role.DRIVER) {
            Driver driver = driverRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Driver not found for user ID: " + user.getId()));

            // Giao hàng thành công
            if (order.getStatus() == OrderStatus.OUT_FOR_DELIVERY &&
                    order.getDeliveryDriver() != null &&
                    order.getDeliveryDriver().getId().equals(driver.getId())) {

                Statistics shipperIncome = new Statistics();
                shipperIncome.setDriver(driver);
                shipperIncome.setOrder(order);
                shipperIncome.setCreatedAt(LocalDateTime.now());
                shipperIncome.setWarehouse(order.getDestinationWarehouse());
                shipperIncome.setAmountShipper(amountShipper);
                shipperIncome.setAmountWarehouse(0.0);
                shipperIncome.setType(IncomeType.SHIPPING_FEE);
                statisticRepository.save(shipperIncome);

                if (order.getPaymentStatus() == PaymentStatus.NOTPAID) {
                    Statistics warehouseIncome = new Statistics();
                    warehouseIncome.setWarehouse(order.getDestinationWarehouse());
                    warehouseIncome.setOrder(order);
                    warehouseIncome.setDriver(order.getDeliveryDriver());
                    warehouseIncome.setCreatedAt(LocalDateTime.now());
                    warehouseIncome.setAmountWarehouse(warehouseShare);
                    warehouseIncome.setAmountShipper(0.0);
                    warehouseIncome.setType(IncomeType.COMMISSION);
                    statisticRepository.save(warehouseIncome);

                    // Trả về warehouse income nếu cần
                    return IncomeResponse.builder()
                            .amountWarehouse(warehouseIncome.getAmountWarehouse())
                            .type(warehouseIncome.getType())
                            .createdAt(warehouseIncome.getCreatedAt())
                            .build();
                }

                // Trả về shipper income
                return IncomeResponse.builder()
                        .amountShipper(shipperIncome.getAmountShipper())
                        .type(shipperIncome.getType())
                        .createdAt(shipperIncome.getCreatedAt())
                        .build();

                // Nhận hàng thành công
            } else if (order.getStatus() == OrderStatus.PICKED_UP_SUCCESSFULLY &&
                    order.getPickupDriver() != null &&
                    order.getPickupDriver().getId().equals(driver.getId())) {

                Statistics income = new Statistics();
                income.setDriver(driver);
                income.setOrder(order);
                income.setCreatedAt(LocalDateTime.now());
                income.setWarehouse(order.getSourceWarehouse());
                income.setAmountShipper(amountShipper);
                income.setAmountWarehouse(0.0);
                income.setType(IncomeType.SHIPPING_FEE);

                Statistics saved = statisticRepository.save(income);
                return IncomeResponse.builder()
                        .amountShipper(saved.getAmountShipper())
                        .type(saved.getType())
                        .createdAt(saved.getCreatedAt())
                        .build();
            }

            throw new RuntimeException("You are not assigned to this order or status is not eligible.");
        }

        throw new RuntimeException("Only warehouse managers and drivers can create income records.");
    }

    //Thống kê doanh thu theo tháng
    @PreAuthorize("hasAnyRole('DRIVER', 'WAREHOUSE_MANAGER')")
    public List<TimeAmountResponse> getMonthlyStats(int year) {
        var context = SecurityContextHolder.getContext();
        var userEmail = context.getAuthentication().getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        Role role = user.getRole();

        if (role == Role.DRIVER) {
            Driver driver = driverRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Driver not found with userId: " + user.getId()));

            List<Object[]> raw = statisticRepository.getMonthlyShipperStats(driver.getId(), year);
            return raw.stream()
                    .map(obj -> {
                        Integer month = (Integer) obj[0];
                        Double amount = (Double) obj[1];
                        return new TimeAmountResponse("Tháng " + month + "/" + year, amount);
                    }).toList();

        } else if (role == Role.WAREHOUSE_MANAGER) {
            WarehouseLocations warehouse = warehouseLocationsRepository.findByManager(user)
                    .orElseThrow(() -> new RuntimeException("Warehouse not found for manager: " + user.getEmail()));

            List<Object[]> raw = statisticRepository.getMonthlyWarehouseStats(warehouse.getId(), year);
            return raw.stream()
                    .map(obj -> {
                        Integer month = (Integer) obj[0];
                        Double amount = (Double) obj[1];
                        return new TimeAmountResponse("Tháng " + month + "/" + year, amount);
                    }).toList();
        } else {
            throw new RuntimeException("User role not supported for stats");
        }
    }

    //Thống kê doanh thu theo quý
    @PreAuthorize("hasAnyRole('DRIVER', 'WAREHOUSE_MANAGER')")
    public List<TimeAmountResponse> getQuarterlyStats(int year) {
        var context = SecurityContextHolder.getContext();
        var userEmail = context.getAuthentication().getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        Role role = user.getRole();

        if (role == Role.DRIVER) {
            Driver driver = driverRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Driver not found with userId: " + user.getId()));

            List<Object[]> raw = statisticRepository.getQuarterlyShipperStats(driver.getId(), year);
            return raw.stream()
                    .map(obj -> {
                        Integer quarter = (Integer) obj[0];
                        Double amount = (Double) obj[1];
                        return new TimeAmountResponse("Quý " + quarter + "/" + year, amount);
                    }).toList();

        } else if (role == Role.WAREHOUSE_MANAGER) {
            WarehouseLocations warehouse = warehouseLocationsRepository.findByManager(user)
                    .orElseThrow(() -> new RuntimeException("Warehouse not found for manager: " + user.getEmail()));

            List<Object[]> raw = statisticRepository.getQuarterlyWarehouseStats(warehouse.getId(), year);
            return raw.stream()
                    .map(obj -> {
                        Integer quarter = (Integer) obj[0];
                        Double amount = (Double) obj[1];
                        return new TimeAmountResponse("Quý " + quarter + "/" + year, amount);
                    }).toList();
        } else {
            throw new RuntimeException("User role not supported for stats");
        }
    }

    //Lấy tổng doanh thu từng kho hàng cho admin
    public List<TimeAmountResponse> getMonthlyStatsByWarehouseId(String warehouseId, int year) {
        var context = SecurityContextHolder.getContext();
        var userEmail = context.getAuthentication().getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        Role role = user.getRole();

        if (role == Role.ADMIN) {
            List<Object[]> raw = statisticRepository.getWarehouseRevenueById(warehouseId, year);

            return raw.stream()
                    .map(obj -> {
                        Integer month = (Integer) obj[0];
                        Double amount = obj[1] != null ? ((Number) obj[1]).doubleValue() : 0.0;
                        return new TimeAmountResponse("Tháng " + month + "/" + year, amount);
                    })
                    .toList();
        } else {
            throw new RuntimeException("Only ADMIN has the right to view other warehouse's revenue statistics.");
        }
    }

}
