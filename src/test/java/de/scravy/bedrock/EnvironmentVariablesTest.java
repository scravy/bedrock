package de.scravy.bedrock;

import com.greghaskins.spectrum.Spectrum;
import lombok.Data;
import org.junit.runner.RunWith;

import java.time.Duration;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static de.scravy.bedrock.Pair.pair;

@SuppressWarnings({"CodeBlock2Expr", "ClassInitializerMayBeStatic"})
@RunWith(Spectrum.class)
public class EnvironmentVariablesTest {

  @Data
  static class Config {
    private String schema = "someschema";
    private String host = "localhost";
    private int port;
    private Double factor = null;
    private Duration duration = Duration.ofSeconds(10);
  }

  {
    describe("EnvironmentVariables", () -> {
      it("readInto", () -> {
        EnvironmentVariables.setDefaultEnvironmentVariableRetriever(ArrayMap.of(
          pair("HOST", "example.org"),
          pair("PORT", "8080"),
          pair("FACTOR", "0.0"),
          pair("DURATION", "PT7H")
        ).setDefaultReturnValue(null));
        final Config config = EnvironmentVariables.read(Config.class);
        expect(config.getHost()).toEqual("example.org");
        expect(config.getPort()).toEqual(8080);
        expect(config.getSchema()).toEqual("someschema");
        expect(config.getDuration()).toEqual(Duration.ofHours(7));
      });
    });
  }
}
