package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.simplaex.bedrock.Pair.pair;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
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
    describe("values", () -> {
      it("should return the values in the order of the keys", () -> {
        val map = ArrayMap.of(
          pair(4, "one"),
          pair(2, "two"),
          pair(3, "three")
        );
        expect(map.values()).toEqual(Seq.of("two", "three", "one"));
      });
    });
    describe("ofSeq", () -> {
      it("should create an ArrayMap from a Seq", () -> {
        val arrayMap = ArrayMap.ofSeq(Seq.of(
          pair("one", 1),
          pair("two", 2),
          pair("three", 3)
        ));
        expect(arrayMap.keys()).toEqual(Seq.of("one", "two", "three").sorted());
        expect(arrayMap.get("one")).toEqual(Optional.of(1));
        expect(arrayMap.get("two")).toEqual(Optional.of(2));
        expect(arrayMap.get("three")).toEqual(Optional.of(3));
      });
    });
    describe("ofMap", () -> {
      it("should create an ArrayMap from a Map", () -> {
        val map = new HashMap<String, Number>();
        map.put("one", 1);
        map.put("ninety-three", BigDecimal.valueOf(93));
        map.put("pi", Math.PI);
        val arrayMap = ArrayMap.ofMap(map);
        expect(arrayMap.keys()).toEqual(Seq.ofCollection(map.keySet()).sorted());
        expect(arrayMap.get("pi")).toEqual(Optional.of(Math.PI));
        expect(arrayMap.get("ninety-three")).toEqual(Optional.of(BigDecimal.valueOf(93)));
        expect(arrayMap.get("one")).toEqual(Optional.of(1));
      });
    });
    describe("apply", () -> {
      val map = ArrayMap.of(
        pair(1, "one"),
        pair(2, "two"),
        pair(3, "three"),
        pair(4, "four"),
        pair(5, "five")
      );
      it("should be usable as a regular function", () -> {
        val seq = Seq.of(1, 3, 5);
        expect(seq.map(map)).toEqual(Seq.of("one", "three", "five"));
      });
      it("should throw a NoSuchElementException if element is not present", () -> {
        expect(() -> map.apply(0)).toThrow(NoSuchElementException.class);
      });
    });
    describe("toMap", () -> {
      it("should create a java map from an array map", () -> {
        val map = ArrayMap.of(
          pair(1, "one"),
          pair(2, "two"),
          pair(3, "three")
        ).toMap();
        val hashMap = new HashMap<Integer, String>();
        hashMap.put(1, "one");
        hashMap.put(2, "two");
        hashMap.put(3, "three");
        expect(map).toEqual(hashMap);
      });
    });
    describe("ofMap", () -> {
      val map = new TreeMap<String, Number>();
      map.put("one", 1);
      map.put("two", 2);
      map.put("three", 3);
      val expected = ArrayMap.of(
        pair("one", 1),
        pair("two", 2),
        pair("three", 3)
      );
      it("should create a map from a TreeMap using ofMap(TreeMap)", () -> {
        val arrayMap = ArrayMap.ofMap(map);
        expect(arrayMap).toEqual(expected);
      });
      it("should create a map from a TreeMap using ofMap(Map)", () -> {
        val arrayMap = ArrayMap.ofMap((Map<String, Number>) map);
        expect(arrayMap).toEqual(expected);
      });
    });
    describe("filter", () -> {
      val map = ArrayMap.of(
        pair(0, "zero"),
        pair(3, "three"),
        pair(4, "four"),
        pair(5, "five")
      );
      it("should filter according to keys", () -> {
        expect(map.filter(k -> k % 2 == 0)).toEqual(ArrayMap.of(
          pair(0, "zero"),
          pair(4, "four")
        ));
      });
    });
    describe("filterWithValue", () -> {
      val map = ArrayMap.of(
        pair(0, "zero"),
        pair(3, "three"),
        pair(4, "four"),
        pair(5, "five")
      );
      it("should filter according to values", () -> {
        expect(map.filterWithValue((k, v) -> v.charAt(0) == 'f')).toEqual(ArrayMap.of(
          pair(4, "four"),
          pair(5, "five")
        ));
      });
    });
    describe("union", () -> {
      val map1 = ArrayMap.of(
        pair(1, "1"),
        pair(2, "2"),
        pair(4, "4")
      );
      val map2 = ArrayMap.of(
        pair(0, "zero"),
        pair(3, "three"),
        pair(4, "four"),
        pair(5, "five")
      );
      it("should union left-biased", () -> {
        expect(map1.union(map2)).toEqual(ArrayMap.of(
          pair(0, "zero"),
          pair(1, "1"),
          pair(2, "2"),
          pair(3, "three"),
          pair(4, "4"),
          pair(5, "five")
        ));
      });
      it("should union left-biased (other way around)", () -> {
        expect(map2.union(map1)).toEqual(ArrayMap.of(
          pair(0, "zero"),
          pair(1, "1"),
          pair(2, "2"),
          pair(3, "three"),
          pair(4, "four"),
          pair(5, "five")
        ));
      });
    });
    describe("intersect", () -> {
      val map1 = ArrayMap.of(
        pair(1, "1"),
        pair(2, "2"),
        pair(4, "4")
      );
      val map2 = ArrayMap.of(
        pair(0, "zero"),
        pair(3, "three"),
        pair(4, "four"),
        pair(5, "five")
      );
      it("should intersect left-biased", () -> {
        expect(map1.intersect(map2)).toEqual(ArrayMap.of(
          pair(4, "4")
        ));
      });
      it("should intersect left-biased (other way around)", () -> {
        expect(map2.intersect(map1)).toEqual(ArrayMap.of(
          pair(4, "four")
        ));
      });
    });
    describe("collector", () -> {
      it("builder should collect values as collector", () -> {
        final ArrayMap<String, Integer> arrayMap = Stream.of(
          pair("one", 1),
          pair("two", 2)
        ).collect(ArrayMap.builder());
        expect(arrayMap).toEqual(ArrayMap.of(pair("one", 1), pair("two", 2)));
      });
    });
  }

}
