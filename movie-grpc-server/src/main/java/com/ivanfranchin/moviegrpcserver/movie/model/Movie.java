package com.ivanfranchin.moviegrpcserver.movie.model;

import com.ivanfranchin.movieserver.movie.model.MovieProto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    private String imdbId;

    private String title;
    private Integer year;
    private Genre genre;

    public static Movie from(MovieProto.CreateMovieRequest request) {
        Genre genre = Genre.valueOf(request.getGenre().name());
        return new Movie(request.getImdbId(), request.getTitle(), request.getYear(), genre);
    }

    public static void updateFrom(MovieProto.UpdateMovieRequest request, Movie movie) {
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

    public MovieProto.Movie toProto() {
        return MovieProto.Movie.newBuilder()
                .setImdbId(this.getImdbId())
                .setTitle(this.getTitle())
                .setYear(this.getYear())
                .setGenre(MovieProto.Genre.valueOf(this.getGenre().name()))
                .build();
    }
}