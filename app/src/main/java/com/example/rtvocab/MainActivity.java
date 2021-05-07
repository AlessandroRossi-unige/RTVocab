package com.example.rtvocab;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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


public class MainActivity extends AppCompatActivity {

    Button btn_getAnalysis = null;
    Button btn_Translate = null;
    public static RecyclerView rv_AnalysisResults = null;
    RecyclerView rv_TranslateResults = null;
    public static EditText et_datainput = null;
    EditText et_Translate = null;

    VisionTask visionTask = null;
    AnalysisCompleted analysisCompleted = null;

    private final static int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_getAnalysis = findViewById(R.id.btn_getAnalysis);
        btn_Translate = findViewById(R.id.btn_Translate);
        et_datainput = findViewById(R.id.et_dataInput);
        et_Translate = findViewById(R.id.et_Translate);
        rv_AnalysisResults = findViewById(R.id.rv_AnalysisResults);
        rv_TranslateResults = findViewById(R.id.rv_TranslateResults);


        btn_getAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onAnalysisCompleted(List<ImageTag> tags){
        List<String> results = null;
        for (ImageTag tag : tags) {
            results.add(tag.name()+ " with " + tag.confidence());
        }
        String result = String.join(", ", results);
        et_datainput.setText(result);

    }*/
}