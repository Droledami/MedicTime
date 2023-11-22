package be.helha.progmobile.medictime.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import be.helha.progmobile.medictime.R;
import be.helha.progmobile.medictime.db.MedicTimeBaseHelper;
import be.helha.progmobile.medictime.db.MedicTimeCursorWrapper;
import be.helha.progmobile.medictime.db.MedicTimeDbSchema;

public class MedicTimeDataAccessObject {
    public static final String DATE_FORMAT = "d/M/yyyy";
    private static MedicTimeDataAccessObject sMedicTimeDataAccessObject;
    private SQLiteDatabase mDatabase;
    private Context mContext;
    private CursorAdapter cursorAdapter;

    public static MedicTimeDataAccessObject getInstance(Context context) {
        if (sMedicTimeDataAccessObject == null)
            sMedicTimeDataAccessObject = new MedicTimeDataAccessObject(context);
        return sMedicTimeDataAccessObject;
    }

    private MedicTimeDataAccessObject(Context context) {
        this.mContext = context.getApplicationContext();
        mDatabase = new MedicTimeBaseHelper(mContext).getWritableDatabase();
    }

    public CursorAdapter getMedicineListCursorAdapter() {
        //CursorAdapter needs a cursor and not a cursorWrapper so it is needed to query a cursor
        //from the database within try with resources.
        //The cursor shouldn't be closed for the adapter to show any views or any data
        Cursor cursorSpinnerMedicine = mDatabase.query(
                MedicTimeDbSchema.MedicineTable.NAME,
                new String[]{"_id", MedicTimeDbSchema.MedicineTable.cols.MEDICINE_ID,MedicTimeDbSchema.MedicineTable.cols.NAME},
                null,
                null,
                null,
                null,
                null
        );
        cursorAdapter = new CursorAdapter(mContext, cursorSpinnerMedicine, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER) {
            //Inflates the view, this is purely visual. The method "bindview" is the one that allows
            //access to the inflated layout's elements
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(R.layout.medicine_spinner_item, parent, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView textViewMedicineName = view.findViewById(R.id.text_view_medicine_item);
                String medicineName = cursor.getString(
                        cursor.getColumnIndexOrThrow(MedicTimeDbSchema.MedicineTable.cols.NAME));
                textViewMedicineName.setText(medicineName);
            }
        };
        return cursorAdapter;
    }

    public void addMedicine(Medicine medicine) {
        mDatabase.insert(MedicTimeDbSchema.MedicineTable.NAME, null, createContentValueOfMedicine(medicine));
    }

    public Medicine getMedicine(String medicineId) {
        String sqlWhereMedicine = MessageFormat.format("{0} = ?", MedicTimeDbSchema.MedicineTable.cols.MEDICINE_ID);
        try (MedicTimeCursorWrapper cursorWrapper = queryMedicine(sqlWhereMedicine, new String[]{medicineId})) {
            if (cursorWrapper.getCount() == 0)
                return null;
            cursorWrapper.moveToFirst();
            return cursorWrapper.getMedicine();
        }
    }

    public int getMedicineSpinnerId(String medicineId){
        String sqlWhereMedicineSpinnerId = MessageFormat.format("{0} = ?", MedicTimeDbSchema.MedicineTable.cols.MEDICINE_ID);
        try (MedicTimeCursorWrapper cursorWrapper = queryMedicine(sqlWhereMedicineSpinnerId, new String[]{medicineId})) {
            if (cursorWrapper.getCount() == 0)
                return -1;
            cursorWrapper.moveToFirst();
            return cursorWrapper.getMedicineSpinnerId() - 1;
        }
    }

    public int getMedicineCount() {
        try (MedicTimeCursorWrapper cursorWrapper = queryMedicine(null, null)) {
            return cursorWrapper.getCount();
        }catch (Error error) {
            error.printStackTrace();
            Log.d("MedicDAO", error.toString());
            return -1;
        }
    }

    public void updateMedicine(Medicine medicine) {
        String sqlWhereUpdateMedicine = MessageFormat.format(
                "{0} = ?", MedicTimeDbSchema.MedicineTable.cols.MEDICINE_ID);
        mDatabase.update(MedicTimeDbSchema.MedicineTable.NAME, createContentValueOfMedicine(medicine), sqlWhereUpdateMedicine,
                new String[]{String.valueOf(medicine.getMedicineId())});
    }

