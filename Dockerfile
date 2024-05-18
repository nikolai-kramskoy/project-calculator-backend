FROM maven:3-eclipse-temurin-17-alpine AS mvn-build
WORKDIR /project-calculator
COPY pom.xml ./
RUN ["mvn", "dependency:go-offline"]
COPY src/ src/
RUN ["mvn", "package"]

FROM eclipse-temurin:17
WORKDIR /project-calculator
COPY config/ config/
COPY --from=mvn-build /project-calculator/target/project-calculator*.jar project-calculator.jar
EXPOSE 8080
CMD ["java", "-jar", "project-calculator.jar"]
