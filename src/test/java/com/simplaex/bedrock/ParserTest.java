package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Predicate;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.simplaex.bedrock.Parser.*;
import static com.simplaex.bedrock.Functions.*;

@SuppressWarnings("CodeBlock2Expr")
@RunWith(Spectrum.class)
public class ParserTest {

  final Seq<Integer> seq = Seq.rangeInclusive(1, 10);

  private Parser<Integer> eq(final int x) {
    return satisfies(Integer.class, z -> z == x);
  }

  private Parser<Integer> lt(final int x) {
    return satisfies(Integer.class, z -> z < x);
  }

  private Parser<Integer> gt(final int x) {
    return satisfies(Integer.class, z -> z > x);
  }

  private Parser<Integer> even() {
    return satisfies(Integer.class, z -> z % 2 == 0);
  }

  private Parser<Integer> odd() {
    return satisfies(Integer.class, z -> z % 2 != 0);
  }

  {
    describe("parser primitives", () -> {
      describe("left", () -> {
        it("success", () -> {
          val p = left(lt(5), lt(5));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(1);
        });
        it("no parse", () -> {
          val p = left(gt(7), lt(5));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeFalse();
        });
      });
      describe("right", () -> {
        it("success", () -> {
          val p = right(lt(5), lt(5));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(2);
        });
        it("no parse", () -> {
          val p = right(gt(5), lt(5));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeFalse();
        });
      });
      describe("optional", () -> {
        it("success", () -> {
          val p = optional(lt(5));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(Optional.of(1));
        });
        it("no parse, but success", () -> {
          val p = optional(gt(5));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(Optional.empty());
        });
      });
      describe("choice", () -> {
        it("success", () -> {
          val p = choice(
            even().map(x -> "not odd"),
            odd().map(x -> "not even")
          );
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual("not even");
        });
        it("success (switched order of parser)", () -> {
          val p = choice(
            odd().map(x -> "not even"),
            even().map(x -> "not odd")
          );
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual("not even");
        });
        it("no parse", () -> {
          val p = choice(
            eq(2),
            eq(3)
          );
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeFalse();
        });
      });
      describe("oneOf", () -> {
        it("success", () -> {
          val p = oneOf(
            eq(0),
            eq(1),
            eq(2)
          );
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(1);
          expect(r.getRemaining()).toEqual(Seq.rangeInclusive(2, 10));
        });
        it("success (2)", () -> {
          val p = many(oneOf(
            eq(0),
            eq(1),
            eq(2),
            eq(3)
          ));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(Seq.rangeInclusive(1, 3));
          expect(r.getRemaining()).toEqual(Seq.rangeInclusive(4, 10));
        });
      });
      describe("sequence", () -> {
        it("success", () -> {
          val p = sequence(eq(1), eq(2), eq(3), eq(4), eq(5));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(Seq.of(1, 2, 3, 4, 5));
        });
        it("no parse", () -> {
          val p = sequence(eq(1), eq(2), eq(30), eq(4), eq(5));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeFalse();
        });
        it("no parse (2)", () -> {
          val p = sequence(eq(1), eq(2), eq(3), eq(4), eq(5));
          val r = p.parse(Seq.rangeInclusive(1, 3));
          expect(r.isSuccess()).toBeFalse();
          expect(r.getRemaining()).toEqual(Seq.rangeInclusive(1, 3));
        });
      });
      describe("seq2", () -> {
        it("success", () -> {
          val p = seq(eq(1), eq(2));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(Pair.of(1, 2));
        });
        it("no parse", () -> {
          val p = seq(eq(2), eq(2));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeFalse();
        });
        it("no parse (2)", () -> {
          val p = seq(eq(1), eq(3));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeFalse();
        });
      });
      describe("seq3", () -> {
        it("success", () -> {
          val p = seq(eq(1), eq(2), eq(3));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(Triple.of(1, 2, 3));
        });
        it("no parse", () -> {
          val p = seq(eq(2), eq(2), eq(2));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeFalse();
        });
        it("no parse (2)", () -> {
          val p = seq(eq(1), eq(3), eq(2));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeFalse();
        });
        it("no parse (3)", () -> {
          val p = seq(eq(1), eq(2), eq(2));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeFalse();
        });
      });
      describe("seq4", () -> {
        it("success", () -> {
          val p = seq(eq(1), eq(2), eq(3), eq(4));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(Quadruple.of(1, 2, 3, 4));
        });
      });
      describe("times", () -> {
        it("success", () -> {
          val p = times(5, lt(15));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(Seq.rangeInclusive(1, 5));
          expect(r.getRemaining()).toEqual(Seq.rangeInclusive(6, 10));
        });
        it("no parse", () -> {
          val p = times(7, lt(3));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeFalse();
          expect(r.getRemaining()).toEqual(seq);
        });
        it("no parse", () -> {
          val p = times(15, lt(20));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeFalse();
          expect(r.getRemaining()).toEqual(seq);
        });
      });
      describe("many", () -> {
        it("success", () -> {
          val p = many(lt(5));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(Seq.rangeInclusive(1, 4));
        });
        it("success (2)", () -> {
          val p = many(lt(20));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(Seq.rangeInclusive(1, 10));
        });
        it("no parse, but success", () -> {
          val p = many(gt(5));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(Seq.empty());
        });
      });
      describe("many1", () -> {
        it("success", () -> {
          val p = many1(lt(5));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(Seq.rangeInclusive(1, 4));
        });
        it("no parse", () -> {
          val p = many1(gt(5));
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeFalse();
        });
      });
      describe("sepBy", () -> {
        it("success", () -> {
          val p = sepBy(lt(6), even());
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(Seq.of(1, 3, 5));
        });
      });
      describe("sepBy1", () -> {
        it("success", () -> {
          val p = sepBy1(lt(6), even());
          val r = p.parse(seq);
          expect(r.isSuccess()).toBeTrue();
          expect(r.getValue()).toEqual(Seq.of(1, 3, 5));
        });
      });
      describe("recurse", () -> {
        it("should parse a tree structure", () -> {
          class X {
            private Parser<Integer> p() {
              return many(
                choice(
                  satisfies(Integer.class, x -> true),
                  recurse(Seq.class, x -> true, x -> x, recursive(this::p))
                )
              ).map(Seq::intSum);
            }
          }
          val x = new X();
          val seq = Seq.of(5, Seq.of(7, 13, Seq.of(5), Seq.of(5, 5)));
          val res = x.p().parse(seq);
          expect(res.isSuccess()).toBeTrue();
          expect(res.getValue()).toEqual(40);
        });
      });
      describe("recurse2", () -> {
        it("should parse a tree structure", () -> {
          class X {
            private Parser<Integer> p() {
              return many(
                choice(
                  satisfies(Integer.class, x -> true),
                  recurse2(Seq.class, x -> x, x -> recursive(this::p).map(z -> z + x.length()))
                )
              ).map(Seq::intSum);
            }
          }
          val x = new X();
          val seq = Seq.of(5, Seq.of(7, 13, Seq.of(5), Seq.of(5, 5)));
          val res = x.p().parse(seq);
          expect(res.isSuccess()).toBeTrue();
          expect(res.getValue()).toEqual(47);
        });
      });
    });
    describe("real world example", () -> {
      it("should parse json", () -> {
        @SuppressWarnings("WeakerAccess")
        class P {

          Parser<Character> character(final Predicate<Character> predicate) {
            return satisfies(Character.class, predicate);
          }

          Predicate<Character> eq(final char character) {
            return x -> x == character;
          }

          Predicate<Character> not(final Predicate<Character> predicate) {
            return predicate.negate();
          }

          Predicate<Character> any() {
            return x -> true;
          }

          Parser<String> string(final String string) {
            return sequence(Seq.ofString(string).map(this::eq).map(this::character)).map(Seq::asString);
          }

          Parser<Character> openingBrace = character(eq('{'));

          Parser<Character> closingBrace = character(eq('}'));

          Parser<Character> openingBracket = character(eq('['));

          Parser<Character> closingBracket = character(eq(']'));

          Parser<Character> colon = character(eq(':'));

          Parser<Character> comma = character(eq(','));

          Parser<String> string =
            seq(
              character(eq('"')),
              many(
                choice(
                  character(not(or(eq('\\'), eq('\"')))),
                  right(
                    character(eq('\\')),
                    character(any())
                  )
                )
              ),
              character(eq('"'))
            ).map(t -> t.getSecond().asString());

          Parser<BigDecimal> number =
            many1(character(Character::isDigit)).map(Seq::asString).map(BigDecimal::new);

          Parser<Boolean> bool =
            choice(
              string("true"),
              string("false")
            ).map(Boolean::valueOf);

          Parser<Void> nil = string("null").map(Functions.constant(null));

          Parser<Object> jsonValue() {
            return oneOf(
              string,
              number,
              bool,
              nil,
              array,
              object
            );
          }

          Parser<Seq<Object>> array = seq(
            openingBracket,
            sepBy(
              recursive(this::jsonValue),
              comma
            ),
            closingBracket
          ).map(Triple::getSecond);

          Parser<Pair<String, Object>> keyValuePair = seq(
            string,
            colon,
            recursive(this::jsonValue)
          ).map(t -> Pair.of(t.getFirst(), t.getThird()));

          Parser<Seq<Pair<String, Object>>> object = seq(
            openingBrace,
            sepBy(
              keyValuePair,
              comma
            ),
            closingBrace
          ).map(Triple::getSecond);

          Parser<Object> json = choice(
            array,
            object
          );

        }

        val p = new P();
        val r = p.json.parse(Seq.ofString("{\"glossary\":{\"title\":\"example glossary\",\"GlossDiv\":{\"title\":\"S\",\"GlossList\":{\"GlossEntry\":{\"ID\":\"SGML\",\"SortAs\":\"SGML\",\"GlossTerm\":\"Standard Generalized Markup Language\",\"Acronym\":\"SGML\",\"Abbrev\":\"ISO 8879:1986\",\"GlossDef\":{\"para\":\"A meta-markup language, used to create markup languages such as DocBook.\",\"GlossSeeAlso\":[\"GML\",\"XML\"]},\"GlossSee\":\"markup\"}}}}}"));
        expect(r.isSuccess()).toBeTrue();
        expect(r.getValue()).toEqual(Seq.of(
          Pair.<String, Object>of("glossary",
            Seq.of(
              Pair.<String, Object>of("title", "example glossary"),
              Pair.<String, Object>of("GlossDiv",
                Seq.of(
                  Pair.<String, Object>of("title", "S"),
                  Pair.<String, Object>of("GlossList",
                    Seq.of(
                      Pair.<String, Object>of("GlossEntry",
                        Seq.of(
                          Pair.<String, Object>of("ID", "SGML"),
                          Pair.<String, Object>of("SortAs", "SGML"),
                          Pair.<String, Object>of("GlossTerm", "Standard Generalized Markup Language"),
                          Pair.<String, Object>of("Acronym", "SGML"),
                          Pair.<String, Object>of("Abbrev", "ISO 8879:1986"),
                          Pair.<String, Object>of("GlossDef",
                            Seq.of(
                              Pair.<String, Object>of(
                                "para",
                                "A meta-markup language, used to create markup languages such as DocBook."
                              ),
                              Pair.<String, Object>of("GlossSeeAlso",
                                Seq.of(
                                  "GML",
                                  "XML"
                                )
                              )
                            )
                          ),
                          Pair.<String, Object>of("GlossSee", "markup")
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        ));
      });
    });
  }
}
