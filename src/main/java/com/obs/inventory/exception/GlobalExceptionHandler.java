package com.obs.inventory.exception;

import com.obs.inventory.dto.response.ResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseMessage<Object>> handleNoResource(
            NoResourceFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("No resource for URL {}", request.getRequestURI(), ex);

        ResponseMessage<Object> resp = new ResponseMessage<>();
        resp.setIsError(true);
        resp.setErrorNumber("ERR-404");
        resp.setMessage("Endpoint not found: " + request.getRequestURI());
        resp.setTrxDateResponse(LocalDateTime.now().format(FORMATTER));
        resp.setData(Collections.emptyList());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }

    // Business error → 400
    @ExceptionHandler(ErrorBusinessException.class)
    public ResponseEntity<ResponseMessage<Object>> handleBusiness(ErrorBusinessException ex) {

        log.warn("Business error [{}] : {}", ex.getErrorNumber(), ex.getMessage());

        ResponseMessage<Object> resp = new ResponseMessage<>();
        resp.setIsError(true);
        resp.setErrorNumber(ex.getErrorNumber());
        resp.setMessage(ex.getMessage());
        resp.setTrxDateResponse(LocalDateTime.now().format(FORMATTER));
        resp.setData(Collections.emptyList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    // Bean Validation error (@NotNull, @NotBlank, dll) → 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseMessage<Object>> handleValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Validation error: {}", msg);

        ResponseMessage<Object> resp = new ResponseMessage<>();
        resp.setIsError(true);
        resp.setErrorNumber("VAL-400");
        resp.setMessage(msg);
        resp.setTrxDateResponse(LocalDateTime.now().format(FORMATTER));
        resp.setData(Collections.emptyList());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    // Other unexpected error → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage<Object>> handleOther(Exception ex,
                                                               HttpServletRequest request) {
        log.error("Unhandled error for URL {}", request.getRequestURI(), ex);

        ResponseMessage<Object> resp = new ResponseMessage<>();
        resp.setIsError(true);
        resp.setErrorNumber("ERR-500");
        // boleh diganti sesuai kebutuhan (jangan terlalu bocorkan detail)
        resp.setMessage("Internal server error: " + ex.getClass().getSimpleName());
        resp.setTrxDateResponse(LocalDateTime.now().format(FORMATTER));
        resp.setData(Collections.emptyList());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
}
