package com.cntt2.logistics.validate;

import java.util.List;

public class RegionUtils {

    public enum Region {
        NORTH, CENTRAL, SOUTH
    }

    private static final List<String> NORTH = List.of(
            "Thành phố Hà Nội",
            "Thành phố Hải Phòng",
            "Tỉnh Quảng Ninh",
            "Tỉnh Nghệ An" // thường được xếp vào Bắc Trung Bộ, nhưng nhiều hệ thống coi là miền Bắc
    );

    private static final List<String> CENTRAL = List.of(
            "Thành phố Đà Nẵng",
            "Tỉnh Thừa Thiên Huế",
            "Tỉnh Quảng Nam",
            "Tỉnh Bình Định"
    );

    private static final List<String> CENTRAL_HIGHLANDS = List.of(
            "Tỉnh Đắk Lắk",
            "Tỉnh Lâm Đồng"
    );

    private static final List<String> SOUTH = List.of(
            "Thành phố Hồ Chí Minh",
            "Thành phố Cần Thơ",
            "Tỉnh Bình Dương",
            "Tỉnh Đồng Nai",
            "Tỉnh An Giang"
    );

    public static Region getRegionByProvince(String provinceName) {
        if (NORTH.contains(provinceName)) return Region.NORTH;
        if (CENTRAL.contains(provinceName) || CENTRAL_HIGHLANDS.contains(provinceName)) return Region.CENTRAL;
        if (SOUTH.contains(provinceName)) return Region.SOUTH;
        throw new IllegalArgumentException("Province name not recognized: " + provinceName);
    }
}
