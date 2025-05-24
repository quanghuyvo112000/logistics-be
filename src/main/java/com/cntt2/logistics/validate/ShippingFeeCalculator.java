package com.cntt2.logistics.validate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ShippingFeeCalculator {

    public static double calculateShippingFee(String fromProvince, String toProvince) {
        RegionUtils.Region fromRegion = RegionUtils.getRegionByProvince(fromProvince);
        RegionUtils.Region toRegion = RegionUtils.getRegionByProvince(toProvince);

        if (fromRegion == toRegion) {
            return 30000; // Nội vùng
        } else if ((fromRegion == RegionUtils.Region.NORTH && toRegion == RegionUtils.Region.CENTRAL)
                || (fromRegion == RegionUtils.Region.CENTRAL && toRegion == RegionUtils.Region.NORTH)
                || (fromRegion == RegionUtils.Region.CENTRAL && toRegion == RegionUtils.Region.SOUTH)
                || (fromRegion == RegionUtils.Region.SOUTH && toRegion == RegionUtils.Region.CENTRAL)) {
            return 50000; // Giáp vùng
        } else {
            return 70000; // Bắc - Nam
        }
    }

    public static String estimateDeliveryTime(String fromProvince, String toProvince) {
        RegionUtils.Region fromRegion = RegionUtils.getRegionByProvince(fromProvince);
        RegionUtils.Region toRegion = RegionUtils.getRegionByProvince(toProvince);

        if (fromRegion == toRegion) {
            return "2-3 ngày";
        } else if (isAdjacentRegion(fromRegion, toRegion)) {
            return "3-4 ngày";
        } else {
            return "4-6 ngày";
        }
    }

    public static String computeDateRange(String rawRange) {
        // tách ra 2 số nguyên X và Y
        String[] parts = rawRange.replace(" ngày","").split("-");
        int minDay = Integer.parseInt(parts[0].trim());
        int maxDay = Integer.parseInt(parts[1].trim());

        LocalDate today = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String start = today.plusDays(minDay).format(fmt);
        String end   = today.plusDays(maxDay).format(fmt);
        return start + " - " + end;
    }

    private static boolean isAdjacentRegion(RegionUtils.Region r1, RegionUtils.Region r2) {
        return (r1 == RegionUtils.Region.NORTH && r2 == RegionUtils.Region.CENTRAL)
                || (r1 == RegionUtils.Region.CENTRAL && r2 == RegionUtils.Region.NORTH)
                || (r1 == RegionUtils.Region.CENTRAL && r2 == RegionUtils.Region.SOUTH)
                || (r1 == RegionUtils.Region.SOUTH && r2 == RegionUtils.Region.CENTRAL);
    }
}

