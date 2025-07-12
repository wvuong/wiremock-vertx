package com.willvuong.wiremockvertx.http;

import io.vertx.core.Vertx;

public enum VertxHolder {

    INSTANCE(Vertx.vertx());

    private final Vertx vertx;

    VertxHolder(Vertx vertx) {
        this.vertx = vertx;
    }

    public Vertx getVertx() {
        return vertx;
    }
}
