package com.example.navigation.ViewFolders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    private ListView videoDateList;
    private DateListAdapter dateListAdapter;

    private ImageButton refreshButton;
    private StorageReference storageRef = null;
    private ArrayList<StorageReference> folderPaths = new ArrayList<>();
    private Toolbar toolbar;

    public static FirebaseStorage storage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_date_list);

        toolbar = findViewById(R.id.toolbarDate);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("ZILE");
        }

        videoDateList = findViewById(R.id.videoDateList);
        refreshButton = findViewById(R.id.refreshButtonDates);

        listAllFolderContent();
        makeDeleteButtonVisible();
        refreshPage();
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
                dateListAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }


    public void onDeleteButtonCliCk(final View view) {
        final TextView folderName = view.findViewById(R.id.textView1);
        Button button = view.findViewById(R.id.deleteFolderButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFolder(folderName.getText().toString());
            }
        });
    }

    public void deleteFolder(final String folderName) {
        storageRef.child(folderName).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference storageReference : listResult.getItems()) {
                    storageReference.delete();
                }
                Toast.makeText(getApplicationContext(), "Folder sters cu succes",
                        Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Stergerea a esuat",
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    public void ifEmptyHeaderGone() {
        ArrayList<String> files = new ArrayList<>();

        for (StorageReference storageReference : folderPaths)
            files.add(storageReference.getPath());

        if (files.isEmpty()) {
            View v = findViewById(R.id.topHeader_dateList);
            v.setVisibility(View.GONE);
        }
    }


    public void makeDeleteButtonVisible() {
        videoDateList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                view.findViewById(R.id.deleteFolderButton).setVisibility(View.VISIBLE);
                onDeleteButtonCliCk(view);
                makeDeleteButtonGone(view);
                return true;
            }
        });
    }

    public void makeDeleteButtonGone(final View view) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.findViewById(R.id.deleteFolderButton).setVisibility(View.GONE);
            }
        }, 5000);
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


    // Store all folder locations in an ArrayList<>
    public void listAllFolderContent() {
        readFolderData(new FirestoreCallback() {
            @Override
            public void onCallback(List<StorageReference> storageReferences) {
                folderPaths = new ArrayList<>(storageReferences);
                dateListAdapter = new DateListAdapter(getApplicationContext(), R.layout.adapter_firebase_date_list_layout, folderPaths);
                videoDateList.setAdapter(dateListAdapter);

                ifEmptyHeaderGone();
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
}


