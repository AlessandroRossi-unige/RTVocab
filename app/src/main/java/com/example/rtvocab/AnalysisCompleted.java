package com.example.rtvocab;

import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageTag;

import java.util.List;

public interface AnalysisCompleted {

    public void onAnalysisCompleted(List<ImageTag> tags);

}
