package com.willvuong.wiremockvertx.http;

import com.github.tomakehurst.wiremock.http.AdminRequestHandler;
import com.github.tomakehurst.wiremock.http.StubRequestHandler;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;

public class VertxHttpServerVerticle extends VerticleBase {

    private final HttpServerOptions httpServerOptions;
    private final AdminRequestHandler adminRequestHandler;
    private final StubRequestHandler stubRequestHandler;

    private HttpServer httpServer;

    public VertxHttpServerVerticle(HttpServerOptions httpServerOptions,
                                   AdminRequestHandler adminRequestHandler,
                                   StubRequestHandler stubRequestHandler) {
        this.httpServerOptions = httpServerOptions;
        this.adminRequestHandler = adminRequestHandler;
        this.stubRequestHandler = stubRequestHandler;
    }

    @Override
    public Future<?> start() throws Exception {
        httpServer = vertx.createHttpServer(httpServerOptions)
                .requestHandler(new VertxRequestHandler(adminRequestHandler, stubRequestHandler));

        return httpServer.listen();
    }

    @Override
    public Future<?> stop() throws Exception {
        return httpServer.close();
    }
}
