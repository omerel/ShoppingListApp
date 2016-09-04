package com.example.omer.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;
import com.example.omer.shoppinglist.util.CategoriesSource;
import com.example.omer.shoppinglist.util.CouchBaseHelper;
import com.example.omer.shoppinglist.util.ListViewAdapter;
import com.example.omer.shoppinglist.util.LiveQueryAdapter;

import java.io.IOException;

public class ShoppingListActivity extends AppCompatActivity implements View.OnClickListener{

    // user id (email)
    private String user;
    private CouchBaseHelper dbHelper = null;
    private ListCategorydapter mCAdapter = null;
    private ListAdapter mAdapter[] = null;
    private ListAdapter mCartAdapter = null;
    private Query query;
    private ListView mainListView;
    private ListView categoryListView;
    private ImageButton categoryImage[];
    private ImageView categoryISelected[];
    private ImageView categoryNotEmpty[];
    private int focusedCategory;
    private Button cartButton;
    private Button cleanCartButton;
    private TextView textCounter;
    private TextView textListSatus;
    private String userDataBase;


    @Override
    public void onBackPressed(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user =  getIntent().getStringExtra("USER");
        if (user != null ) {
            userDataBase = "shopping_list_db_" + user;
            CategoriesSource.setUserDB(userDataBase);
        }

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
            dbHelper = new CouchBaseHelper(ShoppingListActivity.this,CategoriesSource.getUserDB());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        // init and binding
        mainListView = (ListView)findViewById(R.id.main_list_view_in_shopping_list);
        cartButton = (Button) findViewById(R.id.shopping_show_cart);
        cleanCartButton = (Button) findViewById(R.id.shopping_list_clean_cart);
        categoryListView = (ListView)findViewById(R.id.category_in_shopping_list);
        textCounter = (TextView)findViewById(R.id.text_count_items_in_list);
        textListSatus = (TextView)findViewById(R.id.text_shopping_list_status);
        categoryImage = new ImageButton[CategoriesSource.categories.length];
        categoryISelected = new ImageView[CategoriesSource.categories.length];
        categoryNotEmpty = new ImageView[CategoriesSource.categories.length];
        mAdapter = new ListAdapter[CategoriesSource.categories.length];
        focusedCategory = 1;

        // set on click
        cleanCartButton.setOnClickListener(this);
        cartButton.setOnClickListener(this);


        mCAdapter = new ListCategorydapter(this,CategoriesSource.imageId);
        categoryListView.setAdapter(mCAdapter);

        query = dbHelper.createCategoryListInList(CategoriesSource.categories[focusedCategory]);
        mAdapter[focusedCategory] = new ListAdapter(ShoppingListActivity.this,query.toLiveQuery());
        mainListView.setAdapter(mAdapter[focusedCategory]);


         LayoutInflater inflater = (LayoutInflater) mainListView.getContext().
               getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.image_single, null);
        mCAdapter.notifyDataSetChanged();
        categoryListView.performItemClick(convertView,1,1);
        mCAdapter.notifyDataSetChanged();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.shopping_show_cart):
                textListSatus.setText("");
                query = dbHelper.createItemsInCart("Cart");
                mCartAdapter = new ListAdapter(ShoppingListActivity.this,query.toLiveQuery());
                mainListView.setAdapter(mCartAdapter);
                break;
            case(R.id.shopping_list_clean_cart):
                try {
                    dbHelper.cleanitemsInCart("Cart");
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private class ListCategorydapter extends ListViewAdapter{


        int imageId[];
        public ListCategorydapter(Context contex, int[] imageId) {
            super(contex, imageId);
            this.imageId = imageId;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.image_single, null);

                // TODO  I have problem with position 0 - not drawing me star or selected
                if(position != 0 ) {
                    categoryISelected[position] = (ImageView) convertView.findViewById(R.id.shopping_list_category_selcted);
                    categoryNotEmpty[position] =(ImageView) convertView.findViewById(R.id.shopping_list_category_not_empty);
                    categoryImage[position] = (ImageButton) convertView.findViewById(R.id.image_single);
                    categoryImage[position].setImageResource(imageId[position]);
                    // set query
                    query = dbHelper.createCategoryListInList(CategoriesSource.categories[position]);
                    mAdapter[position] = new ListAdapter(ShoppingListActivity.this, query.toLiveQuery());


                    categoryImage[position].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            categoryISelected[focusedCategory].setVisibility(View.INVISIBLE);
                            focusedCategory = position;
                            categoryISelected[focusedCategory].setVisibility(View.VISIBLE);
                            mainListView.setAdapter(mAdapter[position]);
                            updateCounterInList(countSumList(-1));
                            setTextListStatus(position);
                        }
                    });
                }
                else {
                    categoryImage[position] = (ImageButton) convertView.findViewById(R.id.image_single);
                    categoryISelected[position] = (ImageView) convertView.findViewById(R.id.shopping_list_category_selcted);
                    categoryNotEmpty[position] =(ImageView) convertView.findViewById(R.id.shopping_list_category_not_empty);
                    categoryImage[position].setVisibility(View.GONE);
                    categoryISelected[position].setVisibility(View.GONE);
                    categoryNotEmpty[position].setVisibility(View.GONE);
                }
            }
            return convertView;
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
                convertView = inflater.inflate(R.layout.item_in_shopping_list, null);
            }

            // bind objects with layout
            final CheckBox checkBox = (CheckBox)convertView.findViewById(R.id.checkbox_add_item_to_cart);
            TextView textItem = (TextView) convertView.findViewById(R.id.text_item_in_shopping_list);
            ImageButton picButton = (ImageButton) convertView.findViewById(R.id.image_item_in_shopping_list);

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

            if (dbHelper.getAttachment(doc,(String)doc.getProperty("name")) != null)
                picButton.setBackgroundColor(Color.MAGENTA);
            picButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap pictureItem = dbHelper.getAttachment(doc,(String)doc.getProperty("name"));
                    if (pictureItem != null)
                        goToImageActivity(pictureItem);
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
                        updateCounterInList(countSumList(-1));
                    }catch  (CouchbaseLiteException e){
                        e.printStackTrace();
                    }

                }
            });
            //updateCounterInList(countSumList(-1));
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

    public int countSumList(int position){

        int sum =0;
        if(position == -1) {
            for (int i = 1; i < mAdapter.length; i++)
                if (mAdapter[i].getCount() > 0) {
                    categoryNotEmpty[i].setVisibility(View.VISIBLE);
                    sum += mAdapter[i].getCount();
                }else
                    categoryNotEmpty[i].setVisibility(View.INVISIBLE);
        }
        else{
           return mAdapter[position].getCount();
        }
        return sum;
    }

    public void updateCounterInList(int sum){
        textCounter.setText("You have "+sum+" items in list");
    }

    public void setTextListStatus(int position){
        if (countSumList(position) == 0)
            textListSatus.setText(CategoriesSource.categories[position]+" list\n"+"is empty");
        else
            textListSatus.setText("");
    }
    public void goToImageActivity(Bitmap pic) {
        Intent goToImageActivity = new Intent(this,ImageActivity.class);
        goToImageActivity.putExtra("PICTURE",pic);
        startActivity(goToImageActivity);
    }

}
