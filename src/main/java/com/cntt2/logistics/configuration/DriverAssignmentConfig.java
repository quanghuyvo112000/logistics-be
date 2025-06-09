package com.cntt2.logistics.configuration;

import com.cntt2.logistics.entity.*;
import com.cntt2.logistics.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DriverAssignmentConfig {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DriverWorkScheduleRepository scheduleRepository;

    /**
        Mỗi giờ chạy 1 lần theo thời gian thực
     */
    @Scheduled(cron = "0 0 * * * *") // 1 giờ
    @Transactional
    public void assignDriversEveryHour() {
        System.out.println("🔄 Running driver assignment task...");

        List<Order> createdOrders = orderRepository.findByStatus(OrderStatus.CREATED);
        assignDriverToOrders(createdOrders, true);

        List<Order> arrivedOrders = orderRepository.findByStatus(OrderStatus.AT_DESTINATION);
        assignDriverToOrders(arrivedOrders, false);
    }

    private void assignDriverToOrders(List<Order> orders, boolean isPickup) {
        // Sắp xếp đơn hàng theo ngày tạo (lâu hơn đi trước)
        PriorityQueue<Order> orderQueue = new PriorityQueue<>(Comparator.comparing(Order::getCreatedAt));
        orderQueue.addAll(orders);

        // Lấy danh sách tài xế theo warehouse & trạng thái approved
        Map<String, List<DriverWorkSchedule>> schedulesByWarehouse = new HashMap<>();

        while (!orderQueue.isEmpty()) {
            Order order = orderQueue.poll();

            String warehouseId = isPickup
                    ? order.getSourceWarehouse().getId()
                    : order.getDestinationWarehouse().getId();

            double weight = order.getWeight();
            VehicleType requiredVehicleType;

            if (weight <= 15) {
                requiredVehicleType = VehicleType.MOTORBIKE;
            } else if (weight < 100) {
                requiredVehicleType = VehicleType.VAN;
            } else {
                requiredVehicleType = VehicleType.TRUCK;
            }

            // Lấy danh sách tài xế của warehouse này (cached nếu đã lấy)
            List<DriverWorkSchedule> availableSchedules = schedulesByWarehouse.computeIfAbsent(warehouseId, id ->
                    scheduleRepository.findByWarehouseIdAndStatusAndWorkDate(
                            warehouseId,
                            ScheduleStatus.APPROVED,
                            LocalDate.now()
                    )
            );

            // Lọc tài xế có vehicle phù hợp
            List<Driver> suitableDrivers = availableSchedules.stream()
                    .map(DriverWorkSchedule::getDriver)
                    .filter(driver -> driver.getVehicleType() == requiredVehicleType)
                    .collect(Collectors.toList());

            // Tạo map lưu số đơn hàng đã giao cho mỗi tài xế hôm nay
            Map<String, Long> driverAssignedCount = getDriverAssignedCount(suitableDrivers, isPickup);

            // Tìm tài xế phù hợp chưa đạt giới hạn 50 đơn/ngày
            Optional<Driver> selectedDriverOpt = suitableDrivers.stream()
                    .filter(driver -> driverAssignedCount.getOrDefault(driver.getId(), 0L) < 50)
                    .findFirst();

            if (selectedDriverOpt.isPresent()) {
                Driver driver = selectedDriverOpt.get();

                if (isPickup) {
                    order.setPickupDriver(driver);
                    order.setStatus(OrderStatus.ASSIGNED_TO_SHIPPER);
                    System.out.println("✔ Assigned pickupDriver (" + driver.getId() + ") to order " + order.getTrackingCode());
                } else {
                    order.setDeliveryDriver(driver);
                    order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
                    System.out.println("✔ Assigned deliveryDriver (" + driver.getId() + ") to order " + order.getTrackingCode());
                }

                orderRepository.save(order);
            } else {
                String driverType = isPickup ? "pickupDriver" : "deliveryDriver";
                System.out.println("✖ No suitable " + driverType + " with capacity < 50 for order " + order.getTrackingCode()
                        + " at warehouse " + warehouseId + " with vehicle: " + requiredVehicleType);
            }
        }
    }

    /**
     * Lấy số lượng đơn hàng đã giao cho từng tài xế trong ngày hôm nay, tùy loại driver: pickup hoặc delivery
     * Nếu cần, gọi lại DB để đếm hoặc đếm trong cache nếu dữ liệu có sẵn.
     */
    private Map<String, Long> getDriverAssignedCount(List<Driver> drivers, boolean isPickup) {
        List<String> driverIds = drivers.stream().map(Driver::getId).toList();
        LocalDate today = LocalDate.now();

        // Giả sử orderRepository có method đếm số đơn hàng được giao cho tài xế ngày hôm nay
        // Theo kiểu: countByPickupDriverIdAndCreatedDate or countByDeliveryDriverIdAndCreatedDate

        Map<String, Long> counts = new HashMap<>();

        for (String driverId : driverIds) {
            long count;
            if (isPickup) {
                count = orderRepository.countByPickupDriverIdAndCreatedDate(driverId, today);
            } else {
                count = orderRepository.countByDeliveryDriverIdAndCreatedDate(driverId, today);
            }
            counts.put(driverId, count);
        }

        return counts;
    }
}
