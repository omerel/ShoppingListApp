package com.example.omer.shoppinglist;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ImageActivity extends AppCompatActivity {


    private Bitmap picture = null;
    private ImageView imageFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageFrame = (ImageView)findViewById(R.id.image_frame);
        // get image
        picture = (Bitmap) getIntent().getParcelableExtra("PICTURE");

        //set image
        if ( picture != null)
            imageFrame.setImageBitmap(picture);



    }
}
