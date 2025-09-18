//package com.pragma.bootcamp.api;
//
//import com.pragma.bootcamp.model.report.Report;
//import com.pragma.bootcamp.usecase.report.ReportUseCase;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import reactor.core.publisher.Mono;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//@ContextConfiguration(classes = {RouterRest.class, Handler.class, RouterRestTest.TestConfig.class})
//@WebFluxTest
//class RouterRestTest {
//
//    @Configuration
//    static class TestConfig {
//        @Bean
//        public ReportUseCase reportUseCase() {
//            return mock(ReportUseCase.class);
//        }
//    }
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    @Autowired
//    private ReportUseCase reportUseCase;
//
//    @Test
//    void testGetReportEndpoint() {
//        // Mock the use case response
//        Report mockReport = Report.builder()
//                .id("TOTAL_APPROVED_LOANS")
//                .totalLoans(100L)
//                .totalLoanAmount(new BigDecimal("500000.00"))
//                .lastUpdateTime(LocalDateTime.now())
//                .build();
//
//        when(reportUseCase.getReport()).thenReturn(Mono.just(mockReport));
//
//        webTestClient.get()
//                .uri("/api/v1/reportes")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBody(Report.class)
//                .isEqualTo(mockReport);
//    }
//
//    @Test
//    void testGetReportEndpointNotFound() {
//        // Mock the use case to return an empty Mono (report not found scenario)
//        when(reportUseCase.getReport()).thenReturn(Mono.empty());
//
//        webTestClient.get()
//                .uri("/api/v1/reportes")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk() // defaultIfEmpty in use case returns a default report
//                .expectHeader().contentType(MediaType.APPLICATION_JSON)
//                .expectBody(Report.class)
//                .isEqualTo(Report.builder()
//                        .id("TOTAL_APPROVED_LOANS")
//                        .totalLoans(0L)
//                        .totalLoanAmount(BigDecimal.ZERO)
//
//                        .build());
//    }
//
//    @Test
//    void testGetReportEndpointError() {
//        when(reportUseCase.getReport()).thenReturn(Mono.error(new RuntimeException("Error getting report")));
//
//        webTestClient.get()
//                .uri("/api/v1/reportes")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange();
//    }
//}
