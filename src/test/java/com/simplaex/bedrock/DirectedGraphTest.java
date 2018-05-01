package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static com.simplaex.bedrock.Pair.pair;
import static com.simplaex.bedrock.Seq.seq;

@SuppressWarnings("ClassInitializerMayBeStatic")
@RunWith(Spectrum.class)
public class DirectedGraphTest {

  {
    describe("DirectedGraph", () -> {
      it("should count the incoming and outgoing edges", () -> {
        final DirectedGraph<Integer> dg = DirectedGraph.fromEdges(seq(
          pair(1, 2),
          pair(2, 3),
          pair(3, 4),
          pair(4, 5),
          pair(5, 1)
        ));
        expect(dg.countIncomingEdges(1)).toEqual(1);
        expect(dg.countIncomingEdges(2)).toEqual(1);
        expect(dg.countIncomingEdges(3)).toEqual(1);
        expect(dg.countIncomingEdges(4)).toEqual(1);
        expect(dg.countIncomingEdges(5)).toEqual(1);
        expect(dg.countOutgoingEdges(1)).toEqual(1);
        expect(dg.countOutgoingEdges(2)).toEqual(1);
        expect(dg.countOutgoingEdges(3)).toEqual(1);
        expect(dg.countOutgoingEdges(4)).toEqual(1);
        expect(dg.countOutgoingEdges(5)).toEqual(1);
      });
    });
  }

}
