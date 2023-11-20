package be.helha.progmobile.medictime.views;

import static be.helha.progmobile.medictime.views.fragments.TimeOfDayCheckBoxesFragment.createBundleOfCheckBoxesValues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import be.helha.progmobile.medictime.R;
import be.helha.progmobile.medictime.db.MedicTimeDbSchema;
import be.helha.progmobile.medictime.models.MedicTimeDataAccessObject;
import be.helha.progmobile.medictime.models.Medicine;
import be.helha.progmobile.medictime.models.Prescription;
import be.helha.progmobile.medictime.views.fragments.DatePickerFragment;
import be.helha.progmobile.medictime.views.fragments.TimeOfDayCheckBoxesFragment;

public class PrescriptionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public static final String KEY_PRESCRIPTION_ID = "KEY_PRESCRIPTION_ID";
    private MedicTimeDataAccessObject mMedicTimeDataAccessObject;
    private Prescription mPrescription;
    private boolean mPickingBeginningDate;
    private boolean mEditMode;
    private Medicine selectedMedicine;

    private ImageButton mImageButtonBeginningDate;
    private ImageButton mImageButtonEndDate;
    private FloatingActionButton mFloatingButtonAddMedicine;
    private TextView mTextViewBeginningDate;
    private TextView mTextViewEndDate;
    private Spinner mSpinnerMedicine;
    private Button mButtonValidate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViewElementsToVariables();
        mMedicTimeDataAccessObject = MedicTimeDataAccessObject.getInstance(getApplicationContext());
        populateSpinnerAndSetEvents();
        addFragmentWithPrescriptionData();
        setListenersOnDatePickerTriggers();

        mButtonValidate.setOnClickListener((view)->{
            Log.d("Presc Acti",
                    "Id: " + mPrescription.getPrescriptionId().toString() +
                    " start: " + mPrescription.getPrescriptionStartDate().toString() +
                    " end: " + mPrescription.getPrescriptionEndDate().toString() +
                    " Morning intake: " + mPrescription.isPrescriptionMorningIntake() +
                    " Noon intake: " + mPrescription.isPrescriptionNoonIntake() +
                    " Evening intake: " + mPrescription.isPrescriptionEveningIntake() +
                    " Medicine name: " + mPrescription.getPrescriptionMedicine().getMedicineName());
            //TODO:Test if update is fully functional
            if(mEditMode)
                mMedicTimeDataAccessObject.updatePrescription(mPrescription);
            else
                mMedicTimeDataAccessObject.addPrescription(mPrescription);
        });

        //TODO: get result from addmedicine activity to update the CursorAdapter with medicineSpinnerAdapter.changerCursor(getMedicineListCursorAdapter());
        mFloatingButtonAddMedicine.setOnClickListener((view) -> {
            Intent addMedicineIntent = new Intent(this, AddMedicineActivity.class);
            startActivity(addMedicineIntent);
        });
    }

    private void addFragmentWithPrescriptionData() {
        String prescriptionId = getIntent().getStringExtra(KEY_PRESCRIPTION_ID);
        if (prescriptionId != null){
            mPrescription = mMedicTimeDataAccessObject.getPrescription(prescriptionId);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle mCheckBoxFragmentBundleArgs;
        if (mPrescription != null) {
            //We have selected a prescription, we set values of the fragment's checkboxes to the that prescription
            mEditMode = true;
            mCheckBoxFragmentBundleArgs = createBundleOfCheckBoxesValues(
                    mPrescription.isPrescriptionMorningIntake(),
                    mPrescription.isPrescriptionNoonIntake(),
                    mPrescription.isPrescriptionEveningIntake()
            );
            ((TextView)findViewById(R.id.text_view_add_prescription)).setText(R.string.edit_prescription);
            //TODO: remettre dans le spinner le médicament de la prescription
            mTextViewBeginningDate.setText(new SimpleDateFormat("d/M/yyyy").format(mPrescription.getPrescriptionStartDate()));
            mTextViewEndDate.setText(new SimpleDateFormat("d/M/yyyy").format(mPrescription.getPrescriptionEndDate()));
        } else {
            //Create a new prescription and set values of the fragment's checkboxes to all false
            mEditMode = false;
            mCheckBoxFragmentBundleArgs = createBundleOfCheckBoxesValues(false, false, false);
            Log.d("coucou", "je t'ennuie pas");
            mPrescription = new Prescription();
        }
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container_times_of_day_checkboxes, TimeOfDayCheckBoxesFragment.class, mCheckBoxFragmentBundleArgs)
                .commit();

        fragmentManager.setFragmentResultListener(TimeOfDayCheckBoxesFragment.KEY_FRAGMENT_TIMES_OF_DAY_RESULT,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        mPrescription.setIntakes(result.getBoolean(TimeOfDayCheckBoxesFragment.KEY_MORNING),
                                result.getBoolean(TimeOfDayCheckBoxesFragment.KEY_NOON),
                                result.getBoolean(TimeOfDayCheckBoxesFragment.KEY_EVENING));
                        Log.d("Prescription", String.valueOf(result.getBoolean(TimeOfDayCheckBoxesFragment.KEY_MORNING)) +
                                String.valueOf(result.getBoolean(TimeOfDayCheckBoxesFragment.KEY_NOON)) +
                                String.valueOf(result.getBoolean(TimeOfDayCheckBoxesFragment.KEY_EVENING)));
                    }
                });
    }

    private void setListenersOnDatePickerTriggers() {
        mImageButtonBeginningDate.setOnClickListener((view) -> {
            mPickingBeginningDate = true;
            showDatePicker();
        });

        mImageButtonEndDate.setOnClickListener((view) -> {
            mPickingBeginningDate = false;
            showDatePicker();
        });

        mTextViewBeginningDate.setOnClickListener((view) -> {
            mPickingBeginningDate = true;
            showDatePicker();
        });

        mTextViewEndDate.setOnClickListener((view) -> {
            mPickingBeginningDate = false;
            showDatePicker();
        });
    }

    private void bindViewElementsToVariables() {
        setContentView(R.layout.activity_prescription);

        mImageButtonBeginningDate = findViewById(R.id.image_button_pick_beginning_date);
        mImageButtonEndDate = findViewById(R.id.image_button_pick_end_date);
        mTextViewBeginningDate = findViewById(R.id.text_view_date_beginning);
        mTextViewEndDate = findViewById(R.id.text_view_date_end);
        mSpinnerMedicine = findViewById(R.id.spinner_medicine);
        mFloatingButtonAddMedicine = findViewById(R.id.floating_button_add_medicine);
        mButtonValidate = findViewById(R.id.button_validate);
    }

    private void populateSpinnerAndSetEvents() {
        CursorAdapter medicineSpinnerAdapter = mMedicTimeDataAccessObject.getMedicineListCursorAdapter();
        mSpinnerMedicine.setAdapter(medicineSpinnerAdapter);

        mSpinnerMedicine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TODO:In editMode, we should not replace the fragment because that would reset the intake values of the prescription
                //Since "onItemSelected" is called when the activity starts, skip the first call with a local boolean
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String selectedMedicineID = cursor.getString(cursor.getColumnIndexOrThrow(MedicTimeDbSchema.MedicineTable.cols.MEDICINE_ID));
                selectedMedicine = mMedicTimeDataAccessObject.getMedicine(selectedMedicineID);
                mPrescription.setPrescriptionMedicine(selectedMedicine);
                Log.d("item selected", selectedMedicine.getMedicineId().toString());
                Bundle medicineIntakeValues = createBundleOfCheckBoxesValues(
                        mPrescription.getPrescriptionMedicine().isMedicineMorningIntake(),
                        mPrescription.getPrescriptionMedicine().isMedicineNoonIntake(),
                        mPrescription.getPrescriptionMedicine().isMedicineEveningIntake());
                Log.d("coucou", "je t'ennuie");
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_times_of_day_checkboxes, TimeOfDayCheckBoxesFragment.class, medicineIntakeValues)
                        .commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showDatePicker() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, dayOfMonth);
        String date = getDateString(calendar);
        if (mPickingBeginningDate) {
            mTextViewBeginningDate.setText(date);
            mPrescription.setPrescriptionStartDate(calendar.getTime());

            calendar.add(Calendar.DAY_OF_MONTH, mPrescription.getPrescriptionMedicine().getMedicineDuration());
            mPrescription.setPrescriptionEndDate(calendar.getTime());
            mTextViewEndDate.setText(getDateString(calendar));
        } else {
            mTextViewEndDate.setText(date);
            mPrescription.setPrescriptionEndDate(calendar.getTime());
        }
    }

    @NonNull
    private static String getDateString(GregorianCalendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
    }
}