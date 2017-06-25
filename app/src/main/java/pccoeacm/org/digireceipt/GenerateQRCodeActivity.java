package pccoeacm.org.digireceipt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GenerateQRCodeActivity extends AppCompatActivity {
    Intent intent;
    String iD;
    Participant participant;
    ImageView imageViewQrCode;
    Button buttonQRCodeDone, buttonQRCodeEmail;
    QRGEncoder qrgEncoder;
    Bitmap qRBitmap,finalBitmap;
    File file;
    FileOutputStream outputStream;
    String path;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);
        intent = getIntent();
        if(intent != null)
        {
            iD = intent.getStringExtra("iD");
            participant = (Participant) intent.getSerializableExtra("participant");
        }
        imageViewQrCode = (ImageView)findViewById(R.id.imageViewQRCode);
        buttonQRCodeDone = (Button)findViewById(R.id.buttonQRCodeDone);
        buttonQRCodeEmail = (Button)findViewById(R.id.buttonQRCodeEmail);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        qrgEncoder = new QRGEncoder(iD,null, QRGContents.Type.TEXT,300);
        try {
            qRBitmap = qrgEncoder.encodeAsBitmap();
            //imageViewQrCode.setImageBitmap(qRBitmap);
        }
        catch (Exception e)
        {
            Log.d("Exception : ",e.toString());
        }
        finalBitmap = genearateFinalBitmap();
        imageViewQrCode.setImageBitmap(finalBitmap);
        saveImageToDisk(finalBitmap);
        buttonQRCodeDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imageViewQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareImage();
            }
        });
        buttonQRCodeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });
        Toast.makeText(GenerateQRCodeActivity.this,"Tap on image to share.",Toast.LENGTH_SHORT).show();


    }
    void sendEmail()
    {
        String email = "pccoespectrum17@gmail.com";
        final String password = "pccoe@pcet17";
        String body = "Hello " + participant.getName1() + ", \n" +
                "Thank you for registering. PCCOE Spectrum 2017 will be held on 26th of March." +
                "\n\nHere are all the details : " +
                "\nMember 1: " + participant.getName1() +
                "\nMember 2: " + participant.getName2() +
                "\nMember 3: " + participant.getName3() +
                "\nEvent(s): " + convertListToString(participant.getEvents()) +
                "\nAmount paid: Rs " + participant.getTotalPayment() +
                "\nPlease find your receipt attached as a PNG file to this email. Make sure you have this digital receipt with you on the day of the event." +
                "\n\nFeel free to reach out in case of any queries at : " + email +
                "\n\nCheers, \nTeam Spectrum 2017";
        BackgroundMail.newBuilder(GenerateQRCodeActivity.this)
                .withUsername(email)
                .withPassword(password)
                .withMailto(participant.getEmail() + "," + email)
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("PCCOE Spectrum 2017 digital receipt")
                .withBody(body)
                .withAttachments(file.getAbsolutePath())
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        Map<String,Object> map = new HashMap<>();
                        participant.setEmailSent(true);
                        map.put("emailSent",participant.isEmailSent());
                        databaseReference.child("participant").child(iD).updateChildren(map);
                        //Toast.makeText(GenerateQRCodeActivity.this,"Email sent to participant.",Toast.LENGTH_LONG).show();
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        //Toast.makeText(GenerateQRCodeActivity.this,"Email sending FAILED.",Toast.LENGTH_LONG).show();
                    }
                })
                .send();
    }
    void saveImageToDisk(Bitmap bitmap)
    {
        try {

            path = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
            path = path + "/PCCOE";

            //Log.d("Filepath : ",path);
            file = new File(path);
            if(!file.isDirectory()){
                //Log.d("Storage","In if");
                file.mkdir();}
            //file.mkdirs();
            file = new File(path,iD + ".png");
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    Bitmap genearateFinalBitmap()
    {
        Bitmap bitmap = Bitmap.createBitmap(400,400,qRBitmap.getConfig());
        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inScaled = true;
        Bitmap bitmapLogo = BitmapFactory.decodeResource(GenerateQRCodeActivity.this.getResources(),R.drawable.name,null);

        bitmapLogo = bitmapLogo.createScaledBitmap(bitmapLogo,266,80,false);
        for(int i = 0;i < 400;i++)
        {
            for(int j = 0;j < 400;j++)
                bitmap.setPixel(i,j,Color.WHITE);
        }
        Canvas canvas = new Canvas(bitmap);
        TextPaint paint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(25);
        paint.setTextAlign(Paint.Align.CENTER);
        //paint.setFakeBoldText(true);
        paint.setTextSize(18);

        canvas.drawBitmap(qRBitmap,50,50,null);
        canvas.drawBitmap(bitmapLogo,67,5,new Paint(Paint.ANTI_ALIAS_FLAG));
        //canvas.drawText("Techlligent 2017",200,40,paint);
        paint.setFakeBoldText(false);
        paint.setStrokeWidth(20);
        paint.setTextSize(15);
        //canvas.drawText("website.com",200,70,paint);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Name : " + participant.getName1(),50,335,paint);
        canvas.drawText("Events : " + convertListToString(participant.getEvents()),50,355,paint);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Visit us : pccoespectrum17.com",200,380,paint);
        return  bitmap;
    }
    void shareImage()
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(intent,"Share via"));
    }

    String convertListToString(List<String> list)
    {
        String s = null;
        s = list.get(0);

        for(int i = 1;i < list.size();i++)
        {
            s = s + ", " + list.get(i);
        }
        return s;
    }
    @Override
    public void onBackPressed()
    {
        if(keepLoggedIn())
        {
            Intent intent = new Intent(GenerateQRCodeActivity.this,RegisterActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Intent intent = new Intent(GenerateQRCodeActivity.this,MainActivity.class);
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
            Toast.makeText(GenerateQRCodeActivity.this,"Session expired, log in again",Toast.LENGTH_LONG).show();
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
                intent = new Intent(GenerateQRCodeActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuHistory:
                intent = new Intent(GenerateQRCodeActivity.this,HistoryActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuScanQR:
                intent = new Intent(GenerateQRCodeActivity.this,ScanQRActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menuAbout:
                intent = new Intent(GenerateQRCodeActivity.this,AboutActivity.class);
                startActivity(intent);
                finish();
                return true;
        }
        return true;
    }
}
