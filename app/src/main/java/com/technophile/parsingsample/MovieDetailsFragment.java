package com.technophile.parsingsample;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";


    private MoviesDataModel moviesDataModel;

    private OnFragmentInteractionListener mListener;

    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 MoviesDataModel in Parameter 1.
     * @return A new instance of fragment MovieDetailsFragment.
     */

    public static MovieDetailsFragment newInstance(MoviesDataModel param1) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            moviesDataModel = (MoviesDataModel) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_movie_details, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView iv_movie_img=(ImageView)view.findViewById(R.id.iv_movie_img);
        Picasso.with(getContext()).load(moviesDataModel.getInfo().getImage_url()).placeholder(R.drawable.ic_launcher_background).into(iv_movie_img);
        TextView tv_title_fragment,tv_info_fragment,tv_desc_fragment,tv_rating_fragment
                ,tv_cast_fragment,tv_directors_fragment;
        tv_title_fragment=(TextView)view.findViewById(R.id.tv_title_fragment);
        tv_info_fragment=(TextView)view.findViewById(R.id.tv_info_fragment);
        tv_desc_fragment=(TextView)view.findViewById(R.id.tv_desc_fragment);
        tv_rating_fragment=(TextView)view.findViewById(R.id.tv_rating_fragment);
        tv_cast_fragment=(TextView)view.findViewById(R.id.tv_cast_fragment);
        tv_directors_fragment=(TextView)view.findViewById(R.id.tv_directors_fragment);

        tv_title_fragment.setText(moviesDataModel.getTitle());
        String info=moviesDataModel.getYear()+" | "+MyTools.getGenre(moviesDataModel.getInfo().getGenres().toString());
        tv_info_fragment.setText(info);
        tv_desc_fragment.setText(moviesDataModel.getInfo().getPlot());
        RatingBar rb_movie_rating_fragment=(RatingBar)view.findViewById(R.id.rb_movie_rating_fragment);
        tv_rating_fragment.setText( moviesDataModel.getInfo().getRating()==0.0?"N/A":moviesDataModel.getInfo().getRating()+"");
        rb_movie_rating_fragment.setRating(moviesDataModel.getInfo().getRating());
        tv_cast_fragment.setText(MyTools.getGenre(moviesDataModel.getInfo().getActors().toString()));
        tv_directors_fragment.setText(MyTools.getGenre(moviesDataModel.getInfo().getDirectors().toString()));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mListener != null) {
            mListener.onFragmentExit();
        }
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentExit();
    }
}
