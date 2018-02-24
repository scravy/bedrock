package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.simplaex.bedrock.Pair.pair;

@RunWith(Spectrum.class)
public class ArrayMapTest {

  {
    describe("of", () -> {
      it("should build and query an array map", () -> {
        val map = ArrayMap.of(
          pair("foo", "one"),
          pair("bar", "two"),
          pair("baz", "three"),
          pair("quux", "four")
        );
        expect(map.get("junk")).toEqual(Optional.empty());
        expect(map.get("foo")).toEqual(Optional.of("one"));
        expect(map.get("bar")).toEqual(Optional.of("two"));
        expect(map.get("baz")).toEqual(Optional.of("three"));
        expect(map.get("quux")).toEqual(Optional.of("four"));
      });
    });
    describe("equals", () -> {
      it("should compare two equal map for equality successfully", () -> {
        val map = ArrayMap.of(
          pair(3, "three"),
          pair(1, "one"),
          pair(2, "two")
        );
        val otherMap = ArrayMap.of(
          pair(2, "two"),
          pair(1, "one"),
          pair(3, "three")
        );
        expect(map.equals(otherMap)).toBeTrue();
      });
      it("should not deem two different maps as equal", () -> {
        val map = ArrayMap.of(pair("one", "two"));
        val otherMap = ArrayMap.of(pair("two", "one"));
        expect(map.equals(otherMap)).toBeFalse();
      });
    });
    describe("mapValues", () -> {
      it("should create a new map from the old one and transform the values", () -> {
        val map = ArrayMap.of(pair("one", 1), pair("two", 2), pair("three", 3));
        val expected = ArrayMap.of(pair("one", "1"), pair("two", "2"), pair("three", "3"));
        val result = map.mapValues(Object::toString);
        expect(result).toEqual(expected);
      });
    });
    describe("mapValuesWithKeys", () -> {
      it("should create a new map from the old one using the keys as values", () -> {
        val map = ArrayMap.of(pair("one", "some"), pair("two", "some"));
        val expected = ArrayMap.of(pair("one", "one"), pair("two", "two"));
        val result = map.mapValuesWithKey((key, value) -> key);
        expect(result).toEqual(expected);
      });
    });
    describe("iterator", () -> {
      it("should iterate according to the order of the keys", () -> {
        val map = ArrayMap.of(
          pair("one", 1),
          pair("two", 2),
          pair("three", 3)
        );
        expect(Seq.ofIterable(map)).toEqual(Seq.of(
          pair("one", 1),
          pair("three", 3),
          pair("two", 2)
        ));
      });
    });
  }

}
