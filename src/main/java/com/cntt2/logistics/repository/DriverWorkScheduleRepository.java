package com.cntt2.logistics.repository;

import com.cntt2.logistics.entity.Driver;
import com.cntt2.logistics.entity.DriverWorkSchedule;
import com.cntt2.logistics.entity.User;
import com.cntt2.logistics.entity.WarehouseLocations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverWorkScheduleRepository extends JpaRepository<DriverWorkSchedule, String> {
    List<DriverWorkSchedule> findByDriverId(String driverId);
    List<DriverWorkSchedule> findByWarehouseId(String warehouseId);
}
