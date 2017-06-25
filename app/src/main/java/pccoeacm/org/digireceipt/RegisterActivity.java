package pccoeacm.org.digireceipt;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    EditText editTextName1,editTextName2,editTextName3,editTextPhoneNo,editTextEmail,editTextInstitute,editTextDept;
    Button buttonReg, buttonSelctEvent;
    Spinner spinnerYear;
    Dialog dialog;
    ListView listViewEvents;
    SharedPreferences sharedPreferences;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<Event> arrayListEvents;
    EventListAdapter eventListAdapterArrayAdapter;
    TextView textViewTotAmt;
    ArrayList<String> events;

    int totAmt = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initialise();

    }
    void initialise()
    {
        editTextName1 = (EditText)findViewById(R.id.editTextRegName1);
        editTextName2 = (EditText)findViewById(R.id.editTextRegName2);
        editTextName3 = (EditText)findViewById(R.id.editTextRegName3);
        editTextPhoneNo = (EditText)findViewById(R.id.editTextRegPhone);
        editTextEmail = (EditText)findViewById(R.id.editTextRegEmail);
        editTextInstitute = (EditText)findViewById(R.id.editTextRegCollege);
        editTextDept = (EditText)findViewById(R.id.editTextRegDept);
        spinnerYear = (Spinner)findViewById(R.id.spinnerYear);
        //listViewEvents = (ListView)findViewById(R.id.listViewEvents);
        buttonReg = (Button)findViewById(R.id.buttonRegRegister);
        buttonSelctEvent = (Button)findViewById(R.id.buttonRegSelectEvents);
        events = new ArrayList<String>();
        sharedPreferences = getSharedPreferences("DigiReceipt",MODE_PRIVATE);
        textViewTotAmt = (TextView)findViewById(R.id.textViewTotalAmt);
        arrayListEvents = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference().child("events");
        databaseReference.keepSynced(true);
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                updateAdapter(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                updateAdapter(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                updateAdapter(dataSnapshot);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                updateAdapter(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        makeSpinner();
        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regsiterParticipant();
            }
        });
        buttonSelctEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(RegisterActivity.this,android.R.style.Theme_Holo_Light_Dialog);
                dialog.setContentView(R.layout.dialog_selectevents);

                Button buttonOk = (Button)dialog.findViewById(R.id.buttonSelectEventOk);
                ListView listView = (ListView)dialog.findViewById(R.id.listViewSelectEvent);
                listView.setAdapter(eventListAdapterArrayAdapter);
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.getWindow().getAttributes().width = ActionBar.LayoutParams.MATCH_PARENT;
                //dialog.getWindow().setTitle("Select events - ");
                dialog.setTitle("Select events - ");
                dialog.show();

            }
        });

    }
    @Override
    protected void onActivityResult(int reqCode,int result,Intent data)
    {
        super.onActivityResult(reqCode,result,data);
        if(reqCode == 1)
        {
            String name = data.getStringExtra("NAME");
            editTextName1.setText(name);
        }
    }
    void updateAdapter(DataSnapshot dataSnapshot)
    {
        //arrayListEvents.clear();
        //Log.d("Firebase",dataSnapshot.getValue().toString());
        arrayListEvents.add(dataSnapshot.getValue(Event.class));
        eventListAdapterArrayAdapter = new EventListAdapter(RegisterActivity.this,arrayListEvents);
        //listViewEvents.setAdapter(eventListAdapterArrayAdapter);
        //Log.d("Listsize",listViewEvents.getCount() + "");
        System.gc();
    }
    void increment(int val,String name)
    {
        totAmt += val;
        textViewTotAmt.setText("Total : " + totAmt);
        events.add(name);
    }
    void decrement(int val,String name)
    {
        totAmt -= val;
        textViewTotAmt.setText("Total : " + totAmt);
        events.remove(name);
    }
    void makeSpinner()
    {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("First");
        arrayList.add("Second");
        arrayList.add("Third");
        arrayList.add("Fourth");
        ArrayAdapter arrayAdapter = new ArrayAdapter(RegisterActivity.this,android.R.layout.simple_spinner_item,arrayList);
        spinnerYear.setAdapter(arrayAdapter);
    }
    void regsiterParticipant()
    {
        String name1,name2,name3,phoneNo,email,institute,dept,year,username,mode,txnId;
        long timestamp;
        int totPayment;
        name1 = editTextName1.getText().toString();
        name2 = editTextName2.getText().toString();
        name3 = editTextName3.getText().toString();
        phoneNo = editTextPhoneNo.getText().toString();
        email  = editTextEmail.getText().toString();
        institute = editTextInstitute.getText().toString();
        dept = editTextDept.getText().toString();
        year = spinnerYear.getSelectedItem().toString();
        username = sharedPreferences.getString("username","NA");
        mode = "Cash";
        txnId = "Offline";
        totPayment = totAmt;
        timestamp = System.currentTimeMillis();
        //Log.d("Timestamp","timestamp : " + timestamp);
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(RegisterActivity.this,"Invalid email.",Toast.LENGTH_LONG).show();
        }
        else if (name1 != null && totAmt > 0 && events.size() > 0 && phoneNo.length() == 10 && !username.equals("NA") ) {
            Participant participant = new Participant(name1,name2,name3,phoneNo,email,institute,dept,events,timestamp,username,totPayment,mode,year, txnId, false, false);
            Intent intent = new Intent(RegisterActivity.this,PaymentActivity.class);
            intent.putExtra("participant",participant);
            startActivity(intent);
            finish();
        }
        else if(username.equals("NA"))
        {
            Toast.makeText(RegisterActivity.this,"Issue with session, please logout and login again",Toast.LENGTH_LONG).show();

        }
        else
        {
            Toast.makeText(RegisterActivity.this,"Invalid values in field /fields.",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed()
    {
        finish();
        super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        //respond to menu item selection
        switch (item.getItemId())
        {

            case R.id.menuSignOut:
                SharedPreferences sharedPreferences = getSharedPreferences("DigiReceipt",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("kLoggedIn",false);
                editor.putString("username","NA");
                editor.putString("password","NA");
                editor.putBoolean("aa",false);
                editor.commit();
                intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuHistory:
                intent = new Intent(RegisterActivity.this,HistoryActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuScanQR:
                intent = new Intent(RegisterActivity.this,ScanQRActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuAbout:
                intent = new Intent(RegisterActivity.this,AboutActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return true;
    }
}

