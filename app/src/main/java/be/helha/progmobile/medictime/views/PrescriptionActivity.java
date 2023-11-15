package be.helha.progmobile.medictime.views;

import static be.helha.progmobile.medictime.views.fragments.TimeOfDayCheckBoxesFragment.createBundleOfCheckBoxesValues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

import be.helha.progmobile.medictime.R;
import be.helha.progmobile.medictime.models.MedicTimeDataAccessObject;
import be.helha.progmobile.medictime.models.Prescription;
import be.helha.progmobile.medictime.views.fragments.DatePickerFragment;
import be.helha.progmobile.medictime.views.fragments.TimeOfDayCheckBoxesFragment;

public class PrescriptionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private MedicTimeDataAccessObject mMedicTimeDataAccessObject;
    private Prescription mPrescription;
    private boolean mPickingBeginningDate;
    private boolean mEditMode;

    private ImageButton mImageButtonBeginningDate;
    private ImageButton mImageButtonEndDate;
    private FloatingActionButton mFloatingButtonAddMedicine;
    private TextView mTextViewBeginningDate;
    private TextView mTextViewEndDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription);

        mMedicTimeDataAccessObject = MedicTimeDataAccessObject.getInstance(getApplicationContext());

        mImageButtonBeginningDate = findViewById(R.id.image_button_pick_beginning_date);
        mImageButtonEndDate = findViewById(R.id.image_button_pick_end_date);
        mTextViewBeginningDate = findViewById(R.id.text_view_date_beginning);
        mTextViewEndDate = findViewById(R.id.text_view_date_end);
        mFloatingButtonAddMedicine = findViewById(R.id.floating_button_add_medicine);

        //TODO: Manage prescription addition
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle mCheckBoxFragmentBundleArgs;
        if(mPrescription != null){
            //We have selected a prescription, we set values of the fragment's checkboxes to the that prescription
            mEditMode = true;
            mCheckBoxFragmentBundleArgs = createBundleOfCheckBoxesValues(
                    mPrescription.getPrescriptionMedicine().isMedicineMorningIntake(),
                    mPrescription.getPrescriptionMedicine().isMedicineNoonIntake(),
                    mPrescription.getPrescriptionMedicine().isMedicineEveningIntake()
            );
        }else{
            //Create a new prescription and set values of the fragment's checkboxes to all false
            mEditMode = false;
            mCheckBoxFragmentBundleArgs = createBundleOfCheckBoxesValues(false, false, false);
            mPrescription = new Prescription();
        }
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container_times_of_day_checkboxes, TimeOfDayCheckBoxesFragment.class, mCheckBoxFragmentBundleArgs)
                .commit();

        mImageButtonBeginningDate.setOnClickListener((view)->{
            mPickingBeginningDate = true;
            showDatePicker();
        });

        mImageButtonEndDate.setOnClickListener((view)->{
            mPickingBeginningDate = false;
            showDatePicker();
        });

        mTextViewBeginningDate.setOnClickListener((view)->{
            mPickingBeginningDate = true;
            showDatePicker();
        });

        mTextViewEndDate.setOnClickListener((view)->{
            mPickingBeginningDate = false;
            showDatePicker();
        });

        mFloatingButtonAddMedicine.setOnClickListener((view)->{
            Intent addMedicineIntent = new Intent(this, AddMedicineActivity.class);
            startActivity(addMedicineIntent);
        });

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

    private void showDatePicker() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, dayOfMonth);
        String date = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR);
        Log.d("Prescription Act", "I got the date ! It's: " + date);
        if(mPickingBeginningDate){
            mTextViewBeginningDate.setText(date);
        }else{
            mTextViewEndDate.setText(date);
        }
    }
}