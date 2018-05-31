FROM openjdk:10-jre

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/futuremessages.jar"]
ENV SPRING_PROFILES_ACTIVE "prod"
ARG JAR_FILE
ADD target/${JAR_FILE} /usr/share/futuremessages.jar
