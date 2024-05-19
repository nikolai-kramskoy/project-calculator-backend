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
