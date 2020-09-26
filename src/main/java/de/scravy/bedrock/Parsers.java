package de.scravy.bedrock;

import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static de.scravy.bedrock.CharParser.*;

@UtilityClass
public class Parsers {

  public static class NoParseException extends Exception {
    public NoParseException(final CharParser.Result result) {
      super(result.getRemaining().asString());
    }

    public NoParseException(final Parser.Result result) {
      super(result.getRemaining().toString());
    }
  }

  public interface SimpleParser<Source, Target> {
    Target parse(@Nonnull final Source source) throws NoParseException;
  }

  public static JsonParser jsonParser() {
    return new JsonParser();
  }

  public static class JsonParser implements SimpleParser<String, Object> {

    JsonParser() {
      final CharParser<?> delimiter = character(':');
      final CharParser<Void> whitespace = skipMany(anyOf(" \t\r\n"));
      final CharParser<Character> digit = range('0', '9');
      final CharParser<Character> nonZeroDigit = range('1', '9');
      final CharParser<Character> hexDigit = oneOf(digit, range('a', 'z'), range('A', 'Z'));
      final CharParser<Boolean> bool = oneOf(
        string("true").map(Functions.constant(true)),
        string("false").map(Functions.constant(false))
      );
      final CharParser<Void> nothing = string("null").map(Functions.constant(null));
      final CharParser<String> string = between(
        character('"'),
        many(
          choice(
            noneOf("\"\\"),
            right(
              character('\\'),
              choice(
                anyOf("\"\\/bfnrt").map(d -> {
                  switch (d) {
                    case '\"':
                      return '\"';
                    case '\\':
                      return '\\';
                    case '/':
                      return '/';
                    case 'b':
                      return '\b';
                    case 'f':
                      return '\f';
                    case 'n':
                      return '\n';
                    case 'r':
                      return '\r';
                    case 't':
                      return '\t';
                    default:
                      throw new RuntimeException("really never can happen");
                  }
                }),
                right(
                  character('u'),
                  times(4, hexDigit)
                ).map(ds -> (char) Integer.parseInt(ds.asString(), 16))
              )
            )
          )
        ).map(Seq::asString),
        character('"')
      );
      final CharParser<BigDecimal> number = seq(
        option(BigDecimal.ONE, character('-').map(Functions.constant(BigDecimal.ONE.negate()))),
        choice(
          character('0').map(Seq::of),
          seq(
            nonZeroDigit,
            many(digit)
          ).map(p -> Seq.concat(Seq.of(p.fst()), p.snd()))
        ).map(Seq::asString),
        option(
          "",
          right(
            character('.'),
            many1(digit)
          ).map(Seq::asString)
        ),
        option(
          "",
          seq(
            anyOf("eE"),
            option('+', anyOf("-+")),
            many1(digit)
          ).map(p -> Seq.concat(Seq.of(p.getFirst(), p.getSecond()), p.getThird()).asString())
        )
      ).map(p -> p.getFirst().multiply(new BigDecimal(p.getSecond() + p.getThird() + p.getFourth())));
      final CharParser<String> key = between(whitespace, string, whitespace);
      final CharParser<Mapping<String, Object>> object = between(
        character('{'),
        choice(
          sepBy(
            seq(key, right(delimiter, value())),
            character(',')
          ).map(ArrayMap::ofSeq),
          whitespace.map(Functions.constant(Mapping.empty()))
        ),
        character('}')
      );
      final CharParser<Seq<Object>> array = between(
        character('['),
        choice(
          sepBy(
            value(),
            character(',')
          ),
          whitespace.map(Functions.constant(Seq.empty()))
        ),
        character(']')
      );
      value = between(whitespace, oneOf(object, array, string, number, bool, nothing), whitespace);
    }

    private final CharParser<Object> value;

    final CharParser<Object> value() {
      return recursive(() -> value);
    }

    @Override
    public final Object parse(@Nonnull final String string) throws NoParseException {
      final CharParser.Result<Object> result = value().parse(string);
      if (!result.isSuccess()) {
        throw new NoParseException(result);
      }
      return result.getValue();
    }
  }

}
