package com.cntt2.logistics.repository;

import com.cntt2.logistics.entity.HistoryOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryOrderRepository extends JpaRepository<HistoryOrder, String> {
}
