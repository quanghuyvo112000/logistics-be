package com.cntt2.logistics.repository;

import com.cntt2.logistics.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatisticRepository extends JpaRepository<Statistics, String> {

    // --- Tổng amountShipper theo tháng ---
    @Query("""
        SELECT MONTH(s.createdAt), SUM(s.amountShipper)
        FROM Statistics s
        WHERE YEAR(s.createdAt) = :year AND s.driver.id = :driverId
        GROUP BY MONTH(s.createdAt)
        ORDER BY MONTH(s.createdAt)
    """)
    List<Object[]> getMonthlyShipperStats(String driverId, int year);

    // --- Tổng amountWarehouse theo tháng ---
    @Query("""
        SELECT MONTH(s.createdAt), SUM(s.amountWarehouse)
        FROM Statistics s
        WHERE YEAR(s.createdAt) = :year AND s.warehouse.id = :warehouseId
        GROUP BY MONTH(s.createdAt)
        ORDER BY MONTH(s.createdAt)
    """)
    List<Object[]> getMonthlyWarehouseStats(String warehouseId, int year);

    // --- Tổng amountShipper theo quý ---
    @Query("""
        SELECT QUARTER(s.createdAt), SUM(s.amountShipper)
        FROM Statistics s
        WHERE YEAR(s.createdAt) = :year AND s.driver.id = :driverId
        GROUP BY QUARTER(s.createdAt)
        ORDER BY QUARTER(s.createdAt)
    """)
    List<Object[]> getQuarterlyShipperStats(String driverId, int year);

    // --- Tổng amountWarehouse theo quý ---
    @Query("""
        SELECT QUARTER(s.createdAt), SUM(s.amountWarehouse)
        FROM Statistics s
        WHERE YEAR(s.createdAt) = :year AND s.warehouse.id = :warehouseId
        GROUP BY QUARTER(s.createdAt)
        ORDER BY QUARTER(s.createdAt)
    """)
    List<Object[]> getQuarterlyWarehouseStats(String warehouseId, int year);

    // Tổng doanh thu của từ kho hàng dành cho admin
    @Query("""
        SELECT MONTH(s.createdAt), SUM(s.amountWarehouse)
        FROM Statistics s
        WHERE YEAR(s.createdAt) = :year AND s.warehouse.id = :warehouseId
        GROUP BY MONTH(s.createdAt)
        ORDER BY MONTH(s.createdAt)
    """)
    List<Object[]> getWarehouseRevenueById(String warehouseId, int year);
}

