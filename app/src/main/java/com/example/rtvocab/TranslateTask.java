package com.example.rtvocab;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.squareup.okhttp.HttpUrl;

import java.io.IOException;

import java.util.*;
import com.google.gson.*;
import com.squareup.okhttp.*;


public class TranslateTask extends AsyncTask<String, Integer, String> {

    private static String subscriptionKey = "0b6d9f97b32f4d8c84d31db0c9473340";
    private static String location ="westeurope";

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

    // This function prettifies the json response.
    public static String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(json_text);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

    @Override
    protected String doInBackground(String... text) {
        // text[0] -> text
        // text[1] -> text language
        // text[2] -> translate language
        if (text.length < 3) return "ERROR";
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("api.cognitive.microsofttranslator.com")
                .addPathSegment("/translate")
                .addQueryParameter("api-version", "3.0")
                .addQueryParameter("from", text[1])
                .addQueryParameter("to", text[2])
                .build();
        try {
            return Post(text[0], url);
        } catch (Exception e) {
            System.out.println(e);
            return "ERROR";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Bitmap _image = BitmapFactory.decodeFile(s);
        MainActivity.et_Translate.setText(s);
    }

}
