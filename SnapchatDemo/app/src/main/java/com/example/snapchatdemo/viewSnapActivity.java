package com.example.snapchatdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class viewSnapActivity extends AppCompatActivity {

    TextView textView;
    ImageView snapImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_snap);

        textView=findViewById(R.id.textView);
        snapImageView=findViewById(R.id.snapImageView);
        textView.setText(getIntent().getStringExtra("message"));
        imageDownloader image=new imageDownloader();
        Bitmap bitmap;
        try {
            bitmap=image.execute(getIntent().getStringExtra("imageURL")).get();
            snapImageView.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public class imageDownloader extends AsyncTask<String,Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url=new URL(urls[0]);

                HttpURLConnection connection=(HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream in=connection.getInputStream() ;

                Bitmap bitmap= BitmapFactory.decodeStream(in);

                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("snaps").child(getIntent().getStringExtra("snapKey")).removeValue();
        FirebaseStorage.getInstance().getReference().child("Images").child(getIntent().getStringExtra("imageName")).delete();
    }
}
