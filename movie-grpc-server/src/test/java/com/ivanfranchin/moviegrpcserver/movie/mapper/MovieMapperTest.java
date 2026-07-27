package com.ivanfranchin.moviegrpcserver.movie.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ivanfranchin.moviegrpcserver.movie.model.Genre;
import com.ivanfranchin.moviegrpcserver.movie.model.Movie;
import com.ivanfranchin.moviegrpcserver.proto.MovieProto;
import org.junit.jupiter.api.Test;

class MovieMapperTest {

  @Test
  void fromCreateRequestShouldMapAllFields() {
    MovieProto.CreateMovieRequest request =
        MovieProto.CreateMovieRequest.newBuilder()
            .setImdbId("tt001")
            .setTitle("Test Movie")
            .setYear(2024)
            .setGenre(MovieProto.Genre.ACTION)
            .build();

    Movie movie = MovieMapper.fromCreateRequest(request);

    assertThat(movie.getImdbId()).isEqualTo("tt001");
    assertThat(movie.getTitle()).isEqualTo("Test Movie");
    assertThat(movie.getYear()).isEqualTo(2024);
    assertThat(movie.getGenre()).isEqualTo(Genre.ACTION);
  }

  @Test
  void updateFromUpdateRequestShouldUpdateOnlyProvidedFields() {
    Movie movie = new Movie("tt001", "Original", 2020, Genre.DRAMA);

    MovieProto.UpdateMovieRequest request =
        MovieProto.UpdateMovieRequest.newBuilder()
            .setImdbId("tt001")
            .setTitle("Updated Title")
            .build();

    MovieMapper.updateFromUpdateRequest(request, movie);

    assertThat(movie.getTitle()).isEqualTo("Updated Title");
    assertThat(movie.getYear()).isEqualTo(2020);
    assertThat(movie.getGenre()).isEqualTo(Genre.DRAMA);
  }

  @Test
  void updateFromUpdateRequestShouldUpdateAllFields() {
    Movie movie = new Movie("tt001", "Original", 2020, Genre.DRAMA);

    MovieProto.UpdateMovieRequest request =
        MovieProto.UpdateMovieRequest.newBuilder()
            .setImdbId("tt001")
            .setTitle("New Title")
            .setYear(2025)
            .setGenre(MovieProto.Genre.COMEDY)
            .build();

    MovieMapper.updateFromUpdateRequest(request, movie);

    assertThat(movie.getTitle()).isEqualTo("New Title");
    assertThat(movie.getYear()).isEqualTo(2025);
    assertThat(movie.getGenre()).isEqualTo(Genre.COMEDY);
  }

  @Test
  void updateFromUpdateRequestWithNoFieldsShouldNotChangeMovie() {
    Movie movie = new Movie("tt001", "Original", 2020, Genre.DRAMA);

    MovieProto.UpdateMovieRequest request =
        MovieProto.UpdateMovieRequest.newBuilder().setImdbId("tt001").build();

    MovieMapper.updateFromUpdateRequest(request, movie);

    assertThat(movie.getTitle()).isEqualTo("Original");
    assertThat(movie.getYear()).isEqualTo(2020);
    assertThat(movie.getGenre()).isEqualTo(Genre.DRAMA);
  }

  @Test
  void toProtoShouldMapAllFields() {
    Movie movie = new Movie("tt001", "Test Movie", 2024, Genre.ACTION);
    movie.setTitle("Test Movie");
    movie.setYear(2024);
    movie.setGenre(Genre.ACTION);

    MovieProto.Movie proto = MovieMapper.toProto(movie);

    assertThat(proto.getImdbId()).isEqualTo("tt001");
    assertThat(proto.getTitle()).isEqualTo("Test Movie");
    assertThat(proto.getYear()).isEqualTo(2024);
    assertThat(proto.getGenre()).isEqualTo(MovieProto.Genre.ACTION);
  }

  @Test
  void toProtoWithNullFieldsShouldHandleGracefully() {
    Movie movie = new Movie("tt001", null, null, null);

    MovieProto.Movie proto = MovieMapper.toProto(movie);

    assertThat(proto.getImdbId()).isEqualTo("tt001");
    assertThat(proto.getTitle()).isEqualTo("");
    assertThat(proto.getYear()).isEqualTo(0);
    assertThat(proto.getGenre()).isEqualTo(MovieProto.Genre.ACTION);
  }
}
