package com.death.xorbot;

/**
 * Created by deathcode on 06/11/17.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    private Movies movies;
    private String imageURL = "https://image.tmdb.org/t/p/w320";
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView posterView;
        public TextView movieTitle;

        public MyViewHolder(View view) {
            super(view);
            setIsRecyclable(false);
            posterView = view.findViewById(R.id.movie_poster);
            movieTitle = view.findViewById(R.id.movie_title);
        }
    }


    public MovieAdapter(Movies movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Result movie = movies.getResults().get(position);
        holder.movieTitle.setText(movie.getTitle());
        Glide.with(context).load(imageURL+movie.getPosterPath()).into(holder.posterView);
    }

    @Override
    public int getItemCount() {
        return movies.getResults().size();
    }
}