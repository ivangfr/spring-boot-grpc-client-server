package com.ivanfranchin.moviegrpcserver.movie;

import com.ivanfranchin.moviegrpcserver.movie.exception.MovieAlreadyExistsException;
import com.ivanfranchin.moviegrpcserver.movie.exception.MovieNotFoundException;
import com.ivanfranchin.moviegrpcserver.movie.model.Movie;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MovieService {

  private final MovieRepository movieRepository;

  public List<Movie> getMovies(int page, int size) {
    if (page < 0) {
      throw new IllegalArgumentException("Page index must not be negative");
    }
    if (size < 1) {
      throw new IllegalArgumentException("Page size must not be less than one");
    }
    Pageable pageable = PageRequest.of(page, size);
    return movieRepository.findAll(pageable).getContent();
  }

  public Movie validateAndGetMovieById(String imdbId) {
    return movieRepository
        .findById(imdbId)
        .orElseThrow(
            () -> new MovieNotFoundException("Movie with id '%s' not found".formatted(imdbId)));
  }

  public Movie createMovie(Movie movie) {
    if (movie.getImdbId() == null || movie.getImdbId().isBlank()) {
      throw new IllegalArgumentException("imdbId must not be blank");
    }
    if (movie.getTitle() == null || movie.getTitle().isBlank()) {
      throw new IllegalArgumentException("title must not be blank");
    }
    if (movie.getYear() == null) {
      throw new IllegalArgumentException("year must not be null");
    }
    if (movie.getGenre() == null) {
      throw new IllegalArgumentException("genre must not be null");
    }
    if (movieRepository.existsById(movie.getImdbId())) {
      throw new MovieAlreadyExistsException(
          "Movie with id '%s' already exists".formatted(movie.getImdbId()));
    }
    return movieRepository.save(movie);
  }

  public Movie saveMovie(Movie movie) {
    return movieRepository.save(movie);
  }

  public void deleteMovie(Movie movie) {
    movieRepository.delete(movie);
  }
}
