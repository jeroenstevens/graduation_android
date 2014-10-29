package com.example.jeroenstevens.graduation_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jeroenstevens.graduation_android.R;
import com.example.jeroenstevens.graduation_android.object.Item;

import java.util.List;

public class ItemsAdapter extends BaseAdapter {
    private static final String TAG = "SwatchesAdapter";

    private final LayoutInflater mLayoutInflater;
    private Context mContext;
    private final List<Item> mItems;
    private String mSelectedViewText;

    public ItemsAdapter(Context context, List<Item> items) {
        super();
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_row, null);

            viewHolder = new ViewHolder();

            viewHolder.text = (TextView) convertView.findViewById(R.id.text);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.text.setText(getItem(position).getName());

        return convertView;
    }

    private class ViewHolder {
        public TextView text;
    }
}


