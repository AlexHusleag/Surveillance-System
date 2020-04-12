package com.example.navigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListVideoDates extends AppCompatActivity{

    private static final String LOG = "ListLiveVideosActivity";

    private ListView videoDateList;
    private DateListAdapter dateListAdapter;

    static FirebaseStorage storage = null;
    private StorageReference storageRef = null;

    private ArrayList<StorageReference> folderPaths = new ArrayList<>();
    private Map<StorageReference, ArrayList<StorageReference>> folderAndCorrespondingFiles = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_date_list);

        videoDateList = (ListView) findViewById(R.id.videoDateList);

        new UserModel().execute();
    }


    public void listAllFileContent() {
        readFileData(new FirestoreCallbackForFiles() {
            @Override
            public void onCallback(Map<StorageReference, ArrayList<StorageReference>> storageReferences) {
                folderAndCorrespondingFiles = new HashMap<>(storageReferences);
            }
        });
    }


    // Store all folder locations in an ArrayList<>
    public void listAllFolderContent() {
        readFolderData(new FirestoreCallback() {
            @Override
            public void onCallback(List<StorageReference> storageReferences) {
                folderPaths = new ArrayList<>(storageReferences);

                listAllFileContent();

                dateListAdapter = new DateListAdapter(getApplicationContext(), R.layout.adapter_firebase_date_list_layout, folderPaths);
                videoDateList.setAdapter(dateListAdapter);

            }
        });
    }


    private Map<StorageReference, ArrayList<StorageReference>> readFileData(final FirestoreCallbackForFiles firestoreCallback) {
        final Map<StorageReference, ArrayList<StorageReference>> folderAndCorrespondingFiles = new HashMap<>();
        for (final StorageReference folder : folderPaths) {
            final ArrayList<StorageReference> files = new ArrayList<>();
            folder.listAll()
                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            for (StorageReference file : listResult.getItems()) {
                                files.add(file);
                                System.out.println("File: " + file.getName());
                                System.out.println("Download URI: " + file.getDownloadUrl());
                            }
                            folderAndCorrespondingFiles.put(folder, files);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    System.out.println("Files: " + files);
                    System.out.println("VideoFolder and Files" + folderAndCorrespondingFiles);

                    videoDateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            System.out.println("onitemclick " + folderAndCorrespondingFiles);
                            System.out.println("onitemclick" + folderPaths);
                            System.out.println(folderAndCorrespondingFiles.get(folderPaths.get(position)));

                            ArrayList<StorageReference> files = folderAndCorrespondingFiles.get(folderPaths.get(position));
                            ArrayList<String> filePaths = new ArrayList<>();

                            for(StorageReference storageReference : files){
                                filePaths.add(storageReference.getPath());
                            }

                            Bundle extra = new Bundle();
                            extra.putSerializable("files", filePaths);

                            Intent intent = new Intent(ListVideoDates.this, ListVideos.class);
                            intent.putExtra("extra", extra);
                            startActivity(intent);
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("File Read Error");
                        }
                    });
            firestoreCallback.onCallback(folderAndCorrespondingFiles);
        }
        return folderAndCorrespondingFiles;
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
                        System.out.println("FOLDER PATHS: " + folderPaths);

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

    private interface FirestoreCallbackForFiles {
        void onCallback(Map<StorageReference, ArrayList<StorageReference>> storageReferences);
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


