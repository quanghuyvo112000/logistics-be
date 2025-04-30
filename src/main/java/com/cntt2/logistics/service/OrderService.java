package com.cntt2.logistics.service;

import com.cntt2.logistics.dto.request.OrderRequest;
import com.cntt2.logistics.dto.request.OrderUpdateStatusRequest;
import com.cntt2.logistics.dto.response.OrderByManagerResponse;
import com.cntt2.logistics.dto.response.OrderResponse;
import com.cntt2.logistics.entity.*;
import com.cntt2.logistics.repository.*;
import com.cntt2.logistics.validate.ImageUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.cntt2.logistics.validate.ImageUtils.compressImage;
import static com.cntt2.logistics.validate.ImageUtils.decompressImage;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {

    OrderRepository orderRepository;
    WarehouseLocationsRepository warehouseRepo;
    UserRepository userRepository;
    HistoryOrderRepository historyOrderRepository;
    DriverRepository driverRepository;


    public void createOrder(OrderRequest request) throws IOException {

        var context = SecurityContextHolder.getContext();
        var userEmail = context.getAuthentication().getName();

        User customer = userRepository.findByEmail(userEmail).orElse(null);
        if (customer == null) {
            throw new RuntimeException("Không tìm thấy người dùng đang đăng nhập");
        }

        WarehouseLocations sourceWarehouse = warehouseRepo.findById(request.getSourceWarehouseId())
                .orElseThrow(() -> new RuntimeException("Source warehouse not found"));

        WarehouseLocations destinationWarehouse = warehouseRepo.findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new RuntimeException("Destination warehouse not found"));

        var trackingCode = generateTrackingCode();

        Order order = Order.builder()
                .trackingCode(trackingCode)
                .customer(customer)
                .sourceWarehouse(sourceWarehouse)
                .destinationWarehouse(destinationWarehouse)

                .senderName(customer.getFullName())
                .senderPhone(request.getSenderPhone())
                .senderAddress(request.getSenderAddress())

                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .receiverAddress(request.getReceiverAddress())

                .weight(request.getWeight())
                .orderPrice(request.getOrderPrice())
                .shippingFee(request.getShippingFee())

                .pickupImage(ImageUtils.compressImage(request.getPickupImage().getBytes()))
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        orderRepository.save(order);

        HistoryOrder history = HistoryOrder.builder()
                .order(order)
                .warehouse(sourceWarehouse)
                .status(OrderStatus.CREATED)
                .trackingCode(trackingCode)
                .build();

        historyOrderRepository.save(history);
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream().map(order -> {
            return OrderResponse.builder()
                    .trackingCode(order.getTrackingCode())
                    .customerName(order.getCustomer().getFullName())
                    .sourceWarehouseName(order.getSourceWarehouse().getName())
                    .destinationWarehouseName(order.getDestinationWarehouse().getName())

                    .senderName(order.getSenderName())
                    .senderPhone(order.getSenderPhone())
                    .senderAddress(order.getSenderAddress())

                    .receiverName(order.getReceiverName())
                    .receiverPhone(order.getReceiverPhone())
                    .receiverAddress(order.getReceiverAddress())

                    .weight(order.getWeight())
                    .orderPrice(order.getOrderPrice())
                    .shippingFee(order.getShippingFee())

                    .pickupImage(order.getPickupImage() != null
                            ? Base64.getEncoder().encodeToString(ImageUtils.decompressImage(order.getPickupImage()))
                            : null)

                    .deliveryImage(order.getDeliveryImage() != null
                            ? Base64.getEncoder().encodeToString(ImageUtils.decompressImage(order.getDeliveryImage()))
                            : null)

                    .status(order.getStatus().toString())
                    .createdAt(order.getCreatedAt())
                    .updatedAt(order.getUpdatedAt())

                    .build();
        }).collect(Collectors.toList());
    }

    public List<OrderResponse> getOrdersByCustomer() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        List<Order> orders = orderRepository.findByCustomerId(customer.getId());

        return orders.stream().map(order -> {
            return OrderResponse.builder()
                    .trackingCode(order.getTrackingCode())
                    .customerName(order.getCustomer().getFullName())
                    .sourceWarehouseName(order.getSourceWarehouse().getName())
                    .destinationWarehouseName(order.getDestinationWarehouse().getName())

                    .senderName(order.getSenderName())
                    .senderPhone(order.getSenderPhone())
                    .senderAddress(order.getSenderAddress())

                    .receiverName(order.getReceiverName())
                    .receiverPhone(order.getReceiverPhone())
                    .receiverAddress(order.getReceiverAddress())

                    .weight(order.getWeight())
                    .orderPrice(order.getOrderPrice())
                    .shippingFee(order.getShippingFee())

                    .pickupImage(order.getPickupImage() != null
                            ? Base64.getEncoder().encodeToString(ImageUtils.decompressImage(order.getPickupImage()))
                            : null)

                    .deliveryImage(order.getDeliveryImage() != null
                            ? Base64.getEncoder().encodeToString(ImageUtils.decompressImage(order.getDeliveryImage()))
                            : null)

                    .status(order.getStatus().toString())
                    .createdAt(order.getCreatedAt())
                    .updatedAt(order.getUpdatedAt())
                    .isPickupDriverNull(order.getPickupDriver() == null)

                    .build();
        }).collect(Collectors.toList());

    }

    public List<OrderByManagerResponse> getByManager() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User manager = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        if (!manager.getRole().name().equals("WAREHOUSE_MANAGER")) {
            throw new SecurityException("User is not a warehouse manager");
        }

        WarehouseLocations warehouse = warehouseRepo.findByManager(manager)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found for manager"));

        List<Order> sourceOrders = orderRepository.findBySourceWarehouseId(warehouse.getId());
        List<Order> destinationOrders = orderRepository.findByDestinationWarehouseId(warehouse.getId());

        List<OrderByManagerResponse> responses = sourceOrders.stream()
                .map(order -> buildOrderByManagerResponse(order, warehouse, true))  // true: manager từ source warehouse
                .collect(Collectors.toList());

        responses.addAll(destinationOrders.stream()
                .map(order -> buildOrderByManagerResponse(order, warehouse, false))  // false: manager từ destination warehouse
                .collect(Collectors.toList()));

        return responses;
    }

    private OrderByManagerResponse buildOrderByManagerResponse(Order order, WarehouseLocations managerWarehouse, boolean isManagingSourceWarehouse) {
        boolean isSourceWarehouseFlag = false;

        // Nếu status là CREATED -> manager của kho nguồn là true
        if (order.getStatus() == OrderStatus.CREATED) {
            isSourceWarehouseFlag = isManagingSourceWarehouse;
        }

        // Nếu status là LEFT_SOURCE -> manager của kho đích là true
        else if (order.getStatus() == OrderStatus.LEFT_SOURCE) {
            isSourceWarehouseFlag = !isManagingSourceWarehouse;
        }

        // Các status khác thì isSourceWarehouseFlag = false (hoặc giữ như vậy)

        return OrderByManagerResponse.builder()
                .trackingCode(order.getTrackingCode())
                .customerName(order.getCustomer().getFullName())
                .sourceWarehouseName(order.getSourceWarehouse().getName())
                .destinationWarehouseName(order.getDestinationWarehouse().getName())

                .senderName(order.getSenderName())
                .senderPhone(order.getSenderPhone())
                .senderAddress(order.getSenderAddress())

                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())

                .weight(order.getWeight())
                .orderPrice(order.getOrderPrice())
                .shippingFee(order.getShippingFee())

                .pickupImage(order.getPickupImage() != null
                        ? Base64.getEncoder().encodeToString(decompressImage(order.getPickupImage()))
                        : null)

                .deliveryImage(order.getDeliveryImage() != null
                        ? Base64.getEncoder().encodeToString(decompressImage(order.getDeliveryImage()))
                        : null)

                .status(order.getStatus().toString())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())

                .isSourceWarehouse(isSourceWarehouseFlag)
                .isPickupDriverNull(order.getPickupDriver() == null)

                .build();
    }

    public void assignOrderToShipper(OrderUpdateStatusRequest request) {
        // Tìm đơn hàng theo trackingCode
        Order order = orderRepository.findByTrackingCode(request.getTrackingCode());
        if (order == null) {
            throw new EntityNotFoundException("Order not found with tracking code: " + request.getTrackingCode());
        }

        // Tìm driver theo driverId
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + request.getDriverId()));

        // Cập nhật đơn hàng
        order.setPickupDriver(driver);
        order.setStatus(OrderStatus.ASSIGNED_TO_SHIPPER);
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        // Lưu lịch sử đơn hàng (HistoryOrder)
        HistoryOrder historyOrder = HistoryOrder.builder()
                .order(order)
                .warehouse(order.getSourceWarehouse())
                .status(OrderStatus.ASSIGNED_TO_SHIPPER)
                .trackingCode(order.getTrackingCode())
                .build();

        historyOrderRepository.save(historyOrder);
    }

    private String generateTrackingCode() {
        return "VN" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

}
