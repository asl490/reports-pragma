package com.pragma.bootcamp.api.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.pragma.bootcamp.model.report.exception.BadCredentialsException;
import com.pragma.bootcamp.model.report.exception.ForbiddenException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
enum ErrorStrategy {

    VALIDATION(WebExchangeBindException.class) {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                 MessageSource messageSource) {
            WebExchangeBindException bindEx = (WebExchangeBindException) ex;
            List<String> errors = bindEx.getBindingResult().getFieldErrors().stream()
                    .map(fe -> String.format("%s: %s", fe.getField(),
                            Optional.ofNullable(fe.getDefaultMessage()).orElse("Error de validación")))
                    .toList();

            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST.name(), ex.getMessage(), errors,
                    exchange.getRequest().getPath().value());
            log.warn(errorResponse.getMessage(), ex.getMessage());
            return writeResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse, objectMapper);
        }
    },


    ACCESS_DENIED(AccessDeniedException.class) {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                 MessageSource messageSource) {
            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.FORBIDDEN.name(), ex.getMessage(), null,
                    exchange.getRequest().getPath().value());
            log.warn(errorResponse.getMessage(), ex.getMessage());
            return writeResponse(exchange, HttpStatus.FORBIDDEN, errorResponse, objectMapper);
        }
    },

    TIMEOUT(TimeoutException.class) {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                 MessageSource messageSource) {
            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.REQUEST_TIMEOUT.name(), ex.getMessage(), null,
                    exchange.getRequest().getPath().value());
            log.warn(errorResponse.getMessage(), ex.getMessage());
            return writeResponse(exchange, HttpStatus.REQUEST_TIMEOUT, errorResponse, objectMapper);
        }
    },
    CONSTRAINT_VIOLATION(ConstraintViolationException.class) {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                 MessageSource messageSource) {
            ConstraintViolationException cvEx = (ConstraintViolationException) ex;
            List<String> errors = cvEx.getConstraintViolations().stream()
                    .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                    .toList();

            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST.name(), ex.getMessage(), errors,
                    exchange.getRequest().getPath().value());
            log.warn(errorResponse.getMessage() + " {}", errors.toString());
            return writeResponse(exchange, HttpStatus.BAD_REQUEST, errorResponse, objectMapper);
        }
    },
    FORBIDDEN_EXCEPTION(ForbiddenException.class) {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                 MessageSource messageSource) {
            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.FORBIDDEN.name(), ex.getMessage(), null,
                    exchange.getRequest().getPath().value());
            log.warn(errorResponse.getMessage(), ex.getMessage());
            return writeResponse(exchange, HttpStatus.FORBIDDEN, errorResponse, objectMapper);
        }
    },

    BAD_CREDENTIALS(BadCredentialsException.class) {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                 MessageSource messageSource) {
            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.FORBIDDEN.name(), ex.getMessage(), null,
                    exchange.getRequest().getPath().value());
            log.warn(errorResponse.getMessage(), ex.getMessage());
            return writeResponse(exchange, HttpStatus.FORBIDDEN, errorResponse, objectMapper);
        }
    },


//    BAD_REQUEST_NOTIFICATION(BusinessException.class) {
//        @Override
//        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
//                                 MessageSource messageSource) {
//            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST.name(), ex.getMessage(), null,
//                    exchange.getRequest().getPath().value());
//            log.warn(errorResponse.getMessage(), ex.getMessage());
//            return writeResponse(exchange, HttpStatus.FORBIDDEN, errorResponse, objectMapper);
//        }
//    },
//    BAD_REQUEST_NOTIFICATION_AWS(ServerWebInputException.class) {
//        @Override
//        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
//                                 MessageSource messageSource) {
//            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.BAD_REQUEST.name(), BusinessException.Type.REQUEST_LOAN_NOT_FOUND.getMessage(), null,
//                    exchange.getRequest().getPath().value());
//            log.warn(errorResponse.getMessage(), ex.getMessage());
//            return writeResponse(exchange, HttpStatus.FORBIDDEN, errorResponse, objectMapper);
//        }
//    },


    DEFAULT(Throwable.class) {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                 MessageSource messageSource) {
            log.warn("error {}", ex);
            ErrorResponse errorResponse = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), ex.getMessage(),
                    null, exchange.getRequest().getPath().value());

            return writeResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR, errorResponse, objectMapper);
        }
    };

    // Mapa estático para búsqueda rápida O(1)
    private static final Map<Class<? extends Throwable>, ErrorStrategy> STRATEGY_MAP = Arrays
            .stream(ErrorStrategy.values())
            .filter(strategy -> strategy != DEFAULT)
            .collect(Collectors.toUnmodifiableMap(
                    strategy -> strategy.exceptionClass,
                    Function.identity()));
    private final Class<? extends Throwable> exceptionClass;

    public static ErrorStrategy findStrategy(Class<? extends Throwable> exceptionClass) {
        // Búsqueda exacta O(1)
        ErrorStrategy strategy = STRATEGY_MAP.get(exceptionClass);
        if (strategy != null) {
            return strategy;
        }

        // Búsqueda por jerarquía para subclases
        return STRATEGY_MAP.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(exceptionClass))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(DEFAULT);
    }

    // Métodos helper estáticos para reutilización
    protected static ErrorResponse buildErrorResponse(String code, String message,
                                                      List<String> details, String path) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .errors(details)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    protected static Mono<Void> writeResponse(ServerWebExchange exchange, HttpStatus status,
                                              ErrorResponse errorResponse, ObjectMapper objectMapper) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        try {
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);
            DataBuffer buffer = response.bufferFactory().wrap(jsonResponse.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("Error serializing error response", e);
            return response.setComplete();
        }
    }

    public abstract Mono<Void> handle(ServerWebExchange exchange, Throwable ex, ObjectMapper objectMapper,
                                      MessageSource messageSource);
}
