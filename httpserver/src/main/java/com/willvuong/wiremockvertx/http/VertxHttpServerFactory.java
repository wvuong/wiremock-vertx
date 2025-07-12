package com.willvuong.wiremockvertx.http;

import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.http.AdminRequestHandler;
import com.github.tomakehurst.wiremock.http.HttpServer;
import com.github.tomakehurst.wiremock.http.HttpServerFactory;
import com.github.tomakehurst.wiremock.http.StubRequestHandler;
import io.vertx.core.Vertx;

public class VertxHttpServerFactory implements HttpServerFactory {

    private final Vertx vertx;
    private final int eventLoops;

    public VertxHttpServerFactory(Vertx vertx, int eventLoops) {
        this.vertx = vertx;
        this.eventLoops = eventLoops;
    }

    @Override
    public HttpServer buildHttpServer(Options options,
                                      AdminRequestHandler adminRequestHandler,
                                      StubRequestHandler stubRequestHandler) {

        return new VertxHttpServer(vertx, eventLoops, options, adminRequestHandler, stubRequestHandler);
    }
}
