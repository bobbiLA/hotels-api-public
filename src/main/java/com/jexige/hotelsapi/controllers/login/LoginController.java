package com.jexige.hotelsapi.controllers.login;

import com.jexige.hotelsapi.configs.ApiPaths;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(ApiPaths.LOGIN_PATH)
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping
    public ResponseEntity<?> postLogin(@RequestBody @Valid LoginRequest loginRequest) {
        loginService.validateLogin(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(RejectedLoginException.class)
    public ResponseEntity<?> handleLoginRejectedException(RejectedLoginException exception, HttpServletRequest request) {
        final HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        return ResponseEntity
                .status(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                        .withStatus(status)
                        .withInstance(URI.create(request.getServletPath()))
                        .withTitle(status.getReasonPhrase())
                        .withDetail(exception.getMessage())
                );
    }
}
