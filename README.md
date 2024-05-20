# project-calculator-backend

This backend allows clients to estimate time and financial costs of projects.

Projects are divided into milestones and features. Features may be assigned
to a milestone of a project.

Time cost for a feature is computed using three-point estimation
(https://en.wikipedia.org/wiki/Three-point_estimation). Time costs of a
project/milestone is sum of time costs of all features that belong to
the project/milestone.

Financial cost of a project/milestone/feature is computed based on the number
of team members that develop the project, the rates per hour for each position
of team member (each team member has fixed position) and the time costs of the
project/milestone/feature.

Typical CRUD operations are available for key models. User can view all his
projects/milestones/features with estimates.

## Usage

This server is dependent only on single PostgreSQL instance.

### Launch

In the case you're having `.env` file you can simply run this command
(otherwise you can use `-e` option with `docker compose` or some other method
of supplying environment variables):

```shell
docker compose up
```

You can also use `mvn` directly to compile and package it without docker,
but then you will need to provide PostgreSQL instance yourself.

### Environment variables

You can create `.env` file in this directory and place there all the
needed environment variables so docker compose will automatically read
them and inject in the containers.

You need to set these several environment variables:
- `POSTGRES_HOST`
- `POSTGRES_PORT`
- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`

You may optionally set these environment variables:
- `CORS_ORIGINS` - if this variable is set, then the values are used as CORS
origins. Example: `CORS_ORIGINS="http://localhost:9000 https://example.com"`.
- `JDK_JAVA_OPTIONS` - example: `JDK_JAVA_OPTIONS="-ea -Ddebug"`.
- `CLI_ARGS` - example: 
`CLI_ARGS="--logging.level.org.hibernate.SQL=DEBUG --logging.level.org.hibernate.stat=DEBUG"`.

### CLI arguments

These are common useful properties for testing and debugging purposes
(e.g. to show generated SQL by Spring Data JPA, parameters that are used
in the queries and query statistics (by default they are not shown)):
- `logging.level.org.hibernate.SQL=DEBUG`
- `logging.level.org.hibernate.stat=DEBUG`
- `logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE`
- `spring.jpa.properties.hibernate.generate_statistics=true`

## Possible improvements

- Implement pagination for `GET` for projects/milestones/features
- Think more about isolation levels for `@Transactional`, maybe in some
methods non-default isolation levels will be more appropriate 
- Introduce roles for users (e.g. admin, user)
- Introduce some missing API endpoints (like `DELETE /users/{userId}`)
- Improve OpenAPI docs (like describing that at project's creation other
related entities are created)
- Make more unit-tests to cover more lines of code and branches
