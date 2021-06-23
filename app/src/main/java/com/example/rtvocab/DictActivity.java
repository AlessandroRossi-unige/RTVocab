package com.example.rtvocab;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class DictActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.MENU_1).setVisible(true);
        menu.findItem(R.id.MENU_2).setVisible(false);
        menu.findItem(R.id.MENU_3).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        switch(id) {
            case R.id.MENU_1:
                this.startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.MENU_3:
                this.startActivity(new Intent(this, SetLanguagesActivity.class));
                break;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict);

        SharedPreferences sharedPref = DictActivity.this.getSharedPreferences("DICTIONARY",Context.MODE_PRIVATE);

        Map<String, String> allEntries = (Map<String, String>) sharedPref.getAll();

        ListViewAdapter adapter = new ListViewAdapter(this,allEntries);
        listView = findViewById(R.id.listView);
        listView.setEnabled(false);
        listView.setAdapter(adapter);

    }
}