package ru.trjamus.hw2.notification.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        var req = new ContentCachingRequestWrapper(request, 10_000);
        var res = new ContentCachingResponseWrapper(response);

        try {
            chain.doFilter(req, res);
        } finally {
            log.info("""
                    \nRequest {} {}
                    Headers: {}
                    Body: {}
                    """,
                    req.getMethod(),
                    req.getRequestURI(),
                    headers(req),
                    body(req.getContentAsByteArray()));

            log.info("""
                    \nResponse {} {}
                    Status: {}
                    Headers: {}
                    Body: {}
                    """,
                    req.getMethod(),
                    req.getRequestURI(),
                    res.getStatus(),
                    headers(res),
                    body(res.getContentAsByteArray()));

            res.copyBodyToResponse();
        }
    }

    private String headers(HttpServletRequest req) {
        return Collections.list(req.getHeaderNames()).stream()
                .map(h -> h + ": " + String.join("; ", Collections.list(req.getHeaders(h))))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private String headers(ContentCachingResponseWrapper res) {
        return res.getHeaderNames().stream()
                .map(h -> h + ": " + String.join("; ", res.getHeaders(h)))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private String body(byte[] bytes) {
        return bytes.length == 0 ? "-" : new String(bytes, StandardCharsets.UTF_8);
    }
}