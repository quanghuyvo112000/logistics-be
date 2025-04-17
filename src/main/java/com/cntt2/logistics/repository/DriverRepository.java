package com.cntt2.logistics.repository;

import com.cntt2.logistics.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {
    Driver findByUserId(String userId);
    boolean existsByVehiclePlate(String vehiclePlate);

}
