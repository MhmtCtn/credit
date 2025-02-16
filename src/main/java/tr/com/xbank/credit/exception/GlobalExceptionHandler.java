package tr.com.xbank.credit.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tr.com.xbank.credit.dto.ApiResponse;

import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public static ResponseEntity<Object> handleGeneralException(Exception ex,
                                                                        HttpServletRequest request) {
        return errorResponseEntity(
                Collections.singletonList(ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred",
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex,
                                                                          HttpServletRequest request) {
        return errorResponseEntity(
                Collections.singletonList(ex.getMessage()),
                HttpStatus.NOT_FOUND.value(),
                "Resource not found",
                request.getRequestURI(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex,
                                                                         HttpServletRequest request) {
        return errorResponseEntity(
                Collections.singletonList(ex.getMessage()),
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST
        );
    }

    @NonNull
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return errorResponseEntity(
                errors,
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                request.getDescription(false).substring(4),
                HttpStatus.BAD_REQUEST
        );
    }

    public static ResponseEntity<Object> errorResponseEntity(List<String> errors,
                                                                     int errorCode,
                                                                     String message,
                                                                     String path,
                                                                     HttpStatus status) {

        ApiResponse<?> response = ApiResponse.error(errors, errorCode, message, path);

        return new ResponseEntity<>(response, status);
    }
}
