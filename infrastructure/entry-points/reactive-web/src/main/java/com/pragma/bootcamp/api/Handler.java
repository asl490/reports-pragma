package com.pragma.bootcamp.api;

import com.pragma.bootcamp.usecase.report.ReportUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {
    private final ReportUseCase reportUseCase;

    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ServerResponse> getReport(ServerRequest serverRequest) {
        return reportUseCase.getReport()
                .flatMap(report -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(report));
    }
}
