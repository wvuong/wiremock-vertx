#!/bin/bash

if [[ -z "${MEMORY_OPTS}" ]]; then
  MEMORY_OPTS="-Xms512M -Xmx512M"
fi

echo "MEMORY_OPTS=${MEMORY_OPTS}"

if [[ -z "${GC_OPTS}" ]]; then
  GC_OPTS="-Xlog:gc -XX:+UseParallelGC"
fi

echo "GC_OPTS=${GC_OPTS}"

exec java --enable-native-access=ALL-UNNAMED --sun-misc-unsafe-memory-access=allow \
  --add-opens=java.base/java.lang=ALL-UNNAMED -XX:+PrintCommandLineFlags \
   -server $MEMORY_OPTS -XX:+UseNUMA $GC_OPTS \
  -Djava.lang.Integer.IntegerCache.high=10000 -Dvertx.disableMetrics=true \
  -Dvertx.disableWebsockets=true -Dvertx.disableContextTimings=true \
  -Dvertx.disableHttpHeadersValidation=true -Dvertx.cacheImmutableHttpResponseHeaders=true \
  -Dvertx.internCommonHttpRequestHeadersToLowerCase=true -Dio.netty.noUnsafe=false \
  -Dio.netty.buffer.checkBounds=false -Dio.netty.buffer.checkAccessible=false \
  $JVM_OPTS \
  -jar benchmarkserver-fat.jar