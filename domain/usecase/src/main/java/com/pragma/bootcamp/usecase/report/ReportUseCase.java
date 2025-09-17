package com.pragma.bootcamp.usecase.report;

import com.pragma.bootcamp.model.report.Report;
import com.pragma.bootcamp.model.report.gateways.ReportRepository;
import com.pragma.bootcamp.usecase.report.exceptions.InconsistentDataException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class ReportUseCase {

    private static final String REPORT_ID = "TOTAL_APPROVED_LOANS";
    private final ReportRepository reportRepository;

    public Mono<Report> getReport() {
        return reportRepository.findById(REPORT_ID)
                .defaultIfEmpty(Report.builder()
                        .id(REPORT_ID)
                        .totalLoans(0L)
                        .totalLoanAmount(BigDecimal.ZERO)
                        .build());
    }

    public Mono<Report> addApprovedLoan(BigDecimal newLoanAmount, Long expectedNewCount, BigDecimal expectedNewAmount) {
        return reportRepository.findById(REPORT_ID)
                .defaultIfEmpty(Report.builder()
                        .id(REPORT_ID)
                        .totalLoans(0L)
                        .totalLoanAmount(BigDecimal.ZERO)
                        .build())
                .flatMap(currentReport -> {
                    // Validation for count
                    long nextCount = currentReport.getTotalLoans() + 1;
                    if (nextCount != expectedNewCount) {
                        return Mono.error(new InconsistentDataException(
                                "Inconsistency in COUNT detected. Expected " + nextCount +
                                " but received event for " + expectedNewCount));
                    }

                    // Validation for amount
                    BigDecimal newTotalAmount = currentReport.getTotalLoanAmount().add(newLoanAmount);
                    if (newTotalAmount.compareTo(expectedNewAmount) != 0) {
                        return Mono.error(new InconsistentDataException(
                                "Inconsistency in TOTAL AMOUNT detected. Calculated " + newTotalAmount +
                                " but event expected " + expectedNewAmount));
                    }

                    // If both validations pass, update
                    Report updatedReport = currentReport.toBuilder()
                            .totalLoans(nextCount)
                            .totalLoanAmount(newTotalAmount)
                            .lastUpdateTime(LocalDateTime.now())
                            .build();
                    return reportRepository.save(updatedReport);
                });
    }
}
