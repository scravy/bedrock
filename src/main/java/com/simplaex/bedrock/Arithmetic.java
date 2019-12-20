package com.simplaex.bedrock;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.With;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;

@UtilityClass
public class Arithmetic {

  public double minimum(final double... xs) {
    if (xs.length == 0) {
      throw new IllegalArgumentException();
    }
    double r = xs[0];
    for (int i = 1; i < xs.length; i += 1) {
      if (xs[i] < r) {
        r = xs[i];
      }
    }
    return r;
  }

  public int minimum(final int... xs) {
    if (xs.length == 0) {
      throw new IllegalArgumentException();
    }
    int r = xs[0];
    for (int i = 1; i < xs.length; i += 1) {
      if (xs[i] < r) {
        r = xs[i];
      }
    }
    return r;
  }

  public long minimum(final long... xs) {
    if (xs.length == 0) {
      throw new IllegalArgumentException();
    }
    long r = xs[0];
    for (int i = 1; i < xs.length; i += 1) {
      if (xs[i] < r) {
        r = xs[i];
      }
    }
    return r;
  }

  public double maximum(final double... xs) {
    if (xs.length == 0) {
      throw new IllegalArgumentException();
    }
    double r = xs[0];
    for (int i = 1; i < xs.length; i += 1) {
      if (xs[i] > r) {
        r = xs[i];
      }
    }
    return r;
  }

  public int maximum(final int... xs) {
    if (xs.length == 0) {
      throw new IllegalArgumentException();
    }
    int r = xs[0];
    for (int i = 1; i < xs.length; i += 1) {
      if (xs[i] > r) {
        r = xs[i];
      }
    }
    return r;
  }

  public long maximum(final long... xs) {
    if (xs.length == 0) {
      throw new IllegalArgumentException();
    }
    long r = xs[0];
    for (int i = 1; i < xs.length; i += 1) {
      if (xs[i] > r) {
        r = xs[i];
      }
    }
    return r;
  }

  interface Tokenizer {

    enum Ordering {
      LT,
      EQ,
      GT;

      public static Ordering from(final int comparisonResult) {
        if (comparisonResult < 0) {
          return LT;
        } else if (comparisonResult > 0) {
          return GT;
        }
        return EQ;
      }
    }

    Object identify(String lexeme);

    boolean isOpeningParenthesis(String lexeme);

    boolean isClosingParenthesis(String lexeme);

    boolean isValue(String lexeme);

    boolean isOperator(String lexeme);

    boolean isLeftAssociative(String lexeme);

    Ordering comparePrecedence(String leftOperator, String rightOperator);

