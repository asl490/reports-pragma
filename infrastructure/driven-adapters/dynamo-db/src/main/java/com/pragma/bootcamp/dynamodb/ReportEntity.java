package com.pragma.bootcamp.dynamodb;

import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@DynamoDbBean
public class ReportEntity {

    private String id;
    private Long totalLoans;
    private BigDecimal totalLoanAmount;
    private LocalDateTime lastUpdateTime;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @DynamoDbAttribute("total_loans")
    public Long getTotalLoans() {
        return totalLoans;
    }

    @DynamoDbAttribute("total_loan_amount")
    public BigDecimal getTotalLoanAmount() {
        return totalLoanAmount;
    }

    @DynamoDbAttribute("last_update_time")
    public LocalDateTime getLastUpdateTime() {
        return lastUpdateTime;
    }
}
