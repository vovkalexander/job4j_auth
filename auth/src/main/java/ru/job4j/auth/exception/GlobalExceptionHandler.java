package ru.job4j.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@ControllerAdvice
@AllArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(value = {NullPointerException.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", "Some of fields empty");
            put("details", e.getMessage());
        }}));
        log.error(e.getMessage());
    }
}
