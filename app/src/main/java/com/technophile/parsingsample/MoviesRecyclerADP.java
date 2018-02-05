package com.technophile.parsingsample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Technophile on 2/3/2018.
 */

class MoviesRecyclerADP extends RecyclerView.Adapter <MoviesRecyclerADP.VH>{
    private Context context;
    private ArrayList<MoviesDataModel> moviesArrayList=new ArrayList<>();
    private OnMovieClickListener onMovieClickListener;
    public MoviesRecyclerADP(Context context,OnMovieClickListener onMovieClickListener) {
        this.context=context;
        this.onMovieClickListener=onMovieClickListener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {

        return new VH(View.inflate(context,R.layout.adapter_movies_recycler,null));
    }

    @Override
    public void onBindViewHolder(VH holder, int pos) {

        final int position =holder.getAdapterPosition();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onMovieClickListener.onMovieClick(moviesArrayList.get(position));
            }
        });
        MoviesDataModel movie=moviesArrayList.get(position);
        holder.tv_movie_title.setText(movie.getTitle());
        holder.tv_movie_desc.setText(movie.getInfo().getPlot());
        holder.tv_movie_rating.setText( movie.getInfo().getRating()==0.0?"N/A":movie.getInfo().getRating()+"");
        String info=movie.getYear()+" | "+MyTools.getGenre(movie.getInfo().getGenres().toString());
        holder.tv_movie_info.setText(info);
        holder.rb_movie_rating.setRating(movie.getInfo().getRating());
        Picasso.with(context).load(movie.getInfo().getImage_url()).placeholder(R.drawable.ic_launcher_background).into(holder.iv_movie_picture);
    }



    @Override
    public int getItemCount() {
        return moviesArrayList.size();
    }

    public void addItems(ArrayList<MoviesDataModel> moviesArrayList) {
        this.moviesArrayList=moviesArrayList;
            notifyDataSetChanged();
    }

    public static final int SORT_BY_TITLE=0;
    public static final int ORDER_ASCENDING=0;
    public static final int ORDER_DESCENDING=1;
    public static final int SORT_BY_YEAR=1;

    // perform sorting
    public void sortItems(final int sortBy, final int order) {

        if (moviesArrayList!=null&&!moviesArrayList.isEmpty()){
           Collections.sort(moviesArrayList, new Comparator<MoviesDataModel>(){
                public int compare(MoviesDataModel obj1, MoviesDataModel obj2) {

                    if (sortBy==SORT_BY_TITLE){
                        if (order==ORDER_ASCENDING){
                            return obj1.getTitle().compareToIgnoreCase(obj2.getTitle());
                        }else {
                            return obj2.getTitle().compareToIgnoreCase(obj1.getTitle());
                        }

                    }else
                    {
                        if (order==ORDER_ASCENDING){
                            return Integer.valueOf(obj2.getYear()).compareTo(obj1.getYear());
                        }else {
                            return Integer.valueOf(obj1.getYear()).compareTo(obj2.getYear());
                        }

                    }

                }
            });
        }
        notifyDataSetChanged();
    }

    public class VH extends RecyclerView.ViewHolder {
        TextView tv_movie_title,tv_movie_desc,tv_movie_rating,tv_movie_info;
        ImageView iv_movie_picture;
        RatingBar rb_movie_rating;
        public VH(View itemView) {
            super(itemView);
            tv_movie_title=(TextView)itemView.findViewById(R.id.tv_movie_title);
            tv_movie_desc=(TextView)itemView.findViewById(R.id.tv_movie_desc);
            tv_movie_rating=(TextView)itemView.findViewById(R.id.tv_movie_rating);
            tv_movie_info=(TextView)itemView.findViewById(R.id.tv_movie_info);
            iv_movie_picture=(ImageView) itemView.findViewById(R.id.iv_movie_picture);
            rb_movie_rating=(RatingBar) itemView.findViewById(R.id.rb_movie_rating);
        }
    }

    public interface OnMovieClickListener {
        void onMovieClick(MoviesDataModel moviesDataModel);
    }

}
