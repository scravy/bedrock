package com.simplaex.bedrock;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@FunctionalInterface
public interface CharParser<T> {

  CharParser.Result<T> parse(final Seq<Character> seq);

  default CharParser.Result<T> parse(final String string) {
    return parse(Seq.wrap(string));
  }

  default <U> CharParser<U> map(final Function<T, U> f) {
    return seq -> parse(seq).map(f);
  }

  @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
  abstract class Result<E> {

    public abstract <F> CharParser.Result<F> map(final Function<E, F> f);

    public abstract Seq<Character> getRemaining();

    public abstract CharParser.Result<E> withRemaining(final Seq<Character> seq);

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
    <T> CharParser.Result<T> as() {
      return (CharParser.Result<T>) this;
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class Success<E> extends CharParser.Result<E> {

      private final E value;

      @Wither
      private final Seq<Character> remaining;

      @Override
      public <F> CharParser.Result<F> map(final Function<E, F> f) {
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

      @Wither
      private final Seq<Character> remaining;

      @SuppressWarnings("unchecked")
      @Override
      public <F> CharParser.Result<F> map(final Function<E, F> f) {
        return (CharParser.Result<F>) this;
      }

      @Override
      public boolean isNoParse() {
        return true;
      }
    }
  }

  /**
   * Creates a parser that parses p1 and then p2 but returns the result of p1 (the left one).
   *
   * @param p1  The left parser.
   * @param p2  The right parser.
   * @param <T> The type parsed by the left parser.
   * @param <U> The type parsed by the right parser.
   * @return A parser that parses p1 and then p2 but returns the result of p1 (the left one).
   */
  static <T, U> CharParser<T> left(final CharParser<T> p1, final CharParser<U> p2) {
    return seq -> {
      final CharParser.Result<T> r1 = p1.parse(seq);
      if (r1.isSuccess()) {
        final CharParser.Result<U> r2 = p2.parse(r1.getRemaining());
        if (r2.isSuccess()) {
          return new CharParser.Result.Success<>(r1.getValue(), r2.getRemaining());
        }
        return r2.withRemaining(seq).as();
      }
      return r1.withRemaining(seq);
    };
  }

  /**
   * Creates a parser that parses p1 and then p2 but returns the result of p2 (the right one).
   *
   * @param p1  The left parser.
   * @param p2  The right parser.
   * @param <T> The type parsed by the left parser.
   * @param <U> The type parsed by the right parser.
   * @return A parser that parses p1 and then p2 but returns the result of p2 (the right one).
   */
  static <T, U> CharParser<U> right(final CharParser<T> p1, final CharParser<U> p2) {
    return seq -> {
      final CharParser.Result<T> r1 = p1.parse(seq);
      if (r1.isSuccess()) {
        final CharParser.Result<U> r2 = p2.parse(r1.getRemaining());
        if (r2.isSuccess()) {
          return r2;
        }
        return r2.withRemaining(seq);
      }
      return r1.withRemaining(seq).as();
    };
  }

  static <S, T extends S, U extends S> CharParser<S> choice(final CharParser<T> p1, final CharParser<U> p2) {
    return seq -> {
      final CharParser.Result<T> r1 = p1.parse(seq);
      if (r1.isSuccess()) {
        return r1.as();
      }
      return p2.parse(seq).as();
    };
  }

  static <T, U> CharParser<Either<T, U>> either(final CharParser<T> p1, final CharParser<U> p2) {
    return seq -> {
      final CharParser.Result<Either<T, U>> r1 = p1.parse(seq).map(Either::left);
      if (r1.isSuccess()) {
        return r1;
      }
      return p2.parse(seq).map(Either::right);
    };
  }

  @SafeVarargs
  static <T> CharParser<T> oneOf(final CharParser<? extends T>... ps) {
    return seq -> {
      for (final CharParser<? extends T> p : ps) {
        final CharParser.Result<? extends T> result = p.parse(seq);
        if (result.isSuccess()) {
          return result.as();
        }
      }
      return new CharParser.Result.NoParse<>(seq);
    };
  }

  static CharParser<Character> anyOf(final String s) {
    return seq -> {
      if (seq.nonEmpty()) {
        final char c = seq.head();
        if (s.indexOf(c) >= 0) {
          return new CharParser.Result.Success<>(c, seq.tailView());
        }
      }
      return new Result.NoParse<>(seq);
    };
  }

  static CharParser<Character> noneOf(final String s) {
    return seq -> {
      if (seq.nonEmpty()) {
        final char c = seq.head();
        if (s.indexOf(c) < 0) {
          return new CharParser.Result.Success<>(c, seq.tailView());
        }
      }
      return new Result.NoParse<>(seq);
    };
  }

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

  static CharParser<String> string(final String string) {
    return seq -> {
      if (seq.length() < string.length()) {
        return new CharParser.Result.NoParse<>(seq);
      }
      for (int i = 0; i < string.length(); i += 1) {
        if (seq.get(i) != string.charAt(i)) {
          return new CharParser.Result.NoParse<>(seq);
        }
      }
      return new CharParser.Result.Success<>(string, seq.drop(string.length()));
    };
  }

  static CharParser<Character> satisfies(final Predicate<Character> predicate) {
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

  static <U> CharParser<U> satisfies2(final Function<Character, Optional<U>> f) {
    return seq -> {
      if (seq.nonEmpty()) {
        final Character c = seq.head();
        final Optional<U> result = f.apply(c);
        if (result.isPresent()) {
          return new CharParser.Result.Success<>(result.get(), seq.tailView());
        }
      }
      return new CharParser.Result.NoParse<>(seq);
    };
  }

  static <T> CharParser<Optional<T>> optional(final CharParser<T> parser) {
    return seq -> {
      final CharParser.Result<T> result = parser.parse(seq);
      if (result.isNoParse()) {
        return new CharParser.Result.Success<>(Optional.empty(), result.getRemaining());
      }
      return result.map(Optional::of);
    };
  }

  static <T> CharParser<T> option(final T fallback, final CharParser<T> parser) {
    return optional(parser).map(optional -> optional.orElse(fallback));
  }

  static <T> CharParser<T> option(final Supplier<T> fallbackSupplier, final CharParser<T> parser) {
    return optional(parser).map(optional -> optional.orElseGet(fallbackSupplier));
  }

  @SafeVarargs
  static <T> CharParser<Seq<T>> sequence(final CharParser<T>... ps) {
    final Seq<CharParser<T>> s = Seq.wrap(ps);
    return sequence(s);
  }

  static <T> CharParser<Seq<T>> sequence(final Iterable<CharParser<T>> ps) {
    return seq -> {
      final SeqBuilder<T> seqBuilder = Seq.builder();
      Seq<Character> remaining = seq;
      CharParser.Result<T> result;
      for (final CharParser<T> p : ps) {
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

  static <T> CharParser<Seq<T>> times(final int n, final CharParser<T> p) {
    return seq -> {
      final SeqBuilder<T> seqBuilder = Seq.builder();
      Seq<Character> remaining = seq;
      CharParser.Result<T> result;
      for (int i = 0; i < n; i += 1) {
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

  static <T, U> CharParser<Pair<T, U>> seq(
    final CharParser<T> p1,
    final CharParser<U> p2) {

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

  static <T, U, V> CharParser<Triple<T, U, V>> seq(
    final CharParser<T> p1,
    final CharParser<U> p2,
    final CharParser<V> p3) {

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

  static <T, U, V, W> CharParser<Quadruple<T, U, V, W>> seq(
    final CharParser<T> p1,
    final CharParser<U> p2,
    final CharParser<V> p3,
    final CharParser<W> p4) {

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

  static <T> CharParser<Seq<T>> many(final CharParser<T> parser) {
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

  static <T> CharParser<Seq<T>> many1(final CharParser<T> parser) {
    final CharParser<Seq<T>> manyCharParser = many(parser);
    return seq -> {
      final CharParser.Result<Seq<T>> result = manyCharParser.parse(seq);
      if (result.isSuccess() && result.getValue().isEmpty()) {
        return new CharParser.Result.NoParse<>(seq);
      }
      return result;
    };
  }

  static <T> CharParser<Void> skipMany(final CharParser<T> parser) {
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

  static <T, U> CharParser<Seq<T>> sepBy(final CharParser<T> parser, final CharParser<U> sep) {
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

  static <T, U> CharParser<Seq<T>> sepBy1(final CharParser<T> parser, final CharParser<U> sep) {
    final CharParser<Seq<T>> sepByCharParser = sepBy(parser, sep);
    return seq -> {
      final CharParser.Result<Seq<T>> result = sepByCharParser.parse(seq);
      if (result.isSuccess() && result.getValue().isEmpty()) {
        return new CharParser.Result.NoParse<>(seq);
      }
      return result;
    };
  }

  static <S, T, U> CharParser<T> between(final CharParser<S> s, final CharParser<T> t, final CharParser<U> u) {
    return seq(s, t, u).map(Triple::getSecond);
  }

  /**
   * Creates a parser that is constructed at first invocation for recursive invocation
   * of the parser.
   */
  static <T> CharParser<T> recursive(final Supplier<CharParser<T>> supplier) {
    final Supplier<CharParser<T>> parserSupplier = Control.memoizing(supplier);
    return seq -> parserSupplier.get().parse(seq);
  }
}
