package com.willvuong.wiremockvertx;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.AsynchronousResponseSettings;
import com.github.tomakehurst.wiremock.common.JettySettings;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.google.common.primitives.Ints;
import com.willvuong.wiremockvertx.http.VertxHttpServerFactory;
import io.netty.channel.epoll.Epoll;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.impl.cpu.CpuCoreSensor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestServer.class);

    private static final String RANDOM_STRING_16K = RandomStringUtils.insecure().nextAscii(1024 * 16);
    private static final String RANDOM_STRING_64K = RandomStringUtils.insecure().nextAscii(1024 * 64);
    private static final String RANDOM_STRING_256K = RandomStringUtils.insecure().nextAscii(1024 * 256);

    public static void main(String... args) throws Exception {
        final WireMockConfiguration options = new WireMockConfiguration();
        options.disableRequestJournal();
        options.http2PlainDisabled(true);
        options.http2TlsDisabled(true);

        int availableProcessors = CpuCoreSensor.availableProcessors();
        LOGGER.info("cpus={}", availableProcessors);

        String mode = getEnv("MODE", "");
        LOGGER.info("mode={}", mode);

        if ("vertx".equalsIgnoreCase(mode)) {
            final VertxOptions vertxOptions = new VertxOptions();

            final Boolean preferNativeTransport = getEnvAsBool("VERTX_PREFER_NATIVE_TRANSPORT", Boolean.FALSE);
            vertxOptions.setPreferNativeTransport(preferNativeTransport);

            Integer eventLoops = getEnvAsInt("VERTX_EVENT_LOOPS", CpuCoreSensor.availableProcessors() * 2);
            vertxOptions.setEventLoopPoolSize(eventLoops);

            LOGGER.info("vertx eventLoops={}", vertxOptions.getEventLoopPoolSize());
            LOGGER.info("vertx preferNativeTransport={}", vertxOptions.getPreferNativeTransport());
            LOGGER.info("vertx epoll={}", Epoll.isAvailable());

            final Vertx vertx = Vertx.vertx(vertxOptions);
            options.httpServerFactory(new VertxHttpServerFactory(vertx, eventLoops));

        } else if ("jetty".equalsIgnoreCase(mode)) {
            Integer jettyAcceptors = getEnvAsInt("JETTY_ACCEPTORS", availableProcessors);
            options.jettyAcceptors(jettyAcceptors);
            options.asynchronousResponseEnabled(true);
            Integer jettyAsyncResponseThreads = getEnvAsInt("JETTY_ASYNC_RESPONSE_THREADS", availableProcessors * 2);
            options.asynchronousResponseThreads(jettyAsyncResponseThreads);

            LOGGER.info("jetty acceptors={}", jettyAcceptors);
            LOGGER.info("jetty async enabled=true");
            LOGGER.info("jetty async threads={}", jettyAsyncResponseThreads);

        } else {
            JettySettings jettySettings = options.jettySettings();
            AsynchronousResponseSettings asynchronousResponseSettings = options.getAsynchronousResponseSettings();
            LOGGER.info("jetty acceptors={}", jettySettings.getAcceptors());
            LOGGER.info("jetty async enabled={}", asynchronousResponseSettings.isEnabled());
            LOGGER.info("jetty async threads={}", asynchronousResponseSettings.getThreads());
        }

        final WireMockServer server = new WireMockServer(options);
        server.start();

        server.addStubMapping(handler("/ping", "pong"));
        server.addStubMapping(handler("/16k", RANDOM_STRING_16K));
        server.addStubMapping(handler("/64k", RANDOM_STRING_64K));
        server.addStubMapping(handler("/256k", RANDOM_STRING_256K));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Shutting down Wiremock");
            server.shutdownServer();
        }));
    }

    private static StubMapping handler(String path, String bytes) {
        final RequestPattern requestPattern = RequestPatternBuilder
                .newRequestPattern(RequestMethod.GET, WireMock.urlEqualTo(path))
                .build();

        final StubMapping stubMapping = new StubMapping();
        stubMapping.setRequest(requestPattern);
        stubMapping.setResponse(ResponseDefinitionBuilder.responseDefinition()
                .withBody(bytes)
                .withStatus(200)
                .build());

        return stubMapping;
    }

    private static String getEnv(String name, String defaultValue) {
        final String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return value;
    }

    private static Integer getEnvAsInt(String name, Integer defaultValue) {
        final String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return Ints.tryParse(value);
    }

    private static Boolean getEnvAsBool(String name, Boolean defaultValue) {
        final String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return Boolean.valueOf(value);
    }
}
