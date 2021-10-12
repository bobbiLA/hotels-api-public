package com.jexige.hotelsapi.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    final private ObjectMapper mapper;

    public AuthenticationEntryPointImpl(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        final HttpStatus status = HttpStatus.UNAUTHORIZED;

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());
        response.getWriter().write(mapper.writeValueAsString(
                Problem.create()
                        .withStatus(status)
                        .withInstance(URI.create(request.getServletPath()))
                        .withTitle(status.getReasonPhrase())
                        .withDetail("You must authenticate, please provide a valid username and password then retry.")
        ));
    }
}
