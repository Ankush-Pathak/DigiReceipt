package pccoeacm.org.digireceipt;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

public class ScanQRActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView textViewQRStatus;
    ChildEventListener childEventListener;
    SurfaceView surfaceViewCamera;
    Button buttonFullDetails;
    Dialog dialog;
    Participant participant;
    QREader qrEader;
    String key;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        initialise();
        scanQr();
    }
    void initialise()
    {
        firebaseDatabase = FirebaseDatabase.getInstance();
        textViewQRStatus = (TextView)findViewById(R.id.textViewScanQRInfor);
        surfaceViewCamera = (SurfaceView)findViewById(R.id.surfaceViewCamera);
        buttonFullDetails = (Button)findViewById(R.id.buttonScanQRDetails);
        sharedPreferences = getSharedPreferences("DigiReceipt",MODE_PRIVATE);

        buttonFullDetails.setEnabled(false);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(key.equals(dataSnapshot.getKey().toString()))
                    dataValidDisplayNow(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if(key.equals(dataSnapshot.getKey().toString()))
                    dataValidDisplayNow(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if(key.equals(dataSnapshot.getKey().toString()))
                    dataValidDisplayNow(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                if(key.equals(dataSnapshot.getKey().toString()))
                    dataValidDisplayNow(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        buttonFullDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDetailsDialog();
            }
        });
    }
    void scanQr()
    {
        qrEader = new QREader.Builder(ScanQRActivity.this, surfaceViewCamera, new QRDataListener() {
            @Override
            public void onDetected(String data) {
                gotDataNowProcess(data);
            }
        }).facing(QREader.BACK_CAM)
        .enableAutofocus(true)
        .height(surfaceViewCamera.getHeight())
        .width(surfaceViewCamera.getWidth())
        .build();
    }
    void gotDataNowProcess(String data)
    {
        ScanQRActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewQRStatus.setText("QR Code : Invalid");
                textViewQRStatus.setTextColor(Color.RED);
            }
        });

        //Log.d("Key : ",data);
        key = data;
        databaseReference = firebaseDatabase.getReference().child("participant");
        databaseReference.addChildEventListener(childEventListener);

    }
    void dataValidDisplayNow(DataSnapshot dataSnapshot)
    {
        qrEader.stop();
        buttonFullDetails.setEnabled(true);
        //Log.d("QRScanRef : ",dataSnapshot.toString());
        participant = dataSnapshot.getValue(Participant.class);
        textViewQRStatus.setText("QR Code : Valid");
        textViewQRStatus.setTextColor(Color.GREEN);


    }
    @Override
    protected void onResume() {
        super.onResume();

        // Init and Start with SurfaceView
        // -------------------------------
        qrEader.initAndStart(surfaceViewCamera);
    }
    @Override
    protected void onPause() {
        super.onPause();
        qrEader.releaseAndCleanup();
    }
    void showDetailsDialog()
    {
        dialog = new Dialog(ScanQRActivity.this);
        dialog.setContentView(R.layout.dialog_participan_full_details);
        final TextView textViewName1,textViewName2,textViewName3,textViewPhone,textViewInstitute, textViewEmailSent;
        CheckBox checkBoxAttnd;
        ListView listView;
        Button buttonFDOk;
        ArrayList<String> arrayList;
        ArrayAdapter arrayAdapter;
        buttonFDOk = (Button)dialog.findViewById(R.id.buttonFDOk);


        textViewName1 = (TextView)dialog.findViewById(R.id.textViewFDName1);
        textViewName2 = (TextView)dialog.findViewById(R.id.textViewFDName2);
        textViewName3 = (TextView)dialog.findViewById(R.id.textViewFDName3);
        textViewPhone = (TextView)dialog.findViewById(R.id.textViewFDPhone);
        checkBoxAttnd = (CheckBox)dialog.findViewById(R.id.checkBoxFDAttendance);
        textViewEmailSent = (TextView)dialog.findViewById(R.id.textViewFDEmailSent);
        checkBoxAttnd.setEnabled(false);
        textViewInstitute = (TextView)dialog.findViewById(R.id.textViewFDInstitute);
        listView = (ListView)dialog.findViewById(R.id.listViewFDEvents);

        if(sharedPreferences.getBoolean("aa",false))
                    checkBoxAttnd.setEnabled(true);
        textViewName1.setText("Member 1 :" + participant.getName1());
        textViewName2.setText("Member 2 : " + participant.getName2());
        textViewName3.setText("Member 3 : " + participant.getName3());
        textViewInstitute.setText("Institute : " + participant.getInsitute());
        textViewPhone.setText("Contact : " + participant.getPhoneNo());
        if(participant.isEmailSent())
            textViewEmailSent.setText("Notification email : Sent");
        else
            textViewEmailSent.setText("Notification email : Not sent");
        checkBoxAttnd.setChecked(participant.getAttdnc());
        arrayList = (ArrayList<String>) participant.getEvents();
        arrayAdapter = new ArrayAdapter(ScanQRActivity.this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
        buttonFDOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        checkBoxAttnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                databaseReference = firebaseDatabase.getReference();
                Map<String,Object> map = new HashMap<String, Object>();
                map.put("attdnc",b);

                databaseReference.child("participant").child(key).updateChildren(map);
            }
        });

        dialog.setTitle("Full details");
        dialog.show();
    }
    @Override
    public void onBackPressed()
    {
        if(keepLoggedIn())
        {
            Intent intent = new Intent(ScanQRActivity.this,RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Intent intent = new Intent(ScanQRActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    boolean keepLoggedIn()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("DigiReceipt",MODE_PRIVATE);
        boolean check = sharedPreferences.getBoolean("kLoggedIn",false);
        long session = sharedPreferences.getLong("session",0);
        //172800000 = 48hrs
        if(check && (System.currentTimeMillis() - session) <= 172800000)
        {
            return true;
        }
        else if ((System.currentTimeMillis() - session) > 172800000 && session != 0)
            Toast.makeText(ScanQRActivity.this,"Session expired, log in again",Toast.LENGTH_LONG).show();
        return false;
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
                intent = new Intent(ScanQRActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuHistory:
                intent = new Intent(ScanQRActivity.this,HistoryActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuScanQR:
                intent = new Intent(ScanQRActivity.this,ScanQRActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuAbout:
                intent = new Intent(ScanQRActivity.this,AboutActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return true;
    }
}
