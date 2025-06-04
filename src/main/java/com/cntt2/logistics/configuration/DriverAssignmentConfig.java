package com.cntt2.logistics.configuration;

import com.cntt2.logistics.entity.*;
import com.cntt2.logistics.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class DriverAssignmentConfig {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DriverWorkScheduleRepository scheduleRepository;

    /**
     * Thực hiện gán tài xế mỗi 1 giờ.
     * fixedRate = 3600000 ms = 1 giờ
     */
    @Scheduled(fixedRate = 60 * 60 * 1000) // hoặc dùng cron nếu cần chi tiết hơn
    @Transactional
    public void assignDriversEveryHour() {
        System.out.println("🔄 Running driver assignment task...");

        List<Order> createdOrders = orderRepository.findByStatus(OrderStatus.CREATED);
        assignDriverToOrders(createdOrders, true);

        List<Order> arrivedOrders = orderRepository.findByStatus(OrderStatus.AT_DESTINATION);
        assignDriverToOrders(arrivedOrders, false);
    }

    private void assignDriverToOrders(List<Order> orders, boolean isPickup) {
        for (Order order : orders) {
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

            List<DriverWorkSchedule> availableSchedules = scheduleRepository
                    .findByWarehouseIdAndStatusAndWorkDate(
                            warehouseId,
                            ScheduleStatus.APPROVED,
                            LocalDate.now()
                    );

            Optional<DriverWorkSchedule> matchingScheduleOpt = availableSchedules.stream()
                    .filter(schedule -> schedule.getDriver().getVehicleType() == requiredVehicleType)
                    .findFirst();

            if (matchingScheduleOpt.isPresent()) {
                Driver driver = matchingScheduleOpt.get().getDriver();

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
                System.out.println("✖ No suitable " + driverType + " for order " + order.getTrackingCode()
                        + " at warehouse " + warehouseId + " with vehicle: " + requiredVehicleType);
            }
        }
    }
}
