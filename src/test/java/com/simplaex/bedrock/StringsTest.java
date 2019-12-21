package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.util.function.Function;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.simplaex.bedrock.ForEach.forEach;
import static com.simplaex.bedrock.Pair.pair;
import static com.simplaex.bedrock.Triple.triple;

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
  }

}
