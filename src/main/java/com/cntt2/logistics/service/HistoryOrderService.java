package com.cntt2.logistics.service;

import com.cntt2.logistics.dto.response.HistoryOrderResponse;
import com.cntt2.logistics.entity.HistoryOrder;
import com.cntt2.logistics.repository.HistoryOrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HistoryOrderService {
    HistoryOrderRepository historyOrderRepository;

    public HistoryOrderResponse getHistoryByTrackingCode(String trackingCode) {
        List<HistoryOrder> historyOrders = historyOrderRepository.findByTrackingCode(trackingCode);

        if (historyOrders.isEmpty()) {
            throw new NoSuchElementException("Tracking code not found: " + trackingCode);
        }

        // Sort theo timestamp giảm dần
        historyOrders.sort((h1, h2) -> h2.getTimestamp().compareTo(h1.getTimestamp()));

        // Tạo danh sách history đơn giản
        List<HistoryOrderResponse.HistoryItem> items = historyOrders.stream()
                .map(h -> HistoryOrderResponse.HistoryItem.builder()
                        .status(h.getStatus().name())
                        .timestamp(h.getTimestamp())
                        .build())
                .toList();

        return HistoryOrderResponse.builder()
                .trackingCode(trackingCode)
                .histories(items)
                .build();
    }

}
