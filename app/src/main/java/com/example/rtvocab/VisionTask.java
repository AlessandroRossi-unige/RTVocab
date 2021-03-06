package com.example.rtvocab;

import android.os.AsyncTask;
import android.util.Pair;
import com.microsoft.azure.cognitiveservices.vision.computervision.*;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.*;
import java.util.ArrayList;
import java.util.List;

public class VisionTask extends AsyncTask<byte[], Integer, String> {

    private static final String subscriptionKey = "f9779642e5364a89af2473de6a73b49d";
    private static final String endpoint = "https://unige-vision.cognitiveservices.azure.com/";
    private List<Pair<String, Double>> tagList;
    private AnalysisCompleted delegate;

    public VisionTask(AnalysisCompleted delegate) {
        this.delegate = delegate;
    }

    private static ComputerVisionClient Authenticate(){
        return ComputerVisionManager.authenticate(VisionTask.subscriptionKey).withEndpoint(VisionTask.endpoint);
    }

    private String AnalyzeLocalImage(ComputerVisionClient compVisClient, byte[] imageByte) {
        // This list defines the features to be extracted from the image.
        List<VisualFeatureTypes> featuresToExtractFromLocalImage = new ArrayList<>();
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
            e.printStackTrace();
            return "ERROR";
        }
    }

    @Override
    protected String doInBackground(byte[]... imageByte) {
        ComputerVisionClient cvc = Authenticate();
        return AnalyzeLocalImage(cvc, imageByte[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        List<String> res = new ArrayList<>();
        if (result.equals("OK")) {
            for (int i = 0; i < this.tagList.size(); i++) {
                res.add(this.tagList.get(i).first);
            }
        }
        delegate.onAnalysisCompleted(res);

    }
}