package com.technophile.parsingsample;

import android.text.method.MovementMethod;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Technophile on 2/3/2018.
 */

public interface ApiInterface {

    public static final String REST_URL = "http://test.terasol.in/";

    @GET("moviedata.json")
    Call<ArrayList<MoviesDataModel>> getPictures();

}
