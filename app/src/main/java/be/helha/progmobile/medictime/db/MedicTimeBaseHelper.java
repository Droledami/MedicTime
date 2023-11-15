package be.helha.progmobile.medictime.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.MessageFormat;

public class MedicTimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "medicTimeDatabase.db";

    public MedicTimeBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(mSqlOrderCreateTableMedicine);
        db.execSQL(mSqlOrderCreateTablePrescription);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private final String mSqlOrderCreateTableMedicine = MessageFormat.format(
            "CREATE TABLE {0} (_id INTEGER PRIMARY KEY AUTOINCREMENT, {1} TEXT, {2} TEXT, {3} INTEGER, {4} INTEGER, " +
                    "{5} INTEGER, {6} INTEGER)",
            MedicTimeDbSchema.MedicineTable.NAME,//0
            MedicTimeDbSchema.MedicineTable.cols.MEDICINE_ID,//1
            MedicTimeDbSchema.MedicineTable.cols.NAME,//2
            MedicTimeDbSchema.MedicineTable.cols.DURATION,//3
            MedicTimeDbSchema.MedicineTable.cols.MORNING,//4
            MedicTimeDbSchema.MedicineTable.cols.NOON,//5
            MedicTimeDbSchema.MedicineTable.cols.EVENING//6
    );

    private final String mSqlOrderCreateTablePrescription = MessageFormat.format(
            "CREATE TABLE {0} (_id INTEGER PRIMARY KEY AUTOINCREMENT, {1} INTEGER,{2} TEXT,{3} TEXT,{4} INTEGER,{5} INTEGER," +
                    "{6} INTEGER, {7} INTEGER, FOREIGN KEY ({7}) REFERENCES {8}({9}))",
            MedicTimeDbSchema.PrescriptionTable.NAME,//0
            MedicTimeDbSchema.PrescriptionTable.cols.PRESCRIPTION_ID,//1
            MedicTimeDbSchema.PrescriptionTable.cols.START_DATE,//2
            MedicTimeDbSchema.PrescriptionTable.cols.END_DATE,//3
            MedicTimeDbSchema.PrescriptionTable.cols.MORNING,//4
            MedicTimeDbSchema.PrescriptionTable.cols.NOON,//5
            MedicTimeDbSchema.PrescriptionTable.cols.EVENING,//6
            MedicTimeDbSchema.PrescriptionTable.cols.MEDICINE_ID_FK,//7
            MedicTimeDbSchema.MedicineTable.NAME,//8
            MedicTimeDbSchema.MedicineTable.cols.MEDICINE_ID//9
    );
}
