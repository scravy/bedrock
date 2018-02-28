package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class SeqBuilderTest {

  {
    describe("Seq.builder()", () -> {
      it("iterate", () -> {
        val list = new ArrayList<Integer>();
        Seq.<Integer>builder().addAll(1, 2, 3).forEach(list::add);
        expect(list).toEqual(Arrays.asList(1, 2, 3));
      });
    });
  }

}
