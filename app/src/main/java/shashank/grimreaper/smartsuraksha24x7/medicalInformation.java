package shashank.grimreaper.smartsuraksha24x7;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MedicalInformation extends AppCompatActivity {
    SQLiteDatabase db;
    Button saveInfo;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    EditText name,bloodGroup,address,diabetic,allergies,personalDoctorNo,insuranceCompanyName,insuranceId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medical_information);
        Toolbar toolbar = (Toolbar)findViewById(R.id.mytoolbar3);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        name = (EditText)findViewById(R.id.name);
        bloodGroup = (EditText)findViewById(R.id.blood_group);
        address = (EditText)findViewById(R.id.address);
        diabetic = (EditText)findViewById(R.id.diabetic_val);
        allergies = (EditText)findViewById(R.id.allergies_val);
        personalDoctorNo = (EditText)findViewById(R.id.personal_doctor_no);
        insuranceCompanyName = (EditText)findViewById(R.id.insurance_company_name);
        insuranceId = (EditText)findViewById(R.id.insurance_id_val);
        name.setText("");
        bloodGroup.setText("");
        address.setText("");
        diabetic.setText("");
        allergies.setText("");
        personalDoctorNo.setText("");
        insuranceCompanyName.setText("");
        insuranceId.setText("");
        DBHelper helper = new DBHelper(this);
        db = helper.getWritableDatabase();


        boolean dataEntered = sp.getBoolean("dataEntered",false);
        if(dataEntered)
            getMedticalInfo();

        saveInfo = (Button) findViewById(R.id.saveInfo);
        saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put("Name",name.getText().toString());
                values.put("BloodGroup",bloodGroup.getText().toString());
                values.put("Address",address.getText().toString());
                values.put("Diabetic",diabetic.getText().toString());
                values.put("Allergies",allergies.getText().toString());
                values.put("PersonalDoctorNo",personalDoctorNo.getText().toString());
                values.put("InsuranceCompany",insuranceCompanyName.getText().toString());
                values.put("InsuranceID",insuranceId.getText().toString());
                db.insert("UserInfo", null, values);
                editor.putBoolean("dataEntered",true);
                editor.commit();
                Toast.makeText(MedicalInformation.this,"data saved",Toast.LENGTH_LONG).show();
            }
        });
    }


    public void getMedticalInfo(){
        Cursor cursor = db.rawQuery("select * from UserInfo",null);
        if(cursor != null && cursor.moveToFirst()) {
            if (cursor.getCount() > 0) {
                while(cursor.moveToNext()) {
                    String nameVal = cursor.getString(cursor.getColumnIndex("Name"));
                    String bloodGroupVal = cursor.getString(cursor.getColumnIndex("BloodGroup"));
                    String addressVal = cursor.getString(cursor.getColumnIndex("Address"));
                    String diabeticVal = cursor.getString(cursor.getColumnIndex("Diabetic"));
                    String allergiesVal = cursor.getString(cursor.getColumnIndex("Allergies"));
                    String personalDoctorNoVal = cursor.getString(cursor.getColumnIndex("PersonalDoctorNo"));
                    String insuranceCompanyVal = cursor.getString(cursor.getColumnIndex("InsuranceCompany"));
                    String insuranceIdVal = cursor.getString(cursor.getColumnIndex("InsuranceID"));
                    name.setText(nameVal);
                    bloodGroup.setText(bloodGroupVal);
                    address.setText(addressVal);
                    diabetic.setText(diabeticVal);
                    allergies.setText(allergiesVal);
                    personalDoctorNo.setText(personalDoctorNoVal);
                    insuranceCompanyName.setText(insuranceCompanyVal);
                    insuranceId.setText(insuranceIdVal);
                }
            }
        }
    }
}
