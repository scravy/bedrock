package com.simplaex.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.val;
import org.junit.Assert;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.function.Function;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Spectrum.class)
public class UUIDsTest {

  {
    describe("conversion to and from bytes", () -> {

      it("toBytes composed with fromBytes is the same as id", () -> {
        val f = ((Function<byte[], UUID>) UUIDs::fromBytes).compose(UUIDs::toBytes);
        val u = UUID.randomUUID();
        val r = f.apply(u);
        Assert.assertThat(r, equalTo(u));
      });

    });

    describe("v3", () -> {

      it("should create a UUID of version 3", () -> {
        val uuid = UUIDs.v3(UUIDs.NAMESPACE_URL, "text");
        Assert.assertEquals(3, uuid.version());
      });

    });

    describe("v4", () -> {

      it("should create a UUID of version 4", () -> {
        val uuid = UUIDs.v4();
        assertThat(uuid.version(), equalTo(4));
      });

    });

    describe("v5", () -> {

      it("should create a UUID of version 5", () -> {
        val uuid = UUIDs.v5(UUIDs.NAMESPACE_OID, "text");
        Assert.assertEquals(5, uuid.version());
      });

    });
  }

}
