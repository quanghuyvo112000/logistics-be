package com.cntt2.logistics.repository;

import com.cntt2.logistics.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverWorkScheduleRepository extends JpaRepository<DriverWorkSchedule, String> {
    List<DriverWorkSchedule> findByDriverId(String driverId);
    List<DriverWorkSchedule> findByWarehouseId(String warehouseId);
    List<DriverWorkSchedule> findByWarehouseIdAndStatusAndWorkDate(String warehouseId, ScheduleStatus status, LocalDate workDate);
}
