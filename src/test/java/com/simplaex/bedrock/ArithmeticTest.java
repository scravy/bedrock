package com.simplaex.bedrock;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.function.BinaryOperator;

import static com.simplaex.bedrock.Arithmetic.*;

public class ArithmeticTest {

  // ArithmeticExpression

  @Test
  public void shouldCompileAndEvaluateExpression() {

    val expression = ArithmeticExpression.compile("foo + 4 * 2 / (1 - 5) ^ 2 ^ 3");
    val bindings = new HashMap<String, BigDecimal>();

    bindings.put("foo", BigDecimal.ONE);
    Assert.assertEquals(new BigDecimal("1.0001220703125"), expression.eval(ArrayMap.ofMap(bindings)));

    bindings.put("foo", BigDecimal.TEN);
    Assert.assertEquals(new BigDecimal("10.0001220703125"), expression.eval(ArrayMap.ofMap(bindings)));

    bindings.put("foo", BigDecimal.ZERO);
    Assert.assertEquals(new BigDecimal("0.0001220703125"), expression.eval(ArrayMap.ofMap(bindings)));
  }

  // Lexer

  @Test
  public void simpleExpression() {
    val result = Lexer.standard().lex("hello+3");
    val expected = Seq.of(
      Lexer.Lexeme.of("hello"),
      Lexer.Lexeme.of("+"),
      Lexer.Lexeme.of("3")
    );
    Assert.assertEquals(expected, result);
  }

  @Test
  public void simpleExpressionWithWhitespace() {
    val result = Lexer.standard().lex("   hello  + 3 ");
    val expected = Seq.of(
      Lexer.Lexeme.of("hello"),
      Lexer.Lexeme.of("+"),
      Lexer.Lexeme.of("3")
    );
    Assert.assertEquals(expected, result);
  }


  @Test
  public void complexExpression() {
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
  }


  // OptimizedArithmeticExpression

  @Test
  public void shouldOptimizeExpressions() {
    val expression = OptimizedArithmeticExpression.compile("foo + 4 * 2 / (1 - 5) ^ 2 ^ 3");
    Assert.assertTrue(expression instanceof OptimizedArithmeticExpression.BinaryOperation);
    val op = (OptimizedArithmeticExpression.BinaryOperation) expression;
    Assert.assertTrue(op.getLeft() instanceof OptimizedArithmeticExpression.VariableReference);
    Assert.assertTrue(op.getRight() instanceof OptimizedArithmeticExpression.LiteralValue);
  }

  @Test
  public void shouldOptimizeSpecialCaseOneTimesFoo() {
    val expression = OptimizedArithmeticExpression.compile("1 * foo");
    Assert.assertTrue(expression instanceof OptimizedArithmeticExpression.VariableReference);
  }

  @Test
  public void shouldOptimizeSpecialCaseFooTimesOne() {
    val expression = OptimizedArithmeticExpression.compile("foo * 1");
    Assert.assertTrue(expression instanceof OptimizedArithmeticExpression.VariableReference);
  }

  @Test
  public void shouldOptimizeSpecialCaseZeroPlusFoo() {
    val expression = OptimizedArithmeticExpression.compile("0 + foo");
    Assert.assertTrue(expression instanceof OptimizedArithmeticExpression.VariableReference);
  }

  @Test
  public void shouldOptimizeSpecialCaseFooPlusZero() {
    val expression = OptimizedArithmeticExpression.compile("foo + 0");
    Assert.assertTrue(expression instanceof OptimizedArithmeticExpression.VariableReference);
  }

  @Test
  public void shouldOptimizeSpecialCaseFooTimesZero() {
    val expression = OptimizedArithmeticExpression.compile("(a + b) * 0");
    Assert.assertTrue(expression instanceof OptimizedArithmeticExpression.LiteralValue);
    Assert.assertEquals(BigDecimal.ZERO, expression.eval(ArrayMap.empty()));
  }

  @Test
  public void shouldOptimizeSpecialCaseZeroTimesFoo() {
    val expression = OptimizedArithmeticExpression.compile("0 * (a + b)");
    Assert.assertTrue(expression instanceof OptimizedArithmeticExpression.LiteralValue);
    Assert.assertEquals(BigDecimal.ZERO, expression.eval(ArrayMap.empty()));
  }

  @Test
  public void shouldOptimizeSpecialCaseFooToThePowerOfZero() {
    val expression = OptimizedArithmeticExpression.compile("(a + b) ^ 0");
    Assert.assertTrue(expression instanceof OptimizedArithmeticExpression.LiteralValue);
    Assert.assertEquals(BigDecimal.ONE, expression.eval(ArrayMap.empty()));
  }

  @Test
  public void shouldOptimizeSpecialCaseFooToThePowerOfOne() {
    val expression = OptimizedArithmeticExpression.compile("foo ^ 1");
    Assert.assertTrue(expression instanceof OptimizedArithmeticExpression.VariableReference);
  }

  @Test
  public void shouldEvaluateOptimizedExpressions() {

    val expression = ArithmeticExpression.compile("foo + 4 * 2 / (1 - 5) ^ 2 ^ 3").optimize();
    val bindings = new HashMap<String, BigDecimal>();

    bindings.put("foo", BigDecimal.ONE);
    Assert.assertEquals(new BigDecimal("1.0001220703125"), expression.eval(ArrayMap.ofMap(bindings)));

    bindings.put("foo", BigDecimal.TEN);
    Assert.assertEquals(new BigDecimal("10.0001220703125"), expression.eval(ArrayMap.ofMap(bindings)));

    bindings.put("foo", BigDecimal.ZERO);
    Assert.assertEquals(new BigDecimal("0.0001220703125"), expression.eval(ArrayMap.ofMap(bindings)));

  }


  // Parser

  @Test
  public void shouldRespectPrecedencesAndAssociativity() {
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
  }

  /**
   * This test showcases how to on-the-fly interpret/evaluate an expression.
   */
  @Test
  public void shouldInterpreteExpression() {
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
  }

}
