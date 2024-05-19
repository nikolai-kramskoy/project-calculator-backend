FROM maven:3-eclipse-temurin-21-alpine AS mvn-build
WORKDIR /project-calculator
COPY pom.xml ./
RUN ["mvn", "dependency:go-offline"]
COPY src/ src/
RUN ["mvn", "package", "-DskipTests"]

FROM eclipse-temurin:21-alpine
WORKDIR /project-calculator
COPY config/ config/
COPY --from=mvn-build /project-calculator/target/project-calculator*.jar project-calculator.jar
EXPOSE 8080
RUN addgroup -S project-calculator && adduser -S project-calculator -G project-calculator
USER project-calculator
ENTRYPOINT ["sh", "-c", "java -jar project-calculator.jar ${CLI_ARGS}"]
