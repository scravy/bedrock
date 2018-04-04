package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class ReflectionsTest {

  {
    describe("getFactory(S,T)", () -> {
      Seq.of(Integer.class, Long.class, Double.class, BigDecimal.class, BigInteger.class).forEach(clazz -> {
        it("should find a String factory for " + clazz.getName(), () -> {
          expect(Reflections.getFactory(String.class, clazz).isPresent()).toBeTrue();
        });
        it("should instantiate an instance of " + clazz.getName(), () -> {
          expect(Reflections.getFactory(String.class, clazz).map(f -> f.apply("1")).orElse(null)).toBeInstanceOf(clazz);
          expect(Reflections.getFactory(String.class, clazz).map(f -> f.apply("1").intValue()).orElse(0)).toEqual(1);
        });
      });
      Seq.of(UUID.class, Instant.class, LocalDate.class, LocalDateTime.class, String.class).forEach(clazz -> {
        it("should find a String factory for " + clazz.getName(), () -> {
          expect(Reflections.getFactory(String.class, clazz).isPresent()).toBeTrue();
        });
      });
      it("should instantiate an instance of UUID", () -> {
        expect(Reflections.getFactory(String.class, UUID.class).map(f -> f.apply("4B6AB747-ABE3-4035-BE3E-24DB07EBEDBD")).orElse(null)).toBeInstanceOf(UUID.class);
      });
      it("should instantiate an instance of Instant", () -> {
        expect(Reflections.getFactory(String.class, Instant.class).map(f -> f.apply("2018-03-06T17:05:17.856Z")).orElse(null)).toBeInstanceOf(Instant.class);
      });
      it("should instantiate an instance of LocalDate", () -> {
        expect(Reflections.getFactory(String.class, LocalDate.class).map(f -> f.apply("2018-03-06")).orElse(null)).toBeInstanceOf(LocalDate.class);
      });
      it("should instantiate an instance of LocalDateTime", () -> {
        expect(Reflections.getFactory(String.class, LocalDateTime.class).map(f -> f.apply("2018-03-06T18:07:12.020")).orElse(null)).toBeInstanceOf(LocalDateTime.class);
      });
    });
    describe("getFactory(T)", () -> {
      it("should create a factory for Thread", () -> {
        expect(Reflections.getFactory(Thread.class).isPresent()).toBeTrue();
      });
    });
    describe("getCommonBaseClass", () -> {
      it("should find the common base class of GZIPOutputStream and DataOutputStream", () -> {
        expect(Reflections.getCommonBaseClass(GZIPOutputStream.class, DataOutputStream.class))
          .toEqual(Optional.of(FilterOutputStream.class));
      });
    });
    describe("getBoxedClassFor", () -> {
      it("should return the boxed class if it does not have a primitve counter part", () -> {
        expect(Reflections.getBoxedClassFor(String.class)).toEqual(String.class);
      });
      it("should return the boxed class for the primitive type int", () -> {
        expect(Reflections.getBoxedClassFor(int.class)).toEqual(Integer.class);
      });
    });
    describe("getPrimitiveClassFor", () -> {
      it("should return the boxed class if it does not have a primitve counter part", () -> {
        expect(Reflections.getPrimitiveClassFor(String.class)).toEqual(String.class);
      });
      it("should return the primitive class for the boxed type java.lang.Integer", () -> {
        expect(Reflections.getPrimitiveClassFor(Integer.class)).toEqual(int.class);
      });
    });
  }

}

