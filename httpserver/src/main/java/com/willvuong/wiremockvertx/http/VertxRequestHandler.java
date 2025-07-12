package com.willvuong.wiremockvertx.http;

import com.github.tomakehurst.wiremock.http.AdminRequestHandler;
import com.github.tomakehurst.wiremock.http.StubRequestHandler;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;

public class VertxRequestHandler implements Handler<HttpServerRequest> {

    private final AdminRequestHandler adminRequestHandler;
    private final StubRequestHandler stubRequestHandler;

    public VertxRequestHandler(AdminRequestHandler adminRequestHandler, StubRequestHandler stubRequestHandler) {
        this.adminRequestHandler = adminRequestHandler;
        this.stubRequestHandler = stubRequestHandler;
    }

    @Override
    public void handle(HttpServerRequest request) {
        request.bodyHandler(body -> {
            final VertxHttpServerRequestAdapter req = new VertxHttpServerRequestAdapter(request, body);
            final VertxHttpResponder httpResponder = new VertxHttpResponder(request.response());

            if (request.path().startsWith("/__admin/")) {
                adminRequestHandler.handle(req, httpResponder, null);

            } else {
                stubRequestHandler.handle(req, httpResponder, null);
            }
        });
    }
}
