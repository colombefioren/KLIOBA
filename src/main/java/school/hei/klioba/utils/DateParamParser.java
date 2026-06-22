package school.hei.klioba.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class DateParamParser {

  private static final ZoneId ZONE = ZoneId.of("UTC+3");

  public static Instant parseDate(String dateStr) {
    if (dateStr == null) return null;
    return LocalDate.parse(dateStr).atStartOfDay(ZONE).toInstant();
  }

  public static Instant parseDateEnd(String dateStr) {
    if (dateStr == null) return null;
    return LocalDate.parse(dateStr).plusDays(1).atStartOfDay(ZONE).toInstant();
  }
}
