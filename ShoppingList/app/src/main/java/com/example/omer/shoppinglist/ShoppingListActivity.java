package com.example.omer.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;

import java.io.IOException;

public class ShoppingListActivity extends AppCompatActivity {

    // user id (email)
    private String user;
    private CouchBaseHelper dbHelper = null;
    private ListAdapter mAdapter = null;


    // all queries
    Query query_bread;
    Query query_baking;
    Query query_cart;

    // all lsitView
    ListView listView_bread;
    ListView listView_baking;
    ListView listView_cart;

    public void goToAddToListActivity() {
        Intent goToAddToListActivity = new Intent(this,CategoriesActivity.class);
        startActivity(goToAddToListActivity);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user =  getIntent().getStringExtra("USER");
        //R.string.s

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddToListActivity();
            }
        });


        // connect to CB data base
        try {
            dbHelper = new CouchBaseHelper(ShoppingListActivity.this,"shopping_list_db");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        //initilaize queries and listViews for each category

        //bread
        query_bread = dbHelper.createCategoryList("bread");
        mAdapter = new ListAdapter(this, query_bread.toLiveQuery());
        listView_bread = (ListView) findViewById(R.id.listView_bread);
        listView_bread.setAdapter(mAdapter);

        //baking
        query_baking = dbHelper.createCategoryList("baking");
        mAdapter = new ListAdapter(this, query_bread.toLiveQuery());
        listView_baking = (ListView) findViewById(R.id.listView_baking);
        listView_baking.setAdapter(mAdapter);

        //baking
        query_cart = dbHelper.createCategoryList("cart");
        mAdapter = new ListAdapter(this, query_bread.toLiveQuery());
        listView_cart = (ListView) findViewById(R.id.listView_cart);
        listView_cart.setAdapter(mAdapter);
    }

    // list adapter for shoppingList for each category
    private class ListAdapter extends LiveQueryAdapter {

        public ListAdapter(Context context, LiveQuery query) {
            super(context, query);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_single, null);
            }

            // bind objects with layout
            final TextView text = (TextView) convertView.findViewById(R.id.text_single_item);
            final Button editButton = (Button) convertView.findViewById(R.id.button_edit_item);

            // get the current document
            final Document doc = (Document) getItem(position);
            if (doc == null || doc.getCurrentRevision() == null) {
                return convertView;
            }

            // get if in list or not
            boolean inList = (Boolean)doc.getProperty("in_list");

            // set name and amount of the item
            String itemDetail =  String.valueOf((int) doc.getProperty("amount"))+" - "+
                    (String) doc.getProperty("name");
            text.setText(itemDetail);

            // paint it em if it  in Shop list
            if (inList)
                text.setBackgroundColor(Color.YELLOW);
            else
                text.setBackgroundColor(Color.WHITE);

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToEditItemActivity(doc);
                }
            });

            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dbHelper.addToShopList(doc);
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }
                }
            });
            return convertView;
        }

    }

}
