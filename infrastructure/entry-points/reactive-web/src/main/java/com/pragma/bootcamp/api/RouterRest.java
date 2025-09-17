package com.pragma.bootcamp.api;

import com.pragma.bootcamp.model.report.Report;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperation(
            path = "/api/v1/reportes",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            method = RequestMethod.GET,
            beanClass = Handler.class,
            beanMethod = "getReport",
            operation = @Operation(
                    operationId = "getReport",
                    summary = "Get Approved Loans Report",
                    description = "Returns a report with the total count and total amount of all approved loans.",
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Successful operation",
                                    content = @Content(schema = @Schema(implementation = Report.class))
                            )
                    }
            )
    )
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET("/api/v1/reportes"), handler::getReport);
    }
}
