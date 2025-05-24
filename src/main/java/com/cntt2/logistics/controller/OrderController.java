package com.cntt2.logistics.controller;

import com.cntt2.logistics.dto.request.OrderConfirmPickupRequest;
import com.cntt2.logistics.dto.request.OrderRequest;
import com.cntt2.logistics.dto.request.OrderUpdateStatusRequest;
import com.cntt2.logistics.dto.request.ReceivedAtSourceRequest;
import com.cntt2.logistics.dto.response.ApiResponse;
import com.cntt2.logistics.dto.response.OrderByManagerResponse;
import com.cntt2.logistics.dto.response.OrderResponse;
import com.cntt2.logistics.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {

    OrderService orderService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> createOrder(
//            @RequestParam("pickupImage") MultipartFile pickupImage,
            @RequestParam("sourceWarehouseId") String sourceWarehouseId,
            @RequestParam("destinationWarehouseId") String destinationWarehouseId,
            @RequestParam("senderName") String senderName,
            @RequestParam("senderPhone") String senderPhone,
            @RequestParam("senderAddress") String senderAddress,
            @RequestParam("receiverName") String receiverName,
            @RequestParam("receiverPhone") String receiverPhone,
            @RequestParam("receiverAddress") String receiverAddress,
            @RequestParam("weight") Double weight,
            @RequestParam("orderPrice") Double orderPrice,
            @RequestParam("shippingFee") Double shippingFee,
            @RequestParam("expectedDeliveryTime") String expectedDeliveryTime
    ) {
        try {
            OrderRequest request = OrderRequest.builder()
//                    .pickupImage(pickupImage)
                    .sourceWarehouseId(sourceWarehouseId)
                    .destinationWarehouseId(destinationWarehouseId)
                    .senderName(senderName)
                    .senderPhone(senderPhone)
                    .senderAddress(senderAddress)
                    .receiverName(receiverName)
                    .receiverPhone(receiverPhone)
                    .receiverAddress(receiverAddress)
                    .weight(weight)
                    .orderPrice(orderPrice)
                    .shippingFee(shippingFee)
                    .expectedDeliveryTime(expectedDeliveryTime)
                    .build();

            orderService.createOrder(request);

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Order created successfully", null));
        } catch (MultipartException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid file upload", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create order", null));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        try {
            // Lấy danh sách tất cả các đơn hàng
            List<OrderResponse> orders = orderService.getAllOrders();

            // Trả về danh sách đơn hàng dưới dạng API response
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Orders fetched successfully", orders));
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch orders", null));
        }
    }

    @GetMapping("/customer")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByCustomer() {
        try {
            // Lấy danh sách tất cả các đơn hàng
            List<OrderResponse> orders = orderService.getOrdersByCustomer();

            // Trả về danh sách đơn hàng dưới dạng API response
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Orders fetched successfully", orders));
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch orders", null));
        }
    }

    @GetMapping("/shipper")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByShipper() {
        try {
            // Lấy danh sách tất cả các đơn hàng
            List<OrderResponse> orders = orderService.getOrdersByShipper();

            // Trả về danh sách đơn hàng dưới dạng API response
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Orders fetched successfully", orders));
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch orders", null));
        }
    }


    @GetMapping("/manager")
    public ResponseEntity<ApiResponse<List<OrderByManagerResponse>>> getByManager() {
        try {
            // Lấy danh sách tất cả các đơn hàng
            List<OrderByManagerResponse> orders = orderService.getByManager();

            // Trả về danh sách đơn hàng dưới dạng API response
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Orders fetched successfully", orders));
        } catch (Exception e) {
            // Xử lý lỗi nếu có
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch orders", null));
        }
    }

    @PostMapping("/assign-shipper")
    public ResponseEntity<ApiResponse<Void>> assignOrderToShipper(@RequestBody OrderUpdateStatusRequest request) {
        try {
            orderService.assignOrderToShipper(request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Order assigned to shipper successfully", null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to assign order to shipper", null));
        }
    }

    @PostMapping("/assign-shipper-delivery")
    public ResponseEntity<ApiResponse<Void>> assignOrderToShipperDelivery(@RequestBody OrderUpdateStatusRequest request) {
        try {
            orderService.assignOrderToShipperDelivery(request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Order assigned to shipper successfully", null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to assign order to shipper", null));
        }
    }

    @PostMapping(value = "/confirm-pickup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> confirmOrderPickup(
            @RequestParam("trackingCode") String trackingCode,
            @RequestParam("pickupImage") MultipartFile pickupImage
    ) {
        try {
            OrderConfirmPickupRequest request = OrderConfirmPickupRequest.builder()
                    .trackingCode(trackingCode)
                    .pickupImage(pickupImage)
                    .build();

            orderService.confirmOrderPickup(request);

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Pickup confirmed successfully", null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to confirm pickup", null));
        }
    }

    @PostMapping(value = "/confirm-pickup-delivery", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<Void>> confirmOrderPickupDelivery(
            @RequestParam("trackingCode") String trackingCode,
            @RequestParam("pickupImage") MultipartFile pickupImage
    ) {
        try {
            OrderConfirmPickupRequest request = OrderConfirmPickupRequest.builder()
                    .trackingCode(trackingCode)
                    .pickupImage(pickupImage)
                    .build();

            orderService.confirmOrderPickupDelivery(request);

            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Pickup confirmed successfully", null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to confirm pickup", null));
        }
    }


    @PostMapping("/recrived-source")
    public ResponseEntity<ApiResponse<Void>> recrivedAtSource(@RequestBody ReceivedAtSourceRequest request) {
        try {
            orderService.recrivedAtSource(request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Order recrived source successfully", null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to assign order", null));
        }
    }

    @PostMapping("/delivery-warehouse")
    public ResponseEntity<ApiResponse<Void>> recrivedAtDelivery(@RequestBody ReceivedAtSourceRequest request) {
        try {
            orderService.recrivedAtDelivery(request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Order recrived source successfully", null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to assign order", null));
        }
    }

    @PostMapping("/leaved-source")
    public ResponseEntity<ApiResponse<Void>> leaveAtSource(@RequestBody ReceivedAtSourceRequest request) {
        try {
            orderService.leaveAtSource(request);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.value(), "Order leave source successfully", null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to assign order", null));
        }
    }



}
