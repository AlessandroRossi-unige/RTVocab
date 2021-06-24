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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class DictActivity extends AppCompatActivity {

    private ListView listView;
    private LanguagesPref loadLan;
    private TextView dictSelectTextView;
    private String emptyMsg;

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

    // show current language preferences
    private void displayLanSelect() {
        ArrayList<String> lanCod = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.lan_cod_array)));
        ArrayList<String> lanString = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.languages_array)));
        int posFrom = lanCod.indexOf(loadLan.getLanFrom());
        int posTo = lanCod.indexOf(loadLan.getLanTo());
        dictSelectTextView.setText("Dictionary " + lanString.get(posFrom) + "/" + lanString.get(posTo));
        emptyMsg = lanString.get(posFrom) + "/" + lanString.get(posTo) + " is empty";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dict);

        dictSelectTextView = findViewById(R.id.dictSelect);

        // set languagesPref
        SharedPreferences sP = getSharedPreferences(getString(R.string.LAN_PREF), Context.MODE_PRIVATE);
        loadLan = new LanguagesPref(sP, getString(R.string.SAVE_LAN_FROM), getString(R.string.SAVE_LAN_TO));
        displayLanSelect();

        SharedPreferences sharedPref = getSharedPreferences(loadLan.getDictName(),MODE_PRIVATE);

        Map<String, String> allEntries = (Map<String, String>) sharedPref.getAll();
        if (allEntries.isEmpty()) {
            allEntries.put(emptyMsg, "");
        }

        ListViewAdapter adapter = new ListViewAdapter(this,allEntries);
        listView = findViewById(R.id.listView);
        listView.setEnabled(false);
        listView.setAdapter(adapter);

    }
}