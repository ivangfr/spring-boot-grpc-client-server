package com.ivanfranchin.moviegrpcserver.movie;

import com.ivanfranchin.moviegrpcserver.movie.mapper.MovieMapper;
import com.ivanfranchin.moviegrpcserver.movie.model.Movie;
import com.ivanfranchin.moviegrpcserver.proto.MovieProto;
import com.ivanfranchin.moviegrpcserver.proto.MovieServerGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class MovieGrpcService extends MovieServerGrpc.MovieServerImplBase {

  private final MovieService movieService;

  @Override
  public void getMovies(
      MovieProto.GetMoviesRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
    movieService.getMovies(request.getPage(), request.getSize()).stream()
        .map(MovieMapper::toProto)
        .forEach(responseObserver::onNext);
    responseObserver.onCompleted();

    log.info("Get movies with page {} and size {}", request.getPage(), request.getSize());
  }

  @Override
  public void getMovie(
      MovieProto.GetMovieRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
    Movie movie = movieService.validateAndGetMovieById(request.getImdbId());

    MovieProto.Movie movieProtoMovie = MovieMapper.toProto(movie);
    responseObserver.onNext(movieProtoMovie);
    responseObserver.onCompleted();

    log.info("Get movie {}", movie);
  }

  @Override
  public void createMovie(
      MovieProto.CreateMovieRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
    Movie movie = MovieMapper.fromCreateRequest(request);
    movie = movieService.createMovie(movie);

    MovieProto.Movie movieProtoMovie = MovieMapper.toProto(movie);
    responseObserver.onNext(movieProtoMovie);
    responseObserver.onCompleted();

    log.info("Created movie {}", movie);
  }

  @Override
  public void updateMovie(
      MovieProto.UpdateMovieRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
    Movie movie = movieService.validateAndGetMovieById(request.getImdbId());
    MovieMapper.updateFromUpdateRequest(request, movie);

    movieService.saveMovie(movie);

    MovieProto.Movie movieProtoMovie = MovieMapper.toProto(movie);
    responseObserver.onNext(movieProtoMovie);
    responseObserver.onCompleted();

    log.info("Updated movie {}", movie);
  }

  @Override
  public void deleteMovie(
      MovieProto.DeleteMovieRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
    Movie movie = movieService.validateAndGetMovieById(request.getImdbId());
    movieService.deleteMovie(movie);

    MovieProto.Movie movieProtoMovie = MovieMapper.toProto(movie);
    responseObserver.onNext(movieProtoMovie);
    responseObserver.onCompleted();

    log.info("Deleted movie {}", movie);
  }
}
