# spring-boot-grpc-client-server

The goal of this project is to implement two [`Spring Boot`](https://docs.spring.io/spring-boot/index.html) applications using [`gRPC`](https://grpc.io/): the server, called `movie-grpc-server`, and the shell client, named `movie-grpc-client`. The library `movie-grpc-lib` defines the `gRPC` interface that both the server and client applications use.

## Proof-of-Concepts & Articles

On [ivangfr.github.io](https://ivangfr.github.io), I have compiled my Proof-of-Concepts (PoCs) and articles. You can easily search for the technology you are interested in by using the filter. Who knows, perhaps I have already implemented a PoC or written an article about what you are looking for.

## Additional Readings

- \[**Medium**\] [**Implementing gRPC Server and Client using Spring Boot**](https://medium.com/@ivangfr/implementing-grpc-server-and-client-using-spring-boot-4411b26138be)

## Applications

- **movie-grpc-lib**

  A Maven project that defines the `gRPC` interface (using [`Protocol Buffers`](https://protobuf.dev/)) for managing movies. This library is shared by both the `movie-grpc-server` and `movie-grpc-client` to ensure they can communicate properly over `gRPC`.

- **movie-grpc-server**

  A Spring Boot web application that has `movie-grpc-lib` as dependency. It implements the `gRPC` functions for managing movies and runs a `gRPC` server to handle `movie-grpc-client` calls. The movies are stored in a [`PostgreSQL`](https://www.postgresql.org/) database.

- **movie-grpc-client**

  A Spring Boot shell application that has `movie-grpc-lib` as dependency. It uses a `stub` to call `movie-grpc-server` functions.

## Prerequisites

- [`Java 21`](https://www.oracle.com/java/technologies/downloads/#java21) or higher;
- A containerization tool (e.g., [`Docker`](https://www.docker.com), [`Podman`](https://podman.io), etc.)

## Packaging and Installing movie-grpc-lib

In a terminal and inside the `spring-boot-grpc-client-server` root folder, run the command below:
```bash
./mvnw clean install --projects movie-grpc-lib
```

## Start PostgreSQL Docker container

Run the command below to start `postgres` Docker container
```bash
docker run -d --name postgres \
  -p 5432:5432 \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=moviesdb \
  postgres:17.6
```

## Running applications

- **movie-grpc-server**

  In a terminal and inside the `spring-boot-grpc-client-server` root folder, run the following command:
  ```bash
  ./mvnw clean spring-boot:run --projects movie-grpc-server
  ```

- **movie-grpc-client**

  Open another terminal and make sure you are in the `spring-boot-grpc-client-server` root folder. Then, run the command below to build the executable jar file:
  ```bash
  ./mvnw clean package --projects movie-grpc-client -DskipTests
  ```

  Finally, to start the client shell, run:
  ```bash
  ./movie-grpc-client/target/movie-grpc-client-0.0.1-SNAPSHOT.jar
  ```

## Demo

![demo](/documentation/demo.gif)

## Shutdown

- To stop the applications, go to the terminals where they are running and press `Ctrl+C`.
- To stop the `postgres` Docker container, run:
  ```bash
  docker rm -fv postgres
  ```
