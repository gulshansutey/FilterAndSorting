package com.technophile.parsingsample;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Technophile on 2/3/2018.
 */

public class MoviesDataModel implements Serializable{

        @Expose
        private int year;

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Info getInfo() {
            return info;
        }

        public void setInfo(Info info) {
            this.info = info;
        }

        @Expose
        private String title;
        @Expose
        private Info info;



    public class Info implements Serializable {

        public ArrayList<String> getDirectors() {
            return directors;
        }

        public void setDirectors(ArrayList<String> directors) {
            this.directors = directors;
        }

        public String getRelease_date() {
            return release_date;
        }

        public void setRelease_date(String release_date) {
            this.release_date = release_date;
        }

        public String getImage_url() {
            return image_url;
        }

        public void setImage_url(String image_url) {
            this.image_url = image_url;
        }

        public String getPlot() {
            return plot;
        }

        public void setPlot(String plot) {
            this.plot = plot;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public ArrayList<String> getGenres() {



            return genres;
        }

        public void setGenres(ArrayList<String> genres) {
            this.genres = genres;
        }

        public ArrayList<String> getActors() {
            return actors;
        }

        public void setActors(ArrayList<String> actors) {
            this.actors = actors;
        }

        @Expose
        private ArrayList<String> directors;

        @Expose
        private String release_date;

        @Expose
        private String image_url;

        @Expose
        private int running_time_secs;

        @Expose
        private String plot;

        public int getRunning_time_secs() {
            return running_time_secs;
        }

        public void setRunning_time_secs(int running_time_secs) {
            this.running_time_secs = running_time_secs;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        @Expose
        private float rating;

        @Expose
        private int rank;

        @Expose
        private ArrayList<String> genres;

        @Expose
        private ArrayList<String> actors;



    }

}
