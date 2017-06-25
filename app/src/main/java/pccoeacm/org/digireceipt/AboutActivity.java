package pccoeacm.org.digireceipt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {
    TextView textView;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        textView = (TextView)findViewById(R.id.textViewAboutFor);
        button = (Button)findViewById(R.id.buttonAboutReport);

        textView.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/tnri.ttf"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "pccoetechlligent2017@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "DigiReceipt bug report");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Erase this and write here");
//emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text

                startActivity(Intent.createChooser(emailIntent, "Email client"));
            }
        });

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
                intent = new Intent(AboutActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuHistory:
                intent = new Intent(AboutActivity.this,HistoryActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuScanQR:
                intent = new Intent(AboutActivity.this,ScanQRActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuAbout:
                intent = new Intent(AboutActivity.this,AboutActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return true;
    }
    @Override
    public void onBackPressed()
    {
        if(keepLoggedIn())
        {
            Intent intent = new Intent(AboutActivity.this,RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Intent intent = new Intent(AboutActivity.this,MainActivity.class);
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
            Toast.makeText(AboutActivity.this,"Session expired, log in again",Toast.LENGTH_LONG).show();
        return false;
    }
}
