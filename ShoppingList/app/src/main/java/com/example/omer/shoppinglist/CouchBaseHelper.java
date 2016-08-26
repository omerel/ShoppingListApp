package com.example.omer.shoppinglist;

import android.content.Context;
import android.util.Log;


import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by omer on 25/08/16.
 */
public class CouchBaseHelper extends android.app.Application {

    private Manager manager;
    private static Context mContext;
    private Database mdb;

    public CouchBaseHelper(Context context) {
        this.mContext = context;
    }

    public boolean openDB(String dbName) {
        // Java application
        Manager manager = null;
        try {
            manager = new Manager(new AndroidContext(mContext), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.mdb = manager.getDatabase(dbName);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
        return true;

    }


    public boolean createDocument(String title,String userId){
        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put("type", "list");
        properties.put("title", title);
        properties.put("owner", "profile:" + userId);
        properties.put("members", new ArrayList<String>());

        Document document = mdb.createDocument();

        try {
            document.putProperties(properties);
            return true;
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<String, Object> getPropertiesFromDocument(String docId){
        Document doc = mdb.getDocument(docId);
        // We can directly access properties from the document object:
        doc.getProperty("title");
        // Or go through its properties dictionary:
        Map<String, Object> properties = doc.getProperties();
        return properties;
    }

    public boolean updateDocument(String docId, Map<String, Object> properties){

        Document doc = mdb.getDocument(docId);
        try {
            doc.putProperties(properties);
            return true;
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return false;
        }
    }
}
