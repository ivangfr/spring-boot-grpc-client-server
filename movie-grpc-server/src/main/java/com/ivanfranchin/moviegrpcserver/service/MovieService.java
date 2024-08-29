package com.ivanfranchin.moviegrpcserver.service;

import com.ivanfranchin.moviegrpcserver.model.Movie;

import java.util.List;

public interface MovieService {

    List<Movie> getMovies(int offset, int size);

    Movie validateAndGetMovieById(String imdbId);

    Movie saveMovie(Movie movie);

    void deleteMovie(Movie movie);
}