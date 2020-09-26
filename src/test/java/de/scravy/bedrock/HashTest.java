package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static de.scravy.bedrock.Quadruple.quadruple;

@SuppressWarnings("ClassInitializerMayBeStatic")
@RunWith(Spectrum.class)
public class HashTest {

  {
    describe("SHA256", () -> {
      it("should hash empty string", () -> {
        expect(Sha256.singleHash().hash("").toString())
          .toEqual("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
      });
      it("should hash empty collection", () -> {
        expect(Sha256.singleHash().hash(Collections.emptyList()).toString())
          .toEqual("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
      });
      it("should hash empty byte array", () -> {
        expect(Sha256.singleHash().hash((Object) new byte[0]).toString())
          .toEqual("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
      });
      it("should hash ordered collections the same way", () -> {
        final HashAlgorithm<Sha256> algo = Sha256.singleHash();
        final Sha256 _1 = algo.hash("one", "two", "three", "four");
        final Sha256 _2 = algo.hash(Arrays.asList("one", "two", "three", "four"));
        final Sha256 _3 = algo.hash(new LinkedHashSet<String>() {{
          addAll(Arrays.asList("one", "two", "three", "four"));
        }});
        final Sha256 _4 = algo.hash(quadruple("one", "two", "three", "four"));
        expect(_1).toEqual(_2);
        expect(_1).toEqual(_3);
        expect(_1).toEqual(_4);
        expect(_2).toEqual(_3);
        expect(_2).toEqual(_4);
        expect(_3).toEqual(_4);
      });
      it("should hash unordered collections the same way", () -> {
        final HashAlgorithm<Sha256> algo = Sha256.singleHash();
        final Sha256 _1 = algo.hash(new TreeSet<String>() {{
          addAll(Seq.of("one", "two", "three", "four").shuffled().toList());
        }});
        final Sha256 _2 = algo.hash(new HashSet<String>() {{
          addAll(Seq.of("one", "two", "three", "four").shuffled().toList());
        }});
        expect(_1).toEqual(_2);
      });
      it("should hash numbers all the same", () -> {
        final HashAlgorithm<Sha256> algo = Sha256.singleHash();
        final Sha256 _1 = algo.hash((byte) 17);
        final Sha256 _2 = algo.hash((short) 17);
        final Sha256 _3 = algo.hash(17);
        final Sha256 _4 = algo.hash(17L);
        final Sha256 _5 = algo.hash(BigInteger.valueOf(17));
        final Sha256 _6 = algo.hash((float) 17);
        final Sha256 _7 = algo.hash((double) 17);
        final Sha256 _8 = algo.hash(BigDecimal.valueOf(17));
        expect(_1).toEqual(_2);
        expect(_1).toEqual(_3);
        expect(_1).toEqual(_4);
        expect(_1).toEqual(_5);
        expect(_1).toEqual(_6);
        expect(_1).toEqual(_7);
        expect(_1).toEqual(_8);
      });
      it("should hash two list with odd number of elements to the same value", () -> {
        final HashAlgorithm<Sha256> algo = Sha256.doubleHash();
        final Sha256 one = algo.hash(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        final Sha256 two = algo.hash(Seq.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11).toArray());
        expect(one).toEqual(two);
      });
    });
    describe("SHA512", () -> {
      it("should hash empty string", () -> {
        expect(Sha512.singleHash().hash("").toString())
          .toEqual(
            "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce" +
              "47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e");
      });
      it("should hash empty collection", () -> {
        expect(Sha512.singleHash().hash(Collections.emptyList()).toString())
          .toEqual(
            "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce" +
              "47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e");
      });
      it("should hash empty byte array", () -> {
        expect(Sha512.singleHash().hash((Object) new byte[0]).toString())
          .toEqual(
            "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce" +
              "47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e");
      });
    });
    describe("Obj.hash", () -> {
      @Data
      @AllArgsConstructor
      class X {
        private String firstName;
      }
      it("should hash a pojo and a map using SHA256", () -> {
        Sha256 one = Obj.sha256(new X("John Doe"));
        Sha256 two = Obj.sha256(new HashMap<String, String>() {{
          put("firstName", "John Doe");
        }});
        expect(one).toEqual(two);
      });
      it("should hash a pojo and a map using SHA256 (double)", () -> {
        Sha256 one = Obj.sha256double(new X("John Doe"));
        Sha256 two = Obj.sha256double(new HashMap<String, String>() {{
          put("firstName", "John Doe");
        }});
        expect(one).toEqual(two);
      });
      it("should hash a pojo and a map using SHA512", () -> {
        Sha512 one = Obj.sha512(new X("John Doe"));
        Sha512 two = Obj.sha512(new HashMap<String, String>() {{
          put("firstName", "John Doe");
        }});
        expect(one).toEqual(two);
      });
      it("should hash a pojo and a map using SHA512 (double)", () -> {
        Sha512 one = Obj.sha512double(new X("John Doe"));
        Sha512 two = Obj.sha512double(new HashMap<String, String>() {{
          put("firstName", "John Doe");
        }});
        expect(one).toEqual(two);
      });
    });
  }
}
