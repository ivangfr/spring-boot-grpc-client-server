package com.ivanfranchin.moviegrpcclient.client;

import static org.assertj.core.api.Assertions.assertThat;

import com.ivanfranchin.moviegrpcclient.proto.MovieProto;
import org.junit.jupiter.api.Test;

class MovieResponseTest {

  @Test
  void fromShouldMapProtoToRecord() {
    MovieProto.Movie proto =
        MovieProto.Movie.newBuilder()
            .setImdbId("tt001")
            .setTitle("Test Movie")
            .setYear(2024)
            .setGenre(MovieProto.Genre.ACTION)
            .build();

    MovieResponse response = MovieResponse.from(proto);

    assertThat(response.imdbId()).isEqualTo("tt001");
    assertThat(response.title()).isEqualTo("Test Movie");
    assertThat(response.year()).isEqualTo(2024);
    assertThat(response.genre()).isEqualTo("ACTION");
  }
}
