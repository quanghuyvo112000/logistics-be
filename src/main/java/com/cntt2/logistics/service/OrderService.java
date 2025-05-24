package com.cntt2.logistics.service;

import com.cntt2.logistics.dto.request.OrderConfirmPickupRequest;
import com.cntt2.logistics.dto.request.OrderRequest;
import com.cntt2.logistics.dto.request.OrderUpdateStatusRequest;
import com.cntt2.logistics.dto.request.ReceivedAtSourceRequest;
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
import java.util.*;
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

//Tạo đơn hàng mới
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

                .senderName(request.getSenderName())
                .senderPhone(request.getSenderPhone())
                .senderAddress(request.getSenderAddress())

                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .receiverAddress(request.getReceiverAddress())

                .weight(request.getWeight())
                .orderPrice(request.getOrderPrice())
                .shippingFee(request.getShippingFee())
                .expectedDeliveryTime(request.getExpectedDeliveryTime())

//                .pickupImage(ImageUtils.compressImage(request.getPickupImage().getBytes()))
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        orderRepository.save(order);

        HistoryOrder history = HistoryOrder.builder()
                .order(order)
                .expectedDeliveryTime(request.getExpectedDeliveryTime())
                .warehouse(sourceWarehouse)
                .status(OrderStatus.CREATED)
                .trackingCode(trackingCode)
                .build();

        historyOrderRepository.save(history);
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .map(order -> {
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
                    .expectedDeliveryTime(order.getExpectedDeliveryTime())

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

//lấy thông tin đơn hàng theo customer
    public List<OrderResponse> getOrdersByCustomer() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        List<Order> orders = orderRepository.findByCustomerId(customer.getId());

        return orders.stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .map(order -> {
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
                    .expectedDeliveryTime(order.getExpectedDeliveryTime())

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

//    lấy thoong tin đơn hàng theo shipper
    public List<OrderResponse> getOrdersByShipper() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        Driver shipper = driverRepository.findByUserId(customer.getId())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found for user ID: " + customer.getId()));

        List<Order> ordersPickup = orderRepository.findByPickupDriverId(shipper.getId());
        List<Order> ordersDelivery = orderRepository.findByDeliveryDriverId(shipper.getId());

        List<Order> orders = new ArrayList<>();
        orders.addAll(ordersPickup);
        orders.addAll(ordersDelivery);

        return orders.stream()
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .map(order -> {
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
                    .expectedDeliveryTime(order.getExpectedDeliveryTime())

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
                    .isDeliveryDriverNull(order.getDeliveryDriver() == null)
                    .build();
        }).collect(Collectors.toList());

    }

//Lấy thông tin đơn hàng theo manager
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
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .map(order -> buildOrderByManagerResponse(order, warehouse, false))  // false: manager từ destination warehouse
                .collect(Collectors.toList()));

        return responses;
    }

//    Lấy thông tin đơn hàng
    private OrderByManagerResponse buildOrderByManagerResponse(Order order, WarehouseLocations managerWarehouse, boolean isManagingSourceWarehouse) {
        boolean isSourceWarehouseFlag = false;
        String warehouseManagerRole = "";

        // Nếu status là CREATED -> manager của kho nguồn là true
        if (order.getStatus() == OrderStatus.CREATED
                || order.getStatus() == OrderStatus.ASSIGNED_TO_SHIPPER
                || order.getStatus() == OrderStatus.PICKED_UP_SUCCESSFULLY
                || order.getStatus() == OrderStatus.RECEIVED_AT_SOURCE) {
            isSourceWarehouseFlag = isManagingSourceWarehouse;
        }

        // Nếu status là LEFT_SOURCE -> manager của kho đích là true
        else if (order.getStatus() == OrderStatus.LEFT_SOURCE
                || order.getStatus() == OrderStatus.AT_DESTINATION
                || order.getStatus() == OrderStatus.OUT_FOR_DELIVERY) {
            isSourceWarehouseFlag = !isManagingSourceWarehouse;
        }

        if ((order.getStatus() == OrderStatus.CREATED
                || order.getStatus() == OrderStatus.ASSIGNED_TO_SHIPPER
                || order.getStatus() == OrderStatus.PICKED_UP_SUCCESSFULLY
                || order.getStatus() == OrderStatus.RECEIVED_AT_SOURCE
                || order.getStatus() == OrderStatus.AT_DESTINATION
        )) {
            warehouseManagerRole =
                    isSourceWarehouseFlag ? "sourceWarehouseManager" : "destinationWarehouseManager";
        } else {
            warehouseManagerRole =
                    !isSourceWarehouseFlag ? "destinationWarehouseManager" : "sourceWarehouseManager";
        }

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
                .expectedDeliveryTime(order.getExpectedDeliveryTime())

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
                .isDeliveryDriverNull(order.getDeliveryDriver() == null)
                .warehouseManagerRole(warehouseManagerRole)

                .build();
    }
//Cập nhật cho shipper lấy
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
                .expectedDeliveryTime(order.getExpectedDeliveryTime())
                .warehouse(order.getSourceWarehouse())
                .status(OrderStatus.ASSIGNED_TO_SHIPPER)
                .trackingCode(order.getTrackingCode())
                .build();

        historyOrderRepository.save(historyOrder);
    }

    //Cập nhật cho shipper giao
    public void assignOrderToShipperDelivery(OrderUpdateStatusRequest request) {
        // Tìm đơn hàng theo trackingCode
        Order order = orderRepository.findByTrackingCode(request.getTrackingCode());
        if (order == null) {
            throw new EntityNotFoundException("Order not found with tracking code: " + request.getTrackingCode());
        }

        // Kiểm tra trạng thái hiện tại có hợp lệ để xác nhận đã lấy hàng
        if (order.getStatus() != OrderStatus.AT_DESTINATION) {
            throw new IllegalStateException("Order is not ready to be picked up");
        }

        // Tìm driver theo driverId
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + request.getDriverId()));

        // Cập nhật đơn hàng
        order.setDeliveryDriver(driver);
        order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        // Lưu lịch sử đơn hàng (HistoryOrder)
        HistoryOrder historyOrder = HistoryOrder.builder()
                .order(order)
                .expectedDeliveryTime(order.getExpectedDeliveryTime())
                .warehouse(order.getDestinationWarehouse())
                .status(OrderStatus.OUT_FOR_DELIVERY)
                .trackingCode(order.getTrackingCode())
                .build();

        historyOrderRepository.save(historyOrder);
    }
