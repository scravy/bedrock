package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import de.scravy.bedrock.hlist.Nil;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static de.scravy.bedrock.Functions.call;
import static de.scravy.bedrock.Functions.flip;
import static de.scravy.bedrock.hlist.HList.hlist;
import static de.scravy.bedrock.hlist.HList.nil;

@RunWith(Spectrum.class)
@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
public class HListTest {
  {
    describe("HList", () -> {
      it("should toString()", () -> {
        call(
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          ls -> expect(ls.toString()).toEqual("[1,2,3,4,5,6,7,8,9,10]"));
      });
      it("should equal itself", () -> {
        call(
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11),
          (l1, l2) -> expect(l1).toEqual(l2));
      });
      it("should compare equal to itself", () -> {
        call(
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          (l1, l2) -> expect(l1.compareTo(l2)).toEqual(0));
      });
      it("should compare lexicographically", () -> {
        call(
          hlist(0, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          (l1, l2) -> expect(l1.compareTo(l2)).toEqual(-1));
        call(
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
          hlist(1, 2, 3, 4, 5, 6, 7, 8, 9, 11),
          (l1, l2) -> expect(l1.compareTo(l2)).toEqual(-1));
      });
      it("should foldl", () -> {
        call(
          hlist(1L, "2", 3),
          ls -> expect(ls.foldl(StringBuilder::append, new StringBuilder()).toString()).toEqual("123"));
      });
      it("should foldr", () -> {
        call(
          hlist(1L, 2, "3"),
          ls -> expect(ls.foldr(flip(StringBuilder::append), new StringBuilder()).toString()).toEqual("321"));
      });
      it("should calculate size correctly", () -> {
        call(nil(), ls -> expect(ls.size()).toEqual(0));
        call(Nil.cons("one"), ls -> expect(ls.size()).toEqual(1));
        call(Nil.cons("one").cons(123), ls -> expect(ls.size()).toEqual(2));
      });
    });
  }
}
