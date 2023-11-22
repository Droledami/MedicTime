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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import be.helha.progmobile.medictime.R;
import be.helha.progmobile.medictime.db.MedicTimeDbSchema;
import be.helha.progmobile.medictime.models.MedicTimeDataAccessObject;
import be.helha.progmobile.medictime.models.Medicine;
import be.helha.progmobile.medictime.models.Prescription;
import be.helha.progmobile.medictime.views.fragments.DatePickerFragment;
import be.helha.progmobile.medictime.views.fragments.TimeOfDayCheckBoxesFragment;

public class PrescriptionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    public static final String KEY_PRESCRIPTION_ID = "KEY_PRESCRIPTION_ID";
    public static final String KEY_SKIP_SPINNER_LISTENER = "KEY_SKIP_SPINNER_LISTENER";
    private MedicTimeDataAccessObject mMedicTimeDataAccessObject;
    private Prescription mPrescription;
    private boolean mPickingBeginningDate;
    private boolean mEditMode;
    private boolean mSkipSpinnerListener = false;
    private Medicine selectedMedicine;

    private ImageButton mImageButtonBeginningDate;
    private ImageButton mImageButtonEndDate;
    private FloatingActionButton mFloatingButtonAddMedicine;
    private FloatingActionButton mFloatingButtonEditMedicine;
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
        setPrescriptionActivityValues(savedInstanceState);
        addFragmentWithPrescriptionData();
        setListenersOnDatePickerTriggers();
        setEventsOnAddAndEditButton();
        setActivityStarterOnEditMedicineButton();
        setActivityStarterOnAddMedicineButton();

        if(mEditMode){//Set values of the selection to edit. Intakes values are set in addFragmentWithPrescriptionData
            setValuesOfPrescriptionToEdit();
        }
    }

    @Override
    protected void onResume(){//onResume is used to update the spinner when we have added a new medicine
        super.onResume();
        if(mEditMode) return; //Don't change the spinner if we edited medicines when editing a prescription
        populateSpinnerAndSetEvents();
        mSpinnerMedicine.setSelection(mMedicTimeDataAccessObject.getMedicineCount()-1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(KEY_PRESCRIPTION_ID, mPrescription.getPrescriptionId().toString());
        outState.putBoolean(KEY_SKIP_SPINNER_LISTENER, mSkipSpinnerListener);
    }

    private void setPrescriptionActivityValues(Bundle savedInstanceState) {
        //Get prescription Id either from savedInstance or intent
        String prescriptionId = getIntent().getStringExtra(KEY_PRESCRIPTION_ID);
        if(savedInstanceState != null){
            mSkipSpinnerListener = savedInstanceState.getBoolean(KEY_SKIP_SPINNER_LISTENER);
            prescriptionId = savedInstanceState.getString(KEY_PRESCRIPTION_ID);
        }
        if (prescriptionId != null){
            //Then we use the Id to fetch the prescription data from the database
            mPrescription = mMedicTimeDataAccessObject.getPrescription(prescriptionId);
            mEditMode = true;
        }
        else{
            mPrescription = new Prescription();
            mEditMode = false;
        }
    }


    private void setActivityStarterOnAddMedicineButton() {
        mFloatingButtonEditMedicine.setOnClickListener((view->{
            Intent editMedicineIntent = new Intent(this, MedicineActivity.class);
            editMedicineIntent.putExtra(MedicineActivity.KEY_MEDICINE_ID, selectedMedicine.getMedicineId().toString());
            startActivity(editMedicineIntent);
        }));
    }

    private void setActivityStarterOnEditMedicineButton() {
        mFloatingButtonAddMedicine.setOnClickListener((view) -> {
            Intent addMedicineIntent = new Intent(this, MedicineActivity.class);
            startActivity(addMedicineIntent);
        });
    }

    private void setEventsOnAddAndEditButton() {
        mButtonValidate.setOnClickListener((view)->{
            if(checkIfAnEntryIsInvalid())
                return;
            if(mEditMode)
                mMedicTimeDataAccessObject.updatePrescription(mPrescription);
            else
                mMedicTimeDataAccessObject.addPrescription(mPrescription);
            finish();
        });
    }

    private boolean checkIfAnEntryIsInvalid(){
        if(mTextViewBeginningDate.getText().toString().equals("")){
            showToastIncompleteEntry();
            return true;
        }
        if(mTextViewEndDate.getText().toString().equals("")){
            showToastIncompleteEntry();
            return true;
        }
        if(mSpinnerMedicine.getAdapter().getCount()==0){
            showToastIncompleteEntry();
            return true;
        }
        if(!mPrescription.isPrescriptionMorningIntake()
                && !mPrescription.isPrescriptionNoonIntake()
                && !mPrescription.isPrescriptionEveningIntake()){
            showToastIncompleteEntry();
            return true;
        }
        if(mPrescription.getPrescriptionStartDate().after(mPrescription.getPrescriptionEndDate())){
            showToastInvalidDates();
            return true;
        }
        return false;
    }

    private void showToastIncompleteEntry() {
        Toast.makeText(this, R.string.incomplete_entry, Toast.LENGTH_SHORT).show();
    }

    private void showToastInvalidDates() {
        Toast.makeText(this, R.string.start_date_cant_be_after_end_date, Toast.LENGTH_SHORT).show();
    }

    private void setValuesOfPrescriptionToEdit() {
        mSkipSpinnerListener = true;
        String formatPattern = "d/M/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatPattern);
        String prescriptionId = mPrescription.getPrescriptionMedicine().getMedicineId().toString();

        mSpinnerMedicine.setSelection(mMedicTimeDataAccessObject.getMedicineSpinnerId(prescriptionId));
        ((TextView)findViewById(R.id.text_view_add_prescription)).setText(R.string.edit_prescription);
        mTextViewBeginningDate.setText(dateFormat.format(mPrescription.getPrescriptionStartDate()));
        mTextViewEndDate.setText(dateFormat.format(mPrescription.getPrescriptionEndDate()));
    }

    private void addFragmentWithPrescriptionData() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle mCheckBoxFragmentBundleArgs;
        if (mEditMode) {
            //We have selected a prescription, we set values of the fragment's checkboxes to the that prescription
            mCheckBoxFragmentBundleArgs = createBundleOfCheckBoxesValues(
                    mPrescription.isPrescriptionMorningIntake(),
                    mPrescription.isPrescriptionNoonIntake(),
                    mPrescription.isPrescriptionEveningIntake()
            );
        } else {
            //Create a new prescription and set values of the fragment's checkboxes to all false
            mCheckBoxFragmentBundleArgs = createBundleOfCheckBoxesValues(false, false, false);
        }
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_times_of_day_checkboxes, TimeOfDayCheckBoxesFragment.class, mCheckBoxFragmentBundleArgs)
                .commit();

        fragmentManager.setFragmentResultListener(TimeOfDayCheckBoxesFragment.KEY_FRAGMENT_TIMES_OF_DAY_RESULT,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        mPrescription.setIntakes(result.getBoolean(TimeOfDayCheckBoxesFragment.KEY_MORNING),
                                result.getBoolean(TimeOfDayCheckBoxesFragment.KEY_NOON),
                                result.getBoolean(TimeOfDayCheckBoxesFragment.KEY_EVENING));
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
        mFloatingButtonEditMedicine = findViewById(R.id.floating_action_button_edit_medicine);
        mButtonValidate = findViewById(R.id.button_validate);
    }

    private void populateSpinnerAndSetEvents() {
        CursorAdapter medicineSpinnerAdapter = mMedicTimeDataAccessObject.getMedicineListCursorAdapter();
        mSpinnerMedicine.setAdapter(medicineSpinnerAdapter);

        mSpinnerMedicine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String selectedMedicineID = cursor.getString(cursor.getColumnIndexOrThrow(MedicTimeDbSchema.MedicineTable.cols.MEDICINE_ID));
                selectedMedicine = mMedicTimeDataAccessObject.getMedicine(selectedMedicineID);
                if(mSkipSpinnerListener){
                    //Skip the first call of the listener, which is called when the activity starts. This is just to avoid setting
                    //the suggested intakes times of the medicine instead of the selected prescription's
                    mSkipSpinnerListener = false;
                    return;
                }
                Log.d("SPinner", "casse-couille");
                mPrescription.setPrescriptionMedicine(selectedMedicine);
                Bundle medicineIntakeValues = createBundleOfCheckBoxesValues(
                        mPrescription.getPrescriptionMedicine().isMedicineMorningIntake(),
                        mPrescription.getPrescriptionMedicine().isMedicineNoonIntake(),
                        mPrescription.getPrescriptionMedicine().isMedicineEveningIntake());
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

            calendar.add(Calendar.DAY_OF_MONTH, mPrescription.getPrescriptionMedicine().getMedicineDuration() - 1);//Remove one day because we count the first day in it
            mPrescription.setPrescriptionEndDate(calendar.getTime());
            mTextViewEndDate.setText(getDateString(calendar));
            Toast.makeText(this, MessageFormat.format("Ajouté la durée par défaut ({0} jours) à la date sélectionnée",
                            mPrescription.getPrescriptionMedicine().getMedicineDuration()), Toast.LENGTH_LONG).show();
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