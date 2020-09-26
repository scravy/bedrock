package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import org.junit.runner.RunWith;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;

@RunWith(Spectrum.class)
@SuppressWarnings("ClassInitializerMayBeStatic")
public class RingBufferTest {
  {
    describe("RingBuffer", () -> {
      it("should and elements and report size correctly", () -> {
        final RingBuffer<String> buf = new RingBuffer<>(4);
        expect(buf.size()).toEqual(0);
        expect(buf.offer("A")).toBeTrue();
        expect(buf.size()).toEqual(1);
        expect(buf.offer("B")).toBeTrue();
        expect(buf.size()).toEqual(2);
        expect(buf.offer("C")).toBeTrue();
        expect(buf.size()).toEqual(3);
        expect(buf.offer("D")).toBeTrue();
        expect(buf.size()).toEqual(4);
        expect(buf.offer("E")).toBeFalse();
        expect(buf.size()).toEqual(4);
        expect(buf.peek()).toEqual("A");
        expect(buf.size()).toEqual(4);
        expect(buf.poll()).toEqual("A");
        expect(buf.size()).toEqual(3);
        expect(buf.poll()).toEqual("B");
        expect(buf.size()).toEqual(2);
        expect(buf.offer("E")).toBeTrue();
        expect(buf.size()).toEqual(3);
        expect(buf.offer("F")).toBeTrue();
        expect(buf.size()).toEqual(4);
        expect(buf.offer("G")).toBeFalse();
        expect(buf.size()).toEqual(4);
        expect(buf.poll()).toEqual("C");
        expect(buf.size()).toEqual(3);
        expect(buf.poll()).toEqual("D");
        expect(buf.size()).toEqual(2);
        expect(buf.poll()).toEqual("E");
        expect(buf.size()).toEqual(1);
        expect(buf.poll()).toEqual("F");
        expect(buf.size()).toEqual(0);
        expect(buf.poll()).toEqual(null);
        expect(buf.size()).toEqual(0);
        expect(buf.offer("H")).toBeTrue();
        expect(buf.size()).toEqual(1);
        expect(buf.offer("I")).toBeTrue();
        expect(buf.size()).toEqual(2);
        expect(buf.offer("J")).toBeTrue();
        expect(buf.size()).toEqual(3);
        expect(buf.offer("K")).toBeTrue();
        expect(buf.size()).toEqual(4);
        expect(buf.offer("L")).toBeFalse();
        expect(buf.size()).toEqual(4);
        expect(buf.poll()).toEqual("H");
        expect(buf.size()).toEqual(3);
      });
      it("should iterate correctly", () -> {
        final RingBuffer<String> buf = new RingBuffer<>(4);
        buf.offer("A");
        buf.offer("B");
        expect(buf.poll()).toEqual("A");
        final Iterator<String> it = buf.iterator();
        expect(it.hasNext()).toBeTrue();
        expect(it.next()).toEqual("B");
        expect(it.hasNext()).toBeFalse();
        expect(buf.offer("C")).toBeTrue();
        expect(buf.offer("D")).toBeTrue();
        expect(buf.offer("E")).toBeTrue();
        expect(it::next).toThrow(ConcurrentModificationException.class);
        final Iterator<String> it2 = buf.iterator();
        expect(it2.hasNext()).toBeTrue();
        expect(it2.next()).toEqual("B");
        expect(it2.next()).toEqual("C");
        expect(it2.next()).toEqual("D");
        expect(it2.next()).toEqual("E");
        expect(it2.hasNext()).toBeFalse();
        expect(it2::next).toThrow(EmptyIteratorException.class);
      });
    });
  }
}
