package com.simplaex.bedrock;

import lombok.*;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@FunctionalInterface
public interface CharParser<T> {

  CharParser.Result<T> parse(final Seq<Character> seq);

  default CharParser.Result<T> parse(@Nonnull final String string) {
    Objects.requireNonNull(string, "'string' must not be null");
    return parse(Seq.wrap(string));
  }

  default <U> CharParser<U> map(@Nonnull final Function<T, U> function) {
    Objects.requireNonNull(function, "'function' must not be null");
    return seq -> parse(seq).map(function);
  }

  @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
  abstract class Result<E> {

    @Nonnull
    public abstract <F> CharParser.Result<F> map(@Nonnull final Function<E, F> f);

    @Nonnull
    public abstract Seq<Character> getRemaining();

    @Nonnull
    public abstract CharParser.Result<E> withRemaining(@Nonnull final Seq<Character> seq);

    public E getValue() {
      return null;
    }

    public boolean isSuccess() {
      return false;
    }

    public boolean isNoParse() {
      return false;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    <T> CharParser.Result<T> as() {
      return (CharParser.Result<T>) this;
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class Success<E> extends CharParser.Result<E> {

      private final E value;

      @With
      private final Seq<Character> remaining;

      @Override
      @Nonnull
      public <F> CharParser.Result<F> map(@Nonnull final Function<E, F> f) {
        return new Success<>(f.apply(value), remaining);
      }

      @Override
      public boolean isSuccess() {
        return true;
      }
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class NoParse<E> extends CharParser.Result<E> {

      @With
      private final Seq<Character> remaining;

      @SuppressWarnings("unchecked")
      @Override
      @Nonnull
      public <F> CharParser.Result<F> map(@Nonnull final Function<E, F> f) {
        return (CharParser.Result<F>) this;
      }

      @Override
      public boolean isNoParse() {
        return true;
      }
    }
  }

  /**
   * Creates a parser that parses leftParser and then rightParser but returns the result of leftParser (the left one).
   *
   * @param leftParser  The left parser.
   * @param rightParser The right parser.
   * @param <T>         The type parsed by the left parser.
   * @param <U>         The type parsed by the right parser.
   * @return A parser that parses leftParser and then rightParser but returns the result of leftParser (the left one).
   */
  @Nonnull
  static <T, U> CharParser<T> left(
    @Nonnull final CharParser<T> leftParser,
    @Nonnull final CharParser<U> rightParser
  ) {
    Objects.requireNonNull(leftParser, "'leftParser' must not be null.");
    Objects.requireNonNull(rightParser, "'rightParser' must not be null.");
    return seq -> {
      final CharParser.Result<T> r1 = leftParser.parse(seq);
      if (r1.isSuccess()) {
        final CharParser.Result<U> r2 = rightParser.parse(r1.getRemaining());
        if (r2.isSuccess()) {
          return new CharParser.Result.Success<>(r1.getValue(), r2.getRemaining());
        }
        return r2.withRemaining(seq).as();
      }
      return r1.withRemaining(seq);
    };
  }

  /**
   * Creates a parser that parses leftParser and then rightParser but returns the result of rightParser (the right one).
   *
   * @param leftParser  The left parser.
   * @param rightParser The right parser.
   * @param <T>         The type parsed by the left parser.
   * @param <U>         The type parsed by the right parser.
   * @return A parser that parses leftParser and then rightParser but returns the result of rightParser (the right one).
   */
  @Nonnull
  static <T, U> CharParser<U> right(
    @Nonnull final CharParser<T> leftParser,
    @Nonnull final CharParser<U> rightParser
  ) {
    Objects.requireNonNull(leftParser, "'leftParser' must not be null.");
    Objects.requireNonNull(rightParser, "'rightParser' must not be null.");
    return seq -> {
      final CharParser.Result<T> r1 = leftParser.parse(seq);
      if (r1.isSuccess()) {
        final CharParser.Result<U> r2 = rightParser.parse(r1.getRemaining());
        if (r2.isSuccess()) {
          return r2;
        }
        return r2.withRemaining(seq);
      }
      return r1.withRemaining(seq).as();
    };
  }

  @Nonnull
  static <S, T extends S, U extends S> CharParser<S> choice(
    @Nonnull final CharParser<T> leftParser,
    @Nonnull final CharParser<U> rightParser
  ) {
    Objects.requireNonNull(leftParser, "'leftParser' must not be null.");
    Objects.requireNonNull(rightParser, "'rightParser' must not be null.");
    return seq -> {
      final CharParser.Result<T> r1 = leftParser.parse(seq);
      if (r1.isSuccess()) {
        return r1.as();
      }
      return rightParser.parse(seq).as();
    };
  }

  @Nonnull
  static <T, U> CharParser<Either<T, U>> either(
    @Nonnull final CharParser<T> leftParser,
    @Nonnull final CharParser<U> rightParser
  ) {
    Objects.requireNonNull(leftParser, "'leftParser' must not be null.");
    Objects.requireNonNull(rightParser, "'rightParser' must not be null.");
    return seq -> {
      final CharParser.Result<Either<T, U>> r1 = leftParser.parse(seq).map(Either::left);
      if (r1.isSuccess()) {
        return r1;
      }
      return rightParser.parse(seq).map(Either::right);
    };
  }

  @Nonnull
  @SafeVarargs
  static <T> CharParser<T> oneOf(@Nonnull final CharParser<? extends T>... parsers) {
    Objects.requireNonNull(parsers, "'parsers' must not be null.");
    if (parsers.length == 1) {
      @SuppressWarnings("unchecked") final CharParser<T> parser = (CharParser<T>) parsers[0];
      return parser;
    }
    return seq -> {
      for (final CharParser<? extends T> p : parsers) {
        final CharParser.Result<? extends T> result = p.parse(seq);
        if (result.isSuccess()) {
          return result.as();
        }
      }
      return new CharParser.Result.NoParse<>(seq);
    };
  }

  @Nonnull
  static CharParser<Character> anyOf(@Nonnull final String string) {
    Objects.requireNonNull(string, "'string' must not be null.");
    if (string.isEmpty()) {
      return Result.NoParse::new;
    }
    if (string.length() == 1) {
      return character(string.charAt(0));
    }
    final char[] chars = string.toCharArray();
    Arrays.sort(chars);
    char c = chars[0];
    int i = 1;
    while (i < chars.length) {
      if (c + 1 != chars[i]) {
        break;
      }
      c = chars[i++];
    }
    if (i == chars.length) { // loop did not exit before exit condition
      return range(chars[0], chars[chars.length - 1]);
    }
    return seq -> {
      if (seq.nonEmpty()) {
        final char chr = seq.head();
        if (Arrays.binarySearch(chars, chr) >= 0) {
          return new CharParser.Result.Success<>(chr, seq.tailView());
        }
      }
      return new Result.NoParse<>(seq);
    };
  }

  @Nonnull
  static CharParser<Character> noneOf(@Nonnull final String string) {
    Objects.requireNonNull(string, "'string' must not be null.");
    return seq -> {
      if (seq.nonEmpty()) {
        final char c = seq.head();
        if (string.indexOf(c) < 0) {
          return new CharParser.Result.Success<>(c, seq.tailView());
        }
      }
      return new Result.NoParse<>(seq);
    };
  }

  @Nonnull
  static CharParser<Character> character(final char character) {
    return seq -> {
      if (seq.nonEmpty()) {
        final Character c = seq.head();
        if (c == character) {
          return new CharParser.Result.Success<>(c, seq.tailView());
        }
      }
      return new CharParser.Result.NoParse<>(seq);
    };
  }

  @Nonnull
  static CharParser<Character> range(final char lower, final char upper) {
    return seq -> {
      if (seq.nonEmpty()) {
        final Character c = seq.head();
        if (c >= lower && c <= upper) {
          return new CharParser.Result.Success<>(c, seq.tailView());
        }
      }
      return new CharParser.Result.NoParse<>(seq);
    };
  }

  @Nonnull
  static CharParser<String> string(@Nonnull final String string) {
    Objects.requireNonNull(string, "'string' must not be null.");
    return seq -> {
      if (seq.length() < string.length()) {
        return new CharParser.Result.NoParse<>(seq);
      }
      for (int i = 0; i < string.length(); i += 1) {
        if (seq.get(i) != string.charAt(i)) {
          return new CharParser.Result.NoParse<>(seq);
        }
      }
      return new CharParser.Result.Success<>(string, seq.dropView(string.length()));
    };
  }

  @Nonnull
  static CharParser<Character> satisfies(@Nonnull final Predicate<Character> predicate) {
    Objects.requireNonNull(predicate, "'predicate' must not be null.");
    return seq -> {
      if (seq.nonEmpty()) {
        final Character c = seq.head();
        if (predicate.test(c)) {
          return new CharParser.Result.Success<>(c, seq.tailView());
        }
      }
      return new CharParser.Result.NoParse<>(seq);
    };
  }

  @Nonnull
  static <U> CharParser<U> satisfies2(@Nonnull final Function<Character, Optional<U>> function) {
    Objects.requireNonNull(function, "'function' must not be null.");
    return seq -> {
      if (seq.nonEmpty()) {
        final Character c = seq.head();
        final Optional<U> result = function.apply(c);
        if (result.isPresent()) {
          return new CharParser.Result.Success<>(result.get(), seq.tailView());
        }
      }
      return new CharParser.Result.NoParse<>(seq);
    };
  }

  @Nonnull
  static <T> CharParser<Optional<T>> optional(@Nonnull final CharParser<T> parser) {
    Objects.requireNonNull(parser, "'parser' must not be null.");
    return seq -> {
      final CharParser.Result<T> result = parser.parse(seq);
      if (result.isNoParse()) {
        return new CharParser.Result.Success<>(Optional.empty(), result.getRemaining());
      }
      return result.map(Optional::of);
    };
  }

  @Nonnull
  static <T> CharParser<T> option(
    final T fallback,
    @Nonnull final CharParser<T> parser
  ) {
    Objects.requireNonNull(parser, "'parser' must not be null.");
    return optional(parser).map(optional -> optional.orElse(fallback));
  }

  @Nonnull
  static <T> CharParser<T> optionOrGet(
    @Nonnull final Supplier<T> fallbackSupplier,
    @Nonnull final CharParser<T> parser
  ) {
    Objects.requireNonNull(fallbackSupplier, "'fallbackSupplier' must not be null.");
    Objects.requireNonNull(parser, "'parser' must not be null.");
    return optional(parser).map(optional -> optional.orElseGet(fallbackSupplier));
  }

  @Nonnull
  @SafeVarargs
  static <T> CharParser<Seq<T>> sequence(@Nonnull final CharParser<T>... parsers) {
    Objects.requireNonNull(parsers, "'parsers' must not be null");
    if (parsers.length == 1) {
      return parsers[0].map(Seq::of);
    }
    final Seq<CharParser<T>> s = Seq.wrap(parsers);
    return sequence(s);
  }

  @Nonnull
  static <T> CharParser<Seq<T>> sequence(@Nonnull final Iterable<CharParser<T>> parsers) {
    Objects.requireNonNull(parsers, "'parsers' must not be null");
    if (parsers instanceof Collection && ((Collection<CharParser<T>>) parsers).size() == 1
      || (parsers instanceof HasLength && ((HasLength) parsers).length() == 1)) {
      return parsers.iterator().next().map(Seq::of);
    }
    return seq -> {
      final SeqBuilder<T> seqBuilder = Seq.builder();
      Seq<Character> remaining = seq;
      CharParser.Result<T> result;
      for (final CharParser<T> p : parsers) {
        if (remaining.isEmpty()) {
          return new CharParser.Result.NoParse<>(seq);
        }
        result = p.parse(remaining);
        if (!result.isSuccess()) {
          return result.withRemaining(seq).as();
        }
        remaining = result.getRemaining();
        seqBuilder.add(result.getValue());
      }
      return new CharParser.Result.Success<>(seqBuilder.result(), remaining);
    };
  }

  @Nonnull
  static <T> CharParser<Seq<T>> times(@Nonnegative final int n, @Nonnull final CharParser<T> parser) {
    Objects.requireNonNull(parser, "'parser' must not be null.");
    return seq -> {
      final SeqBuilder<T> seqBuilder = Seq.builder();
      Seq<Character> remaining = seq;
      CharParser.Result<T> result;
      for (int i = 0; i < n; i += 1) {
        if (remaining.isEmpty()) {
          return new CharParser.Result.NoParse<>(seq);
        }
        result = parser.parse(remaining);
        if (!result.isSuccess()) {
          return result.withRemaining(seq).as();
        }
        remaining = result.getRemaining();
        seqBuilder.add(result.getValue());
      }
      return new CharParser.Result.Success<>(seqBuilder.result(), remaining);
    };
  }

  @Nonnull
  static <T, U> CharParser<Pair<T, U>> seq(
    @Nonnull final CharParser<T> p1,
    @Nonnull final CharParser<U> p2
  ) {
    Objects.requireNonNull(p1, "'p1' must not be null.");
    Objects.requireNonNull(p2, "'p2' must not be null.");
    return seq -> {
      final CharParser.Result<T> r1 = p1.parse(seq);
      if (r1.isSuccess()) {
        final CharParser.Result<U> r2 = p2.parse(r1.getRemaining());
        if (r2.isSuccess()) {
          return new CharParser.Result.Success<>(Pair.of(r1.getValue(), r2.getValue()), r2.getRemaining());
        }
        return r2.withRemaining(seq).as();
      }
      return r1.withRemaining(seq).as();
    };
  }

  @Nonnull
  static <T, U, V> CharParser<Triple<T, U, V>> seq(
    @Nonnull final CharParser<T> p1,
    @Nonnull final CharParser<U> p2,
    @Nonnull final CharParser<V> p3
  ) {
    Objects.requireNonNull(p1, "'p1' must not be null.");
    Objects.requireNonNull(p2, "'p2' must not be null.");
    Objects.requireNonNull(p3, "'p3' must not be null.");
    return seq -> {
      final CharParser.Result<T> r1 = p1.parse(seq);
      if (r1.isSuccess()) {
        final CharParser.Result<U> r2 = p2.parse(r1.getRemaining());
        if (r2.isSuccess()) {
          final CharParser.Result<V> r3 = p3.parse(r2.getRemaining());
          if (r3.isSuccess()) {
            return new CharParser.Result.Success<>(
              Triple.of(r1.getValue(), r2.getValue(), r3.getValue()), r3.getRemaining()
            );
          }
          return r3.withRemaining(seq).as();
        }
        return r2.withRemaining(seq).as();
      }
      return r1.withRemaining(seq).as();
    };
  }

  @Nonnull
  static <T, U, V, W> CharParser<Quadruple<T, U, V, W>> seq(
    @Nonnull final CharParser<T> p1,
    @Nonnull final CharParser<U> p2,
    @Nonnull final CharParser<V> p3,
    @Nonnull final CharParser<W> p4
  ) {
    Objects.requireNonNull(p1, "'p1' must not be null.");
    Objects.requireNonNull(p2, "'p2' must not be null.");
    Objects.requireNonNull(p3, "'p3' must not be null.");
    Objects.requireNonNull(p4, "'p4' must not be null.");
    return seq -> {
      final CharParser.Result<T> r1 = p1.parse(seq);
      if (r1.isSuccess()) {
        final CharParser.Result<U> r2 = p2.parse(r1.getRemaining());
        if (r2.isSuccess()) {
          final CharParser.Result<V> r3 = p3.parse(r2.getRemaining());
          if (r3.isSuccess()) {
            final CharParser.Result<W> r4 = p4.parse(r3.getRemaining());
            if (r4.isSuccess()) {
              return new CharParser.Result.Success<>(
                Quadruple.of(r1.getValue(), r2.getValue(), r3.getValue(), r4.getValue()), r4.getRemaining()
              );
            }
            return r4.withRemaining(seq).as();
          }
          return r3.withRemaining(seq).as();
        }
        return r2.withRemaining(seq).as();
      }
      return r1.withRemaining(seq).as();
    };
  }

  @Nonnull
  static <T> CharParser<Seq<T>> many(@Nonnull final CharParser<T> parser) {
    Objects.requireNonNull(parser, "'parser' must not be null.");
    return seq -> {
      final SeqBuilder<T> resultBuilder = Seq.builder();
      Seq<Character> remaining = seq;
      while (remaining.nonEmpty()) {
        final CharParser.Result<T> result = parser.parse(remaining);
        if (!result.isSuccess()) {
          return new CharParser.Result.Success<>(resultBuilder.result(), remaining);
        }
        resultBuilder.add(result.getValue());
        remaining = result.getRemaining();
      }
      return new CharParser.Result.Success<>(resultBuilder.result(), remaining);
    };
  }

  @Nonnull
  static <T> CharParser<Seq<T>> many1(@Nonnull final CharParser<T> parser) {
    Objects.requireNonNull(parser, "'parser' must not be null.");
    final CharParser<Seq<T>> manyCharParser = many(parser);
    return seq -> {
      final CharParser.Result<Seq<T>> result = manyCharParser.parse(seq);
      if (result.isSuccess() && result.getValue().isEmpty()) {
        return new CharParser.Result.NoParse<>(seq);
      }
      return result;
    };
  }

  @Nonnull
  static <T> CharParser<Void> skipMany(@Nonnull final CharParser<T> parser) {
    Objects.requireNonNull(parser, "'parser' must not be null.");
    return seq -> {
      Seq<Character> remaining = seq;
      while (remaining.nonEmpty()) {
        final CharParser.Result<T> result = parser.parse(remaining);
        if (!result.isSuccess()) {
          return new CharParser.Result.Success<>(null, remaining);
        }
        remaining = result.getRemaining();
      }
      return new CharParser.Result.Success<>(null, remaining);
    };
  }

  @Nonnull
  static <T, U> CharParser<Seq<T>> sepBy(
    @Nonnull final CharParser<T> parser,
    @Nonnull final CharParser<U> sep
  ) {
    Objects.requireNonNull(parser, "'parser' must not be null.");
    Objects.requireNonNull(sep, "'sep' must not be null.");
    final CharParser<T> p = right(sep, parser);
    return seq -> {
      final SeqBuilder<T> resultBuilder = Seq.builder();
      Seq<Character> remaining = seq;
      CharParser.Result<T> result = parser.parse(remaining);
      if (result.isSuccess()) {
        resultBuilder.add(result.getValue());
        remaining = result.getRemaining();
        while (remaining.nonEmpty()) {
          result = p.parse(remaining);
          if (!result.isSuccess()) {
            return new CharParser.Result.Success<>(resultBuilder.result(), remaining);
          }
          resultBuilder.add(result.getValue());
          remaining = result.getRemaining();
        }
      }
      return new CharParser.Result.Success<>(resultBuilder.result(), remaining);
    };
  }

  @Nonnull
  static <T, U> CharParser<Seq<T>> sepBy1(
    @Nonnull final CharParser<T> parser,
    @Nonnull final CharParser<U> sep
  ) {
    Objects.requireNonNull(parser, "'parser' must not be null.");
    Objects.requireNonNull(sep, "'sep' must not be null.");
    final CharParser<Seq<T>> sepByCharParser = sepBy(parser, sep);
    return seq -> {
      final CharParser.Result<Seq<T>> result = sepByCharParser.parse(seq);
      if (result.isSuccess() && result.getValue().isEmpty()) {
        return new CharParser.Result.NoParse<>(seq);
      }
      return result;
    };
  }

  @Nonnull
  static <S, T, U> CharParser<T> between(
    @Nonnull final CharParser<S> s,
    @Nonnull final CharParser<T> t,
    @Nonnull final CharParser<U> u
  ) {
    Objects.requireNonNull(s, "'s' must not be null.");
    Objects.requireNonNull(t, "'t' must not be null.");
    Objects.requireNonNull(u, "'u' must not be null.");
    return seq(s, t, u).map(Triple::getSecond);
  }

  /**
   * Creates a parser that is constructed at first invocation for recursive invocation
   * of the parser.
   */
  @Nonnull
  static <T> CharParser<T> recursive(@Nonnull final Supplier<CharParser<T>> supplier) {
    Objects.requireNonNull(supplier, "'supplier' must not be null.");
    final Supplier<CharParser<T>> parserSupplier = Control.memoizing(supplier);
    return seq -> parserSupplier.get().parse(seq);
  }
}