//Shipper lấy hàng
    public void confirmOrderPickup(OrderConfirmPickupRequest request) throws IOException {
        // Tìm đơn hàng theo trackingCode
        Order order = orderRepository.findByTrackingCode(request.getTrackingCode());
        if (order == null) {
            throw new EntityNotFoundException("Order not found with tracking code: " + request.getTrackingCode());
        }

        // Kiểm tra trạng thái hiện tại có hợp lệ để xác nhận đã lấy hàng
        if (order.getStatus() != OrderStatus.ASSIGNED_TO_SHIPPER) {
            throw new IllegalStateException("Order is not ready to be picked up");
        }

        // Cập nhật trạng thái và thời gian
        order.setStatus(OrderStatus.PICKED_UP_SUCCESSFULLY);
        order.setPickupImage(ImageUtils.compressImage(request.getPickupImage().getBytes()));
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        // Lưu lịch sử đơn hàng
        HistoryOrder historyOrder = HistoryOrder.builder()
                .order(order)
                .expectedDeliveryTime(order.getExpectedDeliveryTime())
                .warehouse(order.getSourceWarehouse())
                .status(OrderStatus.PICKED_UP_SUCCESSFULLY)
                .trackingCode(order.getTrackingCode())
                .build();

        historyOrderRepository.save(historyOrder);
    }

//Shipper giao hàng
    public void confirmOrderPickupDelivery(OrderConfirmPickupRequest request) throws IOException {
        // Tìm đơn hàng theo trackingCode
        Order order = orderRepository.findByTrackingCode(request.getTrackingCode());
        if (order == null) {
            throw new EntityNotFoundException("Order not found with tracking code: " + request.getTrackingCode());
        }

        // Kiểm tra trạng thái hiện tại có hợp lệ để xác nhận đã lấy hàng
        if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
            throw new IllegalStateException("Order is not ready to be picked up");
        }

        // Cập nhật trạng thái và thời gian
        order.setStatus(OrderStatus.DELIVERED_SUCCESSFULLY);
        order.setDeliveryImage(ImageUtils.compressImage(request.getPickupImage().getBytes()));
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        // Lưu lịch sử đơn hàng
        HistoryOrder historyOrder = HistoryOrder.builder()
                .order(order)
                .expectedDeliveryTime(order.getExpectedDeliveryTime())
                .warehouse(order.getDestinationWarehouse())
                .status(OrderStatus.DELIVERED_SUCCESSFULLY)
                .trackingCode(order.getTrackingCode())
                .build();

        historyOrderRepository.save(historyOrder);
    }

