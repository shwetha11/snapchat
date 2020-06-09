package com.example.snapchatdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText emailEditText;
    EditText passwordEditText;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    TextView signUpTextView;
    Boolean login=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("login");
        emailEditText=findViewById(R.id.emailEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        signUpTextView=findViewById(R.id.signUpTextView);
        signUpTextView.setOnClickListener(this);
        if(mAuth.getCurrentUser() != null){
            logIn();
        }
    }

    public void goClicked(View view){
        if(login){
            Log.i("login value",login.toString());
            mAuth.signInWithEmailAndPassword(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                logIn();
                            } else {
                                Toast.makeText(MainActivity.this,"Login Failed:(",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            mAuth.createUserWithEmailAndPassword(emailEditText.getText().toString().trim(), passwordEditText.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //add user to database
                                FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).child("email").setValue(emailEditText.getText().toString().trim());
                                FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).child("password").setValue(passwordEditText.getText().toString().trim());
                                logIn();
                            } else {
                                Toast.makeText(MainActivity.this,"SignUp Failed:(",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    public void logIn(){
        //move to next activity
        Intent intent=new Intent(getApplicationContext(),SnapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Log.i("click","pressed");
        if(v.getId()==R.id.signUpTextView){
            Log.i("te","pressed");
            Button button=findViewById(R.id.imageButton);
            if(login){
                login=false;
                button.setText("Sign Up");
                signUpTextView.setText("Or,Login");
            }
            else{
                login=true;
                button.setText("Login");
                signUpTextView.setText("Or,Sign Up");
            }
        }
    }
}
