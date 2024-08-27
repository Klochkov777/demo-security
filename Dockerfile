FROM karluto/jdk21-apline3.18
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /security-demo.jar
COPY src/main/resources/application.yml /application.yml
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/security-demo.jar", "--spring.config.location=file:/application.yml"]
