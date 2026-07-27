package com.ivanfranchin.moviegrpcclient.client;

import com.ivanfranchin.moviegrpcclient.command.Genre;
import com.ivanfranchin.moviegrpcclient.proto.MovieProto;
import com.ivanfranchin.moviegrpcclient.proto.MovieServerGrpc;
import io.grpc.StatusRuntimeException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MovieServiceGrpcClient {

  private final MovieServerGrpc.MovieServerBlockingStub stub;

  public List<MovieResponse> getMovies(int page, int size) {
    MovieProto.GetMoviesRequest getMoviesRequest =
        MovieProto.GetMoviesRequest.newBuilder().setPage(page).setSize(size).build();
    try {
      Iterator<MovieProto.Movie> movieIterator = stub.getMovies(getMoviesRequest);

      List<MovieResponse> movieResponses = new ArrayList<>();
      movieIterator.forEachRemaining(movie -> movieResponses.add(MovieResponse.from(movie)));
      return movieResponses;
    } catch (StatusRuntimeException e) {
      log.error("Error getting movies", e);
      throw new RuntimeException("Error getting movies: " + e.getStatus().getDescription(), e);
    }
  }

  public MovieResponse getMovie(String imdbId) {
    MovieProto.GetMovieRequest getMovieRequest =
        MovieProto.GetMovieRequest.newBuilder().setImdbId(imdbId).build();
    try {
      MovieProto.Movie movie = stub.getMovie(getMovieRequest);
      return MovieResponse.from(movie);
    } catch (StatusRuntimeException e) {
      log.error("Error getting movie", e);
      throw new RuntimeException("Error getting movie: " + e.getStatus().getDescription(), e);
    }
  }

  public MovieResponse createMovie(String imdbId, String title, Integer year, Genre genre) {
    MovieProto.CreateMovieRequest createMoviesRequest =
        MovieProto.CreateMovieRequest.newBuilder()
            .setImdbId(imdbId)
            .setTitle(title)
            .setYear(year)
            .setGenre(MovieProto.Genre.valueOf(genre.name()))
            .build();
    try {
      MovieProto.Movie movie = stub.createMovie(createMoviesRequest);
      return MovieResponse.from(movie);
    } catch (StatusRuntimeException e) {
      log.error("Error creating movie", e);
      throw new RuntimeException("Error creating movie: " + e.getStatus().getDescription(), e);
    }
  }

  public MovieResponse updateMovie(String imdbId, String title, Integer year, Genre genre) {
    MovieProto.UpdateMovieRequest.Builder builder = MovieProto.UpdateMovieRequest.newBuilder();
    builder.setImdbId(imdbId);
    if (title != null) {
      builder.setTitle(title);
    }
    if (year != null) {
      builder.setYear(year);
    }
    if (genre != null) {
      builder.setGenre(MovieProto.Genre.valueOf(genre.name()));
    }
    MovieProto.UpdateMovieRequest updateMovieRequest = builder.build();
    try {
      MovieProto.Movie movie = stub.updateMovie(updateMovieRequest);
      return MovieResponse.from(movie);
    } catch (StatusRuntimeException e) {
      log.error("Error updating movie", e);
      throw new RuntimeException("Error updating movie: " + e.getStatus().getDescription(), e);
    }
  }

  public MovieResponse deleteMovie(String imdbId) {
    MovieProto.DeleteMovieRequest deleteMovieRequest =
        MovieProto.DeleteMovieRequest.newBuilder().setImdbId(imdbId).build();
    try {
      MovieProto.Movie movie = stub.deleteMovie(deleteMovieRequest);
      return MovieResponse.from(movie);
    } catch (StatusRuntimeException e) {
      log.error("Error deleting movie", e);
      throw new RuntimeException("Error deleting movie: " + e.getStatus().getDescription(), e);
    }
  }
}
