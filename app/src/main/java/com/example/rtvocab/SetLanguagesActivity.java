package com.example.rtvocab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class SetLanguagesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinnerFrom;
    private Spinner spinnerTo;

    LanguagesPref languagesPref;

    private String lanFrom;
    private String lanTo;

    ArrayList<String> lanArray;

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
        menu.findItem(R.id.MENU_2).setVisible(true);
        menu.findItem(R.id.MENU_3).setVisible(false);
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
            case R.id.MENU_2:
                this.startActivity(new Intent(this, DictActivity.class));
                break;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_lan);

        lanArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.lan_cod_array)));

        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerFrom.setOnItemSelectedListener(this);
        spinnerTo = findViewById(R.id.spinnerTo);
        spinnerTo.setOnItemSelectedListener(this);

        // load language preferences
        SharedPreferences sP = getSharedPreferences(getString(R.string.LAN_PREF), Context.MODE_PRIVATE);
        languagesPref = new LanguagesPref(sP, getString(R.string.SAVE_LAN_FROM), getString(R.string.SAVE_LAN_TO));
        lanFrom = languagesPref.getLanFrom();
        lanTo = languagesPref.getLanTo();

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.languages_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerFrom.setAdapter(spinnerAdapter);
        int posL = lanArray.indexOf(lanFrom);
        spinnerFrom.setSelection(posL);

        spinnerTo.setAdapter(spinnerAdapter);
        posL = lanArray.indexOf(lanTo);
        spinnerTo.setSelection(posL);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String lanCod = lanArray.get(position);
        if (parent == spinnerFrom) {
            // reverse languages
            if (lanTo.equals(lanCod)) {
                lanTo = lanFrom;
                spinnerTo.setSelection(lanArray.indexOf(lanTo));
            }
            lanFrom = lanCod;
        }
        if (parent == spinnerTo) {
            // reverse languages
            if (lanFrom.equals(lanCod)) {
                lanFrom = lanTo;
                spinnerFrom.setSelection(lanArray.indexOf(lanFrom));
            }
            lanTo = lanCod;
        }
        languagesPref.saveLanPref(lanFrom, lanTo);
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (parent == spinnerFrom) parent.setSelection(lanArray.indexOf(lanFrom));
        if (parent == spinnerTo) parent.setSelection(lanArray.indexOf(lanTo));
    }
}
