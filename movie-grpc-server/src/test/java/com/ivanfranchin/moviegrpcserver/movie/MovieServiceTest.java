package com.ivanfranchin.moviegrpcserver.movie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ivanfranchin.moviegrpcserver.movie.exception.MovieAlreadyExistsException;
import com.ivanfranchin.moviegrpcserver.movie.exception.MovieNotFoundException;
import com.ivanfranchin.moviegrpcserver.movie.model.Genre;
import com.ivanfranchin.moviegrpcserver.movie.model.Movie;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import(MovieService.class)
class MovieServiceTest {

  @MockitoBean private MovieRepository movieRepository;

  @Autowired private MovieService movieService;

  @Test
  void getMoviesShouldReturnPagedResults() {
    Movie movie = new Movie("tt001", "Test", 2024, Genre.ACTION);
    when(movieRepository.findAll(any(PageRequest.class)))
        .thenReturn(new PageImpl<>(List.of(movie)));

    List<Movie> result = movieService.getMovies(0, 5);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getImdbId()).isEqualTo("tt001");
  }

  @Test
  void getMoviesShouldThrowWhenPageIsNegative() {
    assertThatThrownBy(() -> movieService.getMovies(-1, 5))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Page index must not be negative");
  }

  @Test
  void getMoviesShouldThrowWhenSizeIsLessThanOne() {
    assertThatThrownBy(() -> movieService.getMovies(0, 0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Page size must not be less than one");
  }

  @Test
  void validateAndGetMovieByIdShouldReturnMovieWhenFound() {
    Movie movie = new Movie("tt001", "Test", 2024, Genre.ACTION);
    when(movieRepository.findById("tt001")).thenReturn(Optional.of(movie));

    Movie result = movieService.validateAndGetMovieById("tt001");

    assertThat(result).isEqualTo(movie);
  }

  @Test
  void validateAndGetMovieByIdShouldThrowWhenNotFound() {
    when(movieRepository.findById("not-found")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> movieService.validateAndGetMovieById("not-found"))
        .isInstanceOf(MovieNotFoundException.class)
        .hasMessage("Movie with id 'not-found' not found");
  }

  @Test
  void createMovieShouldSaveAndReturn() {
    Movie movie = new Movie("tt001", "Test", 2024, Genre.ACTION);
    when(movieRepository.existsById("tt001")).thenReturn(false);
    when(movieRepository.save(movie)).thenReturn(movie);

    Movie result = movieService.createMovie(movie);

    assertThat(result).isEqualTo(movie);
  }

  @Test
  void createMovieShouldThrowWhenImdbIdIsNull() {
    Movie movie = new Movie(null, "Test", 2024, Genre.ACTION);

    assertThatThrownBy(() -> movieService.createMovie(movie))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("imdbId must not be blank");
  }

  @Test
  void createMovieShouldThrowWhenImdbIdIsBlank() {
    Movie movie = new Movie(" ", "Test", 2024, Genre.ACTION);

    assertThatThrownBy(() -> movieService.createMovie(movie))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("imdbId must not be blank");
  }

  @Test
  void createMovieShouldThrowWhenTitleIsNull() {
    Movie movie = new Movie("tt001", null, 2024, Genre.ACTION);

    assertThatThrownBy(() -> movieService.createMovie(movie))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("title must not be blank");
  }

  @Test
  void createMovieShouldThrowWhenTitleIsBlank() {
    Movie movie = new Movie("tt001", " ", 2024, Genre.ACTION);

    assertThatThrownBy(() -> movieService.createMovie(movie))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("title must not be blank");
  }

  @Test
  void createMovieShouldThrowWhenYearIsNull() {
    Movie movie = new Movie("tt001", "Test", null, Genre.ACTION);

    assertThatThrownBy(() -> movieService.createMovie(movie))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("year must not be null");
  }

  @Test
  void createMovieShouldThrowWhenGenreIsNull() {
    Movie movie = new Movie("tt001", "Test", 2024, null);

    assertThatThrownBy(() -> movieService.createMovie(movie))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("genre must not be null");
  }

  @Test
  void createMovieShouldThrowWhenMovieAlreadyExists() {
    Movie movie = new Movie("tt001", "Test", 2024, Genre.ACTION);
    when(movieRepository.existsById("tt001")).thenReturn(true);

    assertThatThrownBy(() -> movieService.createMovie(movie))
        .isInstanceOf(MovieAlreadyExistsException.class)
        .hasMessage("Movie with id 'tt001' already exists");
  }

  @Test
  void saveMovieShouldDelegateToRepository() {
    Movie movie = new Movie("tt001", "Test", 2024, Genre.ACTION);
    when(movieRepository.save(movie)).thenReturn(movie);

    Movie result = movieService.saveMovie(movie);

    assertThat(result).isEqualTo(movie);
    verify(movieRepository).save(movie);
  }

  @Test
  void deleteMovieShouldDelegateToRepository() {
    Movie movie = new Movie("tt001", "Test", 2024, Genre.ACTION);

    movieService.deleteMovie(movie);

    verify(movieRepository).delete(movie);
  }
}
