package com.example.omer.shoppinglist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.example.omer.shoppinglist.util.CategoriesSource;

import com.example.omer.shoppinglist.util.GridAdapter;


public class CategoriesActivity extends AppCompatActivity {

    GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);


        GridAdapter adapter = new GridAdapter(CategoriesActivity.this, CategoriesSource.categories, CategoriesSource.imageId);
        grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(CategoriesActivity.this, "You Clicked at " +CategoriesSource.categories[+ position], Toast.LENGTH_SHORT).show();
                goToAddItemToList(CategoriesSource.categories[+position]);
            }
        });
    }
    public void goToAddItemToList(String category) {
        Intent goToAddItemToList = new Intent(this,AddItemToListActivity.class);
        goToAddItemToList.putExtra("CATEGORY",category);
        startActivity(goToAddItemToList);
    }
}
