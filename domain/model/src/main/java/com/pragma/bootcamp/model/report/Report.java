package com.pragma.bootcamp.model.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Report {
    private String id;
    private Long totalLoans;
    private BigDecimal totalLoanAmount;
    private LocalDateTime lastUpdateTime;
}
