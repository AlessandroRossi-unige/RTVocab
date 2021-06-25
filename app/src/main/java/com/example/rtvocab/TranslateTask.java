package com.example.rtvocab;

import android.os.AsyncTask;
import android.util.Pair;
import com.squareup.okhttp.HttpUrl;
import java.io.IOException;
import java.util.*;
import com.google.gson.*;
import com.squareup.okhttp.*;


public class TranslateTask extends AsyncTask<String, Integer, String> {

    private static final String subscriptionKey = "0b6d9f97b32f4d8c84d31db0c9473340";
    private static final String location ="westeurope";

    private List<Pair<String,String>> result;

    private final AnalysisCompleted delegate;

    public TranslateTask(AnalysisCompleted delegate) {
        this.delegate = delegate;
    }

    // Instantiates the OkHttpClient.
    OkHttpClient client = new OkHttpClient();

    // This function performs a POST request.
    public String Post(String text, HttpUrl url) throws IOException {
        client.setProtocols(Arrays.asList(Protocol.HTTP_1_1));
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "[{\"Text\": \""+text+"\"}]");
        Request request = new Request.Builder().url(url).post(body)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .addHeader("Ocp-Apim-Subscription-Region", location)
                .addHeader("Content-type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    protected String doInBackground(String... text) {
        // text[0] -> user language
        // text[1] -> translate language
        // text[2..6] -> text
        if (text.length < 3) return "ERROR";
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("api.cognitive.microsofttranslator.com")
                .addPathSegment("/translate")
                .addQueryParameter("api-version", "3.0")
                .addQueryParameter("from", "en")
                .addQueryParameter("to", text[0])
                .addQueryParameter("to", text[1])
                .build();
        try {
            this.result = new ArrayList<>();
            JsonParser parser = new JsonParser();
            for (int i = 2; i < text.length; i++) {
                // API call
                String res = Post(text[i], url);
                // json parse
                Object json = parser.parse(res);
                JsonObject jsonObj = (JsonObject)((JsonArray)json).get(0);
                JsonArray translations = (JsonArray) jsonObj.get("translations");
                String first = ((JsonObject)translations.get(0)).get("text").toString().replace("\"","");
                String second = ((JsonObject)translations.get(1)).get("text").toString().replace("\"","");
                // add res
                this.result.add(new Pair<>(first,second));
            }
            return "OK";
        } catch (Exception e) {
            return "ERROR";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s.equals("OK")) {
            delegate.onTranslateCompleted(this.result);
        } else {
            delegate.onTranslateCompleted(null);
        }
    }

}
