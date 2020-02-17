package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.simplaex.bedrock.hlist.HList.hlist;

@RunWith(Spectrum.class)
@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
public class HListTest {
  private static <T> void apply(final T arg, final Consumer<T> consumer) {
    consumer.accept(arg);
  }

  private static <T, U> void apply(final T t, final U u, final BiConsumer<T, U> consumer) {
    consumer.accept(t, u);
  }

  {
    describe("HList", () -> {
      it("should toString()", () -> {
        apply(
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          ls -> expect(ls.toString()).toEqual("[1,2,3,4,5,6,7,8,9,10]"));
      });
      it("should equal itself", () -> {
        apply(
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          (l1, l2) -> expect(l1).toEqual(l2));
      });
      it("should compare equal to itself", () -> {
        apply(
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          (l1, l2) -> expect(l1.compareTo(l2)).toEqual(0));
      });
      it("should compare lexicographically", () -> {
        apply(
          hlist(0, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          (l1, l2) -> expect(l1.compareTo(l2)).toEqual(-1));
        apply(
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 11),
          (l1, l2) -> expect(l1.compareTo(l2)).toEqual(-1));
      });
    });
  }
}
