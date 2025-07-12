package com.willvuong.wiremockvertx.http;

import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.http.AdminRequestHandler;
import com.github.tomakehurst.wiremock.http.HttpServer;
import com.github.tomakehurst.wiremock.http.StubRequestHandler;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class VertxHttpServer implements HttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxHttpServer.class);

    private final Vertx vertx;
    private final int eventLoops;
    private final AdminRequestHandler adminRequestHandler;
    private final StubRequestHandler stubRequestHandler;
    private final HttpServerOptions httpServerOptions;
    private final AtomicBoolean started = new AtomicBoolean();

    public VertxHttpServer(Vertx vertx,
                           int eventLoops,
                           Options options,
                           AdminRequestHandler adminRequestHandler,
                           StubRequestHandler stubRequestHandler) {

        this.vertx = vertx;
        this.eventLoops = eventLoops;
        this.adminRequestHandler = adminRequestHandler;
        this.stubRequestHandler = stubRequestHandler;
        this.httpServerOptions = httpServerOptions(options);
    }

    private HttpServerOptions httpServerOptions(Options options) {
        final HttpServerOptions httpServerOptions = new HttpServerOptions();
        httpServerOptions.setPort(options.portNumber());
        httpServerOptions.setUseAlpn(!options.getHttp2TlsDisabled());
        httpServerOptions.setHttp2ClearTextEnabled(!options.getHttp2PlainDisabled());

        return httpServerOptions;
    }

    @Override
    public void start() {
        final DeploymentOptions options = new DeploymentOptions().setInstances(eventLoops);
        vertx.deployVerticle(() -> new VertxHttpServerVerticle(httpServerOptions, adminRequestHandler, stubRequestHandler), options).onSuccess(id -> {
            LOGGER.info("Deployment id={}", id);
            started.set(true);
        }).onFailure(throwable -> {
            LOGGER.error("Exception while deploying", throwable);
        });
    }

    @Override
    public void stop() {
        vertx.close();
    }

    @Override
    public boolean isRunning() {
        return started.get();
    }

    @Override
    public int port() {
        return httpServerOptions.getPort();
    }

    @Override
    public int httpsPort() {
        return httpServerOptions.getPort();
    }
}
