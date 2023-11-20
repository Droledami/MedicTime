package be.helha.progmobile.medictime.views.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import be.helha.progmobile.medictime.R;
import be.helha.progmobile.medictime.models.DayUniqueIdForView;
import be.helha.progmobile.medictime.models.MedicTimeDataAccessObject;
import be.helha.progmobile.medictime.models.Prescription;
import be.helha.progmobile.medictime.views.PrescriptionActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrescriptionListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrescriptionListFragment extends Fragment {
    public static final int MILLISECONDS_IN_A_DAY = 86400000;
    private final int MONTH_DURATION = 30;
    private GregorianCalendar mToday;
    private MedicTimeDataAccessObject mMedicTimeDataAccessObject;
    private List<Prescription> mPrescriptionList;
    private DayUniqueIdForView[] mDayIDs = new DayUniqueIdForView[MONTH_DURATION];
    private LinearLayout mTopLayerLinearLayout;

    public PrescriptionListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PrescriptionListFragment.
     */
    public static PrescriptionListFragment newInstance() {
        PrescriptionListFragment fragment = new PrescriptionListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i < MONTH_DURATION; i++) {
            mDayIDs[i] = new DayUniqueIdForView();
        }
        mToday = getZeroHouredCalendar();
        //TODO: REMOVE DEBUG LINE INTENDED TO PROJECT TO FUTURE DATES
        //mToday.add(Calendar.DAY_OF_MONTH, 1);
        mMedicTimeDataAccessObject = MedicTimeDataAccessObject.getInstance(getContext());
        mPrescriptionList = mMedicTimeDataAccessObject.getAllPrescriptions();
        Collections.sort(mPrescriptionList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_prescription_list, container, false);
        mTopLayerLinearLayout = v.findViewById(R.id.linear_layout_prescription_list);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance) {
        for (Prescription prescription : mPrescriptionList) {
            addPrescriptionToList(prescription, view);
        }
    }

    private void addPrescriptionToList(Prescription prescription, View parentView) {
        int prescriptionDuration = calculateDayDifference(prescription.getPrescriptionStartDate(), prescription.getPrescriptionEndDate());
        for (int dayAfterStart = 0; dayAfterStart <= prescriptionDuration; dayAfterStart++) {
            addToDayPrescriptionView(prescription, parentView, dayAfterStart);
        }
    }

    private void addToDayPrescriptionView(Prescription prescription, View parentView, int dayAfterStart) {
        GregorianCalendar calForCurrentDayToDisplay = getZeroHouredCalendar();
        calForCurrentDayToDisplay.setTime(prescription.getPrescriptionStartDate());
        calForCurrentDayToDisplay.add(Calendar.DAY_OF_MONTH, dayAfterStart);
        int dayAfterToday = calculateDayDifference(calForCurrentDayToDisplay.getTime(), mToday.getTime());//Also used as an index to get the id of the correct day views
        if(dayAfterToday >= MONTH_DURATION || mToday.getTime().after(calForCurrentDayToDisplay.getTime()))
            //We don't display anything past a month or if the checked date is past Today
            return;
        Log.d("Time diff with today: ", String.valueOf(dayAfterToday));
        LinearLayout dayView = parentView.findViewById(mDayIDs[dayAfterToday].getDayID());
        if (dayView == null) { //Test if we already generated a view for that given day, if it's null, generate the layout
            dayView = (LinearLayout) getLayoutInflater().inflate(R.layout.day_prescription, null);
            mDayIDs[dayAfterToday].setDayID(View.generateViewId());//Add a generated ID to the list so we can Identify this view later thanks to the dayAfterToday calculations
            mDayIDs[dayAfterToday].setMorningID(View.generateViewId());//Same for all times of day for that Day
            mDayIDs[dayAfterToday].setNoonID(View.generateViewId());
            mDayIDs[dayAfterToday].setEveningID(View.generateViewId());
            dayView.setId(mDayIDs[dayAfterToday].getDayID()); //We give a unique ID for that day if another medicine needs to be added to that layout
            String dateToDisplay = new SimpleDateFormat("d MMMM yyyy", Locale.FRENCH).format(calForCurrentDayToDisplay.getTime());
            ((TextView) dayView.findViewById(R.id.text_view_date)).setText(dateToDisplay);
            mTopLayerLinearLayout.addView(dayView);
            //Create times of day in advance to generate time-of-day layouts in the correct order
            addEmptyTimeOfDayPrescriptionView(dayView, R.string.morning, mDayIDs[dayAfterToday].getMorningID());
            addEmptyTimeOfDayPrescriptionView(dayView, R.string.noon, mDayIDs[dayAfterToday].getNoonID());
            addEmptyTimeOfDayPrescriptionView(dayView, R.string.evening, mDayIDs[dayAfterToday].getEveningID());
        }
        //At this point the view for the day already exists or has been added so we just add the medicine to that existing layout
        if (prescription.isPrescriptionMorningIntake())
            addToTimeOfDayPrescriptionView(prescription, dayView, mDayIDs[dayAfterToday].getMorningID());
        if (prescription.isPrescriptionNoonIntake())
            addToTimeOfDayPrescriptionView(prescription, dayView, mDayIDs[dayAfterToday].getNoonID());
        if (prescription.isPrescriptionEveningIntake())
            addToTimeOfDayPrescriptionView(prescription, dayView, mDayIDs[dayAfterToday].getEveningID());
    }

    private int calculateDayDifference(Date date, Date otherDate) {
        long timeDiffMillis = Math.abs(date.getTime() - otherDate.getTime());
        return (int) (timeDiffMillis / MILLISECONDS_IN_A_DAY);
    }

    private void addToTimeOfDayPrescriptionView(Prescription prescription, LinearLayout parentLayout, int timeOfDayViewID) {
        View timeOfDayPrescriptionView = parentLayout.findViewById(timeOfDayViewID);
        LinearLayout medicineLinearLayout = timeOfDayPrescriptionView.findViewById(R.id.linear_layout_medicine);
        TextView timeOfDay = timeOfDayPrescriptionView.findViewById(R.id.text_view_time_of_day);
        timeOfDay.setVisibility(View.VISIBLE);
        TextView medicineText = getMedicineTextView(prescription);
        medicineLinearLayout.addView(medicineText);
    }

    private void addEmptyTimeOfDayPrescriptionView(LinearLayout parentLayout, int timeOfDayResID, int timeOfDayViewID) {
        View timeOfDayPrescriptionView = getLayoutInflater().inflate(R.layout.time_of_day_prescription, null);
        timeOfDayPrescriptionView.setId(timeOfDayViewID);
        TextView timeOfDay = timeOfDayPrescriptionView.findViewById(R.id.text_view_time_of_day);
        timeOfDay.setVisibility(View.GONE); //Since that text view is set to match parent, if it's empty it will not be shown
        timeOfDay.setText(timeOfDayResID);
        parentLayout.addView(timeOfDayPrescriptionView);
    }

    @NonNull
    private TextView getMedicineTextView(Prescription prescription) {
        TextView medicineText = new TextView(getContext());
        medicineText.setPaddingRelative(72, 3, 5, 3);
        medicineText.setTextSize(20);
        medicineText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        medicineText.setText("- " + prescription.getPrescriptionMedicine().getMedicineName());
        medicineText.setOnClickListener((view) -> {
            Intent editPrescription = new Intent(getContext(), PrescriptionActivity.class);
            editPrescription.putExtra(PrescriptionActivity.KEY_PRESCRIPTION_ID, prescription.getPrescriptionId().toString());
            startActivity(editPrescription);
        });
        return medicineText;
    }

    private GregorianCalendar getZeroHouredCalendar() {
        //ensure the hour is 0h 0min 0s 0ms for time difference calculations
        GregorianCalendar cal = new GregorianCalendar(Locale.FRANCE);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
}