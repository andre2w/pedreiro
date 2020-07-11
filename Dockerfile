FROM oracle/graalvm-ce:20.1.0-java11 as graalvm
RUN gu install native-image

COPY . /home/app/pedreiro
WORKDIR /home/app/pedreiro

RUN native-image --no-server -cp build/libs/pedreiro-*-all.jar

FROM frolvlad/alpine-glibc
RUN apk update && apk add libstdc++
EXPOSE 8080
COPY --from=graalvm /home/app/pedreiro/pedreiro /app/pedreiro
ENTRYPOINT ["/app/pedreiro"]
