package com.example.omer.shoppinglist;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.couchbase.lite.Manager;

public class AddItemToList extends AppCompatActivity {


    private CouchBaseHelper cbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to_list);

        // connect to CB data base
        cbHelper = new CouchBaseHelper(AddItemToList.this);
        cbHelper.openDB("test");
        cbHelper.createDocument("test","test");
    }
}
