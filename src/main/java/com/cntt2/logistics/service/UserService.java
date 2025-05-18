package com.cntt2.logistics.service;

import com.cntt2.logistics.dto.request.DriverRequest;
import com.cntt2.logistics.dto.request.UserRequest;
import com.cntt2.logistics.dto.request.UserUpdateRequest;
import com.cntt2.logistics.dto.request.WarehouseManagerRequest;
import com.cntt2.logistics.dto.response.UserProfileResponse;
import com.cntt2.logistics.dto.response.UserResponse;
import com.cntt2.logistics.entity.Driver;
import com.cntt2.logistics.entity.Role;
import com.cntt2.logistics.entity.User;
import com.cntt2.logistics.entity.WarehouseLocations;
import com.cntt2.logistics.mapper.UserMapper;
import com.cntt2.logistics.repository.DriverRepository;
import com.cntt2.logistics.repository.UserRepository;
import com.cntt2.logistics.repository.WarehouseLocationsRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    WarehouseLocationsRepository warehouseLocationsRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    DriverRepository driverRepository;
    EmailService emailService;

    @Transactional
    public User createCustomer(UserRequest request) {
        return createUserWithRole(request, Role.CUSTOMER, "self-register");
    }

    @PreAuthorize("hasRole('ADMIN')")
    public User createWarehouseManager(WarehouseManagerRequest request) {
        // Tạo user
        User user = userMapper.toEntity(request);
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setRole(Role.WAREHOUSE_MANAGER);
        user.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPasswordSet(true);

        User savedUser = userRepository.saveAndFlush(user);

        // Gán warehouse
        WarehouseLocations warehouse = warehouseLocationsRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        warehouse.setManager(savedUser);
        warehouseLocationsRepository.save(warehouse);

        emailService.sendSimpleMessage(
                request.getEmail(),
                "THÔNG TIN MẬT KHẨU ĐĂNG NHẬP CỦA QUẢN LÝ MỚI",
                "Kính gửi Anh/Chị " + request.getFullName() + ",\n\n" +
                        "Hệ thống gửi đến Anh/Chị thông tin tài khoản để đăng nhập vào hệ thống quản lý:\n\n" +
                        "- Tên đăng nhập: " + request.getEmail() + "\n" +
                        "- Mật khẩu tạm thời: " + request.getPassword() + "\n\n" +
                        "Anh/Chị vui lòng đăng nhập vào hệ thống http://localhost:5173/authentication, và đổi mật khẩu ngay sau lần đăng nhập đầu tiên để đảm bảo bảo mật thông tin.\n\n" +
                        "Nếu có bất kỳ thắc mắc hoặc cần hỗ trợ thêm, vui lòng liên hệ bộ phận IT hoặc phản hồi lại email này.\n\n" +
                        "Trân trọng,\n" +
                        "Hệ thống. \n"
        );

        return savedUser;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE_MANAGER')")
    public void createDriver(DriverRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String vehiclePlate = request.getVehiclePlate();
        if (userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        // Lấy warehouse
        WarehouseLocations warehouse = warehouseLocationsRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        // Tạo user
        User user = User.builder()
                .fullName(request.getFullName())
                .email(email)
                .birthday(request.getBirthday())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .province(request.getProvince())
                .district(request.getDistrict())
                .ward(request.getWard())
                .address(request.getAddress())
                .role(Role.DRIVER)
                .createdBy(SecurityContextHolder.getContext().getAuthentication().getName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .passwordSet(true)
                .build();

        User savedUser = userRepository.save(user);

        if (driverRepository.existsByVehiclePlate(vehiclePlate)) {
            throw new DataIntegrityViolationException("Vehicle Plate already exists");
        }

        // Tạo driver info
        Driver driver = Driver.builder()
                .user(savedUser)
                .vehiclePlate(request.getVehiclePlate())
                .vehicleType(request.getVehicleType())
                .warehouse(warehouse)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();


        driverRepository.save(driver);

        emailService.sendSimpleMessage(
                request.getEmail(),
                "THÔNG TIN MẬT KHẨU ĐĂNG NHẬP CỦA NHÂN VIÊN GIAO HÀNG MỚI",
                "Kính gửi Anh/Chị " + request.getFullName() + ",\n\n" +
                        "Hệ thống gửi đến Anh/Chị thông tin tài khoản để đăng nhập vào hệ thống quản lý:\n\n" +
                        "- Tên đăng nhập: " + request.getEmail() + "\n" +
                        "- Mật khẩu tạm thời: " + request.getPassword() + "\n\n" +
                        "Anh/Chị vui lòng đăng nhập vào hệ thống http://localhost:5173/authentication, và đổi mật khẩu ngay sau lần đăng nhập đầu tiên để đảm bảo bảo mật thông tin.\n\n" +
                        "Nếu có bất kỳ thắc mắc hoặc cần hỗ trợ thêm, vui lòng liên hệ bộ phận IT hoặc phản hồi lại email này.\n\n" +
                        "Trân trọng,\n" +
                        "Hệ thống. \n"
        );
    }


    private User createUserWithRole(UserRequest request, Role role, String createdBy) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DataIntegrityViolationException("Email already exists");
        }

        User user = userMapper.toEntity(request);
        user.setEmail(email);
        user.setRole(role);
        user.setCreatedBy(createdBy);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        return userRepository.saveAndFlush(user);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return userRepository.findByRole(Role.WAREHOUSE_MANAGER).stream()
                .map(user -> new UserResponse(
                        user.getFullName(),
                        user.getEmail(),
                        user.getBirthday(),
                        user.getPhone(),
                        user.getProvince(),
                        user.getDistrict(),
                        user.getWard(),
                        user.getAddress(),
                        user.getRole(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    public UserProfileResponse getMyInfor() {
        var context = SecurityContextHolder.getContext();
        var userEmail = context.getAuthentication().getName();

        // Kiểm tra trong bảng User
        User user = userRepository.findByEmail(userEmail).orElse(null);

            return new UserProfileResponse(
                    user.getFullName(),
                    user.getEmail(),
                    user.getBirthday(),
                    user.getPhone(),
                    user.getProvince(),
                    user.getDistrict(),
                    user.getWard(),
                    user.getAddress(),
                    user.getRole(),
                    user.getCreatedAt(),
                    user.getUpdatedAt()
            );
    }

    public Optional<UserUpdateRequest> updateCurrentUser(UserUpdateRequest request) {
        var context = SecurityContextHolder.getContext();
        var userEmail = context.getAuthentication().getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateUserFromRequest(request, user);
        User savedUser = userRepository.save(user);

        return Optional.of(userMapper.toDTO(savedUser));
    }


    public boolean deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
