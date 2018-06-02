package com.simplaex.bedrock;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Wither;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.function.*;

@FunctionalInterface
public interface Parser<T> {

  Result<T> parse(final Seq<?> seq);

  default <U> Parser<U> map(final Function<T, U> f) {
    return seq -> parse(seq).map(f);
  }

  @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
  abstract class Result<E> {

    public abstract <F> Result<F> map(final Function<E, F> f);

    public abstract Seq<?> getRemaining();

    public abstract Result<E> withRemaining(final Seq<?> seq);

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
    <T> Result<T> as() {
      return (Result<T>) this;
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class Success<E> extends Result<E> {

      private final E value;

      @Wither
      private final Seq<?> remaining;

      @Override
      public <F> Result<F> map(final Function<E, F> f) {
        return new Success<>(f.apply(value), remaining);
      }

      @Override
      public boolean isSuccess() {
        return true;
      }
    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    public static class NoParse<E> extends Result<E> {

      @Wither
      private final Seq<?> remaining;

      @SuppressWarnings("unchecked")
      @Override
      public <F> Result<F> map(final Function<E, F> f) {
        return (Result<F>) this;
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
  static <T, U> Parser<T> left(final Parser<T> p1, final Parser<U> p2) {
    return seq -> {
      final Result<T> r1 = p1.parse(seq);
      if (r1.isSuccess()) {
        final Result<U> r2 = p2.parse(r1.getRemaining());
        if (r2.isSuccess()) {
          return new Result.Success<>(r1.getValue(), r2.getRemaining());
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
  static <T, U> Parser<U> right(final Parser<T> p1, final Parser<U> p2) {
    return seq -> {
      final Result<T> r1 = p1.parse(seq);
      if (r1.isSuccess()) {
        final Result<U> r2 = p2.parse(r1.getRemaining());
        if (r2.isSuccess()) {
          return r2;
        }
        return r2.withRemaining(seq);
      }
      return r1.withRemaining(seq).as();
    };
  }

  static <S, T extends S, U extends S> Parser<S> choice(final Parser<T> p1, final Parser<U> p2) {
    return seq -> {
      final Result<T> r1 = p1.parse(seq);
      if (r1.isSuccess()) {
        return r1.as();
      }
      return p2.parse(seq).as();
    };
  }

  static <T, U> Parser<Either<T, U>> either(final Parser<T> p1, final Parser<U> p2) {
    return seq -> {
      final Result<Either<T, U>> r1 = p1.parse(seq).map(Either::left);
      if (r1.isSuccess()) {
        return r1;
      }
      return p2.parse(seq).map(Either::right);
    };
  }

  @SafeVarargs
  static <T> Parser<T> oneOf(final Parser<? extends T>... ps) {
    return seq -> {
      for (final Parser<? extends T> p : ps) {
        final Result<? extends T> result = p.parse(seq);
        if (result.isSuccess()) {
          return result.as();
        }
      }
      return new Result.NoParse<>(seq);
    };
  }

  static <T> Parser<T> satisfies(final Class<T> clazz, final Predicate<T> predicate) {
    return seq -> {
      if (seq.nonEmpty()) {
        final Object obj = seq.head();
        if (clazz.isAssignableFrom(obj.getClass())) {
          @SuppressWarnings("unchecked") final T t = (T) obj;
          if (predicate.test(t)) {
            return new Result.Success<>(t, seq.tailView());
          }
        }
      }
      return new Result.NoParse<>(seq);
    };
  }

  static <T, U> Parser<U> satisfies2(final Class<T> clazz, final Function<T, Optional<U>> f) {
    return seq -> {
      if (seq.nonEmpty()) {
        final Object obj = seq.head();
        if (clazz.isAssignableFrom(obj.getClass())) {
          @SuppressWarnings("unchecked") final T t = (T) obj;
          final Optional<U> result = f.apply(t);
          if (result.isPresent()) {
            return new Result.Success<>(result.get(), seq.tailView());
          }
        }
      }
      return new Result.NoParse<>(seq);
    };
  }

  static <T, U> Parser<U> recurse(
    final Class<T> clazz,
    final Predicate<T> predicate,
    final Function<T, Seq<?>> extractor,
    final Parser<U> parser) {

    return seq -> {
      if (seq.nonEmpty()) {
        final Object obj = seq.head();
        if (clazz.isAssignableFrom(obj.getClass())) {
          @SuppressWarnings("unchecked") final T t = (T) obj;
          if (predicate.test(t)) {
            final Seq<?> rseq = extractor.apply(t);
            final Result<U> result = parser.parse(rseq);
            if (result.isSuccess()) {
              if (result.getRemaining().nonEmpty()) {
                return new Result.NoParse<>(seq);
              }
              return result.withRemaining(seq.tailView());
            }
            return result.withRemaining(seq);
          }
        }
      }
      return new Result.NoParse<>(seq);
    };
  }

  static <T, U> Parser<U> recurse2(
    final Class<T> clazz,
    final Function<T, Seq<?>> extractor,
    final Function<T, Parser<U>> parser) {

    return seq -> {
      if (seq.nonEmpty()) {
        final Object obj = seq.head();
        if (clazz.isAssignableFrom(obj.getClass())) {
          @SuppressWarnings("unchecked") final T t = (T) obj;
          final Seq<?> rseq = extractor.apply(t);
          final Result<U> result = parser.apply(t).parse(rseq);
          if (result.isSuccess()) {
            if (result.getRemaining().nonEmpty()) {
              return new Result.NoParse<>(seq);
            }
            return result.withRemaining(seq.tailView());
          }
          return result.withRemaining(seq);

        }
      }
      return new Result.NoParse<>(seq);
    };
  }

  static <T> Parser<Optional<T>> optional(final Parser<T> parser) {
    return seq -> {
      final Result<T> result = parser.parse(seq);
      if (result.isNoParse()) {
        return new Result.Success<>(Optional.empty(), result.getRemaining());
      }
      return result.map(Optional::of);
    };
  }

  static <T> Parser<T> option(final T fallback, final Parser<T> parser) {
    return optional(parser).map(optional -> optional.orElse(fallback));
  }

  static <T> Parser<T> option(final Supplier<T> fallbackSupplier, final Parser<T> parser) {
    return optional(parser).map(optional -> optional.orElseGet(fallbackSupplier));
  }

  @SafeVarargs
  static <T> Parser<Seq<T>> sequence(final Parser<T>... ps) {

    return seq -> {
      final SeqBuilder<T> seqBuilder = Seq.builder();
      Seq<?> remaining = seq;
      Result<T> result;
      for (final Parser<T> p : ps) {
        if (remaining.isEmpty()) {
          return new Result.NoParse<>(seq);
        }
        result = p.parse(remaining);
        if (!result.isSuccess()) {
          return result.withRemaining(seq).as();
        }
        remaining = result.getRemaining();
        seqBuilder.add(result.getValue());
      }
      return new Result.Success<>(seqBuilder.result(), remaining);
    };
  }

  static <T> Parser<Seq<T>> sequence(final Iterable<Parser<T>> ps) {

    return seq -> {
      final SeqBuilder<T> seqBuilder = Seq.builder();
      Seq<?> remaining = seq;
      Result<T> result;
      for (final Parser<T> p : ps) {
        if (remaining.isEmpty()) {
          return new Result.NoParse<>(seq);
        }
        result = p.parse(remaining);
        if (!result.isSuccess()) {
          return result.withRemaining(seq).as();
        }
        remaining = result.getRemaining();
        seqBuilder.add(result.getValue());
      }
      return new Result.Success<>(seqBuilder.result(), remaining);
    };
  }

  static <T> Parser<Seq<T>> times(final int n, final Parser<T> p) {

    return seq -> {
      final SeqBuilder<T> seqBuilder = Seq.builder();
      Seq<?> remaining = seq;
      Result<T> result;
      for (int i = 0; i < n; i += 1) {
        if (remaining.isEmpty()) {
          return new Result.NoParse<>(seq);
        }
        result = p.parse(remaining);
        if (!result.isSuccess()) {
          return result.withRemaining(seq).as();
        }
        remaining = result.getRemaining();
        seqBuilder.add(result.getValue());
      }
      return new Result.Success<>(seqBuilder.result(), remaining);
    };
  }

  static <T, U> Parser<Pair<T, U>> seq(
    final Parser<T> p1,
    final Parser<U> p2) {

    return seq -> {
      final Result<T> r1 = p1.parse(seq);
      if (r1.isSuccess()) {
        final Result<U> r2 = p2.parse(r1.getRemaining());
        if (r2.isSuccess()) {
          return new Result.Success<>(Pair.of(r1.getValue(), r2.getValue()), r2.getRemaining());
        }
        return r2.withRemaining(seq).as();
      }
      return r1.withRemaining(seq).as();
    };
  }

  static <T, U, V> Parser<Triple<T, U, V>> seq(
    final Parser<T> p1,
    final Parser<U> p2,
    final Parser<V> p3) {

    return seq -> {
      final Result<T> r1 = p1.parse(seq);
      if (r1.isSuccess()) {
        final Result<U> r2 = p2.parse(r1.getRemaining());
        if (r2.isSuccess()) {
          final Result<V> r3 = p3.parse(r2.getRemaining());
          if (r3.isSuccess()) {
            return new Result.Success<>(
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

  static <T, U, V, W> Parser<Quadruple<T, U, V, W>> seq(
    final Parser<T> p1,
    final Parser<U> p2,
    final Parser<V> p3,
    final Parser<W> p4) {

    return seq -> {
      final Result<T> r1 = p1.parse(seq);
      if (r1.isSuccess()) {
        final Result<U> r2 = p2.parse(r1.getRemaining());
        if (r2.isSuccess()) {
          final Result<V> r3 = p3.parse(r2.getRemaining());
          if (r3.isSuccess()) {
            final Result<W> r4 = p4.parse(r3.getRemaining());
            if (r4.isSuccess()) {
              return new Result.Success<>(
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

  static <T> Parser<Seq<T>> many(final Parser<T> parser) {
    return seq -> {
      final SeqBuilder<T> resultBuilder = Seq.builder();
      Seq<?> remaining = seq;
      while (remaining.nonEmpty()) {
        final Result<T> result = parser.parse(remaining);
        if (!result.isSuccess()) {
          return new Result.Success<>(resultBuilder.result(), remaining);
        }
        resultBuilder.add(result.getValue());
        remaining = result.getRemaining();
      }
      return new Result.Success<>(resultBuilder.result(), remaining);
    };
  }

  static <T> Parser<Seq<T>> many1(final Parser<T> parser) {
    final Parser<Seq<T>> manyParser = many(parser);
    return seq -> {
      final Result<Seq<T>> result = manyParser.parse(seq);
      if (result.isSuccess() && result.getValue().isEmpty()) {
        return new Result.NoParse<>(seq);
      }
      return result;
    };
  }

  static <T> Parser<Void> skipMany(final Parser<T> parser) {
    return seq -> {
      Seq<?> remaining = seq;
      while (remaining.nonEmpty()) {
        final Result<T> result = parser.parse(remaining);
        if (!result.isSuccess()) {
          return new Result.Success<>(null, remaining);
        }
        remaining = result.getRemaining();
      }
      return new Result.Success<>(null, remaining);
    };
  }

  static <T, U> Parser<Seq<T>> sepBy(final Parser<T> parser, final Parser<U> sep) {
    final Parser<T> p = right(sep, parser);
    return seq -> {
      final SeqBuilder<T> resultBuilder = Seq.builder();
      Seq<?> remaining = seq;
      Result<T> result = parser.parse(remaining);
      if (result.isSuccess()) {
        resultBuilder.add(result.getValue());
        remaining = result.getRemaining();
        while (remaining.nonEmpty()) {
          result = p.parse(remaining);
          if (!result.isSuccess()) {
            return new Result.Success<>(resultBuilder.result(), remaining);
          }
          resultBuilder.add(result.getValue());
          remaining = result.getRemaining();
        }
      }
      return new Result.Success<>(resultBuilder.result(), remaining);
    };
  }

  static <T, U> Parser<Seq<T>> sepBy1(final Parser<T> parser, final Parser<U> sep) {
    final Parser<Seq<T>> sepByParser = sepBy(parser, sep);
    return seq -> {
      final Result<Seq<T>> result = sepByParser.parse(seq);
      if (result.isSuccess() && result.getValue().isEmpty()) {
        return new Result.NoParse<>(seq);
      }
      return result;
    };
  }

  /**
   * Creates a parser that is constructed at first invocation for recursive invocation
   * of the parser.
   */
  static <T> Parser<T> recursive(final Supplier<Parser<T>> supplier) {
    final Supplier<Parser<T>> parserSupplier = Control.memoizing(supplier);
    return seq -> parserSupplier.get().parse(seq);
  }

  static <T> boolean shuntingYard(
    final Iterable<T> in,
    final Consumer<T> out,
    final Mapping<T, Integer> precedenceMap,
    final Predicate<T> isLeftAssociative,
    final Predicate<T> isLeftBracket,
    final Predicate<T> isRightBracket
  ) {
    return shuntingYard(
      in,
      out,
      precedenceMap.keys()::contains,
      (a, b) -> precedenceMap.apply(a) > precedenceMap.apply(b),
      (a, b) -> precedenceMap.apply(a).equals(precedenceMap.apply(b)),
      isLeftAssociative,
      isLeftBracket,
      isRightBracket
    );
  }

  static <T> boolean shuntingYard(
    final Iterable<T> in,
    final Consumer<T> out,
    final Predicate<T> isOperator,
    final BiPredicate<T, T> hasHigherPrecedenceThan,
    final BiPredicate<T, T> hasEqualPrecedenceAs,
    final Predicate<T> isLeftAssociative,
    final Predicate<T> isLeftBracket,
    final Predicate<T> isRightBracket
  ) {
    final Deque<T> stack = new ArrayDeque<>();
    for (final T s : in) {
      if (isOperator.test(s)) {
        while (!stack.isEmpty() && isOperator.test(stack.peek()) &&
          (hasHigherPrecedenceThan.test(stack.peek(), s)
            || (hasEqualPrecedenceAs.test(stack.peek(), s) && isLeftAssociative.test(stack.peek())))) {
          out.accept(stack.pop());
        }
        stack.push(s);
      } else if (isLeftBracket.test(s)) {
        stack.push(s);
      } else if (isRightBracket.test(s)) {
        while (!stack.isEmpty() && !isLeftBracket.test(stack.peek())) {
          out.accept(stack.pop());
        }
        if (!stack.isEmpty() && isLeftBracket.test(stack.peek())) {
          stack.pop();
        } else {
          return false;
        }
      } else {
        out.accept(s);
      }
    }
    while (!stack.isEmpty()) {
      final T t = stack.pop();
      if (!isOperator.test(t)) {
        return false;
      }
      out.accept(t);
    }
    return true;
  }
}
