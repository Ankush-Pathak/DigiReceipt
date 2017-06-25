package pccoeacm.org.digireceipt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Ankush on 1/14/2017.
 */

public class EventListAdapter extends ArrayAdapter<Event> {
    Context context;
    ArrayList<Event> list;
    LayoutInflater inflater;
    ArrayList<Boolean> check;
    public EventListAdapter(Context context, ArrayList<Event> list)
    {
        super(context,0,list);
        this.context  = context;
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        check = new ArrayList<>(list.size());
        for(int i = 0;i < list.size();i++)
            check.add(false);

    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final Event event = getItem(position);
        View listItem = inflater.inflate(R.layout.eventlistitem,null,true);
        final CheckBox checkBox = (CheckBox)listItem.findViewById(R.id.checkBoxEventListName);
        Button button = (Button)listItem.findViewById(R.id.buttonEventListInfo);
        checkBox.setText(event.getName() + " - Rs." + event.getRegFee());
        checkBox.setChecked(check.get(position));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setTitle("Info");
                alertDialogBuilder.setMessage(event.getInfo());
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                     dialogInterface.dismiss();
                    }
                });
                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                check.set(position,b);
                if(b)
                {
                    ((RegisterActivity)context).increment(event.getRegFee(),event.getName());

                }
                else
                {
                    ((RegisterActivity)context).decrement(event.getRegFee(),event.getName());
                }
            }
        });

        return  listItem;
    }

}
/*public class UsersAdapter extends ArrayAdapter<User> {
    public UsersAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
        // Populate the data into the template view using the data object
        tvName.setText(user.name);
        tvHome.setText(user.hometown);
        // Return the completed view to render on screen
        return convertView;
    }
}*/