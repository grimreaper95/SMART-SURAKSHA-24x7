package shashank.grimreaper.smartsuraksha24x7;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Shashank on 06-03-2017.
 */


class MyContactsAdapter extends ArrayAdapter<Contact> {
    Context context;
    List<Contact> contactList;
    LayoutInflater mInflater;
    private  SparseBooleanArray mSelectedItemsIds;

    public MyContactsAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
        mSelectedItemsIds = new  SparseBooleanArray();
        this.context = context;
        this.contactList = objects;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_contact, parent, false);
        }
        TextView contactName = (TextView) convertView.findViewById(R.id.contact_name);
        contactName.setText(contactList.get(position).name);
        TextView contactPhno = (TextView) convertView.findViewById(R.id.contact_phno);
        contactPhno.setText(contactList.get(position).phno);
        return convertView;
    }



    @Override
    public void remove(Contact object) {
        super.remove(object);
        notifyDataSetChanged();
    }
    public void  removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public  List<Contact> getMyList() {
        return contactList;
    }

    public void  toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void selectView(int position, boolean value) {
        if (value) {
            mSelectedItemsIds.put(position, value);
        }
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }
    public  SparseBooleanArray getSelectedItemIds() {
        return mSelectedItemsIds;
    }
}

