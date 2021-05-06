package com.example.rtvocab;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
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


public class MainActivity extends AppCompatActivity implements AnalysisCompleted {

    Button btn_getAnalysis = null;
    Button btn_Translate = null;
    RecyclerView rv_AnalysisResults = null;
    RecyclerView rv_TranslateResults = null;
    EditText et_datainput = null;
    EditText et_Translate = null;

    VisionTask visionTask = null;
    AnalysisCompleted analysisCompleted = null;


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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                visionTask = new VisionTask(MainActivity.this ,analysisCompleted);


                String imgPath = et_datainput.getText().toString();
                if(Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
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

    }
}