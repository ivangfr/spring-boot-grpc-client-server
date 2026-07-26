package com.ivanfranchin.moviegrpcserver.movie;

import com.ivanfranchin.moviegrpcserver.movie.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, String> {}
