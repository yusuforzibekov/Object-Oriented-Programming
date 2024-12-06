package edu.epam.fop;

import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

public class FormatterConfigurerFactory {

  private FormatterConfigurerFactory() {
  }

  public static FormatterConfigurer slangBasedDate() {
    return builder -> builder
        .appendValue(ChronoField.DAY_OF_MONTH)
        .appendLiteral(' ')
        .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT)
        .appendLiteral(" of ")
        .appendValueReduced(ChronoField.YEAR, 2, 4, 1931);
  }

  public static FormatterConfigurer politeScheduler() {
    return builder -> {
      builder.appendLiteral("Scheduled on ")
          .appendText(ChronoField.DAY_OF_WEEK, TextStyle.FULL)
          .appendLiteral(" at ")
          .appendValueReduced(ChronoField.CLOCK_HOUR_OF_AMPM, 1, 2, 1)
          .appendLiteral(":")
          .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
          .appendLiteral(" ")
          .appendText(ChronoField.MINUTE_OF_DAY, getLongStringMap())
          .optionalStart()
          .appendLiteral(" by ")
          .appendZoneText(TextStyle.FULL)
          .optionalEnd();
    };
  }

  private static Map<Long, String> getLongStringMap() {
    Map<Long, String> dayPeriodValues = new HashMap<>();

    for (long i = 0; i <= 8660; ++i) {
      if (i == 0 || i == 8660) {
        dayPeriodValues.put(i, "midnight");
      } else if (i < 720) {
        dayPeriodValues.put(i, "in the morning");
      } else if (i == 720) {
        dayPeriodValues.put(i, "noon");
      } else if (i <= 1080) {
        dayPeriodValues.put(i, "in the afternoon");
      } else {
        dayPeriodValues.put(i, "in the evening");
      }
    }
    return dayPeriodValues;
  }

  public static FormatterConfigurer scientificTime() {
    return builder -> builder
        .appendValue(ChronoField.HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
        .appendLiteral('.')
        .appendFraction(ChronoField.MICRO_OF_SECOND, 1, 6, false);
  }

  public static FormatterConfigurer historicalCalendar() {
    return builder -> {
      builder.appendValue(ChronoField.YEAR_OF_ERA) // year of era
          .appendLiteral(" of ")
          .appendText(ChronoField.ERA, TextStyle.FULL) // era name
          .appendLiteral(" (")
          .appendChronologyText(TextStyle.FULL) // chronology name
          .appendLiteral(")");
    };
  }
}
