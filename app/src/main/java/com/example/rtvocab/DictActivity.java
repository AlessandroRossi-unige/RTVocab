package com.example.rtvocab;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Map;

public class DictActivity extends Activity {

    TextView tv_getDict = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict);

        SharedPreferences sharedPref = DictActivity.this.getSharedPreferences("DICTIONARY",Context.MODE_PRIVATE);
        String message = "";
        Map<String, ?> allEntries = sharedPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            message += entry.getKey() + ": " + entry.getValue().toString() + "\n";
        }
        tv_getDict = findViewById(R.id.tv_getDict);
        tv_getDict.setText(message);


    }
}