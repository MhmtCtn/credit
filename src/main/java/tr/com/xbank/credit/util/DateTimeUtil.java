package tr.com.xbank.credit.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    public static final String DATE_TIME_PATTERN_WITH_ISO_8601_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    private static final DateTimeFormatter ISO_8601_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_WITH_ISO_8601_TIMEZONE);

    public static String getIso8601Timestamp() {
        return ZonedDateTime.now(ZoneId.of("GMT+3")).format(ISO_8601_FORMATTER);
    }
}
