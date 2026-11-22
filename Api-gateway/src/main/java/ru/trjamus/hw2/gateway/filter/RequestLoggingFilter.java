package ru.trjamus.hw2.gateway.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_BODY = 10_000;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        var req = new ContentCachingRequestWrapper(request, MAX_BODY);
        var res = new ContentCachingResponseWrapper(response);

        try {
            chain.doFilter(req, res);
        } finally {
            log.info("""
                    GATEWAY
                    {} {} → {}
                    Headers: {}
                    Request-Body: {}
                    Response-Body: {}
                    """,
                    req.getMethod(),
                    req.getRequestURI(),
                    res.getStatus(),
                    headers(req),
                    body(req.getContentAsByteArray()),
                    body(res.getContentAsByteArray())
            );
            res.copyBodyToResponse();
        }
    }

    private String headers(HttpServletRequest req) {
        return Collections.list(req.getHeaderNames()).stream()
                .map(h -> h + ": " + String.join("; ", Collections.list(req.getHeaders(h))))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private String body(byte[] bytes) {
        return bytes.length == 0 ? "-" : new String(bytes, StandardCharsets.UTF_8);
    }
}