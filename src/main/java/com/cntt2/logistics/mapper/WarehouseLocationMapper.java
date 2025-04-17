package com.cntt2.logistics.mapper;

import com.cntt2.logistics.dto.request.WarehouseLocationRequest;
import com.cntt2.logistics.dto.request.WarehouseLocationUpdateRequest;
import com.cntt2.logistics.entity.WarehouseLocations;
import org.mapstruct.*;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WarehouseLocationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "manager", ignore = true) // xử lý thủ công trong service
    WarehouseLocations toEntity(WarehouseLocationRequest request);

    WarehouseLocationUpdateRequest toDTO(WarehouseLocations entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "manager", ignore = true) // sẽ gán thủ công
    void updateFromRequest(WarehouseLocationUpdateRequest request, @MappingTarget WarehouseLocations entity);
}
