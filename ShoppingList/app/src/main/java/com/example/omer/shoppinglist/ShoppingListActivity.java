package com.example.omer.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;
import com.example.omer.shoppinglist.util.CouchBaseHelper;
import com.example.omer.shoppinglist.util.LiveQueryAdapter;

import java.io.IOException;

public class ShoppingListActivity extends AppCompatActivity {

    // user id (email)
    private String user;
    private CouchBaseHelper dbHelper = null;
    private ListAdapter mAdapter_bread = null;
    private ListAdapter mAdapter_baking = null;
    private ListAdapter mAdapter_cart = null;


    // all queries
    private Query query_bread;
    private Query query_baking;
    private Query query_cart;

    // all lsitView
    private ListView listView_bread;
    private ListView listView_baking;
    private ListView listView_cart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user =  getIntent().getStringExtra("USER");
        //R.string.s

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
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
        query_bread = dbHelper.createCategoryListInList("Bread");
        mAdapter_bread = new ListAdapter(this, query_bread.toLiveQuery());
        listView_bread = (ListView) findViewById(R.id.listView_bread);
        listView_bread.setAdapter(mAdapter_bread);

        //baking
        query_baking = dbHelper.createCategoryListInList("Baking");
        mAdapter_baking = new ListAdapter(this, query_baking.toLiveQuery());
        listView_baking = (ListView) findViewById(R.id.listView_baking);
        listView_baking.setAdapter(mAdapter_baking);

        //baking
        query_cart = dbHelper.createItemsInCart("Cart");
        mAdapter_cart = new ListAdapter(this, query_cart.toLiveQuery());
        listView_cart = (ListView) findViewById(R.id.listView_cart);
        listView_cart.setAdapter(mAdapter_cart);

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
                convertView = inflater.inflate(R.layout.item_in_shopping_list, null);
            }

            // bind objects with layout
            final CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkbox_add_item_to_cart);
            TextView textItem = (TextView) convertView.findViewById(R.id.text_item_in_shopping_list);


            // get the current document
            final Document doc = (Document) getItem(position);
            if (doc == null || doc.getCurrentRevision() == null) {
                return convertView;
            }

            // set name and amount of the item
            String itemDetail =  String.valueOf((int) doc.getProperty("amount"))+" - "+
                    (String) doc.getProperty("name");
            textItem.setText(itemDetail);

            checkBox.setChecked((Boolean)doc.getProperty("in_cart"));

            textItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToEditItemActivity(doc);
                }
            });

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (checkBox.isChecked())
                            dbHelper.addToCart(doc,true);
                        else
                            dbHelper.addToCart(doc,false);

                    }catch  (CouchbaseLiteException e){
                        e.printStackTrace();
                    }

                }
            });
            return convertView;
        }

    }
    public void goToEditItemActivity(Document doc) {
        Intent goToEditItem = new Intent(this,EditItemActivity.class);
        goToEditItem.putExtra("DOCUMENTID", doc.getId());
        goToEditItem.putExtra("CONTEX", "shoppingList");
        startActivity(goToEditItem);
    }

    public void goToAddToListActivity() {
        Intent goToAddToListActivity = new Intent(this,CategoriesActivity.class);
        startActivity(goToAddToListActivity);
    }

}
