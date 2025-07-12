package com.willvuong.wiremockvertx.http;

import com.github.tomakehurst.wiremock.http.*;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;

import java.util.*;

public class VertxHttpServerRequestAdapter implements Request {

    private final HttpServerRequest request;
    private final RequestMethod requestMethod;
    private final HttpHeaders httpHeaders;
    private final Map<String, QueryParameter> queryParameters;
    private final Map<String, FormParameter> formParameters;
    private final Map<String, Cookie> cookies;
    private final Buffer body;
    private String base64Encoded;

    public VertxHttpServerRequestAdapter(HttpServerRequest request, Buffer body) {
        this.request = request;
        this.requestMethod = requestMethod(request.method());
        this.httpHeaders = httpHeaders(request.headers());
        this.queryParameters = queryParameters(request.params());
        this.formParameters = formParameters(request.formAttributes());
        this.cookies = cookies(request.cookies());
        this.body = body;
    }

    private RequestMethod requestMethod(HttpMethod httpMethod) {
        return RequestMethod.fromString(httpMethod.name());
    }

    private HttpHeaders httpHeaders(MultiMap headers) {
        List<HttpHeader> list = new ArrayList<>(headers.size());
        headers.forEach((k, v) -> list.add(new HttpHeader(k, headers.getAll(k))));

        return new HttpHeaders(list);
    }

    private Map<String, QueryParameter> queryParameters(MultiMap params) {
        final Map<String, QueryParameter> result = new HashMap<>();
        params.forEach((k, v) -> result.put(k, new QueryParameter(k, params.getAll(k))));

        return result;
    }

    private Map<String, FormParameter> formParameters(MultiMap attributes) {
        final Map<String, FormParameter> result = new HashMap<>();
        attributes.forEach((k, v) -> result.put(k, new FormParameter(k, attributes.getAll(k))));

        return result;
    }

    private Map<String, Cookie> cookies(Set<io.vertx.core.http.Cookie> cookies) {
        final Map<String, Cookie> result = new HashMap<>();
        cookies.forEach(c -> result.put(c.getName(), new Cookie(c.getValue())));

        return result;
    }

    @Override
    public String getUrl() {
        return request.uri();
    }

    @Override
    public String getAbsoluteUrl() {
        return request.absoluteURI();
    }

    @Override
    public RequestMethod getMethod() {
        return requestMethod;
    }

    @Override
    public String getScheme() {
        return request.scheme();
    }

    @Override
    public String getHost() {
        return request.authority().host();
    }

    @Override
    public int getPort() {
        return request.authority().port();
    }

    @Override
    public String getClientIp() {
        return request.remoteAddress().hostAddress();
    }

    @Override
    public String getHeader(String key) {
        return httpHeaders.getHeader(key).firstValue();
    }

    @Override
    public HttpHeader header(String key) {
        return httpHeaders.getHeader(key);
    }

    @Override
    public ContentTypeHeader contentTypeHeader() {
        return httpHeaders.getContentTypeHeader();
    }

    @Override
    public HttpHeaders getHeaders() {
        return httpHeaders;
    }

    @Override
    public boolean containsHeader(String key) {
        return httpHeaders.getHeader(key) != null;
    }

    @Override
    public Set<String> getAllHeaderKeys() {
        return httpHeaders.keys();
    }

    @Override
    public QueryParameter queryParameter(String key) {
        return queryParameters.get(key);
    }

    @Override
    public FormParameter formParameter(String key) {
        return formParameters.get(key);
    }

    @Override
    public Map<String, FormParameter> formParameters() {
        return formParameters;
    }

    @Override
    public Map<String, Cookie> getCookies() {
        return cookies;
    }

    @Override
    public byte[] getBody() {
        return body.getBytes();
    }

    @Override
    public String getBodyAsString() {
        return body.toString();
    }

    @Override
    public String getBodyAsBase64() {
        if (base64Encoded == null) {
            base64Encoded = Base64.getEncoder().encodeToString(body.getBytes());
        }

        return base64Encoded;
    }

    @Override
    public boolean isMultipart() {
        /// TODO
        return false;
    }

    @Override
    public Collection<Part> getParts() {
        // TODO
        return List.of();
    }

    @Override
    public Part getPart(String name) {
        // TODO
        return null;
    }

    @Override
    public boolean isBrowserProxyRequest() {
        return false;
    }

    @Override
    public Optional<Request> getOriginalRequest() {
        return Optional.empty();
    }

    @Override
    public String getProtocol() {
        return request.version().alpnName();
    }
}
