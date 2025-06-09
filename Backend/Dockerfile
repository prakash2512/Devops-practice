###Docker
FROM maven  AS build
WORKDIR /
COPY pom.xml .
COPY src ./src
RUN mvn clean install

# Stage 3: Create the runtime image
FROM azul/zulu-openjdk:21-latest
WORKDIR /
COPY --from=build /target/CCM-0.0.1-SNAPSHOT.jar /CCM-0.0.1-SNAPSHOT.jar
EXPOSE 9000
CMD ["java", "-jar", "CCM-0.0.1-SNAPSHOT.jar"]
