package com.example.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class DateListAdapter extends ArrayAdapter<StorageReference> {
    private static final String TAG = "DateListAdapter";

    private Context mContext;

    private int mResource;

    public DateListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<StorageReference> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder = null;
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if(vi == null){
            String folderName = Objects.requireNonNull(getItem(position), "folderName must not be null").getName();
            String bucket = Objects.requireNonNull(getItem(position), "bucket must not be null").getBucket();
            String path = Objects.requireNonNull(getItem(position)).getPath();
            vi = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();

            holder.folderName = (TextView) vi.findViewById(R.id.textView1);
            holder.folderName.setText(folderName);

            holder.bucket = (TextView) vi.findViewById(R.id.textView2);
            holder.bucket.setText(bucket);

            holder.path = (TextView) vi.findViewById(R.id.textView3);
            holder.path.setText(path);
        } else {
            holder = (ViewHolder) vi.getTag();
        }




//        String folderName = Objects.requireNonNull(getItem(position), "folderName must not be null").getName();
//        String bucket = Objects.requireNonNull(getItem(position), "bucket must not be null").getBucket();
//        String path = Objects.requireNonNull(getItem(position)).getPath();
//
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        convertView = inflater.inflate(mResource, parent, false);
//
//        TextView tvFolderName = (TextView) convertView.findViewById(R.id.textView1);
//        TextView tvBucket = (TextView) convertView.findViewById(R.id.textView2);
//        TextView tvPath = (TextView) convertView.findViewById(R.id.textView3);
//
//        tvFolderName.setText(folderName);
//        tvBucket.setText(bucket);
//        tvPath.setText(path);
//
//        return convertView;
        return vi;
    }

    private static class ViewHolder{
        private TextView folderName;
        private TextView bucket;
        private TextView path;
    }
}
