package com.example.omer.shoppinglist;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;
import com.example.omer.shoppinglist.util.CategoriesSource;
import com.example.omer.shoppinglist.util.CouchBaseHelper;
import com.example.omer.shoppinglist.util.LiveQueryAdapter;

import java.io.IOException;

public class AddItemToListActivity extends AppCompatActivity implements View.OnClickListener {


    private CouchBaseHelper dbHelper = null;
    private String chosenCategory = null;
    private ListAdapter mAdapter = null;


    @Override
    public void onBackPressed() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_list);

        // sign buttons to onClickListener
        ((Button) findViewById(R.id.button_backToCategories)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.button_add_item_to_list)).setOnClickListener(this);
        ((Button) findViewById(R.id.button_backToShoppingList)).setOnClickListener(this);

        // Get the name of the category
        chosenCategory = getIntent().getStringExtra("CATEGORY");

        // connect to CB data base
        try {
            dbHelper = new CouchBaseHelper(AddItemToListActivity.this, CategoriesSource.getUserDB());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }


        Query query = dbHelper.createCategoryList(chosenCategory);
        mAdapter = new ListAdapter(this, query.toLiveQuery());
        ListView listView = (ListView) findViewById(R.id.listView_in_add_item);
        listView.setAdapter(mAdapter);

        Toast.makeText(AddItemToListActivity.this, "Mark all the items you want in your list " , Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.button_backToCategories:
                    goToCategories();
                    break;
                case R.id.button_backToShoppingList:
//                    dbHelper.deleteDataBase();
                    goToShoppingList();
                    break;
                case R.id.button_add_item_to_list:
                    addNewItem();
                    break;
            }
        }catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    private void addNewItem() throws CouchbaseLiteException {
        EditText newItem = (EditText)findViewById(R.id.EditText_add_item);
        String itemName = newItem.getText().toString();
        if (itemName.trim().equals(""))
            Toast.makeText(AddItemToListActivity.this, "Please fill up a name " , Toast.LENGTH_SHORT).show();
        else {
            dbHelper.addItem(chosenCategory, itemName);
            ((EditText) findViewById(R.id.EditText_add_item)).setText("");
        }
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
            final ImageButton editButton = (ImageButton) convertView.findViewById(R.id.button_edit_item);

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
                text.setBackgroundColor(Color.argb(100,255,161,167));
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
    public void goToEditItemActivity(Document doc) {
        Intent goToEditItem = new Intent(this,EditItemActivity.class);
         goToEditItem.putExtra("DOCUMENTID", doc.getId());
        goToEditItem.putExtra("CONTEX", "addItem");
        startActivity(goToEditItem);
    }
    private void goToCategories(){
        Intent goToCategoriesActivity = new Intent(this,CategoriesActivity.class);
        startActivity(goToCategoriesActivity);
    }

    private void goToShoppingList(){
        Intent goToShoppingListActivity = new Intent(this,ShoppingListActivity.class);
        startActivity(goToShoppingListActivity);
    }
}
