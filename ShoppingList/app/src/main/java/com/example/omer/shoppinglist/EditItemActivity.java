package com.example.omer.shoppinglist;

import android.content.Intent;
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
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;

import java.io.IOException;
import java.util.Map;

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
    private TextView amount;
    private Button buttonPlus;
    private Button buttonMinus;
    private Button saveChanges;
    private Button cancelChanges;
    private ArrayAdapter<String> categoryAdapter;

    private int amountCounter;

    private String[] categories = {
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


        categoryAdapter = new ArrayAdapter<String>(EditItemActivity.this,android.R.layout.simple_spinner_item,categories);
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

        // get document;
        doc = dbHelper.getDocument(docId);

        // update layout with data base
        checkBoxInList.setChecked((Boolean)doc.getProperty("in_list"));
        itemName.setText((String)doc.getProperty("name"));
        spinnerCategory.setSelection(checkSpinnerSelction());
        amountCounter = (int)doc.getProperty("amount");
        amount.setText(String.valueOf( amountCounter ) );
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
            //TODO imageButton
                break;

            case R.id.button_save_changes:
                try {
                    dbHelper.updateItemProperties(doc,checkBoxInList.isChecked(),itemName.getText().toString(),
                            amountCounter,categories[spinnerCategory.getSelectedItemPosition()]);
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }
                goToAddItemToList(lastCategory);
                Toast.makeText(EditItemActivity.this, "Saved" , Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_cancel_changes:
                Toast.makeText(EditItemActivity.this, "Canceled" , Toast.LENGTH_SHORT).show();
                goToAddItemToList(lastCategory);
                break;

            case R.id.button_delete_item:
                dbHelper.deleteDocument(doc);
                Toast.makeText(EditItemActivity.this, "Item deleted" , Toast.LENGTH_SHORT).show();
                goToAddItemToList(lastCategory);
                break;

        }
    }

    private int checkSpinnerSelction(){
        String category = (String)doc.getProperty("category");

        for (int  i = 0; i < this.categories.length; i++ ){
            if (category.equals(categories[i]))
                return i;
        }
        return 0;
    }

    public void goToAddItemToList(String category) {
        Intent goToAddItemToList = new Intent(this,AddItemToListActivity.class);
        goToAddItemToList.putExtra("CATEGORY",category);
        startActivity(goToAddItemToList);
    }

}
