package com.willvuong.wiremockvertx.http;

import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.HttpResponder;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;

import java.util.Map;

public class VertxHttpResponder implements HttpResponder {

    private final HttpServerResponse httpServerResponse;

    public VertxHttpResponder(HttpServerResponse response) {
        this.httpServerResponse = response;
    }

    @Override
    public void respond(Request request, Response response, Map<String, Object> attributes) {
        for (HttpHeader httpHeader : response.getHeaders().all()) {
            httpServerResponse.putHeader(httpHeader.key(), httpHeader.getValues());
        }

        httpServerResponse
                .setStatusCode(response.getStatus())
                .end(Buffer.buffer(response.getBody()));
    }
}
