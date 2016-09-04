package com.example.omer.shoppinglist.util;

import com.example.omer.shoppinglist.R;

/**
 * Created by omer on 31/08/16.
 */
public class CategoriesSource {

    private static String userDb = null;

    public static String categories[] = {
            "Baking",
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
    } ;
    public static int[] imageId = {
            R.drawable.baking_item,
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
            R.drawable.other_item
    };

    public static String getUserDB(){
        return userDb;
    }
    public static void setUserDB(String name){
         userDb =  name;
    }

}
