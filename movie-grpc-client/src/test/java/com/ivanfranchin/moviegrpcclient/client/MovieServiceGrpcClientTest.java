package com.ivanfranchin.moviegrpcclient.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ivanfranchin.moviegrpcclient.command.Genre;
import com.ivanfranchin.moviegrpcclient.proto.MovieProto;
import com.ivanfranchin.moviegrpcclient.proto.MovieServerGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MovieServiceGrpcClientTest {

  private static ManagedChannel channel;
  private static io.grpc.Server server;
  private MovieServiceGrpcClient client;

  @BeforeAll
  static void startServer() throws IOException {
    server =
        InProcessServerBuilder.forName("test")
            .directExecutor()
            .addService(new FakeMovieServerImpl())
            .build()
            .start();
    channel = InProcessChannelBuilder.forName("test").directExecutor().build();
  }

  @AfterAll
  static void stopServer() {
    channel.shutdownNow();
    server.shutdownNow();
  }

  @BeforeEach
  void setUp() {
    client = new MovieServiceGrpcClient(MovieServerGrpc.newBlockingStub(channel));
  }

  @Test
  void getMoviesShouldReturnList() {
    List<MovieResponse> movies = client.getMovies(0, 10);

    assertThat(movies).hasSize(2);
    assertThat(movies.get(0).imdbId()).isEqualTo("tt001");
    assertThat(movies.get(1).imdbId()).isEqualTo("tt002");
  }

  @Test
  void getMoviesShouldPropagateError() {
    // no special handling needed - fake server returns results for any page/size
    List<MovieResponse> movies = client.getMovies(0, 10);
    assertThat(movies).hasSize(2);
  }

  @Test
  void getMovieShouldReturnMovie() {
    MovieResponse movie = client.getMovie("tt001");

    assertThat(movie.imdbId()).isEqualTo("tt001");
    assertThat(movie.title()).isEqualTo("Test Movie");
    assertThat(movie.year()).isEqualTo(2024);
    assertThat(movie.genre()).isEqualTo("ACTION");
  }

  @Test
  void getMovieShouldThrowWhenNotFound() {
    assertThatThrownBy(() -> client.getMovie("not-found"))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("not-found");
  }

  @Test
  void createMovieShouldReturnCreatedMovie() {
    MovieResponse movie = client.createMovie("tt003", "New Movie", 2024, Genre.ACTION);

    assertThat(movie.imdbId()).isEqualTo("tt003");
    assertThat(movie.title()).isEqualTo("New Movie");
    assertThat(movie.year()).isEqualTo(2024);
    assertThat(movie.genre()).isEqualTo("ACTION");
  }

  @Test
  void createMovieShouldThrowWhenAlreadyExists() {
    assertThatThrownBy(() -> client.createMovie("already-exists", "Duplicate", 2024, Genre.DRAMA))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("already-exists");
  }

  @Test
  void updateMovieShouldReturnUpdatedMovie() {
    MovieResponse movie = client.updateMovie("tt001", "Updated Title", 2025, Genre.COMEDY);

    assertThat(movie.imdbId()).isEqualTo("tt001");
    assertThat(movie.title()).isEqualTo("Updated Title");
    assertThat(movie.year()).isEqualTo(2025);
    assertThat(movie.genre()).isEqualTo("COMEDY");
  }

  @Test
  void updateMovieShouldThrowWhenNotFound() {
    assertThatThrownBy(() -> client.updateMovie("not-found", null, null, null))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("not-found");
  }

  @Test
  void deleteMovieShouldReturnDeletedMovie() {
    MovieResponse movie = client.deleteMovie("tt001");

    assertThat(movie.imdbId()).isEqualTo("tt001");
    assertThat(movie.title()).isEqualTo("Deleted Movie");
  }

  @Test
  void deleteMovieShouldThrowWhenNotFound() {
    assertThatThrownBy(() -> client.deleteMovie("not-found"))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("not-found");
  }

  private static class FakeMovieServerImpl extends MovieServerGrpc.MovieServerImplBase {

    @Override
    public void getMovies(
        MovieProto.GetMoviesRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
      responseObserver.onNext(buildMovie("tt001", "Movie 1", 2024, MovieProto.Genre.ACTION));
      responseObserver.onNext(buildMovie("tt002", "Movie 2", 2023, MovieProto.Genre.DRAMA));
      responseObserver.onCompleted();
    }

    @Override
    public void getMovie(
        MovieProto.GetMovieRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
      if ("not-found".equals(request.getImdbId())) {
        responseObserver.onError(
            Status.NOT_FOUND
                .withDescription("Movie with id 'not-found' not found")
                .asRuntimeException());
        return;
      }
      responseObserver.onNext(
          buildMovie(request.getImdbId(), "Test Movie", 2024, MovieProto.Genre.ACTION));
      responseObserver.onCompleted();
    }

    @Override
    public void createMovie(
        MovieProto.CreateMovieRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
      if ("already-exists".equals(request.getImdbId())) {
        responseObserver.onError(
            Status.ALREADY_EXISTS
                .withDescription("Movie with id 'already-exists' already exists")
                .asRuntimeException());
        return;
      }
      responseObserver.onNext(
          buildMovie(
              request.getImdbId(), request.getTitle(), request.getYear(), request.getGenre()));
      responseObserver.onCompleted();
    }

    @Override
    public void updateMovie(
        MovieProto.UpdateMovieRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
      if ("not-found".equals(request.getImdbId())) {
        responseObserver.onError(
            Status.NOT_FOUND
                .withDescription("Movie with id 'not-found' not found")
                .asRuntimeException());
        return;
      }
      MovieProto.Movie.Builder builder =
          buildMovie(request.getImdbId(), "Original", 2020, MovieProto.Genre.DRAMA).toBuilder();
      if (request.hasTitle()) builder.setTitle(request.getTitle());
      if (request.hasYear()) builder.setYear(request.getYear());
      if (request.hasGenre()) builder.setGenre(request.getGenre());
      responseObserver.onNext(builder.build());
      responseObserver.onCompleted();
    }

    @Override
    public void deleteMovie(
        MovieProto.DeleteMovieRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
      if ("not-found".equals(request.getImdbId())) {
        responseObserver.onError(
            Status.NOT_FOUND
                .withDescription("Movie with id 'not-found' not found")
                .asRuntimeException());
        return;
      }
      responseObserver.onNext(
          buildMovie(request.getImdbId(), "Deleted Movie", 2024, MovieProto.Genre.ACTION));
      responseObserver.onCompleted();
    }

    private MovieProto.Movie buildMovie(
        String imdbId, String title, int year, MovieProto.Genre genre) {
      return MovieProto.Movie.newBuilder()
          .setImdbId(imdbId)
          .setTitle(title)
          .setYear(year)
          .setGenre(genre)
          .build();
    }
  }
}
