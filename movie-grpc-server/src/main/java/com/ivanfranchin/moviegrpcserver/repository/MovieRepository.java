package com.ivanfranchin.moviegrpcserver.repository;

import com.ivanfranchin.moviegrpcserver.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, String> {
}