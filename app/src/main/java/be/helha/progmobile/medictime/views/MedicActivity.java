package be.helha.progmobile.medictime.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import be.helha.progmobile.medictime.views.fragments.PrescriptionListFragment;
import be.helha.progmobile.medictime.R;

public class MedicActivity extends AppCompatActivity {
    private FloatingActionButton mAddPrescriptionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViewElementsToVariables();
        setEventOnAddPrescriptionButton();
        loadListOfPrescriptionFragment();
    }

    private void setEventOnAddPrescriptionButton() {
        mAddPrescriptionButton.setOnClickListener((view)->{
            Intent intent = new Intent(this, PrescriptionActivity.class);
            startActivity(intent);
        });
    }

    private void bindViewElementsToVariables() {
        setContentView(R.layout.activity_medic);
        mAddPrescriptionButton = findViewById(R.id.button_add_prescription);
    }

    private void loadListOfPrescriptionFragment() {
        //Using replace instead of add to prevent fragment superpositions when rotating the screen
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_prescriptions_list, PrescriptionListFragment.class, null)
                .commit();
    }

    @Override
    protected void onResume(){
        super.onResume();
        //onResume is used to update the list after we are done adding or editing prescriptions
        loadListOfPrescriptionFragment();
    }

}