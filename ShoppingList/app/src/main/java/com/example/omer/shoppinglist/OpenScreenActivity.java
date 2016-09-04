package com.example.omer.shoppinglist;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class OpenScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_screen);

        StartScreen start = new StartScreen(this);
        start.execute();

    }
    public void goToLogin() {
        Intent goToMenu = new Intent(this,LoginActivity.class);
        startActivity(goToMenu);
    }


    public class StartScreen extends AsyncTask<Void,Void,Void> {

        Context context;

        public StartScreen(Context context){
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void[] objects) {

            try {
                publishProgress();
                Thread.sleep(5000);
            }catch (Exception ex){
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            Toast.makeText(context,"Please wait",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            goToLogin();
            this.cancel(true);
        }
    }
}
