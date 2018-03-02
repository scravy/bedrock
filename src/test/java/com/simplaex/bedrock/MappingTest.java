package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class MappingTest {

  {
    describe("wrap", () -> {

      it("should create a Map for which get() works", () -> {
        val map = new HashMap<String, Number>();
        map.put("zero", BigDecimal.ZERO);
        map.put("one", BigDecimal.ONE);
        val mapping = Mapping.wrap(map);
        expect(mapping.get("zero")).toEqual(Optional.of(BigDecimal.ZERO));
        expect(mapping.get("one")).toEqual(Optional.of(BigDecimal.ONE));
        expect(mapping.get("two")).toEqual(Optional.empty());
      });

      it("should create a Map for which keys() works", () -> {
        val map = new HashMap<String, Number>();
        map.put("zero", BigDecimal.ZERO);
        map.put("one", BigDecimal.ONE);
        val mapping = Mapping.wrap(map);
        expect(mapping.keys().sorted()).toEqual(Seq.of("one", "zero"));
      });

      it("should create a Map for which values() works", () -> {
        val map = new HashMap<String, Number>();
        map.put("zero", BigDecimal.ZERO);
        map.put("one", BigDecimal.ONE);
        val mapping = Mapping.wrap(map);
        expect(mapping.values().sorted()).toEqual(Seq.of(BigDecimal.ZERO, BigDecimal.ONE));
      });

      it("should create a Map for which keys() works when called twice", () -> {
        val map = new HashMap<String, Number>();
        map.put("zero", BigDecimal.ZERO);
        map.put("one", BigDecimal.ONE);
        val mapping = Mapping.wrap(map);
        mapping.keys();
        expect(mapping.keys().sorted()).toEqual(Seq.of("one", "zero"));
      });

      it("should create a Map for which values() works when called twice", () -> {
        val map = new HashMap<String, Number>();
        map.put("zero", BigDecimal.ZERO);
        map.put("one", BigDecimal.ONE);
        val mapping = Mapping.wrap(map);
        mapping.values();
        expect(mapping.values().sorted()).toEqual(Seq.of(BigDecimal.ZERO, BigDecimal.ONE));
      });

      it("should create a Map for which toMap return the original Map", () -> {
        val map = new HashMap<String, Number>();
        val mapping = Mapping.wrap(map);
        expect(mapping.toMap() == map).toBeTrue();
      });

    });
  }
}
