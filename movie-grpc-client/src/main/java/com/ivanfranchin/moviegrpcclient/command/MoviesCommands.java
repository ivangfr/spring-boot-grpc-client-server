package com.ivanfranchin.moviegrpcclient.command;

import com.ivanfranchin.moviegrpcclient.client.MovieResponse;
import com.ivanfranchin.moviegrpcclient.client.MovieServiceGrpcClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MoviesCommands {

  private final MovieServiceGrpcClient movieServiceGrpcClient;

  @Command(name = "get-movies", description = "Get movies")
  public List<MovieResponse> getMovies(
      @Option(longName = "page", defaultValue = "0") int page,
      @Option(longName = "size", defaultValue = "10") int size) {
    return movieServiceGrpcClient.getMovies(page, size);
  }

  @Command(name = "get-movie", description = "Get movie")
  public MovieResponse getMovie(@Option(longName = "imdbId", required = true) String imdbId) {
    return movieServiceGrpcClient.getMovie(imdbId);
  }

  @Command(name = "create-movie", description = "Create movie")
  public MovieResponse createMovie(
      @Option(longName = "imdbId", required = true) String imdbId,
      @Option(longName = "title", required = true) String title,
      @Option(longName = "year", required = true) Integer year,
      @Option(longName = "genre", required = true) Genre genre) {
    return movieServiceGrpcClient.createMovie(imdbId, title, year, genre);
  }

  @Command(name = "update-movie", description = "Update movie")
  public MovieResponse updateMovie(
      @Option(longName = "imdbId", required = true) String imdbId,
      @Option(longName = "title") String title,
      @Option(longName = "year") Integer year,
      @Option(longName = "genre") Genre genre) {
    return movieServiceGrpcClient.updateMovie(imdbId, title, year, genre);
  }

  @Command(name = "delete-movie", description = "Delete movie")
  public MovieResponse deleteMovie(@Option(longName = "imdbId", required = true) String imdbId) {
    return movieServiceGrpcClient.deleteMovie(imdbId);
  }
}
