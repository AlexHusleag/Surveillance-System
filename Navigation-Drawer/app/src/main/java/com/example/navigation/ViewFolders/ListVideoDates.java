package com.example.navigation.ViewFolders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.navigation.ViewVideos.ListVideos;
import com.example.navigation.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.List;

public class ListVideoDates extends AppCompatActivity {

    private static final String LOG = "ListLiveVideosActivity";

    private ListView videoDateList;
    private DateListAdapter dateListAdapter;

    public static FirebaseStorage storage = null;
    private StorageReference storageRef = null;

    private ArrayList<StorageReference> folderPaths = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_date_list);

        videoDateList = findViewById(R.id.videoDateList);

        new UserModel().execute();
    }

    // Store all folder locations in an ArrayList<>
    public void listAllFolderContent() {
        readFolderData(new FirestoreCallback() {
            @Override
            public void onCallback(List<StorageReference> storageReferences) {
                folderPaths = new ArrayList<>(storageReferences);
                System.out.println("FOLDER PATHS: " + folderPaths);

                dateListAdapter = new DateListAdapter(getApplicationContext(), R.layout.adapter_firebase_date_list_layout, folderPaths);
                videoDateList.setAdapter(dateListAdapter);

                onListViewItemClick(videoDateList);
            }
        });
    }


    public void onListViewItemClick(ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle extra = new Bundle();
                extra.putSerializable("folderPath", folderPaths.get(position).getPath());

                Intent intent = new Intent(ListVideoDates.this, ListVideos.class);
                intent.putExtra("extra", extra);
                startActivity(intent);
            }
        });
    }


    private ArrayList<StorageReference> readFolderData(final FirestoreCallback firestoreCallback) {
        storage = FirebaseStorage.getInstance("gs://video-surveillance-580ac.appspot.com");
        storageRef = storage.getReference().getRoot();

        final ArrayList<StorageReference> folderPaths = new ArrayList<>();

        storageRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            StorageReference children = prefix.getStorage().getReference().child(prefix.getPath());
                            folderPaths.add(children);
                        }
                        firestoreCallback.onCallback(folderPaths);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", "VideoFolder read error");
                    }
                });
        return folderPaths;
    }


    private interface FirestoreCallback {
        void onCallback(List<StorageReference> storageReferences);
    }


    public class UserModel extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            listAllFolderContent();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }
    }
}


