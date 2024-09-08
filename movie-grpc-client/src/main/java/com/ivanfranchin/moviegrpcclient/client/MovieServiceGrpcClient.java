package com.ivanfranchin.moviegrpcclient.client;

import com.ivanfranchin.moviegrpcclient.command.Genre;
import com.ivanfranchin.movieserver.movie.model.MovieProto;
import com.ivanfranchin.movieserver.movie.model.MovieServerGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
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
        Iterator<MovieProto.Movie> movieIterator = stub.getMovies(getMoviesRequest);

        List<MovieResponse> movieResponses = new ArrayList<>();
        movieIterator.forEachRemaining(movie -> movieResponses.add(toMovieResponse(movie)));
        return movieResponses;
    }

    public MovieResponse getMovie(String imdbId) {
        MovieProto.GetMovieRequest getMovieRequest = MovieProto.GetMovieRequest.newBuilder()
                .setImdbId(imdbId)
                .build();
        MovieProto.Movie movie = stub.getMovie(getMovieRequest);
        return toMovieResponse(movie);
    }

    public MovieResponse createMovie(String imdbId, String title, Integer year, Genre genre) {
        MovieProto.CreateMovieRequest createMoviesRequest = MovieProto.CreateMovieRequest.newBuilder()
                .setImdbId(imdbId)
                .setTitle(title)
                .setYear(year)
                .setGenre(MovieProto.Genre.valueOf(genre.name()))
                .build();
        MovieProto.Movie movie = stub.createMovie(createMoviesRequest);
        return toMovieResponse(movie);
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
        MovieProto.Movie movie = stub.updateMovie(updateMovieRequest);
        return toMovieResponse(movie);
    }

    public MovieResponse deleteMovie(String imdbId) {
        MovieProto.DeleteMovieRequest deleteMovieRequest = MovieProto.DeleteMovieRequest.newBuilder()
                .setImdbId(imdbId)
                .build();
        MovieProto.Movie movie = stub.deleteMovie(deleteMovieRequest);
        return toMovieResponse(movie);
    }

    private MovieResponse toMovieResponse(MovieProto.Movie movie) {
        return new MovieResponse(movie.getImdbId(), movie.getTitle(), movie.getYear(), movie.getGenre().name());
    }
}