package com.example.rtvocab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
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


public class MainActivity extends AppCompatActivity {

    Button btn_getAnalysis = null;
    Button btn_Translate = null;
    RecyclerView rv_AnalysisResults = null;
    RecyclerView rv_TranslateResults = null;
    EditText et_datainput = null;
    EditText et_Translate = null;


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
    }
}