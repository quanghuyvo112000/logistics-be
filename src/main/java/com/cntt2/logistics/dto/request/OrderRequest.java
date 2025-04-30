package com.cntt2.logistics.dto.request;

import com.cntt2.logistics.entity.OrderStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {

    String sourceWarehouseId;
    String destinationWarehouseId;

    String senderPhone;
    String senderAddress;

    String receiverName;
    String receiverPhone;
    String receiverAddress;

    Double weight;
    Double orderPrice;
    Double shippingFee;

    MultipartFile pickupImage;
}