    static Tokenizer arithmetic() {
      return new Tokenizer() {

        private final Map<String, Pair<Integer, BinaryOperator<BigDecimal>>> operators = new HashMap<>();

        {
          operators.put("+", Pair.of(1, new BinaryOperator<BigDecimal>() {
            @Override
            public BigDecimal apply(final BigDecimal left, final BigDecimal right) {
              return left.add(right);
            }

            public String toString() {
              return "+";
            }
          }));
          operators.put("-", Pair.of(1, new BinaryOperator<BigDecimal>() {
            @Override
            public BigDecimal apply(final BigDecimal left, final BigDecimal right) {
              return left.subtract(right);
            }

            public String toString() {
              return "-";
            }
          }));
          operators.put("*", Pair.of(2, new BinaryOperator<BigDecimal>() {
            @Override
            public BigDecimal apply(final BigDecimal left, final BigDecimal right) {
              return left.multiply(right);
            }

            public String toString() {
              return "*";
            }
          }));
          operators.put("/", Pair.of(2, new BinaryOperator<BigDecimal>() {
            @Override
            public BigDecimal apply(final BigDecimal a, final BigDecimal b) {
              try {
                //noinspection BigDecimalMethodWithoutRoundingCalled
                return a.divide(b);
              } catch (final ArithmeticException exc) {
                return a.divide(b, 12, RoundingMode.HALF_UP);
              }
            }

            public String toString() {
              return "/";
            }
          }));
          operators.put("^", Pair.of(3, new BinaryOperator<BigDecimal>() {
            @Override
            public BigDecimal apply(final BigDecimal a, final BigDecimal b) {
              return a.pow(b.intValue());
            }

            public String toString() {
              return "^";
            }
          }));
        }

        @Override
        public Object identify(final String lexeme) {
          if (operators.containsKey(lexeme)) {
            return operators.get(lexeme).snd();
          }
          try {
            return new BigDecimal(lexeme);
          } catch (final Exception exc) {
            return lexeme;
          }
        }

        @Override
        public boolean isOpeningParenthesis(final String lexeme) {
          return lexeme.equals("(");
        }

        @Override
        public boolean isClosingParenthesis(final String lexeme) {
          return lexeme.equals(")");
        }

        @Override
        public boolean isValue(final String lexeme) {
          return !isOperator(lexeme) && !isOpeningParenthesis(lexeme) && !isClosingParenthesis(lexeme);
        }

        @Override
        public boolean isOperator(final String lexeme) {
          return operators.containsKey(lexeme);
        }

        @Override
        public boolean isLeftAssociative(final String lexeme) {
          return !lexeme.equals("^");
        }

        @Override
        public Ordering comparePrecedence(final String leftOperator, final String rightOperator) {
          return Ordering.from(Integer.compare(
            Optional.ofNullable(operators.get(leftOperator)).map(Pair::fst).orElse(0),
            Optional.ofNullable(operators.get(rightOperator)).map(Pair::fst).orElse(0)
          ));
        }
      };
    }

  }

  @RequiredArgsConstructor
  @Builder
  @With
  static class Parser {

    private final Lexer lexer;
    private final Tokenizer tokenizer;

    public static Parser arithmetic() {
      return Parser.builder().lexer(Lexer.standard()).tokenizer(Tokenizer.arithmetic()).build();
    }

    @Value(staticConstructor = "of")
    public static class Token {
      private Lexer.Lexeme lexeme;
      private Object value;
    }

    private boolean shouldShunt(final String lookingAt, final String peek) {
      if (tokenizer.isOpeningParenthesis(peek)) {
        return false;
      }
      final Tokenizer.Ordering precedence = tokenizer.comparePrecedence(peek, lookingAt);
      return precedence == Tokenizer.Ordering.GT || (precedence == Tokenizer.Ordering.EQ && tokenizer.isLeftAssociative(lookingAt));
    }

    public Seq<Token> parse(final String string) {
      final SeqBuilder<Token> sequenceBuilder = Seq.builder();
      if (!parse(string, sequenceBuilder::add)) {
        throw new IllegalArgumentException("unclosed parenthesis in expression");
      }
      return sequenceBuilder.result();
    }

    public boolean parse(final String string, final Consumer<Token> tokenHandler) {
      final Seq<Lexer.Lexeme> lexemes = lexer.lex(string);
      final Deque<Lexer.Lexeme> operators = new ArrayDeque<>();
      for (final Lexer.Lexeme lexeme : lexemes) {
        final String value = lexeme.getValue();
        if (tokenizer.isValue(value)) {
          tokenHandler.accept(Token.of(lexeme, tokenizer.identify(lexeme.getValue())));
        } else if (tokenizer.isOperator(value)) {
          while (!operators.isEmpty() && shouldShunt(value, operators.peek().getValue())) {
            final Lexer.Lexeme l = operators.pop();
            tokenHandler.accept(Token.of(l, tokenizer.identify(l.getValue())));
          }
          operators.push(lexeme);
        } else if (tokenizer.isOpeningParenthesis(value)) {
          operators.push(lexeme);
        } else if (tokenizer.isClosingParenthesis(value)) {
          while (!operators.isEmpty() && !(tokenizer.isOpeningParenthesis(operators.peek().getValue()))) {
            final Lexer.Lexeme l = operators.pop();
            tokenHandler.accept(Token.of(l, tokenizer.identify(l.getValue())));
          }
          if (operators.isEmpty()) {
            return false;
          }
          operators.pop();
        }
      }
      while (!operators.isEmpty()) {
        final Lexer.Lexeme l = operators.pop();
        tokenHandler.accept(Token.of(l, tokenizer.identify(l.getValue())));
      }
      return true;
    }

  }

