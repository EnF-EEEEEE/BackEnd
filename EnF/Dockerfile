FROM amazoncorretto:17 AS builder
ARG MODULE_NAME

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

COPY ${MODULE_NAME} ${MODULE_NAME}
COPY module-domain module-domain

RUN chmod +x ./gradlew

RUN ./gradlew :${MODULE_NAME}:bootJar
FROM amazoncorretto:17
ARG MODULE_NAME

COPY --from=builder ${MODULE_NAME}/build/libs/*.jar app.jar

ENTRYPOINT [ "java","-jar","/app.jar" ]

VOLUME /tmp