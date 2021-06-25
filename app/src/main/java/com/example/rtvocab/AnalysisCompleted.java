package com.example.rtvocab;

import android.util.Pair;
import java.util.List;

public interface AnalysisCompleted {

    void onAnalysisCompleted(List<String> tags);
    void onTranslateCompleted(List<Pair<String,String>> results);
    void onSelectCompleted(int position);
}
