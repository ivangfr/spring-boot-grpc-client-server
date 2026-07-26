package com.ivanfranchin.moviegrpcserver.movie.exception;

public class MovieAlreadyExistsException extends RuntimeException {

  public MovieAlreadyExistsException(String message) {
    super(message);
  }
}
