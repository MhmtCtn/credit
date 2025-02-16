package tr.com.xbank.credit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tr.com.xbank.credit.util.DateTimeUtil;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;            // is success or not
    private String message;             // success or error message
    private T data;                     // return object from service class, if successful
    private List<String> errors;        // error details
    private int errorCode;              // http code or a specified system error code
    private long timestamp_unix;        // timestamp in unix format

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeUtil.DATE_TIME_PATTERN_WITH_ISO_8601_TIMEZONE)
    private String timestamp_iso8601;   // timestamp in iso8601 format
    private String path;                // request path

    public static <T> ApiResponse<T> success(T data, String path) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("SUCCESS")
                .data(data)
                .timestamp_unix(System.currentTimeMillis())
                .timestamp_iso8601(DateTimeUtil.getIso8601Timestamp())
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> error(List<String> errors, int errorCode, String message, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .errorCode(errorCode)
                .timestamp_unix(System.currentTimeMillis())
                .timestamp_iso8601(DateTimeUtil.getIso8601Timestamp())
                .path(path)
                .build();
    }
}
