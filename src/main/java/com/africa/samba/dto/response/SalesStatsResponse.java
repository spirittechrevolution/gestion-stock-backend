package com.africa.samba.dto.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalesStatsResponse {
    private Long salesCount;
    private BigDecimal totalRevenue;
    private BigDecimal totalMargin;
}
