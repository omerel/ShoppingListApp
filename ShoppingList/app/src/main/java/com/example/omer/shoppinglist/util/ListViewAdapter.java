
package com.example.omer.shoppinglist.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by omer on 31/08/16.
 */
public class ListViewAdapter  extends BaseAdapter {


    Context context;
    int categories[];
    LayoutInflater inflter;

    public ListViewAdapter(Context contex, int[] categories) {
        this.context = contex;
        this.categories = categories;
        inflter = (LayoutInflater.from(contex));
    }

    @Override
    public int getCount() {
        if (categories != null)
            return categories.length;
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return categories[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

}