  interface ArithmeticExpression {

    BigDecimal eval(final Function<String, BigDecimal> variableBindings);

    OptimizedArithmeticExpression optimize(final Map<String, BigDecimal> constants);

    default OptimizedArithmeticExpression optimize() {
      return optimize(Collections.emptyMap());
    }

    @Value(staticConstructor = "of")
    class BinaryOperation implements ArithmeticExpression {
      private String operation;
      private BinaryOperator<BigDecimal> operator;
      private ArithmeticExpression left;
      private ArithmeticExpression right;

      @Override
      public BigDecimal eval(final Function<String, BigDecimal> variableBindings) {
        return operator.apply(left.eval(variableBindings), right.eval(variableBindings));
      }

      @Override
      public OptimizedArithmeticExpression optimize(final Map<String, BigDecimal> constants) {
        final OptimizedArithmeticExpression oleft = left.optimize(constants);
        final OptimizedArithmeticExpression oright = right.optimize(constants);
        if (oleft instanceof OptimizedArithmeticExpression.LiteralValue && oright instanceof OptimizedArithmeticExpression.LiteralValue) {
          final BigDecimal result = operator.apply(
            ((OptimizedArithmeticExpression.LiteralValue) oleft).getValue(),
            ((OptimizedArithmeticExpression.LiteralValue) oright).getValue()
          );
          return OptimizedArithmeticExpression.LiteralValue.of(result);
        }
        switch (operation) {
          case "+":
            if (oleft instanceof OptimizedArithmeticExpression.LiteralValue &&
              ((OptimizedArithmeticExpression.LiteralValue) oleft).getValue().equals(BigDecimal.ZERO)) {
              return oright;
            }
            if (oright instanceof OptimizedArithmeticExpression.LiteralValue &&
              ((OptimizedArithmeticExpression.LiteralValue) oright).getValue().equals(BigDecimal.ZERO)) {
              return oleft;
            }
            break;
          case "*":
            if (oleft instanceof OptimizedArithmeticExpression.LiteralValue &&
              ((OptimizedArithmeticExpression.LiteralValue) oleft).getValue().equals(BigDecimal.ONE)) {
              return oright;
            }
            if (oright instanceof OptimizedArithmeticExpression.LiteralValue &&
              ((OptimizedArithmeticExpression.LiteralValue) oright).getValue().equals(BigDecimal.ONE)) {
              return oleft;
            }
            if (oleft instanceof OptimizedArithmeticExpression.LiteralValue &&
              ((OptimizedArithmeticExpression.LiteralValue) oleft).getValue().equals(BigDecimal.ZERO)) {
              return OptimizedArithmeticExpression.LiteralValue.of(BigDecimal.ZERO);
            }
            if (oright instanceof OptimizedArithmeticExpression.LiteralValue &&
              ((OptimizedArithmeticExpression.LiteralValue) oright).getValue().equals(BigDecimal.ZERO)) {
              return OptimizedArithmeticExpression.LiteralValue.of(BigDecimal.ZERO);
            }
            break;
          case "^":
            if (oright instanceof OptimizedArithmeticExpression.LiteralValue &&
              ((OptimizedArithmeticExpression.LiteralValue) oright).getValue().equals(BigDecimal.ZERO)) {
              return OptimizedArithmeticExpression.LiteralValue.of(BigDecimal.ONE);
            }
            if (oright instanceof OptimizedArithmeticExpression.LiteralValue &&
              ((OptimizedArithmeticExpression.LiteralValue) oright).getValue().equals(BigDecimal.ONE)) {
              return oleft;
            }
            break;
        }
        return OptimizedArithmeticExpression.BinaryOperation.of(operator, oleft, oright);
      }
    }

    @Value(staticConstructor = "of")
    class LiteralValue implements ArithmeticExpression {
      private BigDecimal value;

      @Override
      public BigDecimal eval(final Function<String, BigDecimal> variableBindings) {
        return value;
      }

