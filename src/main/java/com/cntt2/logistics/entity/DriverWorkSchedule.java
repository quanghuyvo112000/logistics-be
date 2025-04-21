package com.cntt2.logistics.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "driver_work_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DriverWorkSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    @JsonIgnoreProperties({"user", "warehouse", "schedules"})
    Driver driver;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    @JsonIgnoreProperties({"drivers", "manager"})
    WarehouseLocations warehouse;

    @Column(nullable = false)
    LocalDate workDate; // Ngày làm việc

    @Column(nullable = false)
    DriverShift shift;

    @Column(nullable = false)
    String startTime; // Giờ bắt đầu

    @Column(nullable = false)
    String endTime; // Giờ kết thúc

    @Column(length = 255)
    String note; // Ghi chú

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ScheduleStatus status = ScheduleStatus.PENDING;

    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
