package com.example.navigation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ListVideos extends AppCompatActivity {


    private ListView videoList;
    private VideoListAdapter videoListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_video_list);

        Bundle extra = getIntent().getBundleExtra("extra");
        ArrayList<String> files = (ArrayList<String>) extra.getSerializable("files");

        System.out.println(files);

        ArrayList<StorageReference> referencesToFiles = new ArrayList<>();
        for(String file: files){
            referencesToFiles.add(ListVideoDates.storage.getReference(file));
        }

        System.out.println("Reference Storage ListView: " + referencesToFiles);

        videoList = (ListView) findViewById(R.id.videoList);

        videoListAdapter = new VideoListAdapter(getApplicationContext(), R.layout.adapter_firebase_video_list_layout, referencesToFiles);
        videoList.setAdapter(videoListAdapter);
    }

}
