package com.ivanfranchin.moviegrpcserver.service;

import com.ivanfranchin.moviegrpcserver.exception.MovieNotFoundException;
import com.ivanfranchin.moviegrpcserver.model.Movie;
import com.ivanfranchin.moviegrpcserver.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MovieServiceImpl implements MovieService {

    private final MovieRepository movieRepository;

    @Override
    public List<Movie> getMovies(int offset, int size) {
        Pageable pageable = PageRequest.of(offset, size);
        return movieRepository.findAll(pageable).getContent();
    }

    @Override
    public Movie validateAndGetMovieById(String imdbId) {
        return movieRepository.findById(imdbId)
                .orElseThrow(() -> new MovieNotFoundException("Movie with id '%s' not found".formatted(imdbId)));
    }

    @Override
    public Movie saveMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public void deleteMovie(Movie movie) {
        movieRepository.delete(movie);
    }
}