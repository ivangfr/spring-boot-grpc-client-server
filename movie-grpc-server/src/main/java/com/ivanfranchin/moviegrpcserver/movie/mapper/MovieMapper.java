package com.ivanfranchin.moviegrpcserver.movie.mapper;

import com.ivanfranchin.moviegrpcserver.movie.model.Genre;
import com.ivanfranchin.moviegrpcserver.movie.model.Movie;
import com.ivanfranchin.movieserver.movie.model.MovieProto;

public class MovieMapper {

  public static Movie fromCreateRequest(MovieProto.CreateMovieRequest request) {
    return new Movie(
        request.getImdbId(),
        request.getTitle(),
        request.getYear(),
        toServerGenre(request.getGenre()));
  }

  public static void updateFromUpdateRequest(MovieProto.UpdateMovieRequest request, Movie movie) {
    if (request.hasTitle()) {
      movie.setTitle(request.getTitle());
    }
    if (request.hasYear()) {
      movie.setYear(request.getYear());
    }
    if (request.hasGenre()) {
      movie.setGenre(toServerGenre(request.getGenre()));
    }
  }

  public static MovieProto.Movie toProto(Movie movie) {
    MovieProto.Movie.Builder builder = MovieProto.Movie.newBuilder().setImdbId(movie.getImdbId());
    if (movie.getTitle() != null) {
      builder.setTitle(movie.getTitle());
    }
    if (movie.getYear() != null) {
      builder.setYear(movie.getYear());
    }
    if (movie.getGenre() != null) {
      builder.setGenre(MovieProto.Genre.valueOf(movie.getGenre().name()));
    }
    return builder.build();
  }

  private static Genre toServerGenre(MovieProto.Genre protoGenre) {
    return Genre.valueOf(protoGenre.name());
  }
}
