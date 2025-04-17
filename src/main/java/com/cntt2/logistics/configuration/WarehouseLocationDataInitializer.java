package com.cntt2.logistics.configuration;
import com.cntt2.logistics.entity.WarehouseLocations;
import com.cntt2.logistics.repository.WarehouseLocationsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Configuration
public class WarehouseLocationDataInitializer {

    private final WarehouseLocationsRepository warehouseLocationsRepository;

    public WarehouseLocationDataInitializer(WarehouseLocationsRepository warehouseLocationsRepository) {
        this.warehouseLocationsRepository = warehouseLocationsRepository;
    }

    @Bean
    public CommandLineRunner insertWarehouseData() {
        return args -> {
            // Kiểm tra xem bảng warehouse_locations đã có dữ liệu chưa
            if (warehouseLocationsRepository.count() > 0) {
                System.out.println("The warehouse table already contains data, skipping insertion.");
                return; // Nếu đã có dữ liệu thì không chèn thêm
            }

            List<String> provinces = Arrays.asList(
                    "Thành phố Hà Nội", "Thành phố Hồ Chí Minh", "Thành phố Đà Nẵng",
                    "Thành phố Cần Thơ", "Thành phố Hải Phòng", "Tỉnh Lâm Đồng",
                    "Tỉnh Bình Dương", "Tỉnh Đồng Nai", "Tỉnh Quảng Ninh", "Tỉnh Thừa Thiên Huế",
                    "Tỉnh An Giang", "Tỉnh Bà Rịa-Vũng Tàu", "Tỉnh Bình Phước", "Tỉnh Bến Tre", "Tỉnh Quảng Nam"
            );

            Map<String, List<String>> provinceDistrictMap = Map.ofEntries(
                    Map.entry("Thành phố Hà Nội", Arrays.asList("Quận Ba Đình", "Quận Hoàn Kiếm", "Quận Hai Bà Trưng", "Quận Cầu Giấy", "Quận Thanh Xuân")),
                    Map.entry("Thành phố Hồ Chí Minh", Arrays.asList("Quận 1", "Quận 3", "Quận 5", "Thành phố Thủ Đức", "Quận Tân Bình")),
                    Map.entry("Thành phố Đà Nẵng", Arrays.asList("Quận Hải Châu", "Quận Ngũ Hành Sơn", "Quận Sơn Trà", "Quận Liên Chiểu", "Quận Cẩm Lệ")),
                    Map.entry("Thành phố Cần Thơ", Arrays.asList("Quận Ninh Kiều", "Quận Cái Răng", "Quận Bình Thủy", "Quận Ô Môn", "Quận Thốt Nốt")),
                    Map.entry("Thành phố Hải Phòng", Arrays.asList("Quận Hồng Bàng", "Quận Ngô Quyền", "Quận Lê Chân", "Quận Hải An", "Quận Kiến An")),
                    Map.entry("Tỉnh Lâm Đồng", Arrays.asList("Thành phố Đà Lạt", "Huyện Đức Trọng", "Huyện Lạc Dương", "Huyện Lâm Hà", "Huyện Di Linh")),
                    Map.entry("Tỉnh Bình Dương", Arrays.asList("Thành phố Thủ Dầu Một", "Thị xã Dĩ An", "Thị xã Thuận An", "Huyện Bàu Bàng", "Huyện Bắc Tân Uyên")),
                    Map.entry("Tỉnh Đồng Nai", Arrays.asList("Thành phố Biên Hòa", "Thành phố Long Khánh", "Huyện Nhơn Trạch", "Huyện Trảng Bom", "Huyện Vĩnh Cửu")),
                    Map.entry("Tỉnh Quảng Ninh", Arrays.asList("Thành phố Hạ Long", "Thành phố Cẩm Phả", "Thị xã Quảng Yên", "Huyện Đầm Hà", "Huyện Hải Hà")),
                    Map.entry("Tỉnh Thừa Thiên Huế", Arrays.asList("Thành phố Huế", "Huyện Phú Vang", "Huyện Quảng Điền", "Huyện Hương Thủy", "Huyện Hương Trà")),
                    Map.entry("Tỉnh An Giang", Arrays.asList("Thành phố Long Xuyên", "Thành phố Châu Đốc", "Huyện Chợ Mới", "Huyện Tân Châu", "Huyện Phú Tân")),
                    Map.entry("Tỉnh Bà Rịa-Vũng Tàu", Arrays.asList("Thành phố Vũng Tàu", "Thị xã Bà Rịa", "Huyện Long Điền", "Huyện Đất Đỏ", "Huyện Xuyên Mộc")),
                    Map.entry("Tỉnh Bình Phước", Arrays.asList("Thành phố Đồng Xoài", "Huyện Bù Đăng", "Huyện Phước Long", "Huyện Bù Gia Mập", "Huyện Chơn Thành")),
                    Map.entry("Tỉnh Bến Tre", Arrays.asList("Thành phố Bến Tre", "Huyện Châu Thành", "Huyện Giồng Trôm", "Huyện Mỏ Cày Nam", "Huyện Ba Tri")),
                    Map.entry("Tỉnh Quảng Nam", Arrays.asList("Thành phố Tam Kỳ", "Thành phố Hội An", "Huyện Duy Xuyên", "Huyện Đại Lộc", "Huyện Quế Sơn"))
            );

            List<String> wards = Arrays.asList(
                    "Phường 1", "Phường 2", "Phường 3"
            );
            List<String> addresses = Arrays.asList(
                    "123 đường ABC", "456 đường DEF", "789 đường GHI",
                    "321 đường JKL", "654 đường MNO", "987 đường PQR"
            );

            Random random = new Random();

            // Chèn warehouse vào cơ sở dữ liệu khi ứng dụng khởi động
            for (String province : provinces) {
                List<String> districts = provinceDistrictMap.get(province);
                for (String district : districts) {
                    String ward = wards.get(random.nextInt(wards.size()));
                    String address = addresses.get(random.nextInt(addresses.size()));
                    String phone = "090" + (10000000 + random.nextInt(90000000));

                    WarehouseLocations warehouse = WarehouseLocations.builder()
                            .name("Kho hàng " + province + " - " + district)
                            .phone(phone)
                            .province(province)
                            .district(district)
                            .ward(ward)
                            .address(address)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    warehouseLocationsRepository.save(warehouse);
                }
            }

            System.out.println("Warehouses have been inserted into the database.");
        };
    }
}
