package com.cntt2.logistics.service;

import com.cntt2.logistics.dto.response.ProvincesResponse;
import com.cntt2.logistics.entity.Province;
import com.cntt2.logistics.repository.ProvinceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProvincesService {

    ProvinceRepository provinceRepository;

    public List<ProvincesResponse> getAllWarehouseAddresses() {
        List<Province> provinces = provinceRepository.findAll();

        // Nhóm các địa chỉ theo tên tỉnh
        Map<String, List<Province>> groupedByProvince = provinces.stream()
                .collect(Collectors.groupingBy(Province::getProvince));

        // Chuyển đổi và sắp xếp theo tên tỉnh A-Z
        return groupedByProvince.entrySet().stream()
                .map(entry -> {
                    String provinceName = entry.getKey();
                    List<Province> provinceList = entry.getValue();

                    List<String> districts = provinceList.stream()
                            .map(Province::getDistrict)
                            .distinct()
                            .sorted()
                            .collect(Collectors.toList());

                    List<String> wards = provinceList.stream()
                            .map(Province::getWard)
                            .distinct()
                            .sorted()
                            .collect(Collectors.toList());

                    return ProvincesResponse.builder()
                            .province(provinceName)
                            .district(districts)
                            .ward(wards)
                            .build();
                })
                .sorted(Comparator.comparing(ProvincesResponse::getProvince))
                .collect(Collectors.toList());
    }

}
