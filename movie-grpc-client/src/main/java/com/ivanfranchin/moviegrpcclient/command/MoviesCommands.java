package com.ivanfranchin.moviegrpcclient.command;

import com.ivanfranchin.moviegrpcclient.client.MovieResponse;
import com.ivanfranchin.moviegrpcclient.client.MovieServiceGrpcClient;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.List;

@RequiredArgsConstructor
@ShellComponent
public class MoviesCommands {

    private final MovieServiceGrpcClient movieServiceGrpcClient;

    @ShellMethod("Get movies")
    public List<MovieResponse> getMovies(
            @ShellOption(defaultValue = "0") int offset,
            @ShellOption(defaultValue = "10") int size) {
        return movieServiceGrpcClient.getMovies(offset, size);
    }

    @ShellMethod("Get movie")
    public MovieResponse getMovie(String imdbId) {
        return movieServiceGrpcClient.getMovie(imdbId);
    }

    @ShellMethod("Create movie")
    public MovieResponse createMovie(String imdbId, String title, Integer year,
                                     @ShellOption() Genre genre) {
        return movieServiceGrpcClient.createMovie(imdbId, title, year, genre);
    }

    @ShellMethod("Update movie")
    public MovieResponse updateMovie(
            String imdbId,
            @ShellOption(defaultValue = ShellOption.NULL) String title,
            @ShellOption(defaultValue = ShellOption.NULL) Integer year,
            @ShellOption(defaultValue = ShellOption.NULL) Genre genre) {
        return movieServiceGrpcClient.updateMovie(imdbId, title, year, genre);
    }

    @ShellMethod("Delete movie")
    public MovieResponse deleteMovie(String imdbId) {
        return movieServiceGrpcClient.deleteMovie(imdbId);
    }
}