//    Cập nhật đến kho hàng nhận
    public void recrivedAtSource(ReceivedAtSourceRequest request) throws IOException {
        // Tìm đơn hàng theo trackingCode
        Order order = orderRepository.findByTrackingCode(request.getTrackingCode());
        if (order == null) {
            throw new EntityNotFoundException("Order not found with tracking code: " + request.getTrackingCode());
        }

        // Kiểm tra trạng thái hiện tại có hợp lệ để xác nhận đã lấy hàng
        if (order.getStatus() != OrderStatus.PICKED_UP_SUCCESSFULLY) {
            throw new IllegalStateException("Order is not ready to be picked up");
        }

        // Cập nhật trạng thái và thời gian
        order.setStatus(OrderStatus.RECEIVED_AT_SOURCE);
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        // Lưu lịch sử đơn hàng
        HistoryOrder historyOrder = HistoryOrder.builder()
                .order(order)
                .expectedDeliveryTime(order.getExpectedDeliveryTime())
                .warehouse(order.getSourceWarehouse())
                .status(OrderStatus.RECEIVED_AT_SOURCE)
                .trackingCode(order.getTrackingCode())
                .build();

        historyOrderRepository.save(historyOrder);
    }

    //    Cập nhật đến kho hàng giao
    public void recrivedAtDelivery(ReceivedAtSourceRequest request) throws IOException {
        // Tìm đơn hàng theo trackingCode
        Order order = orderRepository.findByTrackingCode(request.getTrackingCode());
        if (order == null) {
            throw new EntityNotFoundException("Order not found with tracking code: " + request.getTrackingCode());
        }

        // Kiểm tra trạng thái hiện tại có hợp lệ để xác nhận đã lấy hàng
        if (order.getStatus() != OrderStatus.LEFT_SOURCE) {
            throw new IllegalStateException("Order is not ready to be picked up");
        }

        // Cập nhật trạng thái và thời gian
        order.setStatus(OrderStatus.AT_DESTINATION);
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        // Lưu lịch sử đơn hàng
        HistoryOrder historyOrder = HistoryOrder.builder()
                .order(order)
                .expectedDeliveryTime(order.getExpectedDeliveryTime())
                .warehouse(order.getDestinationWarehouse())
                .status(OrderStatus.AT_DESTINATION)
                .trackingCode(order.getTrackingCode())
                .build();

        historyOrderRepository.save(historyOrder);
    }
//    Cập nhật đơn hàng đã rời kho hàng
public void leaveAtSource(ReceivedAtSourceRequest request) throws IOException {
    // Tìm đơn hàng theo trackingCode
    Order order = orderRepository.findByTrackingCode(request.getTrackingCode());
    if (order == null) {
        throw new EntityNotFoundException("Order not found with tracking code: " + request.getTrackingCode());
    }

    // Kiểm tra trạng thái hiện tại có hợp lệ để xác nhận đã lấy hàng
    if (order.getStatus() != OrderStatus.RECEIVED_AT_SOURCE) {
        throw new IllegalStateException("Order is not ready to be picked up");
    }

    // Cập nhật trạng thái và thời gian
    order.setStatus(OrderStatus.LEFT_SOURCE);
    order.setUpdatedAt(LocalDateTime.now());

    orderRepository.save(order);

    // Lưu lịch sử đơn hàng
    HistoryOrder historyOrder = HistoryOrder.builder()
            .order(order)
            .expectedDeliveryTime(order.getExpectedDeliveryTime())
            .warehouse(order.getDestinationWarehouse())
            .status(OrderStatus.LEFT_SOURCE)
            .trackingCode(order.getTrackingCode())
            .build();

    historyOrderRepository.save(historyOrder);
}

    private String generateTrackingCode() {
        return "VN" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

}
