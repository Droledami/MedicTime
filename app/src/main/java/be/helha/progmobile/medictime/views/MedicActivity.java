package be.helha.progmobile.medictime.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import be.helha.progmobile.medictime.models.MedicTimeDataAccessObject;
import be.helha.progmobile.medictime.views.fragments.PrescriptionListFragment;
import be.helha.progmobile.medictime.R;

public class MedicActivity extends AppCompatActivity {

    private MedicTimeDataAccessObject mMedicTimeDataAccessObject;
    private FloatingActionButton mAddPrescriptionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic);
        mMedicTimeDataAccessObject = MedicTimeDataAccessObject.getInstance(getApplicationContext());

        mAddPrescriptionButton = findViewById(R.id.button_add_prescription);
        mAddPrescriptionButton.setOnClickListener((view)->{
            Intent intent = new Intent(this, PrescriptionActivity.class);
            startActivity(intent);
        });

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragmentContainerView, PrescriptionListFragment.class, null)
                .commit();
    }

    private View createTimeOfDayPrescription(){
        View prescriptionView = getLayoutInflater().inflate(R.layout.time_of_day_prescription, null);
        ((TextView)prescriptionView.findViewById(R.id.text_view_time_of_day)).setText("MATIN?MIDI?SOIR?");
        ((TextView)prescriptionView.findViewById(R.id.text_view_medicine)).setText("UN GROS SUPO");
        return prescriptionView;
    }
}