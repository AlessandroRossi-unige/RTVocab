package com.example.rtvocab;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements AnalysisCompleted {

    private Button btn_getAnalysis;
    private RecyclerView rv_getAnalysis;
    private TextView lanSelectedTextView;
    private ImageView iv_Image;
    private TextView selectedTextView;
    private ProgressBar progressBar;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;
    Uri photoUri = null;
    private LanguagesPref loadLan;

    private ArrayList <Item> itemList = new ArrayList<Item>();

    private ItemArrayAdapter itemArrayAdapter = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.MENU_1).setVisible(false);
        menu.findItem(R.id.MENU_2).setVisible(true);
        menu.findItem(R.id.MENU_3).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        switch(id) {
            case R.id.MENU_2:
                SharedPreferences sharedPreferences = getSharedPreferences("Dicts", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("DictName", loadLan.getLanFrom() + '-' + loadLan.getLanTo());
                editor.apply();
                this.startActivity(new Intent(this, DictActivity.class));
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
        lanSelectedTextView.setText(lanString.get(posFrom) + " -> " + lanString.get(posTo));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set languagesPref
        SharedPreferences sP = getSharedPreferences(getString(R.string.LAN_PREF), Context.MODE_PRIVATE);
        loadLan = new LanguagesPref(sP, getString(R.string.SAVE_LAN_FROM), getString(R.string.SAVE_LAN_TO));

        btn_getAnalysis = findViewById(R.id.btn_getAnalysis);
        rv_getAnalysis = findViewById(R.id.rv_getAnalysis);
        lanSelectedTextView = findViewById(R.id.lanSelect);
        iv_Image = findViewById(R.id.iv_Image);
        selectedTextView = findViewById(R.id.selectedTextView);
        progressBar = findViewById(R.id.progressBar);

        selectedTextView.setText("Press Analyze to take a picture");
        displayLanSelect();

        progressBar.setVisibility(View.INVISIBLE);

        // Initializing list view with the custom adapter
        itemArrayAdapter = new ItemArrayAdapter(R.layout.list_item, itemList, this);
        rv_getAnalysis = (RecyclerView) findViewById(R.id.rv_getAnalysis);
        rv_getAnalysis.setLayoutManager(new LinearLayoutManager(this));
        rv_getAnalysis.setItemAnimator(new DefaultItemAnimator());
        rv_getAnalysis.setAdapter(itemArrayAdapter);


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
    public void onAnalysisCompleted(List<String> tags) {
        // load language preferences
        tags.add(0, loadLan.getLanFrom());
        tags.add(1, loadLan.getLanTo());

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

        String dictname = loadLan.getLanFrom() + '-' + loadLan.getLanTo();
        SharedPreferences lanPref = this.getSharedPreferences(dictname, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = lanPref.edit();
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


}