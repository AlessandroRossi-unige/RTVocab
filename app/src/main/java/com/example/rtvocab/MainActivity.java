package com.example.rtvocab;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AnalysisCompleted {

    private Button btn_getAnalysis = null;
    private RecyclerView rv_getAnalysis = null;
    private ImageView iv_Image = null;

    private VisionTask visionTask = null;
    private AnalysisCompleted analysisCompleted = null;

    private final static int PICK_IMAGE = 1;

    private List<String> tagsList;
    private ArrayList <Item> itemList = new ArrayList<Item>();

    private ItemArrayAdapter itemArrayAdapter = null;

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
            iv_Image.setImageURI(imageUri);
            try {
                iStream = getContentResolver().openInputStream(imageUri);
                byte[] inputData = this.getBytes(iStream);
                new VisionTask(this).execute(inputData);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    public void onAnalysisCompleted(List<String> tags){
        tags.add(0, "it");
        tags.add(1, "fr");
        String[] input = new String[tags.size()];
        tags.toArray(input);
        new TranslateTask(this).execute(input);
    }

    @Override
    public void onTranslateCompleted(List<Pair<String,String>> results) {
        ArrayList<Item> input = new ArrayList<>();
        for (Pair<String,String> el:results) {
            input.add(new Item(el.first + "-" + el.second));
        }
        itemArrayAdapter.updateData(input);
    }
}