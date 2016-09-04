package com.example.omer.shoppinglist.util;

/**
 * Created by omer on 24/08/16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.omer.shoppinglist.R;

public class GridAdapter extends BaseAdapter{
    private Context mContext;
    private final String[] category;
    private final int[] imageId;

    public GridAdapter(Context c, String[] category, int[] imageId ) {
        mContext = c;
        this.imageId = imageId;
        this.category = category;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return category.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                grid = new View(mContext);
                    grid = inflater.inflate(R.layout.grid_single, null);
                    TextView textView = (TextView) grid.findViewById(R.id.grid_text);
                    ImageView imageView = (ImageView) grid.findViewById(R.id.grid_image);
                    textView.setText(category[position]);
                    imageView.setImageResource(imageId[position]);
            } else {
                grid = (View) convertView;
            }
            return grid;
    }
}