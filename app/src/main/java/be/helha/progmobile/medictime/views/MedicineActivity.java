package be.helha.progmobile.medictime.views;

import static be.helha.progmobile.medictime.views.fragments.TimeOfDayCheckBoxesFragment.createBundleOfCheckBoxesValues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import be.helha.progmobile.medictime.R;
import be.helha.progmobile.medictime.models.MedicTimeDataAccessObject;
import be.helha.progmobile.medictime.models.Medicine;
import be.helha.progmobile.medictime.views.fragments.TimeOfDayCheckBoxesFragment;

public class MedicineActivity extends AppCompatActivity {

    public static final String KEY_MEDICINE_ID = "KEY_MEDICINE_ID";
    public static final String KEY_MEDICINE_ADDED = "KEY_MEDICINE_ADDED";
    public static final String KEY_MEDICINE_EDITED = "KEY_MEDICINE_EDITED";
    private MedicTimeDataAccessObject mMedicTimeDataAccessObject;
    private Medicine mMedicine;
    private boolean mEditMode;

    private TextView mTextViewAddOrEditMedicine;
    private EditText mEditTextMedicineName;
    private EditText mEditTextMedicineDuration;
    private Button mAddOrEditButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViewElementsToVariables();
        mMedicTimeDataAccessObject = MedicTimeDataAccessObject.getInstance(getApplicationContext());
        Bundle mCheckBoxFragmentBundleArgs =
                defineEditModeAndGetFragmentBundleData();
        addFragmentCheckBoxes(mCheckBoxFragmentBundleArgs);
        setEventsOnAddOrEditButton();
    }

    private void setEventsOnAddOrEditButton() {
        mAddOrEditButton.setOnClickListener(view->{
            if(checkIfAnEntryIsInvalid())
                return;
            //Sets the rest of the data of the medicine and validates.
            Intent data = new Intent();
            mMedicine.setMedicineName(mEditTextMedicineName.getText().toString());
            mMedicine.setMedicineDuration(Integer.parseInt(mEditTextMedicineDuration.getText().toString()));
            if(mEditMode){
                mMedicTimeDataAccessObject.updateMedicine(mMedicine);
                data.putExtra(KEY_MEDICINE_EDITED, true);
            }
            else{
                mMedicTimeDataAccessObject.addMedicine(mMedicine);
                data.putExtra(KEY_MEDICINE_ADDED, true);
            }
            this.setResult(RESULT_OK, data);
            finish();
        });
    }

    private boolean checkIfAnEntryIsInvalid(){
        if(mEditTextMedicineDuration.getText().toString().equals("")){
            showToastInvalidEntry();
            return true;
        }
        if(mEditTextMedicineName.getText().toString().equals("")){
            showToastInvalidEntry();
            return true;
        }
        if(!mMedicine.isMedicineNoonIntake() && !mMedicine.isMedicineMorningIntake()
                && !mMedicine.isMedicineEveningIntake()){
            showToastInvalidEntry();
            return true;
        }
        return false;
    }

    private void showToastInvalidEntry() {
        Toast.makeText(this, R.string.incomplete_entry, Toast.LENGTH_SHORT).show();
    }

    private void addFragmentCheckBoxes(Bundle mCheckBoxFragmentBundleArgs) {
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
    }

    @NonNull
    private Bundle defineEditModeAndGetFragmentBundleData() {
        Bundle mCheckBoxFragmentBundleArgs;
        String medicineId = getIntent().getStringExtra(KEY_MEDICINE_ID);
        if(medicineId != null)
            mMedicine = mMedicTimeDataAccessObject.getMedicine(medicineId);

        if(mMedicine != null){
            //We are editing a medicine
            mEditMode = true;
            mAddOrEditButton.setText(R.string.edit);
            mTextViewAddOrEditMedicine.setText(R.string.edit_medicine);
            mEditTextMedicineName.setText(mMedicine.getMedicineName());
            mEditTextMedicineDuration.setText(String.valueOf(mMedicine.getMedicineDuration()));
            mCheckBoxFragmentBundleArgs = createBundleOfCheckBoxesValues(mMedicine.isMedicineMorningIntake(),
                    mMedicine.isMedicineNoonIntake(), mMedicine.isMedicineEveningIntake());
        }else{
            //We are adding a new medicine
            mEditMode = false;
            mMedicine = new Medicine();
            mCheckBoxFragmentBundleArgs = createBundleOfCheckBoxesValues(false, false, false);
        }
        return mCheckBoxFragmentBundleArgs;
    }

    private void bindViewElementsToVariables() {
        setContentView(R.layout.activity_medicine);

        mEditTextMedicineName = findViewById(R.id.edit_text_name_of_new_medicine);
        mEditTextMedicineDuration = findViewById(R.id.edit_text_default_duration);
        mAddOrEditButton = findViewById(R.id.button_validate);
        mTextViewAddOrEditMedicine = findViewById(R.id.text_view_add_medicine);
    }
}