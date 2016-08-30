package com.example.omer.shoppinglist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;




public class CategoriesActivity extends AppCompatActivity {

    GridView grid;
     String[] categories = {
            "Baking",
            "Bread",
            "Canned",
            "Cleaning",
            "Drinks",
            "Fruits",
            "Health And Beauty",
            "Meat",
            "Milk And Dairy",
            "Spices",
            "Other",
            "Favorite"
    } ;
    int[] imageId = {
            R.drawable.baking_item,
            R.drawable.bread_item,
            R.drawable.canned_item,
            R.drawable.cleaning_item,
            R.drawable.drinks_item,
            R.drawable.fruits_item,
            R.drawable.health_and_beauty_item,
            R.drawable.meat_item,
            R.drawable.milk_and_dairy_item,
            R.drawable.spices_item,
            R.drawable.other_item,
            R.drawable.other_item,

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        GridAdapter adapter = new GridAdapter(CategoriesActivity.this, categories, imageId);
        grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(CategoriesActivity.this, "You Clicked at " +categories[+ position], Toast.LENGTH_SHORT).show();
                goToAddItemToList(categories[+position]);
            }
        });
    }
    public void goToAddItemToList(String category) {
        Intent goToAddItemToList = new Intent(this,AddItemToListActivity.class);
        goToAddItemToList.putExtra("CATEGORY",category);
        startActivity(goToAddItemToList);
    }
}
