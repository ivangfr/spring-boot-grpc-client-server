package com.ivanfranchin.moviegrpcserver.movie;

import com.ivanfranchin.moviegrpcserver.movie.exception.MovieNotFoundException;
import com.ivanfranchin.moviegrpcserver.movie.model.Movie;
import com.ivanfranchin.movieserver.movie.model.MovieProto;
import com.ivanfranchin.movieserver.movie.model.MovieServerGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class MovieGrpcService extends MovieServerGrpc.MovieServerImplBase {

    private final MovieService movieService;

    @Override
    public void getMovies(MovieProto.GetMoviesRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
        movieService.getMovies(request.getOffset(), request.getSize())
                .stream()
                .map(Movie::toProto)
                .forEach(responseObserver::onNext);
        responseObserver.onCompleted();

        log.info("Get movies with offset {} and size {}", request.getOffset(), request.getSize());
    }

    @Override
    public void getMovie(MovieProto.GetMovieRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
        try {
            Movie movie = movieService.validateAndGetMovieById(request.getImdbId());

            MovieProto.Movie movieProtoMovie = movie.toProto();
            responseObserver.onNext(movieProtoMovie);
            responseObserver.onCompleted();

            log.info("Get movie {}", movie);
        } catch (MovieNotFoundException e) {
            log.error("Error while getting movie with imdbId {}. Not found.", request.getImdbId());
            responseObserver.onError((Status.NOT_FOUND.withDescription(e.getMessage())).asRuntimeException());
        }
    }

    @Override
    public void createMovie(MovieProto.CreateMovieRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
        Movie movie = Movie.from(request);
        movie = movieService.saveMovie(movie);

        MovieProto.Movie movieProtoMovie = movie.toProto();
        responseObserver.onNext(movieProtoMovie);
        responseObserver.onCompleted();

        log.info("Created movie {}", movie);
    }

    @Override
    public void updateMovie(MovieProto.UpdateMovieRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
        try {
            Movie movie = movieService.validateAndGetMovieById(request.getImdbId());
            Movie.updateFrom(request, movie);

            movieService.saveMovie(movie);

            MovieProto.Movie movieProtoMovie = movie.toProto();
            responseObserver.onNext(movieProtoMovie);
            responseObserver.onCompleted();

            log.info("Updated movie {}", movie);
        } catch (MovieNotFoundException e) {
            log.error("Error while updating movie with imdbId {}. Not found.", request.getImdbId());
            responseObserver.onError((Status.NOT_FOUND.withDescription(e.getMessage())).asRuntimeException());
        }
    }

    @Override
    public void deleteMovie(MovieProto.DeleteMovieRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
        try {
            Movie movie = movieService.validateAndGetMovieById(request.getImdbId());
            movieService.deleteMovie(movie);

            MovieProto.Movie movieProtoMovie = movie.toProto();
            responseObserver.onNext(movieProtoMovie);
            responseObserver.onCompleted();

            log.info("Deleted movie {}", movie);
        } catch (MovieNotFoundException e) {
            log.error("Error while deleting movie with imdbId {}. Not found.", request.getImdbId());
            responseObserver.onError((Status.NOT_FOUND.withDescription(e.getMessage())).asRuntimeException());
        }
    }
}