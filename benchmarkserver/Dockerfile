FROM eclipse-temurin:24.0.1_9-jdk-noble
WORKDIR /app

COPY target/benchmarkserver-fat.jar entrypoint.sh .

EXPOSE 8080

ENTRYPOINT ["/app/entrypoint.sh"]