package com.ivanfranchin.moviegrpcclient.client;

public record MovieResponse(String imdbId, String title, Integer year, String genre) {
}