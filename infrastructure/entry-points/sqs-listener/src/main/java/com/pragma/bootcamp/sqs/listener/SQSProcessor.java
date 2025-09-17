package com.pragma.bootcamp.sqs.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragma.bootcamp.usecase.report.ReportUseCase;
import com.pragma.bootcamp.usecase.report.exceptions.InconsistentDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.math.BigDecimal;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final ReportUseCase reportUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> apply(Message message) {
        log.info("Processing SQS message: {}", message.body());

        try {
            // ✅ Parse the JSON directly into LoanApprovedEvent
            LoanApprovedEvent event = objectMapper.readValue(message.body(), LoanApprovedEvent.class);

            // ✅ Validate required fields
            if (event.getApprovedAmount() == null || event.getNewTotalAmount() == null) {
                log.warn("Missing required fields in message: {}", message.body());
                return Mono.empty();
            }

            BigDecimal approvedAmount = new BigDecimal(event.getApprovedAmount());
            BigDecimal newTotalAmount = new BigDecimal(event.getNewTotalAmount());
            Long newTotalCount = event.getNewTotalCount() != null ? event.getNewTotalCount().longValue() : null;

            // ✅ Call the use case
            return reportUseCase.addApprovedLoan(approvedAmount, newTotalCount, newTotalAmount)
                    .doOnError(InconsistentDataException.class, e -> {
                        log.warn("Inconsistent data detected for SQS message {}: {}", message.messageId(), e.getMessage());
                    })
                    .doOnError(Exception.class, e -> {
                        log.error("Error during use case execution for SQS message {}: {}", message.messageId(), e.getMessage(), e);
                    })
                    .onErrorResume(throwable -> Mono.empty())
                    .then();
        } catch (Exception e) {
            log.error("Critical error parsing SQS message {}: {}", message.messageId(), e.getMessage(), e);
            return Mono.empty();
        }
    }
}
