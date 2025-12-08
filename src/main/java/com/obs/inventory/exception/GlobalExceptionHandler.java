package com.obs.inventory.exception;

import com.obs.inventory.dto.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    // Business error → 400
    @ExceptionHandler(ErrorBusinessException.class)
    public ResponseEntity<ResponseMessage<Object>> handleBusiness(ErrorBusinessException ex) {
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
    public ResponseEntity<ResponseMessage<Object>> handleOther(Exception ex) {
        ResponseMessage<Object> resp = new ResponseMessage<>();
        resp.setIsError(true);
        resp.setErrorNumber("ERR-500");
        resp.setMessage("Internal server error");
        resp.setTrxDateResponse(LocalDateTime.now().format(FORMATTER));
        resp.setData(Collections.emptyList());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
}
