package com.example.navigation.ViewVideos.ExtendedListVideos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.widget.ListView;

import com.example.navigation.R;
import com.example.navigation.ViewVideos.ListVideos;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Map;

public class RecycleView extends AppCompatActivity {
//    private ArrayList<String> URIs;

    private Bundle extra;
    private DatabaseReference databaseReference = ListVideos.mDatabase;
    private Map<String, String> URIs;

    private RecyclerView recycleView;
    private FirebaseDatabase database;

//    private FirebaseRecyclerOptions<MediaObject> options;

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseRecyclerOptions<MediaObject> options =
//                new FirebaseRecyclerOptions.Builder<MediaObject>()
//                        .setQuery(databaseReference, MediaObject.class)
//                        .build();

//        FirebaseRecyclerAdapter<MediaObject, ViewHolder> firebaseRecyclerAdapter =
//                new FirebaseRecyclerAdapter<MediaObject, ViewHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull MediaObject model) {
//                        holder.setVideo(getApplication(), model.getTitle(), model.getMedia_url());
//                    }
//
//                    @NonNull
//                    @Override
//                    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                        View view = LayoutInflater.from(parent.getContext())
//                                .inflate(R.layout.activity_view_extended_videolist, parent, false);
//
//                        return new ViewHolder(view);
//                    }
//                };

        FirebaseRecyclerAdapter<MediaObject, ViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<MediaObject, ViewHolder>(
                        MediaObject.class,
                        R.layout.activity_view_extended_videolist,
                        ViewHolder.class,
                        databaseReference
                ) {
                    @Override
                    protected void populateViewHolder(ViewHolder viewHolder, MediaObject mediaObject, int i) {
                        viewHolder.setVideo(getApplication(), mediaObject.getTitle().replace(".mp4", ""), mediaObject.getMedia_url());
                    }
                };

        recycleView.setAdapter(firebaseRecyclerAdapter);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ListVideos.switchCompat.setChecked(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);


        recycleView = findViewById(R.id.recyclerview_video);
        recycleView.setHasFixedSize(true);
        recycleView.setLayoutManager(new LinearLayoutManager(this));

        extra = getIntent().getBundleExtra("extra");
        String folderPath = (String) extra.getSerializable("folderPath");
        folderPath = folderPath.replaceAll("/", "");
        databaseReference = databaseReference.child(folderPath);


//        getURIS();
//        recycleView = findViewById(R.id.recyclerview_video);
    }


    public interface DataStatus {
        //        void DataIsLoaded(ArrayList<String> uris);
        void DataIsLoaded(Map<String, String> uris);
    }















    public void getURIS() {
        readURIS(new DataStatus() {
            @Override
            public void DataIsLoaded(Map<String, String> uris) {
                System.out.println(uris);
            }
        });
    }

    public void readURIS(final DataStatus dataStatus) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                URIs = new ArrayList<>();
                URIs = new HashMap<>();
//                String[] videoURIs = dataSnapshot.getValue().toString()
//                        .replaceFirst("\\[", "")
//                        .replaceFirst("]", "")
//                        .split(", ");
//                URIs.addAll(Arrays.asList(videoURIs));
                dataStatus.DataIsLoaded(URIs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