      @Override
      public OptimizedArithmeticExpression optimize(final Map<String, BigDecimal> constants) {
        return OptimizedArithmeticExpression.LiteralValue.of(value);
      }
    }

    @Value(staticConstructor = "of")
    class VariableReference implements ArithmeticExpression {
      private String name;

      @Override
      public BigDecimal eval(final Function<String, BigDecimal> variableBindings) {
        return variableBindings.apply(name);
      }

      @Override
      public OptimizedArithmeticExpression optimize(final Map<String, BigDecimal> constants) {
        if (constants.containsKey(name)) {
          return OptimizedArithmeticExpression.LiteralValue.of(constants.get(name));
        }
        return OptimizedArithmeticExpression.VariableReference.of(name);
      }
    }

    static ArithmeticExpression compile(final String expression) {

      @Value
      class Operator {
        private final String operation;
        private final BinaryOperator<BigDecimal> operator;
      }

      final Parser parser = Parser.arithmetic().withTokenizer(new DelegatingTokenizer(Tokenizer.arithmetic()) {
        @SuppressWarnings("unchecked")
        @Override
        public Object identify(final String lexeme) {
          if (isOperator(lexeme)) {
            return new Operator(lexeme, (BinaryOperator<BigDecimal>) super.identify(lexeme));
          }
          return super.identify(lexeme);
        }
      });
      final Deque<ArithmeticExpression> stack = new ArrayDeque<>();
      parser.parse(expression, token -> {
        final Object value = token.getValue();
        if (value instanceof BigDecimal) {
          stack.push(ArithmeticExpression.LiteralValue.of((BigDecimal) value));
        } else if (value instanceof String) {
          stack.push(ArithmeticExpression.VariableReference.of((String) value));
        } else {
          final ArithmeticExpression snd = stack.pop();
          final ArithmeticExpression fst = stack.pop();
          final Operator op = (Operator) value;
          stack.push(ArithmeticExpression.BinaryOperation.of(op.getOperation(), op.getOperator(), fst, snd));
        }
      });

      if (stack.size() != 1) {
        throw new IllegalArgumentException("could not compile expression");
      }

      return stack.pop();
    }

  }

  interface OptimizedArithmeticExpression {

    BigDecimal eval(final Function<String, BigDecimal> variableBindings);

    @Value(staticConstructor = "of")
    class BinaryOperation implements OptimizedArithmeticExpression {
      private BinaryOperator<BigDecimal> operator;
      private OptimizedArithmeticExpression left;
      private OptimizedArithmeticExpression right;

      @Override
      public BigDecimal eval(final Function<String, BigDecimal> variableBindings) {
        return operator.apply(left.eval(variableBindings), right.eval(variableBindings));
      }

      public String toString() {
        return left.toString() + ' ' + operator.toString() + ' ' + right.toString();
      }
    }

    @Value(staticConstructor = "of")
    class LiteralValue implements OptimizedArithmeticExpression {
      private BigDecimal value;

      @Override
      public BigDecimal eval(final Function<String, BigDecimal> variableBindings) {
        return value;
      }

      public String toString() {
        return value.toString();
      }
    }

    @Value(staticConstructor = "of")
    class VariableReference implements OptimizedArithmeticExpression {
      private String name;

      @Override
      public BigDecimal eval(final Function<String, BigDecimal> variableBindings) {
        return variableBindings.apply(name);
      }

      public String toString() {
        return name;
      }
    }

    static OptimizedArithmeticExpression compile(final String expression) {
      return ArithmeticExpression.compile(expression).optimize();
    }

  }

  interface Lexer {

    @Value(staticConstructor = "of")
    class Lexeme {
      private String value;
    }

    static Lexer standard() {
      return new StandardLexer();
    }

    default Seq<Lexeme> lex(final String string) {
      final SeqBuilder<Lexeme> sequenceBuilder = Seq.builder();
      if (!lex(string, sequenceBuilder::add)) {
        throw new IllegalArgumentException("Invalid character in expression.");
      }
      return sequenceBuilder.result();
    }

