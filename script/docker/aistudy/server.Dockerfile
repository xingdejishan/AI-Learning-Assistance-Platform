FROM maven:3.8.8-eclipse-temurin-8 AS build

WORKDIR /workspace
COPY . .
RUN mvn -pl yudao-server -am clean package -DskipTests

FROM eclipse-temurin:8-jre

WORKDIR /app
ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Djava.security.egd=file:/dev/./urandom"
ENV ARGS=""

COPY --from=build /workspace/yudao-server/target/yudao-server.jar /app/app.jar

EXPOSE 48080
CMD ["sh", "-c", "java ${JAVA_OPTS} -jar /app/app.jar ${ARGS}"]
