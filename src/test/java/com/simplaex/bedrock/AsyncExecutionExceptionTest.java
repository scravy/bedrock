package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings("ClassInitializerMayBeStatic")
@RunWith(Spectrum.class)
public class AsyncExecutionExceptionTest {

  {
    describe("AsyncExecutionExceptionTest", () -> {
      it("should convert a static Object to a message when it is actually a string", () -> {
        final Object obj = "actually a message";
        final Exception exc = new AsyncExecutionException(obj);
        expect(exc.getMessage()).toEqual("actually a message");
      });
      it("should convert a static Exception to the cause when it is actually an exception", () -> {
        final Exception obj = new RuntimeException("actually a message");
        final Exception exc = new AsyncExecutionException(obj);
        expect(exc.getCause().getClass()).toEqual(RuntimeException.class);
      });
    });
  }

}
