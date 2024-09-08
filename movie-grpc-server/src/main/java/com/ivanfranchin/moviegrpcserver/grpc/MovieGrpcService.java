package com.ivanfranchin.moviegrpcserver.grpc;

import com.ivanfranchin.moviegrpcserver.exception.MovieNotFoundException;
import com.ivanfranchin.moviegrpcserver.model.Genre;
import com.ivanfranchin.moviegrpcserver.model.Movie;
import com.ivanfranchin.moviegrpcserver.service.MovieService;
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
                .map(this::toMovieProtoMovie)
                .forEach(responseObserver::onNext);
        responseObserver.onCompleted();

        log.info("Get movies with offset {} and size {}", request.getOffset(), request.getSize());
    }

    @Override
    public void getMovie(MovieProto.GetMovieRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
        try {
            Movie movie = movieService.validateAndGetMovieById(request.getImdbId());

            MovieProto.Movie movieProtoMovie = toMovieProtoMovie(movie);
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
        Movie movie = toMovie(request);
        movie = movieService.saveMovie(movie);

        MovieProto.Movie movieProtoMovie = toMovieProtoMovie(movie);
        responseObserver.onNext(movieProtoMovie);
        responseObserver.onCompleted();

        log.info("Created movie {}", movie);
    }

    @Override
    public void updateMovie(MovieProto.UpdateMovieRequest request, StreamObserver<MovieProto.Movie> responseObserver) {
        try {
            Movie movie = movieService.validateAndGetMovieById(request.getImdbId());
            updateMovieFrom(movie, request);

            movieService.saveMovie(movie);

            MovieProto.Movie movieProtoMovie = toMovieProtoMovie(movie);
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

            MovieProto.Movie movieProtoMovie = toMovieProtoMovie(movie);
            responseObserver.onNext(movieProtoMovie);
            responseObserver.onCompleted();

            log.info("Deleted movie {}", movie);
        } catch (MovieNotFoundException e) {
            log.error("Error while deleting movie with imdbId {}. Not found.", request.getImdbId());
            responseObserver.onError((Status.NOT_FOUND.withDescription(e.getMessage())).asRuntimeException());
        }
    }

    private void updateMovieFrom(Movie movie, MovieProto.UpdateMovieRequest request) {
        if (!request.getTitle().isEmpty()) {
            movie.setTitle(request.getTitle());
        }
        if (request.getYear() != 0) {
            movie.setYear(request.getYear());
        }
        if (request.getGenreValue() >= 0) {
            movie.setGenre(Genre.valueOf(request.getGenre().name()));
        }
    }

    private MovieProto.Movie toMovieProtoMovie(Movie movie) {
        return MovieProto.Movie.newBuilder()
                .setImdbId(movie.getImdbId())
                .setTitle(movie.getTitle())
                .setYear(movie.getYear())
                .setGenre(MovieProto.Genre.valueOf(movie.getGenre().name()))
                .build();
    }

    private Movie toMovie(MovieProto.CreateMovieRequest request) {
        Genre genre = Genre.valueOf(request.getGenre().name());
        return new Movie(request.getImdbId(), request.getTitle(), request.getYear(), genre);
    }
}