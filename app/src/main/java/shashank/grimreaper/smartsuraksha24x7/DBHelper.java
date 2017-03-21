package shashank.grimreaper.smartsuraksha24x7;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Shashank on 06-03-2017.
 */



public class DBHelper extends SQLiteOpenHelper {
    //Database Values
    private static final String DB_NAME="PersonalSafety.db";
    private static int DATABASE_VERSION=1;
    private Context context;

    //UserInfo Table Values
    private static final String TABLE_NAME="UserInfo";
    private static final String P_KEY="_userID";
    private static final String NAME="Name";
    private static final String BLOOD_GROUP="BloodGroup";
    private static final String ADDRESS="Address";
    private static final String DIABETIC="Diabetic";
    private static final String ALLERGIES="Allergies";
    private static final String PERSONAL_DOCTOR_NO="PersonalDoctorNo";
    private static final String INSURANCE_COMPANY="InsuranceCompany";
    private static final String INSURANCE_ID="InsuranceID";

    //EmergencyContact Table Values
    private static final String TABLE_NAME1="EmergencyContacts";
    private static final String CONTACT_ID="_contactID";
    private static final String CONTACT_NAME="Name";
    private static final String CONTACT_NUMBER="Number";

    //Queries for UserInfo Table
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("+P_KEY+" INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME+" VARCHAR(255), "+BLOOD_GROUP+" VARCHAR(5), "+ ADDRESS+" VARCHAR(255), "+ DIABETIC+ " INTEGER, "
            + ALLERGIES + " VARCHAR(255), "+PERSONAL_DOCTOR_NO+" VARCHAR(15), "
            +INSURANCE_COMPANY+" VARCHAR(255), "+INSURANCE_ID+" VARCHAR(255));";

    private static final String DROP_TABLE="DROP TABLE IF EXISTS "+ TABLE_NAME+" ;";

    //Queries for EmergencyContact Table
    private static final String CREATE_TABLE1="CREATE TABLE "+TABLE_NAME1+" ("+CONTACT_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            CONTACT_NAME+" VARCHAR(255), " +CONTACT_NUMBER+" VARCHAR(15));";


    private static final String DROP_TABLE1="DROP TABLE IF EXISTS "+TABLE_NAME1+" ;";
    //Constructor
    DBHelper(Context context) {
        super(context,DB_NAME,null,DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(CREATE_TABLE);
            Log.d("UserInfoDBTable","UserInfo TABLE CREATED");
            sqLiteDatabase.execSQL(CREATE_TABLE1);
            Log.d("EmergencyContactsTable","EmergencyContacts TABLE CREATED");
        }
        catch (Exception e) {
            Log.d("CreateTableError",e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        try {
            DATABASE_VERSION++;
            sqLiteDatabase.execSQL(DROP_TABLE);
            sqLiteDatabase.execSQL(DROP_TABLE1);
            onCreate(sqLiteDatabase);
        }
        catch(Exception e) {
            Log.d("onUpgrade",e.getMessage());
        }
    }
}
