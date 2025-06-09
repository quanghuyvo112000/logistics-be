package com.cntt2.logistics.configuration;

import com.cntt2.logistics.repository.DriverWorkScheduleRepository;
import com.cntt2.logistics.repository.InvalidatedTokenRepositoy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

@Component
public class MonthlyCleanupConfig {

    @Autowired
    private DriverWorkScheduleRepository driverWorkScheduleRepository;

    @Autowired
    private InvalidatedTokenRepositoy invalidatedTokenRepository;

    /**
     * Xóa dữ liệu vào 00:00 ngày 30 hàng tháng
     * Cron format: second, minute, hour, day of month, month, day of week
     */
    @Scheduled(cron = "0 0 0 30 * *")
    @Transactional
    public void cleanUpMonthlyData() {
        System.out.println("🧹 Running monthly cleanup for driver_work_schedule and invalidated_tokens...");

        driverWorkScheduleRepository.deleteAll();
        invalidatedTokenRepository.deleteAll();

        System.out.println("✅ Cleanup completed: All records deleted from driver_work_schedule and invalidated_tokens.");
    }
}
