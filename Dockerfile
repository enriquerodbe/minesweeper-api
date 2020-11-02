FROM mozilla/sbt:8u232_1.3.13 as dist
ARG version=1.0.0
COPY build.sbt /dist/
COPY project/ /dist/project/
WORKDIR /dist
RUN sbt update
COPY . /dist
RUN sbt -Dminesweeper.api.version=$version dist &&\
 unzip target/universal/minesweeper-api-$version.zip -d /app

FROM openjdk:8-jre-alpine
RUN apk add --no-cache bash
COPY --from=dist /app /app
WORKDIR /app
ENV HTTP_PORT 9000
CMD bin/minesweeper-api -Dpidfile.path=/dev/null -Dhttp.port=$HTTP_PORT
EXPOSE $HTTP_PORT
