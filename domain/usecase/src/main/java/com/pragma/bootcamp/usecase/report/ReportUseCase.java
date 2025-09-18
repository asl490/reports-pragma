package com.pragma.bootcamp.usecase.report;

import com.pragma.bootcamp.model.report.Report;
import com.pragma.bootcamp.model.report.gateways.ReportRepository;
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

    public Mono<Report> addApprovedLoan(BigDecimal newLoanAmount) {
        return reportRepository.findById(REPORT_ID)
                .defaultIfEmpty(Report.builder()
                        .id(REPORT_ID)
                        .totalLoans(0L)
                        .totalLoanAmount(BigDecimal.ZERO)
                        .build())
                .flatMap(currentReport -> {
                    Report updatedReport = currentReport.toBuilder()
                            .totalLoans(currentReport.getTotalLoans() + 1)
                            .totalLoanAmount(currentReport.getTotalLoanAmount().add(newLoanAmount))
                            .lastUpdateTime(LocalDateTime.now())
                            .build();
                    return reportRepository.save(updatedReport);
                });
    }
}
