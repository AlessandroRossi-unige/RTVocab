package com.example.rtvocab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Map;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private TextView keyTextView;
        private TextView valueTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            this.keyTextView = itemView.findViewById(R.id.rowText1);
            this.valueTextView = itemView.findViewById(R.id.rowText2);
        }

        public void setText(String key, String value) {
            this.keyTextView.setText(WordUtils.capitalize(key));
            this.valueTextView.setText(WordUtils.capitalize(value));
        }
    }

    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private ArrayList<String> dataKey;
    private ArrayList<String> dataValue;

    public ListViewAdapter(Map<String,String> data) {
        this.dataKey = new ArrayList<>(data.keySet());
        this.dataValue = new ArrayList<>(data.values());
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return dataKey.size();
    }

    // specify the row layout file and click for each row
    @NonNull
    @Override
    public ListViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View rowView = inflater.inflate(R.layout.row, parent, false);
        // Return a new holder instance
        return new ViewHolder(rowView);
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(ListViewAdapter.ViewHolder holder, int position) {
        holder.setText(dataKey.get(position), dataValue.get(position));
    }
}
