package com.example.rtvocab;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements AnalysisCompleted, AdapterView.OnItemSelectedListener {

    private Button btn_getAnalysis;
    private Button btn_getDict;
    private RecyclerView rv_getAnalysis;
    private ImageView iv_Image;
    private TextView selectedTextView;
    private ProgressBar progressBar;
    private Spinner spinnerFrom;
    private Spinner spinnerTo;
    private String lanFrom;
    private String lanTo;
    private String dictName;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;
    Uri photoUri = null;

    private ArrayList <Item> itemList = new ArrayList<Item>();

    private ItemArrayAdapter itemArrayAdapter = null;

    public void loadLanPref() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String savedFrom = getString(R.string.SAVE_LAN_FROM);
        String savedTo = getString(R.string.SAVE_LAN_TO);
        lanFrom = sharedPreferences.getString(savedFrom, "en");
        lanTo = sharedPreferences.getString(savedTo, "it");
        dictName = lanFrom + '-' + lanTo;

    }

    public void saveLanPref(String from, String to) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String savedFrom = getString(R.string.SAVE_LAN_FROM);
        String savedTo = getString(R.string.SAVE_LAN_TO);
        editor.putString(savedFrom, from);
        editor.putString(savedTo, to);
        editor.apply();
        dictName = from + '-' + to;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_getAnalysis = findViewById(R.id.btn_getAnalysis);
        btn_getDict = findViewById(R.id.btn_getDict);
        rv_getAnalysis = findViewById(R.id.rv_getAnalysis);
        iv_Image = findViewById(R.id.iv_Image);
        selectedTextView = findViewById(R.id.selectedTextView);
        progressBar = findViewById(R.id.progressBar);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerFrom.setOnItemSelectedListener(this);
        spinnerTo = findViewById(R.id.spinnerTo);
        spinnerTo.setOnItemSelectedListener(this);

        selectedTextView.setText("Press Analyze to take a picture");

        progressBar.setVisibility(View.INVISIBLE);

        // Initializing list view with the custom adapter
        itemArrayAdapter = new ItemArrayAdapter(R.layout.list_item, itemList, this);
        rv_getAnalysis = (RecyclerView) findViewById(R.id.rv_getAnalysis);
        rv_getAnalysis.setLayoutManager(new LinearLayoutManager(this));
        rv_getAnalysis.setItemAnimator(new DefaultItemAnimator());
        rv_getAnalysis.setAdapter(itemArrayAdapter);

        // load language preferences
        loadLanPref();

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.languages_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinnerFrom.setAdapter(spinnerAdapter);
        int posL = getIndex(getResources().getStringArray(R.array.lan_cod_array), lanFrom);
        spinnerFrom.setSelection(posL);

        spinnerTo.setAdapter(spinnerAdapter);
        posL = getIndex(getResources().getStringArray(R.array.lan_cod_array), lanTo);
        spinnerTo.setSelection(posL);


        btn_getAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTextView.setText("Press Analyze to take a picture");
                itemArrayAdapter.clearData();
                photoUri = null;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                "com.example.android.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        takePictureIntent.putExtra("return-data", false);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        photoUri = photoURI;
                    }
                }


            }
        });

        btn_getDict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences sharedPreferences = getSharedPreferences("Dicts", MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("DictName", lanFrom + '-' + lanTo);
                editor.apply();
                Intent intent = new Intent(getString(R.string.LAUNCH_ACTIVITY));
                startActivity(intent);

            }
        });
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Here we need to check if the activity that was triggers was the Image Gallery.
        // If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
        // If the resultCode is RESULT_OK and there is some data we know that an image was picked.
        if (requestCode == REQUEST_IMAGE_CAPTURE && photoUri != null) {
            Uri imageUri = photoUri;
            InputStream iStream = null;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            Picasso.get().load(imageUri).resize(width,height).centerInside().into(iv_Image);
            try {
                iStream = getContentResolver().openInputStream(photoUri);
                byte[] inputData = this.getBytes(iStream);
                progressBar.setVisibility(View.VISIBLE);
                new VisionTask(this).execute(inputData);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    public void onAnalysisCompleted(List<String> tags){
        tags.add(0, lanFrom);
        tags.add(1, lanTo);
        String[] input = new String[tags.size()];
        tags.toArray(input);
        new TranslateTask(this).execute(input);

    }

    @Override
    public void onTranslateCompleted(List<Pair<String,String>> results) {
        if (results != null) {
            ArrayList<Item> input = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                input.add(new Item(results.get(i).first, results.get(i).second));
            }
            itemArrayAdapter.updateData(input);
            selectedTextView.setText("Select from the list");
        } else {
            itemArrayAdapter.clearData();
            selectedTextView.setText("ERROR! Try again");
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSelectCompleted(int position){
        Item selected = itemArrayAdapter.getItem(position);
        itemArrayAdapter.clearData();
        selectedTextView.setText("You chose: " + selected.getFirst() + " = " + selected.getSecond());

        SharedPreferences sharedPref = this.getSharedPreferences(dictName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(selected.getFirst(), selected.getSecond());
        editor.apply();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] lanCod = getResources().getStringArray(R.array.lan_cod_array);
        if (parent == spinnerFrom) lanFrom = lanCod[position];
        if (parent == spinnerTo) lanTo = lanCod[position];
    }

    public int getIndex(String[] arrayS, String el) {
        for (int i = 0; i < arrayS.length; i++) {
            if (arrayS[i].equals(el)) return i;
        }
        return -1;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        String[] lanCod = getResources().getStringArray(R.array.lan_cod_array);
        if (parent == spinnerFrom) parent.setSelection(getIndex(lanCod, lanFrom));
        if (parent == spinnerTo) parent.setSelection(getIndex(lanCod, lanTo));
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveLanPref(lanFrom, lanTo);
    }


}