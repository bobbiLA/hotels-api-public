package com.jexige.hotelsapi.controllers.common;

import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class BadRequestControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, HttpServletRequest request) {
        final HttpStatus status = HttpStatus.BAD_REQUEST;

        final List<InvalidProps> invalidProps = new ArrayList<>();
        exception.getBindingResult().getFieldErrors().forEach(
                fieldError -> invalidProps.add(new InvalidProps(fieldError.getField(), fieldError.getDefaultMessage())));
        exception.getBindingResult().getGlobalErrors().forEach(
                globalError -> invalidProps.add(new InvalidProps(globalError.getObjectName(), globalError.getDefaultMessage()))
        );
        return ResponseEntity
                .status(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withStatus(status)
                        .withInstance(URI.create(request.getServletPath()))
                        .withTitle(status.getReasonPhrase())
                        .withProperties(Map.of(InvalidProps.PROPERTY_NAME, invalidProps))
                );
    }
}
