package com.cntt2.logistics.mapper;

import com.cntt2.logistics.dto.request.UserRequest;
import com.cntt2.logistics.dto.request.UserUpdateRequest;
import com.cntt2.logistics.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true) // ID là UUID tự sinh
    @Mapping(target = "role", constant = "CUSTOMER") // Mặc định là CUSTOMER
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    User toEntity(UserRequest request);

    // Chỉ map các field có trong UserRequest (xóa id)
    UserUpdateRequest toDTO(User user);

    // Cập nhật user từ request mà không thay đổi ID, createdAt
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromRequest(UserUpdateRequest request, @MappingTarget User user);
}
