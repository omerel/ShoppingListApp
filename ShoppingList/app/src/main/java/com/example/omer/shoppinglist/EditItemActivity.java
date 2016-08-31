package com.example.omer.shoppinglist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.example.omer.shoppinglist.util.CategoriesSource;
import com.example.omer.shoppinglist.util.CouchBaseHelper;
import com.example.omer.shoppinglist.util.ImageActivity;

import java.io.IOException;

public class EditItemActivity extends AppCompatActivity implements View.OnClickListener {

    //
    private CouchBaseHelper dbHelper = null;
    private String docId = null;
    private Document doc = null;

    private Spinner spinnerCategory;
    private CheckBox checkBoxInList;
    private EditText itemName ;
    private Button deleteItem;
    private ImageButton itemImage;
    private Button takePicture;
    private TextView amount;
    private Button buttonPlus;
    private Button buttonMinus;
    private Button saveChanges;
    private Button cancelChanges;
    private ArrayAdapter<String> categoryAdapter;

    private int amountCounter;
    private Bitmap pictureItem = null;
    private String contex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // bind layout to class
        itemImage = (ImageButton)findViewById(R.id.imageButton_item);
        deleteItem = (Button)findViewById(R.id.button_delete_item);
        itemName = (EditText)findViewById(R.id.editText_item_name);
        checkBoxInList = (CheckBox)findViewById(R.id.checkBox_add_item_to_list);
        spinnerCategory = (Spinner)findViewById(R.id.spinner_choose_category);
        amount = (TextView) findViewById(R.id.textView_item_amount_number);
        buttonPlus = (Button)findViewById(R.id.button_count_plus);
        buttonMinus = (Button)findViewById(R.id.button_count_minus);
        saveChanges = (Button)findViewById(R.id.button_save_changes);
        cancelChanges = (Button)findViewById(R.id.button_cancel_changes);
        takePicture = (Button)findViewById(R.id.button_take_picture);


        categoryAdapter = new ArrayAdapter<String>(EditItemActivity.this,android.R.layout.simple_spinner_item,CategoriesSource.categories);
        spinnerCategory.setAdapter(categoryAdapter);

        // make layout clickable
        checkBoxInList.setOnClickListener(this);
        itemName.setOnClickListener(this);
        deleteItem.setOnClickListener(this);
        itemImage.setOnClickListener(this);
        buttonPlus.setOnClickListener(this);
        buttonMinus.setOnClickListener(this);
        saveChanges.setOnClickListener(this);
        cancelChanges.setOnClickListener(this);
        takePicture.setOnClickListener(this);

        // connect to CB data base
        try {
            dbHelper = new CouchBaseHelper(EditItemActivity.this,"shopping_list_db");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }

        // get docID Intent
        docId = getIntent().getStringExtra("DOCUMENTID");
        contex = getIntent().getStringExtra("CONTEX");

        // get document;
        doc = dbHelper.getDocument(docId);

        // update layout with data base
        checkBoxInList.setChecked((Boolean)doc.getProperty("in_list"));
        itemName.setText((String)doc.getProperty("name"));
        spinnerCategory.setSelection(checkSpinnerSelction());
        amountCounter = (int)doc.getProperty("amount");
        amount.setText(String.valueOf( amountCounter ));

        pictureItem = dbHelper.getAttachment(doc,(String)doc.getProperty("name"));
        if (pictureItem != null)
            itemImage.setImageBitmap(pictureItem);
    }

    @Override
    public void onClick(View v) {

        String lastCategory = (String)doc.getProperty("category");

        switch (v.getId()){
            case R.id.button_count_minus:
                if (amountCounter > 1)
                    amountCounter--;
                amount.setText(String.valueOf( amountCounter ) );
                break;

            case R.id.button_count_plus:
                amountCounter++;
                amount.setText(String.valueOf(amountCounter) );
                break;

            case R.id.imageButton_item:
                goToImageActivity(pictureItem);
                break;

            case R.id.button_take_picture:
                takePicture();
                break;

            case R.id.button_save_changes:
                try {
                    dbHelper.updateItemProperties(doc,checkBoxInList.isChecked(),itemName.getText().toString(),
                            amountCounter, CategoriesSource.categories[spinnerCategory.getSelectedItemPosition()],pictureItem);
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
                if(contex.equals("shoppingList"))
                    goToShoppingList();
                else
                    goToAddItemToList(lastCategory);
                Toast.makeText(EditItemActivity.this, "Saved" , Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_cancel_changes:
                Toast.makeText(EditItemActivity.this, "Canceled" , Toast.LENGTH_SHORT).show();
                if(contex.equals("shoppingList"))
                    goToShoppingList();
                else
                    goToAddItemToList(lastCategory);
                break;

            case R.id.button_delete_item:
                dbHelper.deleteDocument(doc);
                Toast.makeText(EditItemActivity.this, "Item deleted" , Toast.LENGTH_SHORT).show();
                if(contex.equals("shoppingList"))
                    goToShoppingList();
                else
                    goToAddItemToList(lastCategory);
                break;

        }
    }

    private void takePicture() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            pictureItem = (Bitmap) data.getExtras().get("data");
            itemImage.setImageBitmap(pictureItem);
        }

    }

    private int checkSpinnerSelction(){
        String category = (String)doc.getProperty("category");

        for (int  i = 0; i < CategoriesSource.categories.length; i++ ){
            if (category.equals(CategoriesSource.categories[i]))
                return i;
        }
        return 0;
    }

    public void goToAddItemToList(String category) {
        Intent goToAddItemToList = new Intent(this,AddItemToListActivity.class);
        goToAddItemToList.putExtra("CATEGORY",category);
        startActivity(goToAddItemToList);
    }
    public void goToImageActivity(Bitmap pic) {
        Intent goToImageActivity = new Intent(this,ImageActivity.class);
        goToImageActivity.putExtra("PICTURE",pic);
        startActivity(goToImageActivity);
    }

    private void goToShoppingList(){
        Intent goToShoppingListActivity = new Intent(this,ShoppingListActivity.class);
        startActivity(goToShoppingListActivity);
    }


}
