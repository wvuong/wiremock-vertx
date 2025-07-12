# wiremock-vertx

This project is an implementation of a [WireMock](https://github.com/wiremock/wiremock) server that replaces the embedded Jetty server with [Vert.x](https://github.com/eclipse-vertx/vert.x). 

### Why? 

When using WireMock as part of a load test, the Servlet architecture does not scale as well as a Netty-based reactive non-blocking architecture.
While a servlet architecture is great for most production workloads, it is not the best for a load test situation (see [TechEmpower benchmarks](https://www.techempower.com/benchmarks/#hw=ph&test=plaintext&section=data-r23&l=zik0vz-pa7&p=zik0za-zik0zj-zik0zj-zik0vz-zik0zj-18y67)).

Also, why not?