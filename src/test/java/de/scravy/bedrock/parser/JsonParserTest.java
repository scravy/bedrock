package de.scravy.bedrock.parser;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greghaskins.spectrum.Spectrum;
import de.scravy.bedrock.*;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Map;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static com.mscharhag.oleaster.matcher.Matchers.expect;
import static de.scravy.bedrock.Pair.pair;

@SuppressWarnings("ClassInitializerMayBeStatic")
@RunWith(Spectrum.class)
public class JsonParserTest {

  {
    final String complexJson = "{\n" +
      "    \"apiVersion\": \"v1\",\n" +
      "    \"data\": {\n" +
      "        \"config\": \"apiVersion: kubeproxy.config.k8s.io/v1alpha1\\nbindAddress: 0.0.0.0\\nclientConnection:\\n  acceptContentTypes: \\\"\\\"\\n  burst: 10\\n  contentType: application/vnd.kubernetes.protobuf\\n  kubeconfig: /var/lib/kube-proxy/kubeconfig\\n  qps: 5\\nclusterCIDR: \\\"\\\"\\nconfigSyncPeriod: 15m0s\\nconntrack:\\n  max: 0\\n  maxPerCore: 32768\\n  min: 131072\\n  tcpCloseWaitTimeout: 1h0m0s\\n  tcpEstablishedTimeout: 24h0m0s\\nenableProfiling: false\\nhealthzBindAddress: 0.0.0.0:10256\\nhostnameOverride: \\\"\\\"\\niptables:\\n  masqueradeAll: false\\n  masqueradeBit: 14\\n  minSyncPeriod: 0s\\n  syncPeriod: 30s\\nipvs:\\n  excludeCIDRs: null\\n  minSyncPeriod: 0s\\n  scheduler: \\\"\\\"\\n  syncPeriod: 30s\\nkind: KubeProxyConfiguration\\nmetricsBindAddress: 127.0.0.1:10249\\nmode: \\\"iptables\\\"\\nnodePortAddresses: null\\noomScoreAdj: -998\\nportRange: \\\"\\\"\\nresourceContainer: \\\"\\\"\\nudpIdleTimeout: 250ms\"\n" +
      "    },\n" +
      "    \"kind\": \"ConfigMap\",\n" +
      "    \"metadata\": {\n" +
      "        \"annotations\": {\n" +
      "            \"kubectl.kubernetes.io/last-applied-configuration\": \"{\\\"apiVersion\\\":\\\"v1\\\",\\\"data\\\":{\\\"config\\\":\\\"apiVersion: kubeproxy.config.k8s.io/v1alpha1\\\\nbindAddress: 0.0.0.0\\\\nclientConnection:\\\\n  acceptContentTypes: \\\\\\\"\\\\\\\"\\\\n  burst: 10\\\\n  contentType: application/vnd.kubernetes.protobuf\\\\n  kubeconfig: /var/lib/kube-proxy/kubeconfig\\\\n  qps: 5\\\\nclusterCIDR: \\\\\\\"\\\\\\\"\\\\nconfigSyncPeriod: 15m0s\\\\nconntrack:\\\\n  max: 0\\\\n  maxPerCore: 32768\\\\n  min: 131072\\\\n  tcpCloseWaitTimeout: 1h0m0s\\\\n  tcpEstablishedTimeout: 24h0m0s\\\\nenableProfiling: false\\\\nhealthzBindAddress: 0.0.0.0:10256\\\\nhostnameOverride: \\\\\\\"\\\\\\\"\\\\niptables:\\\\n  masqueradeAll: false\\\\n  masqueradeBit: 14\\\\n  minSyncPeriod: 0s\\\\n  syncPeriod: 30s\\\\nipvs:\\\\n  excludeCIDRs: null\\\\n  minSyncPeriod: 0s\\\\n  scheduler: \\\\\\\"\\\\\\\"\\\\n  syncPeriod: 30s\\\\nkind: KubeProxyConfiguration\\\\nmetricsBindAddress: 127.0.0.1:10249\\\\nmode: \\\\\\\"iptables\\\\\\\"\\\\nnodePortAddresses: null\\\\noomScoreAdj: -998\\\\nportRange: \\\\\\\"\\\\\\\"\\\\nresourceContainer: \\\\\\\"\\\\\\\"\\\\nudpIdleTimeout: 250ms\\\"},\\\"kind\\\":\\\"ConfigMap\\\",\\\"metadata\\\":{\\\"annotations\\\":{},\\\"labels\\\":{\\\"eks.amazonaws.com/component\\\":\\\"kube-proxy\\\",\\\"k8s-app\\\":\\\"kube-proxy\\\"},\\\"name\\\":\\\"kube-proxy-config\\\",\\\"namespace\\\":\\\"kube-system\\\"}}\\n\"\n" +
      "        },\n" +
      "        \"creationTimestamp\": \"2019-10-09T09:06:26Z\",\n" +
      "        \"labels\": {\n" +
      "            \"eks.amazonaws.com/component\": \"kube-proxy\",\n" +
      "            \"k8s-app\": \"kube-proxy\"\n" +
      "        },\n" +
      "        \"name\": \"kube-proxy-config\",\n" +
      "        \"namespace\": \"kube-system\",\n" +
      "        \"resourceVersion\": \"127\",\n" +
      "        \"selfLink\": \"/api/v1/namespaces/kube-system/configmaps/kube-proxy-config\",\n" +
      "        \"uid\": \"12ea5464-ea74-11e9-9b70-0aad3c7e5f5e\"\n" +
      "    }\n" +
      "}\n";
    final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    final Parsers.JsonParser parser = Parsers.jsonParser();

