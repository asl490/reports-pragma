package com.pragma.bootcamp.model.report.gateways;

import com.pragma.bootcamp.model.report.Report;
import reactor.core.publisher.Mono;

public interface ReportRepository {
    Mono<Report> save(Report report);
    Mono<Report> findById(String id);
}
