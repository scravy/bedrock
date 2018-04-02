package com.simplaex.bedrock;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@UtilityClass
public class Strings {

  public static abstract class Template implements Function<Function<String, String>, String> {

    @SafeVarargs
    public final String substitute(final @Nonnull Pair<String, String>... values) {
      return apply(ArrayMap.of(values));
    }

    @Override
    public abstract String apply(final @Nonnull Function<String, String> values);

  }

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
  public static String replace(
    final @Nonnull String string,
    final @Nonnull Pair<String, String>... values) {

    Objects.requireNonNull(string, "'string' must not be null");
    Objects.requireNonNull(values, "'values' must not be null");

    final ArrayMap<String, String> valuesMap = ArrayMap.of(values);
    final String regex = valuesMap.keys().stream().map(Pattern::quote).collect(Collectors.joining("|"));
    final Template template = template(regex, Function.identity(), string);
    return template.apply(valuesMap);
  }

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
      @Override
      public String apply(@Nonnull final Function<String, String> values) {
        final StringBuilder result = new StringBuilder(sizeEstimate);
        builders.forEach(f -> result.append(f.apply(values)));
        return result.toString();
      }
    };
  }

}
