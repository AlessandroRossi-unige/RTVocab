package com.example.rtvocab;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.microsoft.azure.cognitiveservices.vision.computervision.*;
import com.microsoft.azure.cognitiveservices.vision.computervision.implementation.ComputerVisionImpl;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.*;
import com.squareup.okhttp.*;

import static java.nio.file.Files.createDirectory;


public class MainActivity extends AppCompatActivity implements AnalysisCompleted{

    Button btn_getAnalysis = null;
    RecyclerView rv_getAnalysis = null;
    ImageView iv_Image = null;

    VisionTask visionTask = null;
    AnalysisCompleted analysisCompleted = null;

    private final static int PICK_IMAGE = 1;

    ArrayList <Item> itemList = new ArrayList<Item>();

    ItemArrayAdapter itemArrayAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_getAnalysis = findViewById(R.id.btn_getAnalysis);
        rv_getAnalysis = findViewById(R.id.rv_getAnalysis);
        iv_Image = findViewById(R.id.iv_Image);

        // Initializing list view with the custom adapter
        itemArrayAdapter = new ItemArrayAdapter(R.layout.list_item, itemList);
        rv_getAnalysis = (RecyclerView) findViewById(R.id.rv_getAnalysis);
        rv_getAnalysis.setLayoutManager(new LinearLayoutManager(this));
        rv_getAnalysis.setItemAnimator(new DefaultItemAnimator());
        rv_getAnalysis.setAdapter(itemArrayAdapter);

        btn_getAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

//        btn_Translate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String[] inputText = {"ciao","it","en"};
//                new TranslateTask().execute(inputText);
//            }
//        });
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
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            InputStream iStream = null;
            try {
                iStream = getContentResolver().openInputStream(imageUri);
                byte[] inputData = this.getBytes(iStream);
                new VisionTask().execute(inputData);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
        /*
        btn_getAnalysis.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                visionTask = new VisionTask(MainActivity.this ,analysisCompleted);


                String imgPath = et_datainput.getText().toString();
                if(Build.VERSION.SDK_INT >= 11) {
                    visionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    visionTask.execute();
                }
            }
        });
    }
*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onAnalysisCompleted(List<String> tags){

        for (String tag:tags) {

        }
    }
}