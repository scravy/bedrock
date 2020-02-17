package com.simplaex.bedrock;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class Strings {

  public static abstract class Template implements Function<Function<String, String>, String> {

    @SafeVarargs
    @Nonnull
    public final String substitute(@Nonnull final Pair<String, String>... values) {
      return apply(ArrayMap.of(values));
    }

    @Override
    @Nonnull
    public abstract String apply(@Nonnull final Function<String, String> values);

  }

  @Nonnull
  public static String capitalize(@Nonnull final String string) {
    final StringBuilder stringBuilder = new StringBuilder();
    Strings.forEachCodepointWithIndex(string, (index, codepoint) -> {
      if (index == 0) {
        stringBuilder.appendCodePoint(Character.toUpperCase(codepoint));
      } else {
        stringBuilder.appendCodePoint(Character.toLowerCase(codepoint));
      }
    });
    return stringBuilder.toString();
  }

  @Nonnull
  public static String capitalizeFirstCharacter(@Nonnull final String string) {
    final StringBuilder stringBuilder = new StringBuilder();
    Strings.forEachCodepointWithIndex(string, (index, codepoint) -> {
      if (index == 0) {
        stringBuilder.appendCodePoint(Character.toUpperCase(codepoint));
      } else {
        stringBuilder.appendCodePoint(codepoint);
      }
    });
    return stringBuilder.toString();
  }

  @Nonnull
  public static Seq<String> explode(
    final @Nonnull String string,
    final @Nonnull String delimiter) {

    Objects.requireNonNull(string, "'string' must not be null");
    Objects.requireNonNull(delimiter, "'delimiter' must not be null");

    if (string.isEmpty()) {
      return Seq.empty();
    }
    return Seq.ofArrayZeroCopyInternal(string.split(Pattern.quote(delimiter)));
  }

  @SafeVarargs
  @Nonnull
  public static String replace(
    final @Nonnull String string,
    final @Nonnull Pair<String, String>... values) {

    Objects.requireNonNull(string, "'string' must not be null");
    Objects.requireNonNull(values, "'values' must not be null");

    final ArrayMap<String, String> valuesMap = ArrayMap.of(values);
    final String regex = valuesMap.keys().stream().map(Pattern::quote).collect(Collectors.joining("|"));
    final Template template = template(regex, string);
    return template.apply(valuesMap);
  }

  @Nonnull
  public static Template template(
    final @Nonnull String variableBeginDelimiter,
    final @Nonnull String variableEndDelimiter,
    final @Nonnull String template) {

    Objects.requireNonNull(variableBeginDelimiter, "'variableBeginDelimiter' must not be null");
    Objects.requireNonNull(variableEndDelimiter, "'variableEndDelimiter' must not be null");

    final String quotedBeginDelimiter = Pattern.quote(variableBeginDelimiter);
    final String quotedEndDelimiter = Pattern.quote(variableEndDelimiter);

    final String nameRegex = "[a-zA-Z]([_\\-]?[a-zA-Z0-9])*";
    return template(
      quotedBeginDelimiter + nameRegex + quotedEndDelimiter,
      match -> match.substring(
        variableBeginDelimiter.length(),
        match.length() - variableEndDelimiter.length()
      ),
      template
    );
  }

  public static Template template(
    final @Nonnull String regex,
    final @Nonnull String template) {
    return template(regex, Function.identity(), template);
  }

  public static Template template(
    final @Nonnull String regex,
    final @Nonnull Function<String, String> nameExtractor,
    final @Nonnull String template) {

    Objects.requireNonNull(regex, "'regex' must not be null");
    Objects.requireNonNull(nameExtractor, "'nameExtractor' must not be null");
    Objects.requireNonNull(template, "'template' must not be null");

    final Pattern variablesPattern = Pattern.compile(regex);
    final Matcher matcher = variablesPattern.matcher(template);
    final SeqBuilder<Function<Function<String, String>, String>> buildersBuilder = Seq.builder();

    int start = 0;
    int length = 0;
    while (matcher.find()) {
      final int matchStartIndex = matcher.start();
      final int matchEndIndex = matcher.end();
      final String templateFragment = template.substring(start, matchStartIndex);
      if (!templateFragment.isEmpty()) {
        buildersBuilder.add(__ -> templateFragment);
        length += templateFragment.length();
      }
      final String match = matcher.group(0);
      final String variableName = nameExtractor.apply(match);
      buildersBuilder.add(variables -> variables.apply(variableName));
      length += 20;
      start = matchEndIndex;
    }
    if (start < template.length()) {
      final String endFragment = template.substring(start);
      buildersBuilder.add(__ -> endFragment);
      length += endFragment.length();
    }
    final Seq<Function<Function<String, String>, String>> builders = buildersBuilder.result();
    final int sizeEstimate = length;
    return new Template() {
      @Nonnull
      @Override
      public String apply(@Nonnull final Function<String, String> values) {
        final StringBuilder result = new StringBuilder(sizeEstimate);
        builders.forEach(f -> result.append(f.apply(values)));
        return result.toString();
      }
    };
  }

  public static void forEach(@Nonnull final String string, @Nonnull final IntConsumer consumer) {
    for (int i = 0; i < string.length(); i += 1) {
      consumer.accept(string.charAt(i));
    }
  }

  public static void forEachCodepoint(@Nonnull final String string, @Nonnull final IntConsumer consumer) {
    for (int i = 0; i < string.length(); i += 1) {
      final char c1 = string.charAt(i);
      if (Character.isHighSurrogate(c1)) {
        i += 1;
        if (i < string.length()) {
          final char c2 = string.charAt(i);
          if (Character.isLowSurrogate(c2)) {
            final int codepoint = Character.toCodePoint(c1, c2);
            consumer.accept(codepoint);
          }
        }
      } else {
        consumer.accept(c1);
      }
    }
  }

  public static boolean isNullOrEmpty(@Nullable final String string) {
    return string == null || string.isEmpty();
  }

  public static boolean isNonEmpty(@Nullable final String string) {
    return !isNullOrEmpty(string);
  }

  public static boolean isNullOrBlank(@Nullable final String string) {
    return string == null || string.trim().isEmpty();
  }

  public static boolean isNonBlank(@Nullable final String string) {
    return !isNullOrBlank(string);
  }

  @FunctionalInterface
  interface CodepointWithIndexConsumer extends BiConsumer<Integer, Integer> {

    void accept(int a, int b);

    @Override
    default void accept(@Nonnull final Integer a, @Nonnull final Integer b) {
      accept(a.intValue(), b.intValue());
    }
  }

  public static void forEachCodepointWithIndex(
    @Nonnull final String string,
    @Nonnull final CodepointWithIndexConsumer consumer
  ) {
    for (int i = 0; i < string.length(); i += 1) {
      final char c1 = string.charAt(i);
      if (Character.isHighSurrogate(c1)) {
        final int j = i + 1;
        if (j < string.length()) {
          final char c2 = string.charAt(j);
          if (Character.isLowSurrogate(c2)) {
            final int codepoint = Character.toCodePoint(c1, c2);
            consumer.accept(j, codepoint);
          }
        }
        i = j;
      } else {
        consumer.accept(i, c1);
      }
    }
  }

  @Nonnull
  public static String toUpperCamelCase(@Nonnull final String string) {
    return parseName(string).renderUpperCamelCase();
  }

  @Nonnull
  public static String toLowerCamelCase(@Nonnull final String string) {
    return parseName(string).renderLowerCamelCase();
  }

  @Nonnull
  public static String toLowerKebapCase(@Nonnull final String string) {
    return parseName(string).renderLowerKebapCase();
  }

  @Nonnull
  public static String toKebapCamelCase(@Nonnull final String string) {
    return parseName(string).renderKebapCamelCase();
  }

  @Nonnull
  public static String toUpperSnakeCase(@Nonnull final String string) {
    return parseName(string).renderUpperSnakeCase();
  }

  @Nonnull
  public static String toLowerSnakeCase(@Nonnull final String string) {
    return parseName(string).renderLowerSnakeCase();
  }

  @Nonnull
  public static Name parseName(@Nonnull final String name) {
    final Seq<String> components;
    final StringBuilder componentBuilder = new StringBuilder();
    final SeqBuilder<String> nameBuilder = Seq.builder();
    forEachCodepoint(name, codepoint -> {
      if (Character.isLetterOrDigit(codepoint)) {
        componentBuilder.appendCodePoint(codepoint);
      } else if (componentBuilder.length() > 0) {
        nameBuilder.add(componentBuilder.toString());
        componentBuilder.setLength(0);
      }
    });
    if (componentBuilder.length() > 0) {
      nameBuilder.add(componentBuilder.toString());
    }
    components = nameBuilder.result();
    final Seq<String> finalNameComponents = components.flatMap(component -> {
      componentBuilder.setLength(0);
      nameBuilder.clear();
      final Box.IntBox previousCodepoint = Box.intBox(-1);
      forEachCodepoint(component, codepoint -> {
        if (previousCodepoint.intExists(c -> c > -1)) {
          if (Character.isUpperCase(codepoint) && !previousCodepoint.intExists(Character::isUpperCase)) {
            componentBuilder.appendCodePoint(Character.toLowerCase(previousCodepoint.get()));
            nameBuilder.add(componentBuilder.toString());
            componentBuilder.setLength(0);
          } else {
            if (!Character.isUpperCase(codepoint) && previousCodepoint.intExists(Character::isUpperCase)) {
              if (componentBuilder.length() > 0) {
                nameBuilder.add(componentBuilder.toString());
                componentBuilder.setLength(0);
              }
            }
            componentBuilder.appendCodePoint(Character.toLowerCase(previousCodepoint.get()));
          }
        }
        previousCodepoint.setValue(codepoint);
      });
      componentBuilder.appendCodePoint(Character.toLowerCase(previousCodepoint.get()));
      nameBuilder.add(componentBuilder.toString());
      return nameBuilder.result();
    });
    return new Name(finalNameComponents);
  }

  @Value
  @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
  public static class Name {

    private Seq<String> components;

    @Nonnull
    public String renderLowerCamelCase() {
      final StringBuilder stringBuilder = new StringBuilder();
      components.forEachWithIndex((index, component) -> {
        if (index == 0) {
          stringBuilder.append(component);
        } else {
          stringBuilder.append(Strings.capitalizeFirstCharacter(component));
        }
      });
      return stringBuilder.toString();
    }

    @Nonnull
    public String renderUpperCamelCase() {
      final StringBuilder stringBuilder = new StringBuilder();
      components.forEach(component -> stringBuilder.append(Strings.capitalize(component)));
      return stringBuilder.toString();
    }

    @Nonnull
    public String renderUpperSnakeCase() {
      final StringBuilder stringBuilder = new StringBuilder();
      components.forEachWithIndex((index, component) -> {
        if (index != 0) {
          stringBuilder.append('_');
        }
        stringBuilder.append(component.toUpperCase());
      });
      return stringBuilder.toString();
    }

    @Nonnull
    public String renderLowerSnakeCase() {
      final StringBuilder stringBuilder = new StringBuilder();
      components.forEachWithIndex((index, component) -> {
        if (index != 0) {
          stringBuilder.append('_');
        }
        stringBuilder.append(component);
      });
      return stringBuilder.toString();
    }

    @Nonnull
    public String renderKebapCamelCase() {
      final StringBuilder stringBuilder = new StringBuilder();
      components.forEachWithIndex((index, component) -> {
        if (index != 0) {
          stringBuilder.append('-');
        }
        stringBuilder.append(capitalizeFirstCharacter(component));
      });
      return stringBuilder.toString();
    }

    @Nonnull
    public String renderLowerKebapCase() {
      final StringBuilder stringBuilder = new StringBuilder();
      components.forEachWithIndex((index, component) -> {
        if (index != 0) {
          stringBuilder.append('-');
        }
        stringBuilder.append(component);
      });
      return stringBuilder.toString();
    }
  }

}
