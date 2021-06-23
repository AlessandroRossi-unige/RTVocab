package com.example.rtvocab;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.Map;

public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> dataKey;
    private ArrayList<String> dataValue;
    private static LayoutInflater inflater = null;

    public ListViewAdapter(Context context, Map<String,String> data) {
        this.context = context;
        this.dataKey = new ArrayList<>(data.keySet());
        this.dataValue = new ArrayList<>(data.values());
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dataKey.size();
    }

    @Override
    public Object getItem(int position) {
        return new Pair<>(dataKey.get(position), dataValue.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null) vi = inflater.inflate(R.layout.row, null);
        vi.setBackgroundResource(R.drawable.custom_shape);
        TextView text1 = vi.findViewById(R.id.rowText1);
        TextView text2 = vi.findViewById(R.id.rowText2);
        String first = WordUtils.capitalize(dataKey.get(position));
        String second = WordUtils.capitalize(dataValue.get(position));
        text1.setText(first);
        text2.setText(second);
        return vi;
    }
}
