package com.iscte.dam;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.iscte.dam.models.News;

import java.util.ArrayList;

/**
 * Created by b.coitos on 5/31/2018.
 */

public class RecViewAdapter extends RecyclerView.Adapter<RecViewAdapter.ViewHolder> {
    private ArrayList<News> newsList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View view;
        public TextView title;
        public TextView shortDescription;
        public String desc;

        public ViewHolder(View v) {
            super(v);

            view = v;
            this.title = (TextView)v.findViewById(R.id.tView_title);
            this.shortDescription = (TextView)v.findViewById(R.id.tView_desc);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Title:" + title.getText(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(v.getContext(),NewsView.class);
                    intent.putExtra("title",title.getText());
                    intent.putExtra("shortDesc",shortDescription.getText());
                    intent.putExtra("desc", desc);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecViewAdapter(ArrayList<News> newsList) {
        this.newsList = newsList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.title.setText(newsList.get(position).getTitle());
        holder.shortDescription.setText(newsList.get(position).getShortDescription());
        holder.desc = newsList.get(position).getDescription();

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(newsList.size()>0){
            Log.e("nooooo", ">0!!!!!");
        }else{
            Log.e("nooooo", "<0   NOOOOOOOOOOOOOOOOOOOO");
        }

        return newsList.size();
    }
}
