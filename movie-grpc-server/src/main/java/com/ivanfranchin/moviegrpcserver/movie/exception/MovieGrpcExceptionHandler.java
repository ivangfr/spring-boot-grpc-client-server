package com.ivanfranchin.moviegrpcserver.movie.exception;

import io.grpc.Status;
import io.grpc.StatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MovieGrpcExceptionHandler implements GrpcExceptionHandler {

  @Override
  public StatusException handleException(Throwable exception) {
    return switch (exception) {
      case MovieNotFoundException e -> {
        log.error("Not found: {}", e.getMessage());
        yield Status.NOT_FOUND.withDescription(e.getMessage()).asException();
      }
      case MovieAlreadyExistsException e -> {
        log.error("Already exists: {}", e.getMessage());
        yield Status.ALREADY_EXISTS.withDescription(e.getMessage()).asException();
      }
      case IllegalArgumentException e -> {
        log.error("Invalid argument: {}", e.getMessage());
        yield Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asException();
      }
      default -> {
        log.error("Unexpected error", exception);
        yield Status.INTERNAL.withDescription(exception.getMessage()).asException();
      }
    };
  }
}
