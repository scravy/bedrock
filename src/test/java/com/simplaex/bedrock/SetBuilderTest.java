package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.Value;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.stream.Stream;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class SetBuilderTest {

  @Value
  private static class IteratorEnumeration<E> implements Enumeration<E> {
    private final Iterator<E> iterator;

    @Override
    public boolean hasMoreElements() {
      return iterator.hasNext();
    }

    @Override
    public E nextElement() {
      return iterator.next();
    }
  }

  {
    describe("Set.builder()", () -> {
      it("iterate", () -> {
        final java.util.Set<Integer> set = new TreeSet<>();
        Set.<Integer>builder()
          .addAll(1, 2, 3)
          .forEach(set::add);
        expect(set).toEqual(new TreeSet<>(Arrays.asList(1, 2, 3)));
      });

      it("addFromIterator", () -> {
        final java.util.Set<Integer> set = new TreeSet<>();
        Set.<Integer>builder()
          .addFromIterator(Arrays.asList(1, 2, 3).iterator())
          .forEach(set::add);
        expect(set).toEqual(new TreeSet<>(Arrays.asList(1, 2, 3)));
      });

      it("addFromEnumeration", () -> {
        final java.util.Set<Integer> set = new TreeSet<>();
        Set.<Integer>builder()
          .addFromEnumeration(new IteratorEnumeration<>(Arrays.asList(1, 2, 3).iterator()))
          .forEach(set::add);
        expect(set).toEqual(new TreeSet<>(Arrays.asList(1, 2, 3)));
      });
    });

    describe("collector", () -> {
      it("builder should collect values as collector", () -> {
        final Set<String> arrayMap = Stream.of(
          "one",
          "two"
        ).collect(Set.builder());
        expect(arrayMap).toEqual(Set.of("two", "one"));
      });
    });
  }

}
