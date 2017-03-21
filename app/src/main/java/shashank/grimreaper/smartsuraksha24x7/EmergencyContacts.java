package shashank.grimreaper.smartsuraksha24x7;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;



public class EmergencyContacts extends AppCompatActivity {
    static final int PICK_CONTACT=1;
    ListView contactListView;
    ArrayList<Contact> contactList;
    MyContactsAdapter myContactsAdapter;
    FloatingActionButton fab;
    SQLiteDatabase db;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    ImageView policeImage,medicalImage,fireImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_contacts);

        Toolbar toolbar = (Toolbar)findViewById(R.id.mytoolbar4);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        fab = (FloatingActionButton)findViewById(R.id.fab);

        //Toast.makeText(this,"Single tap a contact to make it your primary emergency contact",Toast.LENGTH_LONG).show();

        DBHelper helper = new DBHelper(this);
        db = helper.getWritableDatabase();


        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        contactList = new ArrayList<>();

        policeImage = (ImageView)findViewById(R.id.police_image);
        medicalImage = (ImageView)findViewById(R.id.medical_image);
        fireImage = (ImageView)findViewById(R.id.fire_image);

        contactList.clear();
        getContactsFromDB();

        policeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = "tel:"+"321";
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Call permission", "denied");
                    return;
                }
                Log.d("Call", "success");
                startActivity(callIntent);
            }
        });
        medicalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = "tel:"+"321";
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Call permission", "denied");
                    return;
                }
                Log.d("Call", "success");
                startActivity(callIntent);
            }
        });
        fireImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = "tel:"+"321";
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber));
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Call permission", "denied");
                    return;
                }
                Log.d("Call", "success");
                startActivity(callIntent);
            }
        });

        contactListView = (ListView)findViewById(R.id.contact_list);
        myContactsAdapter = new MyContactsAdapter(EmergencyContacts.this,0,contactList);
        contactListView.setAdapter(myContactsAdapter);

        contactListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        contactListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                final int checkedCount = contactListView.getCheckedItemCount();
                // Set the CAB title according to total checked items
                actionMode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                myContactsAdapter.toggleSelection(i);

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.multiselect_delete, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.delete:
                        AlertDialog.Builder  builder = new AlertDialog.Builder(
                                EmergencyContacts.this);
                        builder.setMessage("Do you  want to delete selected contact(s)?");
                        builder.setNegativeButton("No", new  DialogInterface.OnClickListener() {
                            @Override
                            public void  onClick(DialogInterface dialog, int which) {
                                // TODO  Auto-generated method stub
                            }
                        });
                        builder.setPositiveButton("Yes", new  DialogInterface.OnClickListener() {

                            @Override
                            public void  onClick(DialogInterface dialog, int which) {
                                // TODO  Auto-generated method stub
                                SparseBooleanArray  selected = myContactsAdapter.getSelectedItemIds();
                                String primaryEmergencyContactName = sp.getString("primaryContactName","dummy");
                                String primaryEmergencyContactPhno = sp.getString("primaryContactPhno","123");
                                for (int i =  (selected.size() - 1); i >= 0; i--) {
                                    if  (selected.valueAt(i)) {
                                        Contact selecteditem = myContactsAdapter.getItem(selected.keyAt(i));
                                        //Remove  selected items following the ids

                                        myContactsAdapter.remove(selecteditem);
                                        myContactsAdapter.notifyDataSetChanged();

                                        db.delete("EmergencyContacts","Name=?",new String[]{selecteditem.name});
                                        if(primaryEmergencyContactName.equals(selecteditem.name)){
                                            if(primaryEmergencyContactPhno.equals(selecteditem.phno)) {
                                                Toast.makeText(EmergencyContacts.this,"Primary Contact deleted",Toast.LENGTH_LONG).show();
                                                editor.putString("primaryContactName", "dummy");
                                                editor.putString("primaryContactPhno", "123");
                                                editor.commit();
                                            }
                                        }
                                    }
                                }
                                actionMode.finish();
                                selected.clear();
                            }
                        });
                        // Close CAB
                        actionMode.finish();
                        AlertDialog alert =  builder.create();
                        //alert.setIcon(R.drawable.questionicon);// dialog  Icon
                        alert.setTitle("Confirmation"); // dialog  Title
                        alert.show();
                        return true;
                    case R.id.selectAll:
                        final int checkedCount  = contactList.size();
                        // If item  is already selected or checked then remove or
                        // unchecked  and again select all
                        myContactsAdapter.removeSelection();
                        for (int i = 0; i <  checkedCount; i++) {
                            contactListView.setItemChecked(i,   true);
                        }
                        actionMode.setTitle(checkedCount  + "  Selected");
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });


        contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView contactPhno = (TextView) view.findViewById(R.id.contact_phno);
                    TextView contactName = (TextView) view.findViewById(R.id.contact_name);
                    String phno = contactPhno.getText().toString();
                    String name = contactName.getText().toString();
                    editor.putString("primaryContactName",name);
                    editor.putString("primaryContactPhno",phno);
                    editor.apply();
                    Toast.makeText(EmergencyContacts.this,name + " is now your primary emergency contact",Toast.LENGTH_LONG).show();
            }
        });




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, PICK_CONTACT);
                contactList.clear();
                myContactsAdapter.notifyDataSetChanged();
                getContactsFromDB();
            }
        });


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri contact = data.getData();
        ContentResolver cr = getContentResolver();
        Cursor c = managedQuery(contact, null, null, null, null);
        while(c.moveToNext()){
            String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
                int cnt = 0 ;
                    while (pCur.moveToNext()) {
                        String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        Toast.makeText(this, name + " " + phone + " added as an emergency contact", Toast.LENGTH_LONG).show();
                        ContentValues values1 = new ContentValues();
                        values1.put("Name", name);
                        values1.put("Number", phone);
                        db.insert("EmergencyContacts", null, values1);
                        Contact tempContact = new Contact();
                        tempContact.name = name;
                        tempContact.phno = phone;
                        contactList.add(tempContact);
                        myContactsAdapter.notifyDataSetChanged();
                        cnt++;
                        if (cnt == 1) break;
                    }
            }
        }
    }

    public void addContact(String name,String phno){
        Contact tempContact = new Contact();
        tempContact.name = name;
        tempContact.phno = phno;
        contactList.add(tempContact);
    }


    public void getContactsFromDB(){
        Log.d("contactdet","entered");
        Cursor cursor = db.rawQuery("select Name,Number from EmergencyContacts",null);
        if(cursor != null && cursor.moveToFirst()) {
            if (cursor.getCount() > 0) {
                while(cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex("Name"));
                    String phone = cursor.getString(cursor.getColumnIndex("Number"));
                    Log.d("contactdet",name);
                    Log.d("contactdet",phone);
                    addContact(name, phone);
                }
                Log.d("contactdet",cursor.getCount()+"");
            }
            else{
                Log.d("contactdet",cursor.getCount()+"");
            }
        }
        else{

            Toast.makeText(this,"No emergency contacts present",Toast.LENGTH_LONG).show();
            contactList.clear();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this,"Single tap a contact to make it your primary emergency contact",Toast.LENGTH_LONG).show();
    }
}

