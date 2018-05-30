package com.simplaex.bedrock;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Immutable
public abstract class Either<L, R> implements Serializable, Comparable<Either<L, R>> {

  @SuppressWarnings("unchecked")
  @Value
  @EqualsAndHashCode(callSuper = false)
  @Immutable
  public static class Left<L, R> extends Either<L, R> {
    private L value;

    @Override
    public int compareTo(final Either<L, R> other) {
      if (other instanceof Right) {
        return -1;
      }
      return ((Comparable) value).compareTo(((Left) other).value);
    }

    @Override
    public <L2> Either<L2, R> mapLeft(final Function<L, L2> f) {
      return new Left<>(f.apply(value));
    }

    @Override
    public <R2> Either<L, R2> mapRight(final Function<R, R2> f) {
      return (Either<L, R2>) this;
    }

    @Override
    public void forEachLeft(final Consumer<L> f) {
      f.accept(value);
    }

    @Override
    public void forEachRight(final Consumer<R> f) {
      // do nothing
    }

    @Override
    public boolean isLeft() {
      return true;
    }

    @Override
    public boolean isRight() {
      return false;
    }

    @Override
    public Optional<Left<L, R>> getLeft() {
      return Optional.of(this);
    }

    @Override
    public Optional<Right<L, R>> getRight() {
      return Optional.empty();
    }

    @Override
    public Optional<L> getLeftValue() {
      return Optional.of(value);
    }

    @Override
    public Optional<R> getRightValue() {
      return Optional.empty();
    }

    @Override
    public <T> T fold(final Function<L, T> f, final Function<R, T> g) {
      return f.apply(value);
    }
  }

  @SuppressWarnings("unchecked")
  @Value
  @EqualsAndHashCode(callSuper = false)
  @Immutable
  public static class Right<L, R> extends Either<L, R> {
    private R value;

    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(final Either<L, R> other) {
      if (other instanceof Left) {
        return 1;
      }
      return ((Comparable) value).compareTo(((Right) other).value);
    }

    @Override
    public <L2> Either<L2, R> mapLeft(final Function<L, L2> f) {
      return (Either<L2, R>) this;
    }

    @Override
    public <R2> Either<L, R2> mapRight(final Function<R, R2> f) {
      return new Right<>(f.apply(value));
    }

    @Override
    public void forEachLeft(final Consumer<L> f) {
      // do nothing
    }

    @Override
    public void forEachRight(final Consumer<R> f) {
      f.accept(value);
    }

    @Override
    public boolean isLeft() {
      return false;
    }

    @Override
    public boolean isRight() {
      return true;
    }

    @Override
    public Optional<Left<L, R>> getLeft() {
      return Optional.empty();
    }

    @Override
    public Optional<Right<L, R>> getRight() {
      return Optional.of(this);
    }

    @Override
    public Optional<L> getLeftValue() {
      return Optional.empty();
    }

    @Override
    public Optional<R> getRightValue() {
      return Optional.of(value);
    }

    @Override
    public <T> T fold(final Function<L, T> f, final Function<R, T> g) {
      return g.apply(value);
    }
  }

  public abstract Object getValue();

  public abstract <L2> Either<L2, R> mapLeft(final Function<L, L2> f);

  public abstract <R2> Either<L, R2> mapRight(final Function<R, R2> f);

  public abstract void forEachLeft(final Consumer<L> f);

  public abstract void forEachRight(final Consumer<R> f);

  public abstract boolean isLeft();

  public abstract boolean isRight();

  public abstract Optional<Left<L, R>> getLeft();

  public abstract Optional<Right<L, R>> getRight();

  public abstract Optional<L> getLeftValue();

  public abstract Optional<R> getRightValue();

  public abstract <T> T fold(final Function<L, T> f, final Function<R, T> g);

  public static <L, R> Either<L, R> left(final L value) {
    return new Left<>(value);
  }

  public static <L, R> Either<L, R> right(final R value) {
    return new Right<>(value);
  }
}
