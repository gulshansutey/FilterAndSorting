package com.technophile.parsingsample;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RatingBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements MoviesRecyclerADP.OnMovieClickListener,MovieDetailsFragment.OnFragmentInteractionListener {

    private static final String PREFS_KEY = "myJson";
    private FrameLayout fl_fragment_container;
    private static final int DIRECTORS_DIALOG = 1;
    private static final int GENRES_DIALOG = 0;
    private ArrayList<MoviesDataModel> moviesArrayList = new ArrayList<>();
    private MoviesRecyclerADP moviesRecyclerADP;
    private ProgressBar pbar_loading;
    private boolean[] selectedGenresBoolArray;
    private boolean[] selectedDirectorsBoolArray;
    private String[] selectedDirectorsStringArray;
    private String[] selectedGenresStringArray;
    private float ratingFilterValue = 0;

    private boolean applyRatingFilter, applyGenreFilter, applyDirectorFilter;
    private Handler handler;
    private OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        RecyclerView rv_movies_list = (RecyclerView) findViewById(R.id.rv_movies_list);
        fl_fragment_container=(FrameLayout)findViewById(R.id.fl_fragment_container);
        pbar_loading = (ProgressBar) findViewById(R.id.pbar_loading);
        rv_movies_list.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        moviesRecyclerADP = new MoviesRecyclerADP(MainActivity.this,this);
        rv_movies_list.setAdapter(moviesRecyclerADP);
        getFromDatabase();

    }



    public void getFromDatabase() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);

        TypeToken<ArrayList<MoviesDataModel>> token = new TypeToken<ArrayList<MoviesDataModel>>() {
        };

        if (TextUtils.isEmpty(sharedPreferences.getString(PREFS_KEY, null))) {
            getMoviesDataFromServer();
        } else {
            moviesArrayList = new Gson().fromJson(sharedPreferences.getString(PREFS_KEY, null), token.getType());
            populateList();
        }
    }

    private Menu optionMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        optionMenu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handling item selection
        switch (item.getItemId()) {
            case R.id.filter_by_genres:
                showDialog("Genres", selectedGenresStringArray, selectedGenresBoolArray, GENRES_DIALOG);
                break;
            case R.id.filter_by_directors:
                showDialog("Directors", selectedDirectorsStringArray, selectedDirectorsBoolArray, DIRECTORS_DIALOG);
                break;
            case R.id.sort_by_title_ascending:
                moviesRecyclerADP.sortItems(MoviesRecyclerADP.SORT_BY_TITLE,MoviesRecyclerADP.ORDER_ASCENDING);
                break;
            case R.id.sort_by_title_descending:
                moviesRecyclerADP.sortItems(MoviesRecyclerADP.SORT_BY_TITLE,MoviesRecyclerADP.ORDER_DESCENDING);
                break;
            case R.id.sort_by_year_ascending:
                moviesRecyclerADP.sortItems(MoviesRecyclerADP.SORT_BY_YEAR, MoviesRecyclerADP.ORDER_ASCENDING);
                break;
            case R.id.sort_by_year_descending:
                moviesRecyclerADP.sortItems(MoviesRecyclerADP.SORT_BY_YEAR, MoviesRecyclerADP.ORDER_DESCENDING);
                break;
            case R.id.filter_by_rating:
                RatingDialog();
                break;
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
                break;
        }
        return true;
    }

    public void getMoviesDataFromServer() {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create()).client(httpClient)
                .baseUrl(ApiInterface.REST_URL).build();
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        pbar_loading.setVisibility(View.VISIBLE);
        apiInterface.getPictures().enqueue(new Callback<ArrayList<MoviesDataModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<MoviesDataModel>> call, @NonNull Response<ArrayList<MoviesDataModel>> response) {
                System.out.println("onResponse call = " + response);
                pbar_loading.setVisibility(View.GONE);
                moviesArrayList = response.body();
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putString(PREFS_KEY, new Gson().toJson(moviesArrayList));
                editor.apply();
                populateList();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<MoviesDataModel>> call, @NonNull Throwable t) {
                System.out.println("onFailure call = " + t.getLocalizedMessage());
                pbar_loading.setVisibility(View.GONE);
            }
        });
    }

    private void populateList() {
        TreeSet<String> genresList = new TreeSet<>();
        TreeSet<String> directorsList = new TreeSet<>();
        moviesRecyclerADP.addItems(moviesArrayList);
        moviesRecyclerADP.notifyDataSetChanged();

        for (MoviesDataModel eachDataModel : moviesArrayList) {
            if (eachDataModel.getInfo().getGenres() != null)
                genresList.addAll(eachDataModel.getInfo().getGenres());
            if (eachDataModel.getInfo().getDirectors() != null) {
                directorsList.addAll(eachDataModel.getInfo().getDirectors());
            }
        }

        selectedGenresBoolArray = new boolean[genresList.size()];
        selectedDirectorsBoolArray = new boolean[directorsList.size()];
        selectedDirectorsStringArray = directorsList.toArray(new String[0]);
        selectedGenresStringArray = genresList.toArray(new String[0]);

        for (int p = 0; p < genresList.size(); p++) {
            selectedGenresBoolArray[p] = false;
        }
        for (int i = 0; i < directorsList.size(); i++) {
            selectedDirectorsBoolArray[i] = false;
        }
    }


    private void showDialog(String title, String[] listItems, boolean[] listItemsBool, final int dialogId) {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMultiChoiceItems(listItems, listItemsBool, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int indexSelected, boolean isChecked) {
                        //change bool array value accordingly
                        if (dialogId == DIRECTORS_DIALOG) {
                            selectedDirectorsBoolArray[indexSelected] = isChecked;
                        } else {
                            selectedGenresBoolArray[indexSelected] = isChecked;
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (dialogId == DIRECTORS_DIALOG) {
                            applyDirectorFilter = true;
                        } else {
                            applyGenreFilter = true;
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                putFilterOnResult();
                            }
                        });

                        dialog.dismiss();
                    }
                }).setNegativeButton("CLEAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  clear bool array by dialog id
                        if (dialogId == DIRECTORS_DIALOG) {
                            for (int i = 0; i < selectedDirectorsBoolArray.length; i++) {
                                selectedDirectorsBoolArray[i] = false;
                            }
                            applyDirectorFilter = false;
                        } else {
                            for (int i = 0; i < selectedGenresBoolArray.length; i++)
                                selectedGenresBoolArray[i] = false;
                            applyGenreFilter = false;
                        }
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }


    private void RatingDialog() {
        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMaxValue(10);
        numberPicker.setMinValue(0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(numberPicker);
        builder.setTitle("Filter by Rating");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ratingFilterValue=numberPicker.getValue();
                applyRatingFilter = true;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        putFilterOnResult();
                    }
                });
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                applyRatingFilter = false;
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void putFilterOnResult() {
        long startTime = System.nanoTime();
        HashSet<MoviesDataModel> filteredMoveis = new HashSet<>();
        //add directors & genres in filter {@Link HashSet} for uniqueness
        HashSet<String> filter = new HashSet<>();
        for (int i = 0; i < selectedDirectorsBoolArray.length; i++) {
            if (selectedDirectorsBoolArray[i]) {
                filter.add(selectedDirectorsStringArray[i]);
            }
        }
        for (int j = 0; j < selectedGenresBoolArray.length; j++) {
            if (selectedGenresBoolArray[j]) {
                filter.add(selectedGenresStringArray[j]);
            }
        }
        //add filtered items in @link filteredMoveis
        if (applyRatingFilter || applyGenreFilter || applyDirectorFilter) {
            for (MoviesDataModel eachMoviesDataModel : moviesArrayList) {
                //filter by directors
                if (applyDirectorFilter && eachMoviesDataModel.getInfo().getDirectors() != null) {
                    for (String s : eachMoviesDataModel.getInfo().getDirectors()) {
                        if (filter.contains(s)) {
                            filteredMoveis.add(eachMoviesDataModel);
                        }
                    }
                }
                //filter by genres
                if (applyGenreFilter && eachMoviesDataModel.getInfo().getGenres() != null) {
                    for (String s : eachMoviesDataModel.getInfo().getGenres()) {
                        if (filter.contains(s)) {
                            filteredMoveis.add(eachMoviesDataModel);
                        }
                    }
                }
                //filter by rating
                if (applyRatingFilter) {
                    if (eachMoviesDataModel.getInfo().getRating() == ratingFilterValue) {
                        filteredMoveis.add(eachMoviesDataModel);
                    }
                }
            }
            moviesRecyclerADP.addItems(new ArrayList<MoviesDataModel>(filteredMoveis));
        } else {
            moviesRecyclerADP.addItems(moviesArrayList);
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        Log.d("Filter Time ", duration + "");
    }

    private void setUpActionBar(boolean isShowing){
          ActionBar actionBar =getSupportActionBar();
          if (actionBar!=null){
              actionBar.setDisplayHomeAsUpEnabled(isShowing);
              optionMenu.setGroupVisible(R.id.main_menu_group,!isShowing);
          }
        fl_fragment_container.setVisibility(isShowing?View.VISIBLE:View.GONE);
    }

    @Override
    public void onMovieClick(MoviesDataModel moviesDataModel) {
        setUpActionBar(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_fragment_container,MovieDetailsFragment.newInstance(moviesDataModel)).addToBackStack("m").commit();
    }

    @Override
    public void onFragmentExit() {
        setUpActionBar(false);
    }
}
