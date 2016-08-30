package com.example.omer.shoppinglist;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.couchbase.lite.Attachment;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.Revision;
import com.couchbase.lite.UnsavedRevision;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by omer on 25/08/16.
 */
public class CouchBaseHelper extends android.app.Application {

    private static Context mContext;
    private Database mdb;

    // connect to data base and bind layout
    public CouchBaseHelper(Context context,String dbName) throws IOException, CouchbaseLiteException {
        this.mContext = context;
        Manager  manager = new Manager(new AndroidContext(mContext), Manager.DEFAULT_OPTIONS);
        this.mdb = manager.getDatabase(dbName);
    }

    // delete database
    public boolean deleteDataBase() throws CouchbaseLiteException {
            mdb.delete();
            return true;
    }

    // add specific document into data base
    public boolean addItem(String category, String name) throws CouchbaseLiteException {
        Map<String, Object> properties = new HashMap<String, Object>();

        properties.put("type", "item");
        properties.put("category", category);
        properties.put("name", name);
        properties.put("amount", 1);
        properties.put("in_list", Boolean.FALSE);
        properties.put("in_cart", Boolean.FALSE);
        properties.put("favorite_list", new ArrayList<String>());

        Document document = mdb.createDocument();
        document.putProperties(properties);
        return true;
    }


    public Document getDocument(String documentId){
         return mdb.getDocument(documentId);
    }

    public boolean checkDocumentExist(String documentId){
        // if  document not exist return false;
        if (mdb.getExistingDocument(documentId) == null)
            return false;
        return true;
    }


    public boolean deleteDocument(Document doc){

        try {
            doc.delete();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean setTTLToDocument(String docId, int ttl){

        Date new_ttl = new Date(System.currentTimeMillis() + ttl);

        // if  document not exist return false;
        if (mdb.getExistingDocument(docId) == null)
            return false;

        Document doc = (Document) mdb.getDocument(docId);
        doc.setExpirationDate(new_ttl);
        return true;
    }
    /*
    // Add an image in a callback after invoking the Android Camera activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        InputStream stream = null;
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            InputStream stream = getContentResolver().openInputStream(data.getData());
            // Add or update an image to a document as a JPEG attachment:
            Document doc = database.getDocument("Robin");
            UnsavedRevision newRev = doc.getCurrentRevision().createRevision();
            newRev.setAttachment("photo.jpg", "image/jpeg", stream);
            newRev.save();
        }
    }
    */

    public void attachImage(Document doc, Bitmap image,String itemName) {
        if (doc == null || image == null) return;

        UnsavedRevision revision = doc.createRevision();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, out);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        revision.setAttachment("pic_"+itemName, "image/jpg", in);
        try {
            revision.save();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getAttachment(Document doc,String imageName) {

        Attachment attachment = doc.getCurrentRevision().getAttachment("pic_" + imageName);
        if (attachment == null)
            return null;

        Bitmap image = null;
        InputStream is = null;
        try {
            is = attachment.getContent();
            image = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return image;
    }

    // Read attachment from document. if not exist return null
    public Drawable readAttachment(String docId,String attachmentName){

        Document doc = mdb.getDocument(docId);
        Revision rev = doc.getCurrentRevision();
        Attachment att = rev.getAttachment(attachmentName);
        if (att != null) {
           InputStream is = null;
            try {
                is =  att.getContent();
                return Drawable.createFromStream(is, attachmentName);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    public boolean removeAttachment(String docId,String attachmentName){
        // Remove an attachment from a document:
        Document doc = mdb.getDocument(docId);
        UnsavedRevision newRev = doc.getCurrentRevision().createRevision();
        newRev.removeAttachment(attachmentName);
        // (option - update newRev.properties while you're here)
        try {
            newRev.save();
            return true;
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
            return false;
        }
    }

    // shows all the items in db in the selected category
    public Query createCategoryList(String category) {

        final String m_category = category;

        // update view or create new one
        View view = mdb.getView("item_list_"+category);
        view.setMap(new Mapper() {
            @Override
            public void map(Map<String, Object> document, Emitter emitter) {

                if (m_category.equals(document.get("category"))) {
                   // List<Object> keys = new ArrayList<Object>();
                  //  keys.add(document.get("name"));
                  //  keys.add(document.get("amount"));
                    emitter.emit(document, null);
                }
            }
        }, "1");

        Query query = view.createQuery();
        query.setDescending(false);

        return query;
    }
    // Add an item to shop list
    public void addToShopList(Document doc) throws CouchbaseLiteException {
        // verify that the document exist
        if (mdb.getExistingDocument(doc.getId()) != null){
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.putAll(doc.getProperties());

            Boolean status = (Boolean) properties.get("in_list");
            status = !status;
            properties.put("in_list",status);
            doc.putProperties(properties);
        }
    }


    public void updateItemProperties(Document doc,
                                     boolean checked, String itemName, int amountCounter,
                                     String category,Bitmap image) throws CouchbaseLiteException {
        // verify that the document exist
        if (mdb.getExistingDocument(doc.getId()) != null) {
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.putAll(doc.getProperties());

            properties.put("in_list", checked);
            properties.put("name", itemName);
            properties.put("amount", amountCounter);
            properties.put("category", category);
            doc.putProperties(properties);
            if (image != null)
                attachImage(doc,image,itemName);
        }
    }

}
