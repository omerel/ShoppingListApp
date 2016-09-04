package com.example.omer.shoppinglist.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by omer on 20/08/16.
 */
public class DBHelper extends SQLiteOpenHelper{

        public static final String DATABASE_NAME = "UsersDB.db";

        public DBHelper(Context context)
        {
            super(context, DATABASE_NAME , null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub

            db.execSQL(
                    " CREATE TABLE `users` (`email`	TEXT,`password`	TEXT,PRIMARY KEY(email))"
            );

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS users");
            onCreate(db);
        }

        public boolean insertUser  (String email, String password)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("email", email);
            contentValues.put("password", password);
            db.insert("users", null, contentValues);
            return true;
        }

        public Cursor getPassword(String email){
            String selectQuery = "SELECT password FROM users WHERE email=?";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery(selectQuery, new String[] { email });
            return res;

        }

        public boolean updatePassword (String email, String password)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("email", email);
            contentValues.put("password", password);
            db.update("users", contentValues, "email = ? ", new String[] { email } );
            return true;
        }

        public Integer deleteUser (String email)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete("users",
                    "email = ? ",
                    new String[] { email });
        }

}
