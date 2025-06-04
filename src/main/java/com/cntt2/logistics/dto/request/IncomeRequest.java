package com.cntt2.logistics.dto.request;

import com.cntt2.logistics.entity.IncomeType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IncomeRequest {

    String trackingCode;
}
