package com.pragma.bootcamp.dynamodb;

import com.pragma.bootcamp.dynamodb.helper.TemplateAdapterOperations;
import com.pragma.bootcamp.model.report.Report;
import com.pragma.bootcamp.model.report.gateways.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

@Repository
@Slf4j
public class DynamoDbReportAdapter extends TemplateAdapterOperations<Report, String, ReportEntity> implements ReportRepository {

    public DynamoDbReportAdapter(DynamoDbEnhancedAsyncClient connectionFactory, ObjectMapper mapper) {
        super(connectionFactory, mapper, d -> mapper.map(d, Report.class), "reporte_aprobados");
    }

    @Override
    public Mono<Report> save(Report report) {
        return super.save(report);
    }

    @Override
    public Mono<Report> findById(String id) {
        log.info("Fetching report in repository with ID: {}", id);
        return super.getById(id);
    }
}
