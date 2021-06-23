package com.example.rtvocab;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.Map;

public class DictActivity extends Activity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict);

        SharedPreferences sharedPreferences = getSharedPreferences("Dicts",MODE_PRIVATE);
        String dictName = sharedPreferences.getString("DictName", "null");
        SharedPreferences sharedPref = DictActivity.this.getSharedPreferences(dictName,Context.MODE_PRIVATE);

        Map<String, String> allEntries = (Map<String, String>) sharedPref.getAll();

        ListViewAdapter adapter = new ListViewAdapter(this,allEntries);
        listView = findViewById(R.id.listView);
        listView.setEnabled(false);
        listView.setAdapter(adapter);

    }
}