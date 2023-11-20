package be.helha.progmobile.medictime.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import be.helha.progmobile.medictime.models.MedicTimeDataAccessObject;
import be.helha.progmobile.medictime.models.Prescription;
import be.helha.progmobile.medictime.views.fragments.PrescriptionListFragment;
import be.helha.progmobile.medictime.R;

public class MedicActivity extends AppCompatActivity {
    private FloatingActionButton mAddPrescriptionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medic);

        mAddPrescriptionButton = findViewById(R.id.button_add_prescription);
        mAddPrescriptionButton.setOnClickListener((view)->{
            Intent intent = new Intent(this, PrescriptionActivity.class);
            startActivity(intent);
        });

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container_prescriptions_list, PrescriptionListFragment.class, null)
                .commit();
    }
}