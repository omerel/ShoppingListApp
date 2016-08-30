package com.example.omer.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class ShoppingListActivity extends AppCompatActivity {

    // user id (email)
    private String user;

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
    }

}
