package com.example.rtvocab;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.util.Pair;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.microsoft.azure.cognitiveservices.vision.computervision.*;
import com.microsoft.azure.cognitiveservices.vision.computervision.implementation.ComputerVisionImpl;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.*;

import java.io.*;
import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.nio.file.*;

/*
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionClient;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionManager;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageAnalysis;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageTag;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.VisualFeatureTypes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
*/

public class VisionTask extends AsyncTask<byte[], Integer, String> {

    private static String subscriptionKey = "f9779642e5364a89af2473de6a73b49d";
    private static String endpoint = "https://unige-vision.cognitiveservices.azure.com/";
    private List<Pair<String, Double>> tagList;

    //---------------------------------------------//

    private static ComputerVisionClient Authenticate(String subscriptionKey, String endpoint){
        return ComputerVisionManager.authenticate(subscriptionKey).withEndpoint(endpoint);
    }

    private String AnalyzeLocalImage(ComputerVisionClient compVisClient, byte[] imageByte) {
        // This list defines the features to be extracted from the image.
        List<VisualFeatureTypes> featuresToExtractFromLocalImage = new ArrayList<>();
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.CATEGORIES);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.TAGS);

        try {
            // Call the Computer Vision service and tell it to analyze the loaded image.
            ImageAnalysis analysis = compVisClient.computerVision().analyzeImageInStream().withImage(imageByte).withVisualFeatures(featuresToExtractFromLocalImage).execute();
            // Get image tags and confidence values.
            this.tagList = new ArrayList<>();
            for (int i = 0; i < analysis.tags().size() && i < 5; i++) {
                ImageTag tag = analysis.tags().get(i);
                this.tagList.add(new Pair<>(tag.name(), tag.confidence()));
            }
            return "OK";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return "ERROR";
        }
    }

    @Override
    protected String doInBackground(byte[]... imageByte) {
        ComputerVisionClient cvc = Authenticate(subscriptionKey, endpoint);
        return AnalyzeLocalImage(cvc, imageByte[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.equals("OK")) MainActivity.et_datainput.setText(this.tagList.get(0).first);
        else MainActivity.et_datainput.setText(result);
    }

    //---------------------------------------------//
    /*
    Bitmap image = null;

    List<ImageTag> tags = null;
    AnalysisCompleted analysisCompleted = null;
    private WeakReference<Context> contextRef;

    public VisionTask (Context _context, AnalysisCompleted _ac) {
        this.analysisCompleted = _ac;
        this.contextRef = new WeakReference<>(_context);
        //TODO initialize image from function
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected String doInBackground(String... strings) {

        // Create an authenticated Computer Vision client.
        ComputerVisionClient compVisClient = Authenticate(subscriptionKey, endpoint);
        tags = AnalyzeLocalImage(compVisClient); //TODO pass image
        ReadFromFile(compVisClient);


        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        this.analysisCompleted.onAnalysisCompleted(tags);
    }

    public static ComputerVisionClient Authenticate(String subscriptionKey, String endpoint){
        return ComputerVisionManager.authenticate(subscriptionKey).withEndpoint(endpoint);
    }

    private static void ReadFromFile(ComputerVisionClient client) {


        String localFilePath ="/sdcard/example.jpg";

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static List<ImageTag> AnalyzeLocalImage(ComputerVisionClient compVisClient) {
        //*
         //* Analyze a local image:
         //*
        // * Set a string variable equal to the path of a local image. The image path
        // * below is a relative path.
        // *
        String pathToLocalImage = "/storage/emmc/DCIM/Camera/IMG_20210506_163108690.jpg";
        // </snippet_analyzelocal_refs>

        // <snippet_analyzelocal_features>
        // This list defines the features to be extracted from the image.
        List<VisualFeatureTypes> featuresToExtractFromLocalImage = new ArrayList<>();
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.CATEGORIES);
        featuresToExtractFromLocalImage.add(VisualFeatureTypes.TAGS);

        // </snippet_analyzelocal_features>

        System.out.println("\nAnalyzing local image ...");
        // <snippet_analyzelocal_analyze>
        try {
            // Need a byte array for analyzing a local image.
            File imagefile = new File(pathToLocalImage);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(imagefile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap bm = BitmapFactory.decodeStream(fis);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100 , baos);
            byte[] b = baos.toByteArray();
//            byte[] imageByteArray = Files.readAllBytes(Paths.get(pathToLocalImage));

            // Call the Computer Vision service and tell it to analyze the loaded image.
            ImageAnalysis analysis = compVisClient.computerVision().analyzeImageInStream().withImage(b)
                    .withVisualFeatures(featuresToExtractFromLocalImage).execute();

            // </snippet_analyzelocal_analyze>

            // <snippet_analyzelocal_captions>
            // Display image captions and confidence values.

            return analysis.tags();

        }

        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }*/

}