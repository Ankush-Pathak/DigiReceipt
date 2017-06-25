package pccoeacm.org.digireceipt;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PaymentActivity extends AppCompatActivity {
    Participant participant;
    TextView textViewTotalAmt;
    RadioButton radioButtonCash,radioButtonOnline;
    Button buttonPay;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    String iD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        final Intent intent = getIntent();
        if(intent != null)
        {
            participant = (Participant) intent.getSerializableExtra("participant");
            initialise();
            buttonPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(PaymentActivity.this,android.R.style.Theme_Holo_Light_Dialog);
                    dialog.setContentView(R.layout.dialog_cash_confirm);
                    dialog.setTitle("Confirm payment ?");
                    TextView total = (TextView) dialog.findViewById(R.id.textViewCashConfirmTotal);
                    Button button = (Button)dialog.findViewById(R.id.buttonCashConfirmOk);
                    final EditText editText = (EditText)dialog.findViewById(R.id.editTextCashConfirmPass);
                    total.setText("Amount to be paid : " + participant.getTotalPayment());

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(radioButtonCash.isChecked()) {
                                makeCashPayment(dialog,editText);

                            }
                            else if(radioButtonOnline.isChecked())
                            {

                            }
                        }
                    });
                    dialog.show();
                }
            });
        }
    }
    void initialise()
    {
        textViewTotalAmt = (TextView)findViewById(R.id.textViewTotalPaymentActivity);
        radioButtonCash = (RadioButton)findViewById(R.id.radioButtonCashPayment);
        radioButtonOnline = (RadioButton)findViewById(R.id.radioButtonOnlinePayment);
        buttonPay = (Button)findViewById(R.id.buttonMakePayment);
        sharedPreferences = getSharedPreferences("DigiReceipt",MODE_PRIVATE);
        radioButtonOnline.setEnabled(false);
        radioButtonCash.setEnabled(true);
        textViewTotalAmt.setText("Total amount : Rs." + participant.getTotalPayment());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("participant");
        databaseReference.keepSynced(true);
    }
    void makeCashPayment(Dialog dialog,EditText editText)
    {
        String pass = sharedPreferences.getString("password","NA");
        if (editText.getText().toString().equals(pass)) {
            iD = databaseReference.push().getKey();
            databaseReference.child(iD).setValue(participant);
            //Log.d("Firebase","Confirm key : " + iD);
            //Log.d("Firebase","Key : " + databaseReference.getKey());
            //Log.d("Firebase","Object : " + databaseReference.toString());
            Intent intent1 = new Intent(PaymentActivity.this, GenerateQRCodeActivity.class);
            intent1.putExtra("participant", participant);
            intent1.putExtra("iD",iD);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dialog.dismiss();
            startActivity(intent1);
            finish();
        } else if (pass.equals("NA")) {
            Toast.makeText(PaymentActivity.this, "Invalid session, logout and login again.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(PaymentActivity.this, "Invalid password.", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed()
    {
        if(keepLoggedIn())
        {
            Intent intent = new Intent(PaymentActivity.this,RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Intent intent = new Intent(PaymentActivity.this,MainActivity.class);
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
            Toast.makeText(PaymentActivity.this,"Session expired, log in again",Toast.LENGTH_LONG).show();
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
                intent = new Intent(PaymentActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuHistory:
                intent = new Intent(PaymentActivity.this,HistoryActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuScanQR:
                intent = new Intent(PaymentActivity.this,ScanQRActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuAbout:
                intent = new Intent(PaymentActivity.this,AboutActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return true;
    }
}
