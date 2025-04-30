package com.cntt2.logistics.configuration;
import com.cntt2.logistics.entity.Province;
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

            Map<String, List<String>> provinceDistrictMap = Map.ofEntries(
                    Map.entry("Thành phố Hà Nội", Arrays.asList(
                            "Quận Ba Đình", "Quận Hoàn Kiếm", "Quận Hai Bà Trưng", "Quận Cầu Giấy",
                            "Quận Thanh Xuân", "Quận Đống Đa", "Quận Long Biên", "Quận Hà Đông")),

                    Map.entry("Thành phố Hồ Chí Minh", Arrays.asList(
                            "Quận 1", "Quận 3", "Quận 5", "Quận 7",
                            "Quận Tân Bình", "Quận Bình Thạnh", "Thành phố Thủ Đức", "Quận Phú Nhuận")),

                    Map.entry("Thành phố Đà Nẵng", Arrays.asList(
                            "Quận Hải Châu", "Quận Thanh Khê", "Quận Ngũ Hành Sơn", "Quận Sơn Trà",
                            "Quận Liên Chiểu", "Quận Cẩm Lệ", "Huyện Hòa Vang", "Phường Hòa Hải")),

                    Map.entry("Thành phố Cần Thơ", Arrays.asList(
                            "Quận Ninh Kiều", "Quận Cái Răng", "Quận Bình Thủy", "Quận Ô Môn",
                            "Quận Thốt Nốt", "Huyện Phong Điền", "Huyện Vĩnh Thạnh", "Huyện Thới Lai")),

                    Map.entry("Thành phố Hải Phòng", Arrays.asList(
                            "Quận Ngô Quyền", "Quận Lê Chân", "Quận Hồng Bàng", "Quận Hải An",
                            "Quận Kiến An", "Quận Dương Kinh", "Huyện An Dương", "Huyện Thủy Nguyên")),

                    Map.entry("Tỉnh Quảng Ninh", Arrays.asList(
                            "Thành phố Hạ Long", "Thành phố Cẩm Phả", "Thành phố Uông Bí", "Thành phố Móng Cái",
                            "Huyện Đầm Hà", "Huyện Hải Hà", "Thị xã Quảng Yên", "Huyện Tiên Yên")),

                    Map.entry("Tỉnh Thừa Thiên Huế", Arrays.asList(
                            "Thành phố Huế", "Thị xã Hương Thủy", "Thị xã Hương Trà",
                            "Huyện Phú Vang", "Huyện Quảng Điền", "Huyện Phong Điền", "Huyện Nam Đông", "Huyện A Lưới")),

                    Map.entry("Tỉnh Quảng Nam", Arrays.asList(
                            "Thành phố Tam Kỳ", "Thành phố Hội An", "Thị xã Điện Bàn",
                            "Huyện Đại Lộc", "Huyện Duy Xuyên", "Huyện Núi Thành", "Huyện Phú Ninh", "Huyện Thăng Bình")),

                    Map.entry("Tỉnh Bình Định", Arrays.asList(
                            "Thành phố Quy Nhơn", "Thị xã An Nhơn", "Thị xã Hoài Nhơn",
                            "Huyện Tây Sơn", "Huyện Phù Cát", "Huyện Phù Mỹ", "Huyện Tuy Phước", "Huyện Vĩnh Thạnh")),

                    Map.entry("Tỉnh Nghệ An", Arrays.asList(
                            "Thành phố Vinh", "Thị xã Cửa Lò", "Thị xã Thái Hòa",
                            "Huyện Quỳnh Lưu", "Huyện Diễn Châu", "Huyện Yên Thành", "Huyện Nam Đàn", "Huyện Hưng Nguyên")),

                    Map.entry("Tỉnh Đắk Lắk", Arrays.asList(
                            "Thành phố Buôn Ma Thuột", "Huyện Buôn Đôn", "Huyện Cư M’gar",
                            "Huyện Ea Kar", "Huyện Krông Pắc", "Huyện Krông Năng", "Huyện Lắk", "Huyện M’Đrắk")),

                    Map.entry("Tỉnh Lâm Đồng", Arrays.asList(
                            "Thành phố Đà Lạt", "Thành phố Bảo Lộc", "Huyện Đức Trọng",
                            "Huyện Lạc Dương", "Huyện Lâm Hà", "Huyện Di Linh", "Huyện Đạ Huoai", "Huyện Đạ Tẻh")),

                    Map.entry("Tỉnh Bình Dương", Arrays.asList(
                            "Thành phố Thủ Dầu Một", "Thành phố Dĩ An", "Thành phố Thuận An",
                            "Thành phố Tân Uyên", "Thị xã Bến Cát", "Huyện Bàu Bàng", "Huyện Bắc Tân Uyên", "Huyện Phú Giáo")),

                    Map.entry("Tỉnh Đồng Nai", Arrays.asList(
                            "Thành phố Biên Hòa", "Thành phố Long Khánh",
                            "Huyện Nhơn Trạch", "Huyện Trảng Bom", "Huyện Vĩnh Cửu", "Huyện Cẩm Mỹ", "Huyện Thống Nhất", "Huyện Định Quán")),

                    Map.entry("Tỉnh An Giang", Arrays.asList(
                            "Thành phố Long Xuyên", "Thành phố Châu Đốc", "Thị xã Tân Châu",
                            "Huyện Chợ Mới", "Huyện Phú Tân", "Huyện Thoại Sơn", "Huyện Tri Tôn", "Huyện An Phú"))

            );

            // Danh sách 8 phường mẫu
            List<String> wards = Arrays.asList(
                    "Phường 1", "Phường 2", "Phường 3", "Phường 4", "Phường 5", "Phường 6", "Phường 7", "Phường 8"
            );
            List<String> addresses = Arrays.asList(
                    "123 đường ABC", "456 đường DEF", "789 đường GHI",
                    "321 đường JKL", "654 đường MNO", "987 đường PQR"
            );

            Random random = new Random();

            for (Map.Entry<String, List<String>> entry : provinceDistrictMap.entrySet()) {
                String provinceName = entry.getKey();
                List<String> districts = entry.getValue();
                String address = addresses.get(random.nextInt(addresses.size()));
                String phone = "090" + (10000000 + random.nextInt(90000000));

                for (String district : districts) {
                        WarehouseLocations warehouse = WarehouseLocations.builder()
                                .name("Kho hàng " + district + " - " + provinceName)
                                .phone(phone)
                                .province(provinceName)
                                .district(district)
                                .address(address)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                        warehouseLocationsRepository.save(warehouse);
                }
            }

//            // Chèn warehouse vào cơ sở dữ liệu khi ứng dụng khởi động
//            for (String province : provinces) {
//                List<String> districts = provinceDistrictMap.get(province);
//                for (String district : districts) {
//                    String ward = wards.get(random.nextInt(wards.size()));
//                    String address = addresses.get(random.nextInt(addresses.size()));
//                    String phone = "090" + (10000000 + random.nextInt(90000000));
//
//                    WarehouseLocations warehouse = WarehouseLocations.builder()
//                            .name("Kho hàng " + province + " - " + district)
//                            .phone(phone)
//                            .province(province)
//                            .district(district)
//                            .ward(ward)
//                            .address(address)
//                            .createdAt(LocalDateTime.now())
//                            .updatedAt(LocalDateTime.now())
//                            .build();
//
//                    warehouseLocationsRepository.save(warehouse);
//                }
//            }

            System.out.println("Warehouses have been inserted into the database.");
        };
    }
}
