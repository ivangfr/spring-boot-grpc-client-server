package com.ivanfranchin.moviegrpcclient.client;

import com.ivanfranchin.movieserver.movie.model.MovieProto;

public record MovieResponse(String imdbId, String title, Integer year, String genre) {

  public static MovieResponse from(MovieProto.Movie movie) {
    return new MovieResponse(
        movie.getImdbId(), movie.getTitle(), movie.getYear(), movie.getGenre().name());
  }
}
