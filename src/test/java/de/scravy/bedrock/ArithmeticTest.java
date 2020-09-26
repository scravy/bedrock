package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.Assert;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static de.scravy.bedrock.Arithmetic.*;

@SuppressWarnings({"ClassInitializerMayBeStatic", "CodeBlock2Expr"})
@RunWith(Spectrum.class)
public class ArithmeticTest {
  {
    describe(Arithmetic.class.getSimpleName(), () -> {
      it("minimum(int...)", () -> {
        expect(Arithmetic.minimum(2, 1, 9, 0, 3, -4, 9, -2)).toEqual(-4);
      });
      it("maximum(int...)", () -> {
        expect(Arithmetic.maximum(2, 1, 9, 0, 3, -4, 9, -2)).toEqual(9);
      });
      it("minimum(long...)", () -> {
        expect(Arithmetic.minimum(2L, 1L, 9L, 0L, 3L, -4L, 9L, -2L)).toEqual(-4L);
      });
      it("minimum()", () -> {
        expect(Arithmetic::minimum).toThrow(IllegalArgumentException.class);
      });
      it("minimum(new int[0])", () -> {
        expect(() -> Arithmetic.minimum(new int[0])).toThrow(IllegalArgumentException.class);
      });
      it("minimum(new long[0])", () -> {
        expect(() -> Arithmetic.minimum(new long[0])).toThrow(IllegalArgumentException.class);
      });
      it("minimum(new double[0])", () -> {
        expect(() -> Arithmetic.minimum(new double[0])).toThrow(IllegalArgumentException.class);
      });
      it("maximum(long...)", () -> {
        expect(Arithmetic.maximum(2L, 1L, 9L, 0L, 3L, -4L, 9L, -2L)).toEqual(9L);
      });
      it("minimum(double...)", () -> {
        expect(Arithmetic.minimum(2.0, 1.0, 9.0, 0.0, 3.0, -4.0, 9.0, -2.0)).toEqual(-4.0);
      });
      it("maximum(double...)", () -> {
        expect(Arithmetic.maximum(2.0, 1.0, 9.0, 0.0, 3.0, -4.0, 9.0, -2.0)).toEqual(9.0);
      });
      it("maximum()", () -> {
        expect(Arithmetic::maximum).toThrow(IllegalArgumentException.class);
      });
      it("maximum(new int[0])", () -> {
        expect(() -> Arithmetic.maximum(new int[0])).toThrow(IllegalArgumentException.class);
      });
      it("maximum(new long[0])", () -> {
        expect(() -> Arithmetic.maximum(new long[0])).toThrow(IllegalArgumentException.class);
      });
      it("maximum(new double[0])", () -> {
        expect(() -> Arithmetic.maximum(new double[0])).toThrow(IllegalArgumentException.class);
      });
    });
    describe(Expression.class.getSimpleName(), () -> {
      it("should compile and evaluate expression", () -> {
        final Expression expression = Expression.compile("foo + 4 * 2 / (1 - 5) ^ 2 ^ 3");
        final Map<String, BigDecimal> bindings = new HashMap<>();

        bindings.put("foo", BigDecimal.ONE);
        Assert.assertEquals(new BigDecimal("1.0001220703125"), expression.eval(ArrayMap.ofMap(bindings)));

        bindings.put("foo", BigDecimal.TEN);
        Assert.assertEquals(new BigDecimal("10.0001220703125"), expression.eval(ArrayMap.ofMap(bindings)));

        bindings.put("foo", BigDecimal.ZERO);
        Assert.assertEquals(new BigDecimal("0.0001220703125"), expression.eval(ArrayMap.ofMap(bindings)));
      });
    });
    describe(Lexer.class.getSimpleName(), () -> {
      it("should lex a simple expression", () -> {
        val result = Lexer.standard().lex("hello+3");
        val expected = Seq.of(
          Lexer.Lexeme.of("hello"),
          Lexer.Lexeme.of("+"),
          Lexer.Lexeme.of("3")
        );
        Assert.assertEquals(expected, result);
      });
      it("should lex a simple expression with whitespace", () -> {
        val result = Lexer.standard().lex("   hello  + 3 ");
        val expected = Seq.of(
          Lexer.Lexeme.of("hello"),
          Lexer.Lexeme.of("+"),
          Lexer.Lexeme.of("3")
        );
        Assert.assertEquals(expected, result);
      });
      it("should lex a complex expression", () -> {
        val result = Lexer.standard().lex("(a&&b)||c(quux+b) - foo+baz/3.0+((a_b) **c)");
        val expected = Seq.of(
          Lexer.Lexeme.of("("),
          Lexer.Lexeme.of("a"),
          Lexer.Lexeme.of("&&"),
          Lexer.Lexeme.of("b"),
          Lexer.Lexeme.of(")"),
          Lexer.Lexeme.of("||"),
          Lexer.Lexeme.of("c"),
          Lexer.Lexeme.of("("),
          Lexer.Lexeme.of("quux"),
          Lexer.Lexeme.of("+"),
          Lexer.Lexeme.of("b"),
          Lexer.Lexeme.of(")"),
          Lexer.Lexeme.of("-"),
          Lexer.Lexeme.of("foo"),
          Lexer.Lexeme.of("+"),
          Lexer.Lexeme.of("baz"),
          Lexer.Lexeme.of("/"),
          Lexer.Lexeme.of("3.0"),
          Lexer.Lexeme.of("+"),
          Lexer.Lexeme.of("("),
          Lexer.Lexeme.of("("),
          Lexer.Lexeme.of("a_b"),
          Lexer.Lexeme.of(")"),
          Lexer.Lexeme.of("**"),
          Lexer.Lexeme.of("c"),
          Lexer.Lexeme.of(")")
        );
        Assert.assertEquals(expected, result);
      });
    });
    describe(OptimizedExpression.class.getSimpleName(), () -> {
      it("should optimize expressions", () -> {
        val expression = OptimizedExpression.compile("foo + 4 * 2 / (1 - 5) ^ 2 ^ 3");
        Assert.assertTrue(expression instanceof OptimizedExpression.BinaryOperation);
        val op = (OptimizedExpression.BinaryOperation) expression;
        Assert.assertTrue(op.getLeft() instanceof OptimizedExpression.VariableReference);
        Assert.assertTrue(op.getRight() instanceof OptimizedExpression.LiteralValue);
      });
      it("should optimize special case one times foo", () -> {

        val expression = OptimizedExpression.compile("1 * foo");
        Assert.assertTrue(expression instanceof OptimizedExpression.VariableReference);
      });
      it("should optimize special case foo times one", () -> {
        val expression = OptimizedExpression.compile("foo * 1");
        Assert.assertTrue(expression instanceof OptimizedExpression.VariableReference);
      });
      it("should optimize special case zero plus foo", () -> {
        val expression = OptimizedExpression.compile("0 + foo");
        Assert.assertTrue(expression instanceof OptimizedExpression.VariableReference);
      });
      it("should optimize special case foo plus zero", () -> {
        val expression = OptimizedExpression.compile("foo + 0");
        Assert.assertTrue(expression instanceof OptimizedExpression.VariableReference);
      });
      it("should optimize special case foo times zero", () -> {
        val expression = OptimizedExpression.compile("(a + b) * 0");
        Assert.assertTrue(expression instanceof OptimizedExpression.LiteralValue);
        Assert.assertEquals(BigDecimal.ZERO, expression.eval(ArrayMap.empty()));
      });
      it("should optimize special case zero times foo", () -> {
        val expression = OptimizedExpression.compile("0 * (a + b)");
        Assert.assertTrue(expression instanceof OptimizedExpression.LiteralValue);
        Assert.assertEquals(BigDecimal.ZERO, expression.eval(ArrayMap.empty()));
      });
      it("should optimize special case foo to the power of zero", () -> {
        val expression = OptimizedExpression.compile("(a + b) ^ 0");
        Assert.assertTrue(expression instanceof OptimizedExpression.LiteralValue);
        Assert.assertEquals(BigDecimal.ONE, expression.eval(ArrayMap.empty()));
      });
      it("should optimize special case foo to the power of one", () -> {
        val expression = OptimizedExpression.compile("foo ^ 1");
        Assert.assertTrue(expression instanceof OptimizedExpression.VariableReference);
      });
      it("should evaluate optimized expressions", () -> {
        val expression = Expression.compile("foo + 4 * 2 / (1 - 5) ^ 2 ^ 3").optimize();
        val bindings = new HashMap<String, BigDecimal>();

        bindings.put("foo", BigDecimal.ONE);
        Assert.assertEquals(new BigDecimal("1.0001220703125"), expression.eval(ArrayMap.ofMap(bindings)));

        bindings.put("foo", BigDecimal.TEN);
        Assert.assertEquals(new BigDecimal("10.0001220703125"), expression.eval(ArrayMap.ofMap(bindings)));

        bindings.put("foo", BigDecimal.ZERO);
        Assert.assertEquals(new BigDecimal("0.0001220703125"), expression.eval(ArrayMap.ofMap(bindings)));
      });
    });
    describe(Parser.class.getSimpleName(), () -> {
      it("should respect precedences and associativity", () -> {
        val parser = Arithmetic.Parser.arithmetic();
        val result = parser.parse("ab + 4 * 2 / (1 - 5) ^ 2 ^ 3");
        val expected = Seq.of(
          "ab",
          "4",
          "2",
          "*",
          "1",
          "5",
          "-",
          "2",
          "3",
          "^",
          "^",
          "/",
          "+"
        );
        Assert.assertEquals(expected, result.map(t -> t.getLexeme().getValue()));
      });
      it("should interprete expression", () -> {
        val parser = Arithmetic.Parser.arithmetic();
        val stack = new ArrayDeque<BigDecimal>();
        parser.parse("quux + 4 * 2 / (1 - 5) ^ 2 ^ 3", token -> {
          val value = token.getValue();
          if (value instanceof BigDecimal) {
            stack.push((BigDecimal) value);
          } else if (value instanceof String) {
            stack.push(BigDecimal.TEN);
          } else {
            val two = stack.pop();
            val one = stack.pop();
            @SuppressWarnings("unchecked") val result = ((BinaryOperator<BigDecimal>) value).apply(one, two);
            stack.push(result);
          }
        });
        Assert.assertEquals(Seq.of(new BigDecimal("10.0001220703125")), Seq.ofCollection(stack));
      });
    });
  }
}