    public void addPrescription(Prescription prescription) {
        mDatabase.insert(MedicTimeDbSchema.PrescriptionTable.NAME, null, createContentValuesOfPrescription(prescription));
    }

    public Prescription getPrescription(String prescriptionId) {
        String sqlWherePrescription = MessageFormat.format("{0} = ?", MedicTimeDbSchema.PrescriptionTable.cols.PRESCRIPTION_ID);
        try (MedicTimeCursorWrapper cursorWrapper
                     = queryPrescriptions(sqlWherePrescription, new String[]{prescriptionId})) {
            if (cursorWrapper.getCount() == 0)
                return null;
            cursorWrapper.moveToFirst();
            return cursorWrapper.getPrescription();
        }
    }

    public List<Prescription> getAllPrescriptions() {
        List<Prescription> prescriptionList = new ArrayList<>();
        try (MedicTimeCursorWrapper cursorWrapper = queryPrescriptions(null, null)) {
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()) { //REMINDER: isAfterLast returns whether the cursor has been moved after the last row
                prescriptionList.add(cursorWrapper.getPrescription());
                cursorWrapper.moveToNext();
            }
        }
        return prescriptionList;
    }

    public void updatePrescription(Prescription prescription) {
        String sqlWhereUpdatePrescription = MessageFormat.format(
                "{0} = ?", MedicTimeDbSchema.PrescriptionTable.cols.PRESCRIPTION_ID);
        mDatabase.update(MedicTimeDbSchema.PrescriptionTable.NAME, createContentValuesOfPrescription(prescription),
                sqlWhereUpdatePrescription, new String[]{String.valueOf(prescription.getPrescriptionId())});

    }

    //REMINDER: Queries (such as the two below) return "cursors", which are essentially the rows of a query with extra steps to
    //                                  select each row.
    private MedicTimeCursorWrapper queryMedicine(String whereClause, String[] whereArgs) {
        return new MedicTimeCursorWrapper(mDatabase.query(
                MedicTimeDbSchema.MedicineTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        ), mContext);
    }

    private MedicTimeCursorWrapper queryPrescriptions(String whereClause, String[] whereArgs) {
        return new MedicTimeCursorWrapper(mDatabase.query(
                MedicTimeDbSchema.PrescriptionTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        ), mContext);
    }

    private ContentValues createContentValueOfMedicine(Medicine medicine) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MedicTimeDbSchema.MedicineTable.cols.MEDICINE_ID, medicine.getMedicineId().toString());
        contentValues.put(MedicTimeDbSchema.MedicineTable.cols.NAME, medicine.getMedicineName());
        contentValues.put(MedicTimeDbSchema.MedicineTable.cols.DURATION, medicine.getMedicineDuration());
        contentValues.put(MedicTimeDbSchema.MedicineTable.cols.MORNING, medicine.isMedicineMorningIntake() ? 1 : 0);
        contentValues.put(MedicTimeDbSchema.MedicineTable.cols.NOON, medicine.isMedicineNoonIntake() ? 1 : 0);
        contentValues.put(MedicTimeDbSchema.MedicineTable.cols.EVENING, medicine.isMedicineEveningIntake() ? 1 : 0);
        return contentValues;
    }

    private ContentValues createContentValuesOfPrescription(Prescription prescription) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        ContentValues contentValues = new ContentValues();
        contentValues.put(MedicTimeDbSchema.PrescriptionTable.cols.PRESCRIPTION_ID, prescription.getPrescriptionId().toString());
        contentValues.put(MedicTimeDbSchema.PrescriptionTable.cols.START_DATE, dateFormat.format(prescription.getPrescriptionStartDate()));
        contentValues.put(MedicTimeDbSchema.PrescriptionTable.cols.END_DATE, dateFormat.format(prescription.getPrescriptionEndDate()));
        contentValues.put(MedicTimeDbSchema.PrescriptionTable.cols.MORNING, prescription.isPrescriptionMorningIntake() ? 1 : 0);
        contentValues.put(MedicTimeDbSchema.PrescriptionTable.cols.NOON, prescription.isPrescriptionNoonIntake() ? 1 : 0);
        contentValues.put(MedicTimeDbSchema.PrescriptionTable.cols.EVENING, prescription.isPrescriptionEveningIntake() ? 1 : 0);
        contentValues.put(MedicTimeDbSchema.PrescriptionTable.cols.MEDICINE_ID_FK,
                prescription.getPrescriptionMedicine().getMedicineId().toString());
        return contentValues;
    }
}
