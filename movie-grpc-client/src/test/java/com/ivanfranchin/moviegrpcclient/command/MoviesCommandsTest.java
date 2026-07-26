package com.ivanfranchin.moviegrpcclient.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.ivanfranchin.moviegrpcclient.client.MovieResponse;
import com.ivanfranchin.moviegrpcclient.client.MovieServiceGrpcClient;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
class MoviesCommandsTest {

  @MockitoBean private MovieServiceGrpcClient movieServiceGrpcClient;

  @Autowired private MoviesCommands moviesCommands;

  @Test
  void getMoviesShouldReturnList() {
    when(movieServiceGrpcClient.getMovies(0, 10))
        .thenReturn(List.of(new MovieResponse("tt001", "Test Movie", 2024, "ACTION")));

    List<MovieResponse> result = moviesCommands.getMovies(0, 10);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).imdbId()).isEqualTo("tt001");
  }

  @Test
  void getMovieShouldReturnMovie() {
    when(movieServiceGrpcClient.getMovie("tt001"))
        .thenReturn(new MovieResponse("tt001", "Test Movie", 2024, "ACTION"));

    MovieResponse result = moviesCommands.getMovie("tt001");

    assertThat(result.imdbId()).isEqualTo("tt001");
  }

  @Test
  void createMovieShouldReturnCreatedMovie() {
    when(movieServiceGrpcClient.createMovie("tt001", "New Movie", 2024, Genre.ACTION))
        .thenReturn(new MovieResponse("tt001", "New Movie", 2024, "ACTION"));

    MovieResponse result = moviesCommands.createMovie("tt001", "New Movie", 2024, Genre.ACTION);

    assertThat(result.imdbId()).isEqualTo("tt001");
  }

  @Test
  void updateMovieShouldReturnUpdatedMovie() {
    when(movieServiceGrpcClient.updateMovie("tt001", "Updated", 2025, Genre.COMEDY))
        .thenReturn(new MovieResponse("tt001", "Updated", 2025, "COMEDY"));

    MovieResponse result = moviesCommands.updateMovie("tt001", "Updated", 2025, Genre.COMEDY);

    assertThat(result.title()).isEqualTo("Updated");
  }

  @Test
  void deleteMovieShouldReturnDeletedMovie() {
    when(movieServiceGrpcClient.deleteMovie("tt001"))
        .thenReturn(new MovieResponse("tt001", "Deleted Movie", 2024, "ACTION"));

    MovieResponse result = moviesCommands.deleteMovie("tt001");

    assertThat(result.imdbId()).isEqualTo("tt001");
  }
}
