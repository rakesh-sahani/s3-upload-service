FROM  openjdk:17-alpine
ENV CONFIG_SERVER_URL=http://config-server:8888
ENV HOSTNAME=eureka-server
ENV SPR_PROFILE=docker
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]