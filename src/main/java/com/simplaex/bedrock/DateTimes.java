package com.simplaex.bedrock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.UtilityClass;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.function.Function;

@UtilityClass
public class DateTimes {

  @Value
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public class Interval {

    private Instant begin;
    private Instant end;

    public static Interval parse(final String string) {
      final int indexOfSlash = string.indexOf('/');
      if (indexOfSlash > 0) {
        final String begin = string.substring(0, indexOfSlash);
        final String end = string.substring(indexOfSlash + 1);
        try {
          return parse(begin, end);
        } catch (final Exception exc) {
          throw new IllegalArgumentException(string + " failed to parse as an Interval", exc);
        }
      }
      throw new IllegalArgumentException(string + " failed to parse as an Interval");
    }

    public static Interval parse(final String begin, final String end) {
      final Instant beginInstant = parseInstant(begin);
      final Instant endInstant = parseInstant(end);
      return of(beginInstant, endInstant);
    }

    public static Interval of(final Instant beginInclusive, final Instant endExclusive) {
      Objects.requireNonNull(beginInclusive, "'beginInclusive' must not be null");
      Objects.requireNonNull(endExclusive, "'endExclusive' must not be null");
      if (beginInclusive.isAfter(endExclusive)) {
        return new Interval(endExclusive, beginInclusive);
      }
      return new Interval(beginInclusive, endExclusive);
    }

    public Interval adjustBegin(final Function<Instant, Instant> function) {
      return Interval.of(function.apply(begin), end);
    }

    public Interval adjustEnd(final Function<Instant, Instant> function) {
      return Interval.of(begin, function.apply(end));
    }

    /**
     * Expand this interval to completely cover the specified units it spans into.
     * <p>
     * Example: If you have an Interval covering Apr 12th 4pm till Apr 15th 6am then
     * it touches Apr 12th, 13t, 14th, and 15th. expandTo will extend the interval to
     * properly reflect that and for the granularity days will make it span from
     * Apr 12th 12am till Apr 16th am (end date exclusive as always).
     *
     * @return A new Interval
     */
    public Interval expandTo(final TemporalUnit timeUnit) {
      final Instant expandedBegin = begin.truncatedTo(timeUnit);
      final Instant expandedEnd = end.truncatedTo(timeUnit).plus(1, timeUnit);
      return new Interval(expandedBegin, expandedEnd);
    }

    public Seq<LocalDate> decomposeIntoDays() {
      final Interval expandedToDays = expandTo(ChronoUnit.DAYS);
      final SeqBuilder<LocalDate> builder = Seq.builder();
      Instant start = expandedToDays.getBegin();
      while (start.compareTo(expandedToDays.getEnd()) < 0) {
        builder.add(toLocalDate(start));
        start = start.plus(1, ChronoUnit.DAYS);
      }
      return builder.result();
    }
  }

  public Interval parseInterval(final String string) {
    return Interval.parse(string);
  }

  public Interval parseInterval(final String begin, final String end) {
    return Interval.parse(begin, end);
  }

  public Instant parseInstant(final String string) {
    return Try
      .execute(() -> OffsetDateTime.parse(string).toInstant())
      .recover(__ -> LocalDateTime.parse(string).toInstant(ZoneOffset.UTC))
      .fold(exc -> {
        throw new IllegalArgumentException(string + " failed to parse as an instant in time", exc);
      }, x -> x);
  }

  public LocalDate toLocalDate(final Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalDate();
  }

  public LocalTime toLocalTime(final Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalTime();
  }

}
