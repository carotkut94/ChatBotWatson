package com.death.xorbot;

/**
 * Created by deathcode on 06/11/17.
 */

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BotAdapter extends RecyclerView.Adapter<BotAdapter.MyViewHolder> {

    private List<ResponseModel> responseModels;
    RecyclerView collection_container;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView title;


        public MyViewHolder(View v, int viewType) {
            super(v);
            setIsRecyclable(false);
            title = v.findViewById(R.id.edit_query);
            if (viewType == 4||viewType == 5 || viewType == 6) {
                collection_container = v.findViewById(R.id.collection_container);
            }
        }


    }


    BotAdapter(List<ResponseModel> responseModels, Context context) {
        this.responseModels = responseModels;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (responseModels.get(position).isMine()) {
            return 1;
        } else if(!responseModels.get(position).isMine() && responseModels.get(position).isLoading()){
            return 3;
        } else if(responseModels.get(position).isMovie() && responseModels.get(position).getMovieQueryType()!=null)
        {
            if(responseModels.get(position).getMovieQueryType().equals("PopularMovies"))
                return 4;
            else
                return 5;
        }else if(responseModels.get(position).isStackOverflow()){
            return 6;
        }
        else{
            return 2;
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == 1) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.query_mine, parent, false);
        } else if(viewType == 2) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.query_bot, parent, false);
        } else if(viewType == 3){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.loader, parent, false);
        }else if(viewType == 4){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.query_bot_movie, parent, false);
        }
        else if(viewType == 6)
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.query_bot_stackoverflow, parent, false);
        }
        else{
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.query_bot_top_movie, parent, false);
        }
        return new MyViewHolder(itemView, viewType);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ResponseModel model = responseModels.get(position);
        holder.title.setText(model.getResponse());
        if(model.isCollection()){
            if(model.isMovie()){
                collection_container.setVisibility(View.VISIBLE);
                MovieAdapter movieAdapter = new MovieAdapter(model.getMovies(), context);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                collection_container.setLayoutManager(linearLayoutManager);
                collection_container.addItemDecoration(new ItemDecorator(model.getMovies().getResults().size(), 20, true));
                collection_container.setAdapter(movieAdapter);
            }
            if(model.isStackOverflow())
            {
                collection_container.setVisibility(View.VISIBLE);
                StackOverFlowAdapter stackOverFlowAdapter = new StackOverFlowAdapter(model.getStackOverflow(), context);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                collection_container.setLayoutManager(linearLayoutManager);
                collection_container.addItemDecoration(new ItemDecorator(model.getStackOverflow().size(), 20, true));
                collection_container.setAdapter(stackOverFlowAdapter);
            }
        }
    }

    @Override
    public int getItemCount() {
        return responseModels.size();
    }


    int removeLastElement(){
        responseModels.remove(responseModels.size()-1);
        return responseModels.size()-1;
    }
}