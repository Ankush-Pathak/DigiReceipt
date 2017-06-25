package pccoeacm.org.digireceipt;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    ListView listViewHistory;
    FirebaseDatabase firebaseDatabase;
    //DatabaseReference databaseReference;
    HistoryListAdapter historyListAdapter;
    SharedPreferences sharedPreferences;
    ArrayList<Participant> participantArrayList;
    ArrayList<String> keyList;
    Dialog dialog;
    Query query;
    ChildEventListener childEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initialise();
    }
    void initialise()
    {
        listViewHistory = (ListView)findViewById(R.id.listViewHistory);
        sharedPreferences = getSharedPreferences("DigiReceipt",MODE_PRIVATE);
        participantArrayList = new ArrayList<>();
        keyList = new ArrayList<>();

        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference();
        query = databaseReference.child("participant").orderByChild("username").equalTo(sharedPreferences.getString("username","NA"));
        //Log.d("History Firebase : ",query.toString());
        //Log.d("Username : ",sharedPreferences.getString("username","NA"));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.d("History Firebase : ",dataSnapshot.toString());
                addDataToList(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    void addDataToList(DataSnapshot dataSnapshot)
    {
        for(DataSnapshot data : dataSnapshot.getChildren())
        {
            participantArrayList.add(data.getValue(Participant.class));
            keyList.add(data.getKey());
        }

        historyListAdapter = new HistoryListAdapter(HistoryActivity.this,participantArrayList,keyList);
        listViewHistory.setAdapter(historyListAdapter);
        listViewHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int j = i;
                dialog = new Dialog(HistoryActivity.this);
                dialog.setContentView(R.layout.dialog_history_activity);
                dialog.setTitle("Details");
                Button buttonGenQR, buttonOk;
                TextView textViewName1,textViewName2,textViewName3,textViewPhone,textViewInstitute,textViewEmailSent,textViewAttendance;
                ListView listView = (ListView)dialog.findViewById(R.id.listViewHDEvents);
                buttonGenQR = (Button)dialog.findViewById(R.id.buttonHDGenQR);
                buttonOk = (Button)dialog.findViewById(R.id.buttonHDOk);

                textViewName1 = (TextView)dialog.findViewById(R.id.textViewHDName1);
                textViewName1.setText("Member 1 : " + participantArrayList.get(i).getName1());

                textViewName2 = (TextView)dialog.findViewById(R.id.textViewHDName2);
                textViewName2.setText("Member 2 : " + participantArrayList.get(i).getName2());

                textViewName3 = (TextView)dialog.findViewById(R.id.textViewHDName3);
                textViewName3.setText("Member 3 : " + participantArrayList.get(i).getName3());

                textViewPhone = (TextView)dialog.findViewById(R.id.textViewHDPhone);
                textViewPhone.setText("Phone : " + participantArrayList.get(i).getPhoneNo());

                textViewInstitute = (TextView)dialog.findViewById(R.id.textViewHDInstitute);
                textViewInstitute.setText("Institute : " + participantArrayList.get(i).getInsitute());

                textViewEmailSent = (TextView)dialog.findViewById(R.id.textViewHDEmailSent);
                if(participantArrayList.get(i).isEmailSent()) {
                    textViewEmailSent.setText("Notification email : Sent");

                }
                else {
                    textViewEmailSent.setText("Notification email : Not sent");
                    textViewEmailSent.setTextColor(Color.RED);
                }

                textViewAttendance = (TextView)dialog.findViewById(R.id.textViewHDAttnd);
                if(participantArrayList.get(i).getAttdnc())
                    textViewAttendance.setText("Attendance Status : Marked");
                else
                    textViewAttendance.setText("Attendance Status : Not marked");

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(HistoryActivity.this,android.R.layout.simple_list_item_1,participantArrayList.get(i).getEvents());
                listView.setAdapter(arrayAdapter);

                buttonGenQR.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(HistoryActivity.this,GenerateQRCodeActivity.class);
                        intent.putExtra("participant",participantArrayList.get(j));
                        intent.putExtra("iD",keyList.get(j));
                        dialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                });
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        System.gc();
    }
    @Override
    public void onBackPressed()
    {
        if(keepLoggedIn())
        {
            Intent intent = new Intent(HistoryActivity.this,RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Intent intent = new Intent(HistoryActivity.this,MainActivity.class);
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
            Toast.makeText(HistoryActivity.this,"Session expired, log in again",Toast.LENGTH_LONG).show();
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
                intent = new Intent(HistoryActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuHistory:
                intent = new Intent(HistoryActivity.this,HistoryActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuScanQR:
                intent = new Intent(HistoryActivity.this,ScanQRActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuAbout:
                intent = new Intent(HistoryActivity.this,AboutActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return true;
    }
}
