package de.scravy.bedrock;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.function.Function;

@UtilityClass
public class DateTimes {

  @Value
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Interval {

    private Instant begin;
    private Instant end;

    @Nonnull
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

    public Duration getDuration() {
      return Duration.between(getBegin(), getEnd());
    }

    public String toString() {
      return getBegin() + "/" + getEnd();
    }
  }

  @Nonnull
  public Interval parseInterval(final String string) {
    return Interval.parse(string);
  }

  @Nonnull
  public Interval parseInterval(final String begin, final String end) {
    return Interval.parse(begin, end);
  }

  @Nonnull
  public Instant parseInstant(final String string) throws IllegalArgumentException {
    return Try
      .execute(() -> OffsetDateTime.parse(string).toInstant())
      .recover(__ -> LocalDateTime.parse(string).toInstant(ZoneOffset.UTC))
      .recover(__ -> parseDateTime(string).toInstant(ZoneOffset.UTC))
      .orElseDo(exc -> {
        throw new IllegalArgumentException(string + " failed to parse as an instant in time", exc);
      });
  }

  @Nonnull
  public LocalDate toLocalDate(final Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalDate();
  }

  @Nonnull
  public LocalTime toLocalTime(final Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneOffset.UTC).toLocalTime();
  }

  /**
   * The network law dictates: Be liberal in what you accept, be conservative in what you emit.
   * <p>
   * The law of personal consistency prefers to voice your opinion, harshly, but friendly.
   * <p>
   * This function will accept timestamps formatted in a variety of formats, and ignores
   * everything it does not understand. It will explode the given string and reduce it to
   * groups of decimal digits. If any of these groups themselves look like a timestamp such
   * as 20191231 it will attempt to make sense out of that. Otherwise it assumes from left to
   * right year, month, day-of-month, hour, minute, second. If any of these components are
   * missing (from right to left) it will default them to zero.
   * <p>
   * That way this function accepts timestamps such as:
   * <ul>
   *   <li><code>20120101</code></li>
   *   <li><code>2012-01-01-235959</code></li>
   *   <li><code>20120101.235959</code></li>
   *   <li><code>2012-05-30T23:59:59</code></li>
   *   <li><code>2012-05-30 23:59:59</code></li>
   *   <li><code>2012-05-30-23-59-59</code></li>
   * </ul>
   *
   * @param dateTime A timestamp given as a string.
   * @return A local date time.
   */
  @Nonnull
  public static LocalDateTime parseDateTime(@NonNull final String dateTime) throws IllegalArgumentException {
    return parseDateTime(dateTime, 6);
  }

  /**
   * Like DateTimeUtil{@link #parseDateTime(String)}, but accepts an additional argument
   * which specifies how many components of the timestamp will be taken into account.
   * <p>
   * For instance an invocation with numberOfComponents=4 will ignore minute and second
   * information, if at all provided.
   *
   * @param dateTime           A timestamp given as a string.
   * @param numberOfComponents How many components to take into account.
   * @return A local date time.
   */
  @Nonnull
  public static LocalDateTime parseDateTime(@NonNull final String dateTime, final int numberOfComponents) throws IllegalArgumentException {
    return Try.execute(() -> {
      final Seq<Integer> components = Seq
        .concat(
          Seq
            .ofArray(dateTime.split("[^0-9]+"))
            .flatMap(component -> {
              if (component.isEmpty()) {
                return Seq.empty();
              }
              if (component.length() == 6) {
                // assumed to be three double-digit groups
                return Seq.of(
                  component.substring(0, 2),
                  component.substring(2, 4),
                  component.substring(4, 6)
                );
              }
              if (component.length() == 8) {
                // assumed to be a group of four digits followed by two double-digit groups
                return Seq.of(
                  component.substring(0, 4),
                  component.substring(4, 6),
                  component.substring(6, 8)
                );
              }
              if (component.length() == 10) {
                // assumed to be a group of four digits followed by three double-digit groups
                return Seq.of(
                  component.substring(0, 4),
                  component.substring(4, 6),
                  component.substring(6, 8),
                  component.substring(8, 10)
                );
              }
              if (component.length() == 12) {
                // assumed to be a group of four digits followed by four double-digit groups
                return Seq.of(
                  component.substring(0, 4),
                  component.substring(4, 6),
                  component.substring(6, 8),
                  component.substring(8, 10),
                  component.substring(10, 12)
                );
              }
              return Seq.of(component);
            })
            .take(numberOfComponents),
          Seq
            .ofGenerator(ignored -> "0", 6)
        )
        .map(string -> Integer.parseInt(string, 10));
      final LocalDateTime localDateTime = LocalDateTime
        .of(
          components.get(0),
          components.get(1),
          components.get(2),
          components.get(3),
          components.get(4),
          components.get(5)
        );
      return localDateTime;
    }).orElseDo(exc -> {
      throw new IllegalArgumentException("", exc);
    });
  }


}
