package com.ivanfranchin.moviegrpcserver.movie;

import com.ivanfranchin.moviegrpcserver.movie.exception.MovieAlreadyExistsException;
import com.ivanfranchin.moviegrpcserver.movie.exception.MovieNotFoundException;
import com.ivanfranchin.moviegrpcserver.movie.model.Genre;
import com.ivanfranchin.moviegrpcserver.movie.model.Movie;
import com.ivanfranchin.movieserver.movie.model.MovieProto;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class MovieGrpcServiceTest {

    @MockitoBean
    private MovieService movieService;

    private MovieGrpcService movieGrpcService;

    @BeforeEach
    void setUp() {
        movieGrpcService = new MovieGrpcService(movieService);
    }

    @Test
    void getMoviesShouldStreamResultsAndComplete() {
        when(movieService.getMovies(0, 5)).thenReturn(List.of(
                new Movie("tt001", "Movie 1", 2024, Genre.ACTION),
                new Movie("tt002", "Movie 2", 2023, Genre.DRAMA)
        ));

        TestStreamObserver<MovieProto.Movie> observer = new TestStreamObserver<>();
        movieGrpcService.getMovies(
                MovieProto.GetMoviesRequest.newBuilder().setPage(0).setSize(5).build(),
                observer);

        assertThat(observer.values).hasSize(2);
        assertThat(observer.values.get(0).getImdbId()).isEqualTo("tt001");
        assertThat(observer.values.get(1).getImdbId()).isEqualTo("tt002");
        assertThat(observer.completed).isTrue();
        assertThat(observer.error).isNull();
    }

    @Test
    void getMoviesShouldHandleError() {
        when(movieService.getMovies(anyInt(), anyInt()))
                .thenThrow(new RuntimeException("DB error"));

        TestStreamObserver<MovieProto.Movie> observer = new TestStreamObserver<>();
        movieGrpcService.getMovies(
                MovieProto.GetMoviesRequest.newBuilder().setPage(0).setSize(5).build(),
                observer);

        assertThat(observer.completed).isFalse();
        assertThat(observer.error).isInstanceOf(StatusRuntimeException.class);
        assertThat(((StatusRuntimeException) observer.error).getStatus().getCode())
                .isEqualTo(Status.INTERNAL.getCode());
    }

    @Test
    void getMovieShouldReturnMovieAndComplete() {
        when(movieService.validateAndGetMovieById("tt001"))
                .thenReturn(new Movie("tt001", "Test", 2024, Genre.ACTION));

        TestStreamObserver<MovieProto.Movie> observer = new TestStreamObserver<>();
        movieGrpcService.getMovie(
                MovieProto.GetMovieRequest.newBuilder().setImdbId("tt001").build(),
                observer);

        assertThat(observer.values).hasSize(1);
        assertThat(observer.values.get(0).getImdbId()).isEqualTo("tt001");
        assertThat(observer.completed).isTrue();
        assertThat(observer.error).isNull();
    }

    @Test
    void getMovieShouldReturnNotFound() {
        when(movieService.validateAndGetMovieById("not-found"))
                .thenThrow(new MovieNotFoundException("Movie with id 'not-found' not found"));

        TestStreamObserver<MovieProto.Movie> observer = new TestStreamObserver<>();
        movieGrpcService.getMovie(
                MovieProto.GetMovieRequest.newBuilder().setImdbId("not-found").build(),
                observer);

        assertThat(observer.completed).isFalse();
        assertThat(observer.error).isInstanceOf(StatusRuntimeException.class);
        assertThat(((StatusRuntimeException) observer.error).getStatus().getCode())
                .isEqualTo(Status.NOT_FOUND.getCode());
    }

    @Test
    void createMovieShouldReturnCreatedMovieAndComplete() {
        when(movieService.createMovie(any(Movie.class)))
                .thenReturn(new Movie("tt001", "New Movie", 2024, Genre.ACTION));

        TestStreamObserver<MovieProto.Movie> observer = new TestStreamObserver<>();
        movieGrpcService.createMovie(
                MovieProto.CreateMovieRequest.newBuilder()
                        .setImdbId("tt001")
                        .setTitle("New Movie")
                        .setYear(2024)
                        .setGenre(MovieProto.Genre.ACTION)
                        .build(),
                observer);

        assertThat(observer.values).hasSize(1);
        assertThat(observer.values.get(0).getImdbId()).isEqualTo("tt001");
        assertThat(observer.completed).isTrue();
        assertThat(observer.error).isNull();
    }

    @Test
    void createMovieShouldReturnAlreadyExists() {
        when(movieService.createMovie(any(Movie.class)))
                .thenThrow(new MovieAlreadyExistsException("Movie with id 'tt001' already exists"));

        TestStreamObserver<MovieProto.Movie> observer = new TestStreamObserver<>();
        movieGrpcService.createMovie(
                MovieProto.CreateMovieRequest.newBuilder()
                        .setImdbId("tt001")
                        .setTitle("New Movie")
                        .setYear(2024)
                        .setGenre(MovieProto.Genre.ACTION)
                        .build(),
                observer);

        assertThat(observer.completed).isFalse();
        assertThat(observer.error).isInstanceOf(StatusRuntimeException.class);
        assertThat(((StatusRuntimeException) observer.error).getStatus().getCode())
                .isEqualTo(Status.ALREADY_EXISTS.getCode());
    }

    @Test
    void createMovieShouldHandleInternalError() {
        when(movieService.createMovie(any(Movie.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        TestStreamObserver<MovieProto.Movie> observer = new TestStreamObserver<>();
        movieGrpcService.createMovie(
                MovieProto.CreateMovieRequest.newBuilder()
                        .setImdbId("tt001")
                        .setTitle("New Movie")
                        .setYear(2024)
                        .setGenre(MovieProto.Genre.ACTION)
                        .build(),
                observer);

        assertThat(observer.completed).isFalse();
        assertThat(observer.error).isInstanceOf(StatusRuntimeException.class);
        assertThat(((StatusRuntimeException) observer.error).getStatus().getCode())
                .isEqualTo(Status.INTERNAL.getCode());
    }

    @Test
    void updateMovieShouldReturnUpdatedMovieAndComplete() {
        when(movieService.validateAndGetMovieById("tt001"))
                .thenReturn(new Movie("tt001", "Original", 2020, Genre.DRAMA));
        when(movieService.saveMovie(any(Movie.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TestStreamObserver<MovieProto.Movie> observer = new TestStreamObserver<>();
        movieGrpcService.updateMovie(
                MovieProto.UpdateMovieRequest.newBuilder()
                        .setImdbId("tt001")
                        .setTitle("Updated")
                        .setYear(2025)
                        .setGenre(MovieProto.Genre.COMEDY)
                        .build(),
                observer);

        assertThat(observer.values).hasSize(1);
        assertThat(observer.values.get(0).getTitle()).isEqualTo("Updated");
        assertThat(observer.values.get(0).getYear()).isEqualTo(2025);
        assertThat(observer.values.get(0).getGenre()).isEqualTo(MovieProto.Genre.COMEDY);
        assertThat(observer.completed).isTrue();
        assertThat(observer.error).isNull();
    }

    @Test
    void updateMovieShouldReturnNotFound() {
        when(movieService.validateAndGetMovieById("not-found"))
                .thenThrow(new MovieNotFoundException("Movie with id 'not-found' not found"));

        TestStreamObserver<MovieProto.Movie> observer = new TestStreamObserver<>();
        movieGrpcService.updateMovie(
                MovieProto.UpdateMovieRequest.newBuilder().setImdbId("not-found").build(),
                observer);

        assertThat(observer.completed).isFalse();
        assertThat(observer.error).isInstanceOf(StatusRuntimeException.class);
        assertThat(((StatusRuntimeException) observer.error).getStatus().getCode())
                .isEqualTo(Status.NOT_FOUND.getCode());
    }

    @Test
    void deleteMovieShouldReturnDeletedMovieAndComplete() {
        Movie deletedMovie = new Movie("tt001", "To Delete", 2024, Genre.ACTION);
        when(movieService.validateAndGetMovieById("tt001")).thenReturn(deletedMovie);

        TestStreamObserver<MovieProto.Movie> observer = new TestStreamObserver<>();
        movieGrpcService.deleteMovie(
                MovieProto.DeleteMovieRequest.newBuilder().setImdbId("tt001").build(),
                observer);

        assertThat(observer.values).hasSize(1);
        assertThat(observer.values.get(0).getImdbId()).isEqualTo("tt001");
        assertThat(observer.completed).isTrue();
        assertThat(observer.error).isNull();
        verify(movieService).deleteMovie(deletedMovie);
    }

    @Test
    void deleteMovieShouldReturnNotFound() {
        when(movieService.validateAndGetMovieById("not-found"))
                .thenThrow(new MovieNotFoundException("Movie with id 'not-found' not found"));

        TestStreamObserver<MovieProto.Movie> observer = new TestStreamObserver<>();
        movieGrpcService.deleteMovie(
                MovieProto.DeleteMovieRequest.newBuilder().setImdbId("not-found").build(),
                observer);

        assertThat(observer.completed).isFalse();
        assertThat(observer.error).isInstanceOf(StatusRuntimeException.class);
        assertThat(((StatusRuntimeException) observer.error).getStatus().getCode())
                .isEqualTo(Status.NOT_FOUND.getCode());
    }

    private static class TestStreamObserver<T> implements StreamObserver<T> {

        private final java.util.List<T> values = new java.util.ArrayList<>();
        private Throwable error;
        private boolean completed;

        @Override
        public void onNext(T value) {
            values.add(value);
        }

        @Override
        public void onError(Throwable t) {
            this.error = t;
        }

        @Override
        public void onCompleted() {
            this.completed = true;
        }
    }
}
