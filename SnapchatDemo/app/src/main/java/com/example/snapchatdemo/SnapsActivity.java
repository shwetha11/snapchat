package com.example.snapchatdemo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SnapsActivity extends AppCompatActivity {

    private static final boolean USER_IS_GOING_TO_EXIT = true;
    FirebaseAuth mAuth=FirebaseAuth.getInstance();
    ListView snapsListView;
    ArrayList<String> email=new ArrayList<>();
    ArrayList<DataSnapshot> snapshots=new ArrayList<>();
    private Toast backtoast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaps);
        snapsListView=findViewById(R.id.snapsListView);
        final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,email);
        snapsListView.setAdapter(arrayAdapter);
        FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("snaps").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                email.add(dataSnapshot.child("from").getValue().toString());
                arrayAdapter.notifyDataSetChanged();
                snapshots.add(dataSnapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                int index = 0;

                for (DataSnapshot snap : snapshots) {

                    Log.i("B4IFSnapLog", snap.getKey().toString());
                    Log.i("B4IFDatasnapLog", dataSnapshot.getKey().toString());

                    if (dataSnapshot.getKey().equals(snap.getKey())) {

                        Log.i("SnapLog", snap.getKey().toString());
                        Log.i("DatasnapLog", dataSnapshot.getKey().toString());

                        snapshots.remove(index);
                        email.remove(index);

                    } else {

                        Log.i("ERROR", "COULD NOT EXECUTE");

                    }

                    index++;

                }

                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        snapsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataSnapshot shot=snapshots.get(position);
                Intent intent=new Intent(getApplicationContext(),viewSnapActivity.class);
                intent.putExtra("message",shot.child("message").getValue().toString());
                intent.putExtra("imageURL",shot.child("imageURL").getValue().toString());
                intent.putExtra("imageName",shot.child("imageName").getValue().toString());
                intent.putExtra("snapKey", shot.getKey());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu_resources,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){
            //logout
            mAuth.signOut();
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }else if(item.getItemId()==R.id.createSnap){
            Intent intent=new Intent(getApplicationContext(),CreateSnapActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(USER_IS_GOING_TO_EXIT) {
            if(backtoast!=null&&backtoast.getView().getWindowToken()!=null) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                //finish();
            } else {
                backtoast = Toast.makeText(this, "Press back to exit", Toast.LENGTH_SHORT);
                backtoast.show();
            }
        } else {
            //other stuff...
            super.onBackPressed();
        }
    }
}
