# project-calculator-backend

This backend allows clients to estimate financial and time
(https://en.wikipedia.org/wiki/Three-point_estimation) costs of projects.
Typical CRUD operations are available for key models. Projects are divided
into milestones and features. Projects and milestones as a whole are estimated
based on the estimates of each feature of the project. The client can view
all his projects with estimates.

Estimates are recomputed when any of the project components change and are
stored in the database, so this mechanism avoids time-consuming computations
of estimates for all projects with each GET request.

## Usage

This server is dependent only on single PostgreSQL instance.

### Environment variables

You need to set several environment variables:
- `POSTGRES_HOST`
- `POSTGRES_PORT`
- `POSTGRES_DB`
- `POSTGRES_USER`
- `POSTGRES_PASSWORD`

You may optionally set this environment variables:
- `CLIENT_URL` - if this variable is set, then it is used for CORS
- `JDK_JAVA_OPTIONS`
- `CLI_ARGS`

You can create `.env` file in this directory and place there all the 
needed environment variables so docker compose will automatically read
them and inject in the containers.

### Launch

In this case you're having `.env` file can simply run this command (otherwise
you also can use `-e` option with `docker compose`):

```shell
docker compose up
```

You also can use `mvn` directly to compile and package it without docker.

### CLI arguments

You can set needed CLI arguments, for example you can set some
properties for testing and debugging purposes to show generated SQL by
Spring Data JPA, parameters that are used in the queries and query statistics
(by default they are not shown):

- `logging.level.org.hibernate.SQL=DEBUG`
- `logging.level.org.hibernate.stat=DEBUG`
- `logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE`
- `spring.jpa.properties.hibernate.generate_statistics=true`
