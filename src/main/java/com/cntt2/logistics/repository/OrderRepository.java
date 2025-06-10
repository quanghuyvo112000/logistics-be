package com.cntt2.logistics.repository;

import com.cntt2.logistics.entity.Order;
import com.cntt2.logistics.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findBySourceWarehouseId(String sourceWarehouse);
    List<Order> findByDestinationWarehouseId(String destinationWarehouse);
    List<Order> findByCustomerId(String customerId);
    List<Order> findByPickupDriverId(String pickupDriverId);
    List<Order> findByDeliveryDriverId(String deliveryDriverId);

    Order findByTrackingCode(String trackingCode);

    List<Order> findByStatus(OrderStatus status);

    // Đếm số đơn hàng đã giao cho pickupDriver trong ngày cụ thể
    @Query("SELECT COUNT(o) FROM Order o WHERE o.pickupDriver.id = :driverId AND FUNCTION('DATE', o.createdAt) = :date")
    long countByPickupDriverIdAndCreatedDate(@Param("driverId") String driverId, @Param("date") LocalDate date);

    // Đếm số đơn hàng đã giao cho deliveryDriver trong ngày cụ thể
    @Query("SELECT COUNT(o) FROM Order o WHERE o.deliveryDriver.id = :driverId AND FUNCTION('DATE', o.createdAt) = :date")
    long countByDeliveryDriverIdAndCreatedDate(@Param("driverId") String driverId, @Param("date") LocalDate date);

    //Thống kê theo tháng của customer
    @Query("""
    SELECT MONTH(o.createdAt) AS month, SUM(o.orderPrice) AS totalOrderPrice
    FROM Order o
    WHERE YEAR(o.createdAt) = :year AND o.customer.id = :customerId
    GROUP BY MONTH(o.createdAt)
    ORDER BY MONTH(o.createdAt)
""")
    List<Object[]> getMonthlyOrderStatsByCustomer(@Param("customerId") String customerId, @Param("year") int year);

    //Thống kê theo quý của customer
    @Query("""
    SELECT QUARTER(o.createdAt) AS quarter, SUM(o.orderPrice) AS totalOrderPrice
    FROM Order o
    WHERE YEAR(o.createdAt) = :year AND o.customer.id = :customerId
    GROUP BY QUARTER(o.createdAt)
    ORDER BY QUARTER(o.createdAt)
""")
    List<Object[]> getQuarterlyOrderStatsByCustomer(@Param("customerId") String customerId, @Param("year") int year);

    //1. Query: Đếm số đơn hàng có status = CREATED theo tháng
    @Query(value = """
                SELECT MONTH(o.createdAt), COUNT(o.id)
                FROM Order o
                WHERE YEAR(o.createdAt) = :year
                  AND o.customer.id = :customerId
                  AND o.status = 'CREATED'
                GROUP BY MONTH(o.createdAt)
                ORDER BY MONTH(o.createdAt)
            """)
    List<Object[]> countCreatedOrdersByMonth(
            @Param("customerId") String customerId,
            @Param("year") int year
    );

    //Query: Đếm số đơn hàng có status = DELIVERED_SUCCESSFULLY theo tháng
    @Query(value = """
                SELECT MONTH(o.createdAt), COUNT(o.id)
                FROM Order o
                WHERE YEAR(o.createdAt) = :year
                  AND o.customer.id = :customerId
                  AND o.status = 'DELIVERED_SUCCESSFULLY'
                GROUP BY MONTH(o.createdAt)
                ORDER BY MONTH(o.createdAt)
            """)
    List<Object[]> countDeliveredSuccessfullyOrdersByMonth(
            @Param("customerId") String customerId,
            @Param("year") int year
    );


    //Thống kê theo source warehouse
    @Query(value = """
                SELECT MONTH(o.createdAt), COUNT(o.id)
                FROM Order o
                WHERE YEAR(o.createdAt) = :year
                  AND (
                      o.sourceWarehouse.id = :warehouseId OR o.destinationWarehouse.id = :warehouseId
                  )
                  AND o.status = 'DELIVERED_SUCCESSFULLY'
                GROUP BY MONTH(o.createdAt)
                ORDER BY MONTH(o.createdAt)
            """)
    List<Object[]> countOrdersBySourceWarehouseMonthly(
            @Param("warehouseId") String warehouseId,
            @Param("year") int year
    );


    //Thống kê theo destination warehouse
    @Query(value = """
                SELECT MONTH(o.createdAt), COUNT(o.id)
                FROM Order o
                WHERE YEAR(o.createdAt) = :year
                  AND (
                      o.sourceWarehouse.id = :warehouseId OR o.destinationWarehouse.id = :warehouseId
                  )
                  AND o.status = 'CREATED'
                GROUP BY MONTH(o.createdAt)
                ORDER BY MONTH(o.createdAt)
            """)
    List<Object[]> countOrdersByDestinationWarehouseMonthly(
            @Param("warehouseId") String warehouseId,
            @Param("year") int year
    );


    //ADMIN
    @Query(value = """
                SELECT MONTH(o.createdAt), COUNT(o.id)
                FROM Order o
                WHERE YEAR(o.createdAt) = :year
                  AND (
                      o.sourceWarehouse.id = :warehouseId OR o.destinationWarehouse.id = :warehouseId
                  )
                  AND o.status = 'DELIVERED_SUCCESSFULLY'
                GROUP BY MONTH(o.createdAt)
                ORDER BY MONTH(o.createdAt)
            """)
    List<Object[]> countOrdersBySourceAdminMonthly(
            @Param("warehouseId") String warehouseId,
            @Param("year") int year
    );


    //Thống kê theo destination warehouse
    @Query(value = """
                SELECT MONTH(o.createdAt), COUNT(o.id)
                FROM Order o
                WHERE YEAR(o.createdAt) = :year
                  AND (
                      o.sourceWarehouse.id = :warehouseId OR o.destinationWarehouse.id = :warehouseId
                  )
                  AND o.status = 'CREATED'
                GROUP BY MONTH(o.createdAt)
                ORDER BY MONTH(o.createdAt)
            """)
    List<Object[]> countOrdersByDestinationAdminMonthly(
            @Param("warehouseId") String warehouseId,
            @Param("year") int year
    );

}
