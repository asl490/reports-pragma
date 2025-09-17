package com.pragma.bootcamp.sqs.listener;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoanApprovedEvent {
    private String approvedAmount;
    private Integer newTotalCount;
    private String newTotalAmount;
}