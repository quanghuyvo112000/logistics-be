package com.cntt2.logistics.repository;

import com.cntt2.logistics.entity.User;
import com.cntt2.logistics.entity.WarehouseLocations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseLocationsRepository extends JpaRepository<WarehouseLocations, String> {
    Optional<WarehouseLocations> findByManager(User manager);

    List<WarehouseLocations> findByProvinceAndDistrict(String province, String district);
}
