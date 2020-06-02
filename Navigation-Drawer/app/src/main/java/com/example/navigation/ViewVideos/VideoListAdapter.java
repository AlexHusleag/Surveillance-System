package com.example.navigation.ViewVideos;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.navigation.R;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VideoListAdapter extends ArrayAdapter<StorageReference> {

    private static final String TAG = "VideoListAdapter";
    private Context mContext;
    private int mResource;

    private ArrayList<StorageReference> objects;
    private ArrayList<StorageReference> objectsFull;

    public VideoListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<StorageReference> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.objectsFull = new ArrayList<>(objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (vi == null) {
            vi = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.folderName = (TextView) vi.findViewById(R.id.videoTextView);
            holder.bucket = (TextView) vi.findViewById(R.id.videoTextView2);
            holder.path = (TextView) vi.findViewById(R.id.videoTextView3);

        } else {
            holder = (ViewHolder) vi.getTag();
        }
        String folderName = Objects.requireNonNull(getItem(position), "folderName must not be null").getName();
        String bucket = Objects.requireNonNull(getItem(position), "bucket must not be null").getBucket();
        String path = Objects.requireNonNull(getItem(position)).getPath();
        holder.folderName.setText(folderName);
        holder.bucket.setText(bucket);
        holder.path.setText(path);
        vi.setTag(holder);

        if (position % 2 == 0) {
            Objects.requireNonNull(vi)
                    .setBackgroundColor(ContextCompat.getColor(mContext, R.color.lightGray));
        } else {
            Objects.requireNonNull(vi).setBackgroundColor(Color.WHITE);
        }

        return vi;
    }


    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<StorageReference> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(objectsFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (StorageReference item : objects) {
                        if (item.getName().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                objects.clear();
                objects.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }


    private static class ViewHolder {
        private TextView folderName;
        private TextView bucket;
        private TextView path;
    }

}
