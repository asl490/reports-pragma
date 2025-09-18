package com.pragma.bootcamp.usecase.report;

import com.pragma.bootcamp.model.report.Report;
import com.pragma.bootcamp.model.report.gateways.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReportUseCaseTest {

    private ReportRepository reportRepository;
    private ReportUseCase reportUseCase;

    private static final String REPORT_ID = "TOTAL_APPROVED_LOANS";

    @BeforeEach
    void setUp() {
        reportRepository = mock(ReportRepository.class);
        reportUseCase = new ReportUseCase(reportRepository);
    }

    @Test
    void getReport_existingReport_shouldReturnReport() {
        Report existingReport = Report.builder()
                .id(REPORT_ID)
                .totalLoans(10L)
                .totalLoanAmount(BigDecimal.valueOf(5000))
                .build();

        when(reportRepository.findById(REPORT_ID)).thenReturn(Mono.just(existingReport));

        StepVerifier.create(reportUseCase.getReport())
                .expectNext(existingReport)
                .verifyComplete();

        verify(reportRepository).findById(REPORT_ID);
    }

    @Test
    void getReport_noExistingReport_shouldReturnDefaultReport() {
        when(reportRepository.findById(REPORT_ID)).thenReturn(Mono.empty());

        StepVerifier.create(reportUseCase.getReport())
                .expectNextMatches(report ->
                        report.getId().equals(REPORT_ID)
                        && report.getTotalLoans() == 0
                        && report.getTotalLoanAmount().compareTo(BigDecimal.ZERO) == 0
                )
                .verifyComplete();
    }

    @Test
    void addApprovedLoan_existingReport_shouldUpdateAndSaveReport() {
        Report existingReport = Report.builder()
                .id(REPORT_ID)
                .totalLoans(3L)
                .totalLoanAmount(BigDecimal.valueOf(3000))
                .build();

        BigDecimal newLoanAmount = BigDecimal.valueOf(1000);

        when(reportRepository.findById(REPORT_ID)).thenReturn(Mono.just(existingReport));
        when(reportRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(reportUseCase.addApprovedLoan(newLoanAmount))
                .expectNextMatches(updatedReport ->
                        updatedReport.getTotalLoans() == 4 &&
                        updatedReport.getTotalLoanAmount().compareTo(BigDecimal.valueOf(4000)) == 0 &&
                        updatedReport.getLastUpdateTime() != null
                )
                .verifyComplete();

        verify(reportRepository).findById(REPORT_ID);
        verify(reportRepository).save(any());
    }

    @Test
    void addApprovedLoan_noExistingReport_shouldCreateAndSaveNewReport() {
        when(reportRepository.findById(REPORT_ID)).thenReturn(Mono.empty());

        BigDecimal newLoanAmount = BigDecimal.valueOf(500);

        when(reportRepository.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(reportUseCase.addApprovedLoan(newLoanAmount))
                .expectNextMatches(report ->
                        report.getTotalLoans() == 1 &&
                        report.getTotalLoanAmount().compareTo(BigDecimal.valueOf(500)) == 0 &&
                        report.getLastUpdateTime() != null
                )
                .verifyComplete();

        verify(reportRepository).findById(REPORT_ID);
        verify(reportRepository).save(any());
    }
}
