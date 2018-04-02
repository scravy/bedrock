package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.simplaex.bedrock.Pair.pair;

@SuppressWarnings("CodeBlock2Expr")
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
  }

}
