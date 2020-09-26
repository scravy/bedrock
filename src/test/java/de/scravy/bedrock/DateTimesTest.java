package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class DateTimesTest {

  {
    describe("parse an interval", () -> {

      final Seq<String> endings = Seq.of(
        "06:07",
        "06:07Z",
        "06:07:30",
        "06:07:30Z",
        "06:07:30.208Z",
        "06:07:30.000Z",
        "06:07+00:00",
        "06:07:30+00:00",
        "06:07:30.208+00:00",
        "06:07:30.000+00:00"
      );
      final Seq<String> beginTimes = endings.map(s -> "2018-03-20T" + s);
      final Seq<String> endTimes = endings.map(s -> "2018-03-23T" + s);

      final DateTimes.Interval expected = DateTimes.Interval.of(
        OffsetDateTime.parse("2018-03-20T06:07Z").toInstant(),
        OffsetDateTime.parse("2018-03-23T06:07Z").toInstant()
      );
      final Seq<LocalDate> expectedDays = Seq.of(
        LocalDate.parse("2018-03-20"),
        LocalDate.parse("2018-03-21"),
        LocalDate.parse("2018-03-22"),
        LocalDate.parse("2018-03-23")
      );

      for (final String begin : beginTimes) {
        for (String end : endTimes) {
          final String interval = begin + "/" + end;
          it("should parse " + interval, () -> {
            expect(
              DateTimes.Interval.parse(interval)
                .adjustBegin(b -> b.truncatedTo(ChronoUnit.MINUTES))
                .adjustEnd(e -> e.truncatedTo(ChronoUnit.MINUTES))
            ).toEqual(expected);
          });

          it("should extract all days in that interval", () -> {
            expect(DateTimes.parseInterval(interval).decomposeIntoDays()).toEqual(expectedDays);
          });
        }
      }
    });

    describe("Interval", () -> {
      describe("getDuration", () -> {
        it("shoul calculate a duration for an interval", () -> {
          final DateTimes.Interval interval = DateTimes.parseInterval("2000-01-03T04:00/2000-02-22T06:30");
          expect(interval.getDuration())
            .toEqual(Duration.of(50, ChronoUnit.DAYS).plus(Duration.of(150, ChronoUnit.MINUTES)));
        });
      });
    });

    describe("DateTimeUtil", () -> {
      describe("parseDateTime(String)", () -> {
        it("should parse 20120101", () ->
          expect(DateTimes.parseDateTime("20120101").toInstant(ZoneOffset.UTC))
            .toEqual(Instant.parse("2012-01-01T00:00:00Z")));
        it("should parse 2012-01-01-235959", () ->
          expect(DateTimes.parseDateTime("2012-01-01-235959").toInstant(ZoneOffset.UTC))
            .toEqual(Instant.parse("2012-01-01T23:59:59Z")));
        it("should parse 20120101.235959", () ->
          expect(DateTimes.parseDateTime("20120101.235959").toInstant(ZoneOffset.UTC))
            .toEqual(Instant.parse("2012-01-01T23:59:59Z")));
        it("should parse 2012-05-30T23:59:59", () ->
          expect(DateTimes.parseDateTime("2012-05-30T23:59:59").toInstant(ZoneOffset.UTC))
            .toEqual(Instant.parse("2012-05-30T23:59:59Z")));
        it("should parse 2012-05-30 23:59:59", () ->
          expect(DateTimes.parseDateTime("2012-05-30 23:59:59").toInstant(ZoneOffset.UTC))
            .toEqual(Instant.parse("2012-05-30T23:59:59Z")));
        it("should parse 2012-05-30-23-59-59", () ->
          expect(DateTimes.parseDateTime("2012-05-30-23-59-59").toInstant(ZoneOffset.UTC))
            .toEqual(Instant.parse("2012-05-30T23:59:59Z")));
        final Instant someInstant = Instant.parse("2019-12-19T22:41:49Z");
        it("should parse " + someInstant, () ->
          expect(DateTimes.parseDateTime(someInstant.toString()).toInstant(ZoneOffset.UTC)).toEqual(someInstant));
      });
      describe("parseDateTime(String,4)", () -> {
        it("should parse 20120101", () ->
          expect(DateTimes.parseDateTime("20120101", 4).toInstant(ZoneOffset.UTC))
            .toEqual(Instant.parse("2012-01-01T00:00:00Z")));
        it("should parse 2012-01-01-235959", () ->
          expect(DateTimes.parseDateTime("2012-01-01-235959", 4).toInstant(ZoneOffset.UTC))
            .toEqual(Instant.parse("2012-01-01T23:00:00Z")));
        it("should parse 20120101.235959", () ->
          expect(DateTimes.parseDateTime("20120101.235959", 4).toInstant(ZoneOffset.UTC))
            .toEqual(Instant.parse("2012-01-01T23:00:00Z")));
        it("should parse 2012-05-30T23:59:59", () ->
          expect(DateTimes.parseDateTime("2012-05-30T23:59:59", 4).toInstant(ZoneOffset.UTC))
            .toEqual(Instant.parse("2012-05-30T23:00:00Z")));
        it("should parse 2012-05-30 23:59:59", () ->
          expect(DateTimes.parseDateTime("2012-05-30 23:59:59", 4).toInstant(ZoneOffset.UTC))
            .toEqual(Instant.parse("2012-05-30T23:00:00Z")));
        it("should parse 2012-05-30-23-59-59", () ->
          expect(DateTimes.parseDateTime("2012-05-30-23-59-59", 4).toInstant(ZoneOffset.UTC))
            .toEqual(Instant.parse("2012-05-30T23:00:00Z")));
      });
    });
  }

}
