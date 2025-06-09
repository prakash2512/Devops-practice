###Docker
FROM maven  AS build
COPY pom.xml .
COPY src ./src
RUN mvn clean install

# Stage 3: Create the runtime image
FROM azul/zulu-openjdk:21-latest
WORKDIR /
COPY --from=build /target/Renew-0.0.1-SNAPSHOT.jar /Renew-0.0.1-SNAPSHOT.jar
EXPOSE 9000
CMD ["java", "-jar", "Renew-0.0.1-SNAPSHOT.jar"]
