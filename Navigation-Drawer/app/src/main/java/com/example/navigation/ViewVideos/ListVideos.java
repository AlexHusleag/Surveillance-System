package com.example.navigation.ViewVideos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.navigation.R;
import com.example.navigation.ViewVideos.ExtendedListVideos.MediaObject;
import com.example.navigation.ViewVideos.ExtendedListVideos.RecycleView;
import com.example.navigation.ViewFolders.ListVideoDates;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ListVideos extends AppCompatActivity {

    private static final String TAG = "ListVideos";

    private ListView videoList;
    private VideoListAdapter videoListAdapter;
    private ImageButton refreshButton;
    private Bundle extra;
    private StorageReference folderPath;
    private ArrayList<StorageReference> filesReferences = new ArrayList<>();
    private Toolbar toolbar;

    private int parentViewPosition;

    public static DatabaseReference mDatabase;
    public static SwitchCompat switchCompat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_video_list);

        toolbar = findViewById(R.id.toolbarVideos);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Videouri");
        }


        videoList = findViewById(R.id.videoList);
        switchCompat = findViewById(R.id.switchButton);
        refreshButton = findViewById(R.id.refreshButton);

        extra = getIntent().getBundleExtra("extra");
        folderPath = ListVideoDates.storage.getReference((String) extra.getSerializable("folderPath"));

        mDatabase = FirebaseDatabase.getInstance("https://video-surveillance-580ac.firebaseio.com/")
                .getReference();


        listAllFileContent();
        toggleState();
        refreshPage();
        playVideo();
        makeButtonsVisible();
        uploadAllDownloadURIS();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                videoListAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }


    public void updateListViewAdapter(int parentViewPosition) {
        videoListAdapter.remove(videoListAdapter.getItem(parentViewPosition));
        videoListAdapter.notifyDataSetChanged();
    }


    public void refreshPage() {
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }


    public void toggleState() {
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Bundle extra = new Bundle();
                    extra.putSerializable("folderPath", folderPath.getPath());

                    Intent intent = new Intent(ListVideos.this, RecycleView.class);
                    intent.putExtra("extra", extra);
                    startActivity(intent);

                } else {
                    System.out.println("NOT CHECKED");
                }
            }
        });
    }


    public void uploadAllDownloadURIS() {
        final AtomicInteger integer = new AtomicInteger();
        integer.set(1);

        folderPath.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (final StorageReference file : listResult.getItems()) {
                            file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    int current = integer.getAndIncrement();
                                    System.out.println("SET MDATASAE: " + mDatabase.child(file.getParent().getPath()));
                                    mDatabase.child(file.getParent().getPath()).child(String.valueOf(current))
                                            .setValue(new MediaObject(file.getName(), uri.toString()));
                                }
                            });
                        }
                    }
                });
    }

    private void playVideo() {
        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView downloadUrl = view.findViewById(R.id.videoTextView3);
                final StorageReference videoPath = ListVideoDates.storage.getReference(downloadUrl.getText().toString());

                videoPath.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                ViewVideo.setVideoUri(uri);

                                if (!ViewVideo.getVideoUri().toString().isEmpty()) {
                                    Intent intent = new Intent(ListVideos.this, ViewVideo.class);
                                    startActivity(intent);
                                } else {
                                    Log.d("ListVideos", "Invalid URL");
                                }
                            }
                        });
            }
        });
    }


    public void ifEmptyHeaderGone() {

        ArrayList<String> files = new ArrayList<>();

        for (StorageReference storageReference : filesReferences)
            files.add(storageReference.getPath());

        if (files.isEmpty()) {
            View v = findViewById(R.id.topHeader_videoList);
            v.setVisibility(View.GONE);
        }
    }


    public void makeButtonsVisible() {
        videoList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                parentViewPosition = position;

                view.findViewById(R.id.deleteButton).setVisibility(View.VISIBLE);
                view.findViewById(R.id.downloadButton).setVisibility(View.VISIBLE);
                onDownloadButtonClick(view);
                onDeleteButtonCliCk(view);
                makeButtonsGone(view);
                return true;
            }
        });
    }


    public void makeButtonsGone(final View view) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.findViewById(R.id.deleteButton).setVisibility(View.GONE);
                view.findViewById(R.id.downloadButton).setVisibility(View.GONE);
            }
        }, 5000);
    }

    public void onDownloadButtonClick(final View view) {
        final TextView fileName = view.findViewById(R.id.videoTextView);
        Button button = view.findViewById(R.id.downloadButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadElement(fileName.getText().toString());
            }
        });
    }


    public void downloadElement(final String filename) {

        folderPath.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference storageReference : listResult.getItems()) {
                    if (storageReference.getName().equals(filename)) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                download(getApplicationContext(), uri, DIRECTORY_DOWNLOADS, filename);

                                Toast.makeText(getApplicationContext(), "Element downloadat cu succes",
                                        Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                }
            }
        });
    }


    public void download(Context context, Uri uri, String destinationDirectory, String fileName) {
        DownloadManager downloadManager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName);

        downloadManager.enqueue(request);
    }


    public void onDeleteButtonCliCk(final View view) {
        final TextView fileName = view.findViewById(R.id.videoTextView);
        Button button = view.findViewById(R.id.deleteButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteElement(fileName.getText().toString());

            }
        });
    }

    public void deleteElement(final String fileName) {
        folderPath.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference storageReference : listResult.getItems()) {
                    if (storageReference.getName().equals(fileName)) {
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Element sters cu succes",
                                        Toast.LENGTH_LONG).show();
                                updateListViewAdapter(parentViewPosition);
                                ifEmptyHeaderGone();
                                deleteDatabaseDownloadReference(mDatabase);
                                uploadAllDownloadURIS();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println("STERGEREA A ESUAT");
                            }
                        });
                    }
                }
            }
        });
    }


    public void deleteDatabaseDownloadReference(DatabaseReference databaseReference) {
        databaseReference.child(folderPath.getPath().replace("/", ""))
                .removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        System.out.println("STERS CU SUCCES");
                    }
                });
    }


    private ArrayList<StorageReference> readFileData(final FirestoreCallbackForFiles firestoreCallback) {
        final ArrayList<StorageReference> filesReferences = new ArrayList<>();
        folderPath.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference file : listResult.getItems()) {
                            filesReferences.add(file);
                        }
                        firestoreCallback.onCallback(filesReferences);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("File Read Error");
                    }
                });

        return filesReferences;
    }

    public void listAllFileContent() {
        readFileData(new FirestoreCallbackForFiles() {
            @Override
            public void onCallback(ArrayList<StorageReference> storageReferences) {
                filesReferences = new ArrayList<>(storageReferences);
                videoListAdapter = new VideoListAdapter(getApplicationContext(), R.layout.adapter_firebase_video_list_layout, filesReferences);
                videoList.setAdapter(videoListAdapter);
                ifEmptyHeaderGone();
            }
        });
    }

    private interface FirestoreCallbackForFiles {
        void onCallback(ArrayList<StorageReference> storageReferences);
    }
}
