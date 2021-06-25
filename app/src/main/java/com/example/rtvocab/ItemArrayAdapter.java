
package com.example.rtvocab;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ItemArrayAdapter extends RecyclerView.Adapter<ItemArrayAdapter.ViewHolder> {

    private static AnalysisCompleted delegate = null;

    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private final int listItemLayout;
    private ArrayList<Item> itemList;
    // Constructor of the class
    public ItemArrayAdapter(int layoutId, ArrayList<Item> itemList, AnalysisCompleted delegate) {
        listItemLayout = layoutId;
        this.itemList = itemList;
        this.delegate = delegate;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public Item getItem(int pos) {
        return itemList.get(pos);
    }

    // specify the row layout file and click for each row
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        return new ViewHolder(view);
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        TextView item = holder.item;
        item.setText(itemList.get(listPosition).getFirst());
    }
    public void clearData() {
        itemList.clear();
        notifyDataSetChanged();
    }

    public void updateData(ArrayList<Item> data) {
        itemList.clear();
        itemList = data;
        notifyDataSetChanged();
    }

    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView item;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            item = itemView.findViewById(R.id.row_item);
        }
        @Override
        public void onClick(View view) {
            delegate.onSelectCompleted(getLayoutPosition());
        }
    }
}