package com.death.xorbot;

import java.util.List;

/**
 * Created by deathcode on 06/11/17.
 */

public class ResponseModel {

    private boolean isMine;
    private String response;
    private boolean isLoading;
    private boolean isCollection;
    private boolean isMovie;
    private boolean isNews;
    private String movieQueryType;
    private boolean isStackOverflow;

    public List<StackOverflow> getStackOverflow() {
        return stackOverflow;
    }

    public void setStackOverflow(List<StackOverflow> stackOverflow) {
        this.stackOverflow = stackOverflow;
    }

    private List<StackOverflow> stackOverflow;


    public boolean isStackOverflow() {
        return isStackOverflow;
    }

    public void setStackOverflow(boolean stackOverflow) {
        isStackOverflow = stackOverflow;
    }

    public String getMovieQueryType() {
        return movieQueryType;
    }

    public void setMovieQueryType(String movieQueryType) {
        this.movieQueryType = movieQueryType;
    }

    private Movies movies;

    public Movies getMovies() {
        return movies;
    }

    public void setMovies(Movies movies) {
        this.movies = movies;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public boolean isMovie() {
        return isMovie;
    }

    public void setMovie(boolean movie) {
        isMovie = movie;
    }

    public boolean isNews() {
        return isNews;
    }

    public void setNews(boolean news) {
        isNews = news;
    }
}
