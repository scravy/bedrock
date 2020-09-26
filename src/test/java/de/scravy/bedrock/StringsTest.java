package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.util.function.Function;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static de.scravy.bedrock.ForEach.forEach;
import static de.scravy.bedrock.Pair.pair;
import static de.scravy.bedrock.Triple.triple;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class StringsTest {
  {
    describe("StringUtil", () -> {
      it("should create a template tht substitutes variables", () -> {
        final Strings.Template template = Strings.template(
          "${",
          "}",
          "Hello ${user}, today is ${day}! Goodbye ${user}."
        );
        final String result = template.substitute(
          pair("user", "Chico ${day}"),
          pair("day", "a sunny day")
        );
        expect(result).toEqual("Hello Chico ${day}, today is a sunny day! Goodbye Chico ${day}.");
      });
    });
    describe("Strings.Name", () -> {
      describe("parseName", () -> {
        final Seq<Pair<String, Seq<String>>> values = Seq.of(
          pair("AsyncSQLClient", Seq.of("async", "sql", "client")),
          pair("AsyncSqlClient", Seq.of("async", "sql", "client")),
          pair("Async_SQL_client", Seq.of("async", "sql", "client")),
          pair("text", Seq.of("text")),
          pair("teXt9", Seq.of("te", "xt9")),
          pair("some__text", Seq.of("some", "text")),
          pair("__namespaced_name", Seq.of("namespaced", "name")),
          pair("Some", Seq.of("some"))
        );
        forEach(values, (name, components) -> {
          it("should parse " + name, () -> {
            expect(Strings.parseName(name).getComponents()).toEqual(components);
          });
        });
      });
      final Seq<String> strings = Seq.of(
        "AsyncSQLClient",
        "async-sql-client",
        "async_sql_client",
        "AsyncSqlClient"
      );
      final Seq<Triple<Function<Strings.Name, String>, Function<String, String>, String>> funcs = Seq.of(
        triple(Strings.Name::renderLowerCamelCase, Strings::toLowerCamelCase, "asyncSqlClient"),
        triple(Strings.Name::renderUpperCamelCase, Strings::toUpperCamelCase, "AsyncSqlClient"),
        triple(Strings.Name::renderLowerKebapCase, Strings::toLowerKebapCase, "async-sql-client"),
        triple(Strings.Name::renderKebapCamelCase, Strings::toKebapCamelCase, "Async-Sql-Client"),
        triple(Strings.Name::renderLowerSnakeCase, Strings::toLowerSnakeCase, "async_sql_client"),
        triple(Strings.Name::renderUpperSnakeCase, Strings::toUpperSnakeCase, "ASYNC_SQL_CLIENT")
      );
      forEach(funcs, (func, strFunc, rendering) -> {
        describe(func.toString(), () -> {
          strings.forEach(string -> {
            final Strings.Name name = Strings.parseName(string);
            it("should turn " + string + " into " + rendering, () -> {
              expect(strFunc.apply(string)).toEqual(rendering);
            });
            it("should render " + name + " into " + rendering, () -> {
              expect(func.apply(name)).toEqual(rendering);
            });
          });
        });
      });
    });
    describe("replace", () -> {
      it("should replace values", () -> {
        expect(Strings.replace("abba", pair("a", "bb"), pair("bb", "a")))
          .toEqual("bbabb");
      });
    });
    describe("isNullOrEmpty", () -> {
      it("should check null", () -> {
        //noinspection ConstantConditions
        expect(Strings.isNullOrEmpty(null)).toBeTrue();
      });
      it("should check the empty string", () -> {
        expect(Strings.isNullOrEmpty("")).toBeTrue();
      });
      it("should check all else", () -> {
        expect(Strings.isNullOrEmpty("x")).toBeFalse();
      });
    });
    describe("isNonEmpty", () -> {
      it("should check null", () -> {
        //noinspection ConstantConditions
        expect(Strings.isNonEmpty(null)).toBeFalse();
      });
      it("should check the empty string", () -> {
        expect(Strings.isNonEmpty("")).toBeFalse();
      });
      it("should check all else", () -> {
        expect(Strings.isNonEmpty("x")).toBeTrue();
      });
    });
    describe("isNullOrBlank", () -> {
      it("should identify null not as blank", () -> {
        expect(Strings.isNullOrBlank(null)).toBeTrue();
      });
      it("should identify a string consisting of whitespace as blank", () -> {
        expect(Strings.isNullOrBlank("   ")).toBeTrue();
      });
      it("should identify the empty string as blank", () -> {
        expect(Strings.isNullOrBlank("")).toBeTrue();
      });
    });
    describe("forEachCodePointWithIndex", () -> {
      it("should iterate emojis", () -> {
        final Seq<Pair<Integer, Integer>> expectedResult = Seq.of(pair(1, 0x1F308), pair(3, 0x1F929));
        final SeqBuilder<Pair<Integer, Integer>> resultBuilder = Seq.builder();
        Strings.forEachCodepointWithIndex("\uD83C\uDF08\uD83E\uDD29", (cp, index) -> {
          resultBuilder.add(pair(cp, index));
        });
        expect(resultBuilder.result()).toEqual(expectedResult);
      });
    });
  }

}