    describe("A JsonParser", () -> {
      it("should parse literal values", () -> {
        expect(parser.parse("{}")).toEqual(Mapping.empty());
        expect(parser.parse("[]")).toEqual(Seq.empty());
        expect(parser.parse("null")).toBeNull();
        expect(parser.parse("true")).toEqual(true);
        expect(parser.parse("false")).toEqual(false);
        expect(parser.parse("0")).toEqual(BigDecimal.ZERO);
        expect(parser.parse("0.0")).toEqual(BigDecimal.ZERO);
        expect(parser.parse("0e0")).toEqual(BigDecimal.ZERO);
        expect(parser.parse("0e+0")).toEqual(BigDecimal.ZERO);
        expect(parser.parse("0e-0")).toEqual(BigDecimal.ZERO);
        expect(parser.parse("-0e+0")).toEqual(BigDecimal.ZERO);
        expect(parser.parse("-0e-0")).toEqual(BigDecimal.ZERO);
        expect(parser.parse("-0.0e+0")).toEqual(BigDecimal.ZERO);
        expect(parser.parse("-0.0e-0")).toEqual(BigDecimal.ZERO);
        expect(parser.parse("-1")).toEqual(BigDecimal.ONE.negate());
        expect(parser.parse("1")).toEqual(BigDecimal.ONE);
        expect(parser.parse("\"\"")).toEqual("");
        expect(parser.parse("\"x\"")).toEqual("x");
        expect(parser.parse("\"xyz\"")).toEqual("xyz");
      });
      it("should parse a complex object", () -> {
        final Mapping<String, Object> expected = ArrayMap.of(pair("foo", "bar"), pair("abc", "xyz"));
        expect(parser.parse(" { \"foo\" : \"bar\", \"abc\" : \"xyz\" }")).toEqual(expected);
        expect(parser.parse("{\"foo\":\"bar\",\"abc\":\"xyz\"}")).toEqual(expected);
      });
      it("should parse an array", () -> {
        expect(parser.parse("[[],[]]")).toEqual(
          Seq.of(Seq.empty(), Seq.empty())
        );
        expect(parser.parse("[[null, true, {}],[],1]")).toEqual(
          Seq.of(Seq.of(null, true, Mapping.empty()), Seq.empty(), BigDecimal.ONE)
        );
        parser.parse(complexJson);
      });
      final int iterations = 10000;
      it("some", () -> Control.times(iterations, i -> parser.parse(complexJson)));
      it("some else", () -> Control.times(iterations, i -> objectMapper.readValue(complexJson, Map.class)));
    });
  }
}
