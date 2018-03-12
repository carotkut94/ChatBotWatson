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

import java.util.List;
import java.util.Stack;


public class StackOverFlowAdapter extends RecyclerView.Adapter<StackOverFlowAdapter.MyViewHolder> {

    private List<StackOverflow> overflows;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView vote;
        public TextView answer;
        public TextView question;
        public TextView excerpt;

        public MyViewHolder(View v) {
            super(v);
            setIsRecyclable(false);
            vote = v.findViewById(R.id.votes);
            answer = v.findViewById(R.id.answers);
            question = v.findViewById(R.id.question);
            excerpt = v.findViewById(R.id.excerpt);
        }
    }


    public StackOverFlowAdapter(List<StackOverflow> overflows, Context context) {
        this.overflows = overflows;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stackoverlfow_query, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        StackOverflow stackOverflow = overflows.get(position);
        holder.question.setText(stackOverflow.getQuestion());
        holder.excerpt.setText(stackOverflow.getExcerpt());
        holder.vote.setText(stackOverflow.getVotes());
        holder.answer.setText(stackOverflow.getAnswers_count());
    }

    @Override
    public int getItemCount() {
        return overflows.size();
    }
}