package pccoeacm.org.digireceipt;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ankush on 1/15/2017.
 */
//So that setPersistanceEnabled is called only once
public class DigiReceipt extends Application{
    @Override
    public void onCreate()
    {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
