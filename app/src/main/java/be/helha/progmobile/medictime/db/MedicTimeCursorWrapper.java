package be.helha.progmobile.medictime.db;

import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import be.helha.progmobile.medictime.models.MedicTimeDataAccessObject;
import be.helha.progmobile.medictime.models.Medicine;
import be.helha.progmobile.medictime.models.Prescription;

public class MedicTimeCursorWrapper extends CursorWrapper {

    private MedicTimeDataAccessObject mMedicTimeDataAccessObject;
    public MedicTimeCursorWrapper(Cursor cursor, Context context){
        super(cursor);
        mMedicTimeDataAccessObject = MedicTimeDataAccessObject.getInstance(context);
    }

    public Medicine getMedicine(){
        String id = getString(getColumnIndex(MedicTimeDbSchema.MedicineTable.cols.MEDICINE_ID));
        String name = getString(getColumnIndex(MedicTimeDbSchema.MedicineTable.cols.NAME));
        int duration = getInt(getColumnIndex(MedicTimeDbSchema.MedicineTable.cols.DURATION));
        int morningIntake = getInt(getColumnIndex(MedicTimeDbSchema.MedicineTable.cols.MORNING));
        int noonIntake = getInt(getColumnIndex(MedicTimeDbSchema.MedicineTable.cols.NOON));
        int eveningIntake = getInt(getColumnIndex(MedicTimeDbSchema.MedicineTable.cols.EVENING));

        return new Medicine(UUID.fromString(id), name, duration,
                morningIntake!=0, noonIntake!=0, eveningIntake!=0);
    }

    public Prescription getPrescription(){
        SimpleDateFormat dateFormat = new SimpleDateFormat(MedicTimeDataAccessObject.DATE_FORMAT);

        String id = getString(getColumnIndex(MedicTimeDbSchema.PrescriptionTable.cols.PRESCRIPTION_ID));
        String startDate = getString(getColumnIndex(MedicTimeDbSchema.PrescriptionTable.cols.START_DATE));
        String endDate = getString(getColumnIndex(MedicTimeDbSchema.PrescriptionTable.cols.END_DATE));
        int morningIntake = getInt(getColumnIndex(MedicTimeDbSchema.PrescriptionTable.cols.MORNING));
        int noonIntake = getInt(getColumnIndex(MedicTimeDbSchema.PrescriptionTable.cols.NOON));
        int eveningIntake = getInt(getColumnIndex(MedicTimeDbSchema.PrescriptionTable.cols.EVENING));
        String medicineId = getString(getColumnIndex(MedicTimeDbSchema.PrescriptionTable.cols.MEDICINE_ID_FK));

        Date prescriptionStartDate;
        Date prescriptionEndDate;
        try {
            prescriptionStartDate = dateFormat.parse(startDate);
            prescriptionEndDate = dateFormat.parse(endDate);
        } catch (ParseException e) {
            Log.e("MedicTime Cursor Wrapper","Date parsing error, start and end dates will be set as today");
            e.printStackTrace();
            prescriptionStartDate = new Date();
            prescriptionEndDate = new Date();
        }

        return new Prescription(UUID.fromString(id), prescriptionStartDate, prescriptionEndDate,
                morningIntake !=0, noonIntake!=0, eveningIntake!=0,
                mMedicTimeDataAccessObject.getMedicine(medicineId));
    }
}
