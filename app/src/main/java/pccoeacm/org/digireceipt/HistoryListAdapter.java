package pccoeacm.org.digireceipt;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Ankush on 1/23/2017.
 */

public class HistoryListAdapter extends ArrayAdapter<Participant> {
    Context context;
    ArrayList<Participant> participantArrayList;
    ArrayList<String> keyList;
    LayoutInflater inflater;
    public HistoryListAdapter(Context context, ArrayList<Participant> participantArrayList,ArrayList<String> keyList) {
        super(context,0,participantArrayList);
        this.context = context;
        this.participantArrayList = participantArrayList;
        this.keyList = keyList;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Participant participant = getItem(position);
        View listItem = inflater.inflate(R.layout.item_history_list, null, true);
        TextView textViewName,textViewPhone,textViewEmailSent;
        textViewName = (TextView)listItem.findViewById(R.id.textViewHLName);
        textViewPhone = (TextView)listItem.findViewById(R.id.textViewHLPhone);
        textViewEmailSent = (TextView)listItem.findViewById(R.id.textViewHLEmail);
        textViewName.setText("Name : " + participant.getName1());
        textViewPhone.setText("Events : " + convertListToString(participant.getEvents()));
        if(participant.isEmailSent())
            textViewEmailSent.setText("Notification email : Sent");
        else {
            textViewEmailSent.setText("Notification email : Not sent");
            textViewEmailSent.setTextColor(Color.RED);
        }
        return listItem;
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
}
