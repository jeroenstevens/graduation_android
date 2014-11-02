package com.example.jeroenstevens.graduation_android.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jeroenstevens.graduation_android.R;
import com.example.jeroenstevens.graduation_android.object.Collection;

import java.util.List;

public class CollectionsAdapter extends BaseAdapter {
    private static final String TAG = "CollectionsAdapter";
    private static final float ITEM_HEIGHT_PERCENTAGE = 30.0f;

    private final LayoutInflater mLayoutInflater;
    private Context mContext;
    private final List<Collection> mCollections;
    private final int mItemHeight;

    public CollectionsAdapter(Context context, List<Collection> collections) {
        super();
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mCollections = collections;

        // Make rows ITEM_HEIGHT_PERCENTAGE of
        // screenHeight on portrait
        // screenWidth on landscape
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        int screenHeight = displayMetrics.heightPixels;
        int screenWidth = displayMetrics.widthPixels;
        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mItemHeight = (int) (screenHeight / 100.0f * ITEM_HEIGHT_PERCENTAGE);
        } else {
            mItemHeight = (int) (screenWidth / 100.0f * ITEM_HEIGHT_PERCENTAGE);
        }
    }

    @Override
    public int getCount() {
        return mCollections.size();
    }

    @Override
    public Collection getItem(int position) {
        return mCollections.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_view_collection_row, null);

            viewHolder = new ViewHolder();

            viewHolder.name = (TextView) convertView.findViewById(R.id.collection_name);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.collection_image);

            viewHolder.image.setLayoutParams(new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    mItemHeight));

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Collection collection = getItem(position);

        viewHolder.name.setText(collection.name + " " + collection.updatedAt);
        if (collection.imagePath != null) {
            viewHolder.image.setBackground(new BitmapDrawable(mContext.getResources(), collection.imagePath));
        } else {
            viewHolder.image.setBackgroundResource(R.drawable.default_icon);
        }

        return convertView;
    }

    private class ViewHolder {
        public TextView name;
        public ImageView image;
    }
}
