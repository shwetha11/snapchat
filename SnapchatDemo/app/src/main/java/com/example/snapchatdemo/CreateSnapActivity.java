package com.example.snapchatdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class CreateSnapActivity extends AppCompatActivity {

    ImageView imageView;
    EditText editText;
    String imagename= UUID.randomUUID().toString()+".jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_snap);
        imageView=findViewById(R.id.imageView);
        editText=findViewById(R.id.editText);
    }

    public void imageClicked(View view){
        Log.i("im","clik");
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        else{
            getPhoto();
        }
    }
    public void getPhoto(){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getPhoto();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImage=data.getData();//Uri contains the path of the image selected
        if(requestCode==1 && resultCode==RESULT_OK && data!=null){
            try{
                Bitmap image=MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                ImageView imageView=findViewById(R.id.imageView);
                imageView.setImageBitmap(image);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void nextClicked(View view){
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] data = baos.toByteArray();
        final UploadTask[] uploadTask = { FirebaseStorage.getInstance().getReference().child("Images").child(imagename).putBytes(data)};
        uploadTask[0].addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(CreateSnapActivity.this,"failed",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        final StorageReference ref = FirebaseStorage.getInstance().getReference().child("Images").child(imagename);
                                        uploadTask[0] = ref.putBytes(data);

                                        final Task<Uri> urlTask = uploadTask[0].continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                if (!task.isSuccessful()) {
                                                    throw task.getException();
                                                }
                                                return ref.getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    Uri downloadUri = task.getResult();
                                                    Log.i("URLdddd", downloadUri.toString());
                                                    Intent intent =new Intent(getApplicationContext(),chooseUserActivity.class);
                                                    intent.putExtra("imageName",imagename);
                                                    intent.putExtra("imageURL",downloadUri.toString());
                                                    intent.putExtra("message",editText.getText().toString());
                                                    startActivity(intent);
                                                } else {
                                                    // Handle failures
                                                    // ...
                                                }
                                            }
                                        });
                                    }
                                }
            );
    }
}

