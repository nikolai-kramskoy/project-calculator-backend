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

## Installation

This server uses single PostgreSQL instance.

### Environment variables

You need to set several env variables:
- `PG_HOST` - by default this server uses `project-calculator-dev` database
- `PG_PORT`
- `PG_USER`
- `PG_PASSWORD`
- `CLIENT_URL` - this is used for CORS

### CLI arguments

You can set needed CLI arguments, for example you can set some
properties for testing and debugging purposes to show generated SQL by
Spring Data JPA, parameters that are used in the queries and query statistics
(by default they are not shown):

- `logging.level.org.hibernate.SQL=DEBUG`
- `logging.level.org.hibernate.stat=DEBUG`
- `logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE`
- `spring.jpa.properties.hibernate.generate_statistics=true`
