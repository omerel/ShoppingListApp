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
    public void onBackPressed() {
        goToShoppingList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        final String category[] = new String[CategoriesSource.categories.length-1];
        int categoryImg[] = new int[CategoriesSource.imageId.length-1];

        for(int i =0; i< category.length;i++){
            category[i] = CategoriesSource.categories[i+1];
            categoryImg[i] = CategoriesSource.imageId[i+1];

        }


        GridAdapter adapter = new GridAdapter(CategoriesActivity.this, category, categoryImg);
        grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                goToAddItemToList(category[+position]);
            }
        });
    }
    public void goToAddItemToList(String category) {
        Intent goToAddItemToList = new Intent(this,AddItemToListActivity.class);
        goToAddItemToList.putExtra("CATEGORY",category);
        startActivity(goToAddItemToList);
    }
    private void goToShoppingList(){
        Intent goToShoppingListActivity = new Intent(this,ShoppingListActivity.class);
        startActivity(goToShoppingListActivity);
    }
}
