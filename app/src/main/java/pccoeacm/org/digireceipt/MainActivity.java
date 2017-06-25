package pccoeacm.org.digireceipt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    EditText editTextUsername,editTextPassword;
    Button buttonLogin;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String username, password, dbPassword;
    CheckBox checkBoxKeepMeLoggedIn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static int VERSION = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        checkVersion();


    }
    void continueOnCreate()
    {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = editTextUsername.getText().toString();
                password = editTextPassword.getText().toString();
                databaseReference = firebaseDatabase.getReference().child("volunteers").child(username).child("password");;

                //databaseReference.
                //databaseReference.keepSynced(true);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dbPassword = dataSnapshot.getValue(String.class);
                        //Check if this volunteer has Attendance authority
                        DatabaseReference dR = firebaseDatabase.getReference().child("volunteers").child(username).child("aa");
                        dR.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(password.equals(dbPassword)) //&& databaseReference.equals(password))
                                {
                                    editor.putBoolean("kLoggedIn",checkBoxKeepMeLoggedIn.isChecked());
                                    editor.putLong("session", System.currentTimeMillis());
                                    editor.putString("username",username);
                                    editor.putString("password",password);
                                    editor.putBoolean("aa",dataSnapshot.getValue(Boolean.class));
                                    Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                                    editor.commit();
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this,"Invalid credentials",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //Log.d("Firebase","dbPassword : " + dbPassword  + " password : " + password);
                        //Log.d("Firebase",databaseReference.toString());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });
    }
    void initialise()
    {
        buttonLogin = (Button)findViewById(R.id.buttonLogin);
        editTextPassword = (EditText)findViewById(R.id.editTextLoginPass);
        editTextUsername = (EditText) findViewById(R.id.editTextLoginUsername);

        databaseReference = firebaseDatabase.getReference();

        databaseReference.keepSynced(true);
        checkBoxKeepMeLoggedIn = (CheckBox) findViewById(R.id.checkBoxKeepMeLoggedIn);
        sharedPreferences = getSharedPreferences("DigiReceipt",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if(keepLoggedIn())
        {
            Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
            finish();
        }
    }
    boolean keepLoggedIn()
    {
        boolean check = sharedPreferences.getBoolean("kLoggedIn",false);
        long session = sharedPreferences.getLong("session",0);
        //172800000 = 48hrs
        if(check && (System.currentTimeMillis() - session) <= 172800000)
        {
            return true;
        }
        else if ((System.currentTimeMillis() - session) > 172800000 && session != 0)
            Toast.makeText(MainActivity.this,"Session expired, log in again",Toast.LENGTH_LONG).show();
        return false;
    }
    void checkVersion()
    {
        databaseReference = firebaseDatabase.getReference().child("ver");
        //databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(int.class) == VERSION)
                {
                    //Log.d("Firebase : ", "Ver got: " + dataSnapshot.getValue(int.class));
                    setContentView(R.layout.activity_main);
                    initialise();
                    continueOnCreate();
                    return;
                }
                else
                {
                    Toast.makeText(MainActivity.this,"App outdated, please update.",Toast.LENGTH_LONG).show();
                    finish();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