    boolean lex(final String string, final Consumer<Lexeme> tokenHandler);

  }

  static class StandardLexer implements Lexer {

    private enum State {
      SPACE,
      VARIABLE,
      NUMBER,
      OPERATOR,
    }

    private static boolean isOperatorChar(final char c) {
      return "~!@#$%^&*-+=:/|\\?<>/".indexOf(c) >= 0;
    }

    private static boolean isVarNameStartChar(final char c) {
      return Character.isLetter(c);
    }

    private static boolean isVarNameChar(final char c) {
      return isVarNameStartChar(c) || isNumberStartChar(c) || c == '_';
    }

    private static boolean isNumberStartChar(final char c) {
      return "0123456789".indexOf(c) >= 0;
    }

    private static boolean isNumberChar(final char c) {
      return isNumberStartChar(c) || ".".indexOf(c) >= 0;
    }

    private static boolean isSpace(final char c) {
      return " \t\n\r".indexOf(c) >= 0;
    }

    private static boolean isStartOfSomething(final char c) {
      return isVarNameStartChar(c) || isNumberStartChar(c) || isOperatorChar(c);
    }

    @Override
    public boolean lex(final String string, final Consumer<Lexeme> tokenHandler) {
      State state = State.SPACE;
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i <= string.length(); i += 1) {
        final char lookingAt = i < string.length() ? string.charAt(i) : ' ';
        stateHandler:
        for (; ; ) {
          switch (state) {
            case SPACE:
              switch (lookingAt) {
                case '(':
                  tokenHandler.accept(Lexeme.of("("));
                  break stateHandler;
                case ')':
                  tokenHandler.accept(Lexeme.of(")"));
                  break stateHandler;
                default:
                  if (isStartOfSomething(lookingAt)) {
                    sb.setLength(0);
                    sb.append(lookingAt);
                    if (isOperatorChar(lookingAt)) {
                      state = State.OPERATOR;
                    } else if (isNumberStartChar(lookingAt)) {
                      state = State.NUMBER;
                    } else {
                      state = State.VARIABLE;
                    }
                  } else if (!isSpace(lookingAt)) {
                    return false;
                  }
              }
              break stateHandler;
            case VARIABLE:
              if (isVarNameChar(lookingAt)) {
                sb.append(lookingAt);
              } else {
                state = State.SPACE;
                tokenHandler.accept(Lexeme.of(sb.toString()));
                continue stateHandler;
              }
              break stateHandler;
            case NUMBER:
              if (isNumberChar(lookingAt)) {
                sb.append(lookingAt);
              } else {
                state = State.SPACE;
                tokenHandler.accept(Lexeme.of(sb.toString()));
                continue stateHandler;
              }
              break stateHandler;
            case OPERATOR:
              if (isOperatorChar(lookingAt)) {
                sb.append(lookingAt);
              } else {
                state = State.SPACE;
                tokenHandler.accept(Lexeme.of(sb.toString()));
                continue stateHandler;
              }
              break stateHandler;
          }
        }
      }
      return true;
    }

  }

  @RequiredArgsConstructor
  abstract static class DelegatingTokenizer implements Tokenizer {

    private final Tokenizer tokenizer;

    @Override
    public Object identify(final String lexeme) {
      return tokenizer.identify(lexeme);
    }

    @Override
    public boolean isOpeningParenthesis(final String lexeme) {
      return tokenizer.isOpeningParenthesis(lexeme);
    }

    @Override
    public boolean isClosingParenthesis(final String lexeme) {
      return tokenizer.isClosingParenthesis(lexeme);
    }

    @Override
    public boolean isValue(final String lexeme) {
      return tokenizer.isValue(lexeme);
    }

    @Override
    public boolean isOperator(final String lexeme) {
      return tokenizer.isOperator(lexeme);
    }

    @Override
    public boolean isLeftAssociative(final String lexeme) {
      return tokenizer.isLeftAssociative(lexeme);
    }

    @Override
    public Ordering comparePrecedence(final String leftOperator, final String rightOperator) {
      return tokenizer.comparePrecedence(leftOperator, rightOperator);
    }
  }

}
