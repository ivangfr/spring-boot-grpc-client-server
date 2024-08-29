package com.ivanfranchin.moviegrpcclient.client;

import com.ivanfranchin.moviegrpcclient.command.Genre;
import com.ivanfranchin.movieserver.movie.model.MovieProto;
import com.ivanfranchin.movieserver.movie.model.MovieServerGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceGrpcClient {

    @GrpcClient("movie-grpc-server")
    MovieServerGrpc.MovieServerBlockingStub stub;

    public List<MovieResponse> getMovies(int offset, int size) {
        MovieProto.GetMoviesRequest getMoviesRequest = MovieProto.GetMoviesRequest.newBuilder()
                .setOffset(offset)
                .setSize(size)
                .build();
        MovieProto.GetMoviesResponse getMoviesResponse = stub.getMovies(getMoviesRequest);
        return getMoviesResponse.getMoviesList()
                .stream()
                .map(this::toMovieResponse)
                .toList();
    }

    public MovieResponse getMovie(String imdbId) {
        MovieProto.GetMovieRequest getMovieRequest = MovieProto.GetMovieRequest.newBuilder()
                .setImdbId(imdbId)
                .build();
        MovieProto.MovieResponse movieResponse = stub.getMovie(getMovieRequest);
        return toMovieResponse(movieResponse.getMovie());
    }

    public MovieResponse createMovie(String imdbId, String title, Integer year, Genre genre) {
        MovieProto.CreateMovieRequest createMoviesRequest = MovieProto.CreateMovieRequest.newBuilder()
                .setImdbId(imdbId)
                .setTitle(title)
                .setYear(year)
                .setGenre(MovieProto.Genre.valueOf(genre.name()))
                .build();
        MovieProto.MovieResponse movieResponse = stub.createMovie(createMoviesRequest);
        return toMovieResponse(movieResponse.getMovie());
    }

    public MovieResponse updateMovie(String imdbId, String title, Integer year, Genre genre) {
        MovieProto.UpdateMovieRequest.Builder builder = MovieProto.UpdateMovieRequest.newBuilder();
        builder.setImdbId(imdbId);
        if (title != null) {
            builder.setTitle(title);
        }
        if (year != null) {
            builder.setYear(year);
        }
        if (genre != null) {
            builder.setGenre(MovieProto.Genre.valueOf(genre.name()));
        } else {
            builder.setGenreValue(-1);
        }
        MovieProto.UpdateMovieRequest updateMovieRequest = builder.build();
        MovieProto.MovieResponse movieResponse = stub.updateMovie(updateMovieRequest);
        return toMovieResponse(movieResponse.getMovie());
    }

    public MovieResponse deleteMovie(String imdbId) {
        MovieProto.DeleteMovieRequest deleteMovieRequest = MovieProto.DeleteMovieRequest.newBuilder()
                .setImdbId(imdbId)
                .build();
        MovieProto.MovieResponse movieResponse = stub.deleteMovie(deleteMovieRequest);
        return toMovieResponse(movieResponse.getMovie());
    }

    private MovieResponse toMovieResponse(MovieProto.Movie movie) {
        return new MovieResponse(movie.getImdbId(), movie.getTitle(), movie.getYear(), movie.getGenre().name());
    }
}