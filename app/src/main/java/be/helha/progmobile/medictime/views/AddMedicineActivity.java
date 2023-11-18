package be.helha.progmobile.medictime.views;

import static be.helha.progmobile.medictime.views.fragments.TimeOfDayCheckBoxesFragment.createBundleOfCheckBoxesValues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import be.helha.progmobile.medictime.R;
import be.helha.progmobile.medictime.models.MedicTimeDataAccessObject;
import be.helha.progmobile.medictime.models.Medicine;
import be.helha.progmobile.medictime.views.fragments.TimeOfDayCheckBoxesFragment;

public class AddMedicineActivity extends AppCompatActivity {

    private MedicTimeDataAccessObject mMedicTimeDataAccessObject;
    private Medicine mMedicine;
    private boolean mEditMode;

    private EditText mEditTextMedicineName;
    private EditText mEditTextMedicineDuration;
    private Button mAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        mMedicTimeDataAccessObject = MedicTimeDataAccessObject.getInstance(getApplicationContext());
        Bundle mCheckBoxFragmentBundleArgs;
        if(mMedicine != null){
            //We are editing a medicine
            mEditMode = true;
            mCheckBoxFragmentBundleArgs = createBundleOfCheckBoxesValues(mMedicine.isMedicineNoonIntake(),
                    mMedicine.isMedicineNoonIntake(), mMedicine.isMedicineEveningIntake());
        }else{
            //We are adding a new medicine
            mEditMode = false;
            mMedicine = new Medicine();
            mCheckBoxFragmentBundleArgs = createBundleOfCheckBoxesValues(false, false, false);
        }

        mEditTextMedicineName = findViewById(R.id.edit_text_name_of_new_medicine);
        mEditTextMedicineDuration = findViewById(R.id.edit_text_default_duration);
        mAddButton = findViewById(R.id.button_validate);


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container_times_of_day_checkboxes, TimeOfDayCheckBoxesFragment.class, mCheckBoxFragmentBundleArgs)
                .commit();

        fragmentManager.setFragmentResultListener(TimeOfDayCheckBoxesFragment.KEY_FRAGMENT_TIMES_OF_DAY_RESULT,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        //Sets part of the medicine's data through the fragment
                        mMedicine.setIntakes(result.getBoolean(TimeOfDayCheckBoxesFragment.KEY_MORNING),
                                result.getBoolean(TimeOfDayCheckBoxesFragment.KEY_NOON),
                                result.getBoolean(TimeOfDayCheckBoxesFragment.KEY_EVENING));
                    }
                });

        //TODO: check input data is correct and all fields are filled
        mAddButton.setOnClickListener(view->{
            //Sets the rest of the data of the medicine and validates.
            mMedicine.setMedicineName(mEditTextMedicineName.getText().toString());
            mMedicine.setMedicineDuration(Integer.parseInt(mEditTextMedicineDuration.getText().toString()));
            mMedicTimeDataAccessObject.addMedicine(mMedicine);
            finish();
        });

    }



}