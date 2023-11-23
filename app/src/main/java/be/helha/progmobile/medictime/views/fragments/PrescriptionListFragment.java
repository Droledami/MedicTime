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

    /**
     * Adds prescription information to the day views within the parent view for the entire duration of the prescription.
     * This method calculates the duration of the prescription in days and iteratively calls the addToDayPrescriptionView
     * method for each day, adding the prescription information to the corresponding day views within the parent view.
     *
     * @param prescription The Prescription object containing information about the medication.
     * @param parentView The parent view containing day views.
     */
    private void addPrescriptionToList(Prescription prescription, View parentView) {
        int prescriptionDuration = calculateDayDifference(prescription.getPrescriptionStartDate(), prescription.getPrescriptionEndDate());
        for (int dayAfterStart = 0; dayAfterStart <= prescriptionDuration; dayAfterStart++) {
            addToDayPrescriptionView(prescription, parentView, dayAfterStart);
        }
    }

    /**
     * Adds prescription information to the day view within the parent view based on the given day offset.
     * This method calculates the date corresponding to the specified day offset after the prescription's start date.
     * It then determines if the day view for that date already exists; if not, it generates the layout.
     * Finally, it adds the medicine information to the existing or newly created day view based on the prescription's intake times.
     *
     * @param prescription The Prescription object containing information about the medication.
     * @param parentView The parent view containing day views.
     * @param dayAfterStart The offset, in days, after the prescription's start date.
     */
    private void addToDayPrescriptionView(Prescription prescription, View parentView, int dayAfterStart) {
        GregorianCalendar calForCurrentDayToDisplay = getZeroHouredCalendar();
        calForCurrentDayToDisplay.setTime(prescription.getPrescriptionStartDate());
        calForCurrentDayToDisplay.add(Calendar.DAY_OF_MONTH, dayAfterStart);
        int dayAfterToday = calculateDayDifference(calForCurrentDayToDisplay.getTime(), mToday.getTime());//Also used as an index to get the id of the correct day views

        //We don't display anything past a month or if the checked date is past Today
        if(dayAfterToday >= MONTH_DURATION || mToday.getTime().after(calForCurrentDayToDisplay.getTime()))
            return;

        LinearLayout dayView = parentView.findViewById(mDayIDs[dayAfterToday].getDayID());//If it was created before, get the dayView based on how late we are after the date of today
        if (dayView == null) { //Test if we already generated a view for that given day, if it's null, generate the layout
            dayView = createDayViewWithIDs(calForCurrentDayToDisplay, dayAfterToday);
        }
        //At this point the view for the day already exists or has been added so we just add the medicine to that existing layout
        if (prescription.isPrescriptionMorningIntake())
            addToTimeOfDayPrescriptionView(prescription, dayView, mDayIDs[dayAfterToday].getMorningID());
        if (prescription.isPrescriptionNoonIntake())
            addToTimeOfDayPrescriptionView(prescription, dayView, mDayIDs[dayAfterToday].getNoonID());
        if (prescription.isPrescriptionEveningIntake())
            addToTimeOfDayPrescriptionView(prescription, dayView, mDayIDs[dayAfterToday].getEveningID());
    }

    /**
     * @param calForCurrentDayToDisplay The calendar containing the date that has to be shown
     * @param dayAfterToday the day after the day of today which is used as an index to identify
     *                      where we have to create new IDs for a dayView
     * @return a LinearLayout "dayView" used to display a date with sub-layouts for each time of day
     *         each holding a generated ViewID.
     *         The IDs thus generated are held in the global variable mDayIDs at the index of
     *         dayAfterToday's value, this is for later identification of layouts.
     */
    @NonNull
    private LinearLayout createDayViewWithIDs(GregorianCalendar calForCurrentDayToDisplay, int dayAfterToday) {
        LinearLayout dayView;
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
        return dayView;
    }

    /**
     * Creates a view meant to be inside a day view, which will display the given time of day
     * ("Morning", "Noon", "Evening"). It will be assigned the given ViewID for later identification
     *
     * @param parentLayout The layout where an empty time of day View will be added
     * @param timeOfDayResID The resource ID of the string for that time of day ("Morning", "Noon", "Evening")
     * @param timeOfDayViewID The viewID to add to the time of day View to identify it after it is created
     */
    private void addEmptyTimeOfDayPrescriptionView(LinearLayout parentLayout, int timeOfDayResID, int timeOfDayViewID) {
        View timeOfDayPrescriptionView = getLayoutInflater().inflate(R.layout.time_of_day_prescription, null);
        timeOfDayPrescriptionView.setId(timeOfDayViewID);
        TextView timeOfDay = timeOfDayPrescriptionView.findViewById(R.id.text_view_time_of_day);
        timeOfDay.setVisibility(View.GONE); //Since that text view is set to match parent, if it's empty it will not be shown
        timeOfDay.setText(timeOfDayResID);
        parentLayout.addView(timeOfDayPrescriptionView);
    }

    private int calculateDayDifference(Date date, Date otherDate) {
        long timeDiffMillis = Math.abs(date.getTime() - otherDate.getTime());
        return (int) (timeDiffMillis / MILLISECONDS_IN_A_DAY);
    }

    /**
     * Adds prescription information to the designated time of day view within the parent layout.
     * This method retrieves the specified time of day prescription view from the parent layout,
     * makes the time of day TextView visible, and adds the medicine information TextView to
     * the medicine linear layout within the time of day prescription view.
     *
     * @param prescription The Prescription object containing information about the medicine.
     * @param parentLayout The parent LinearLayout where the time of day prescription view is located.
     * @param timeOfDayViewID The resource ID of the time of day prescription view within the parent layout.
     */
    private void addToTimeOfDayPrescriptionView(Prescription prescription, LinearLayout parentLayout, int timeOfDayViewID) {
        View timeOfDayPrescriptionView = parentLayout.findViewById(timeOfDayViewID);
        LinearLayout medicineLinearLayout = timeOfDayPrescriptionView.findViewById(R.id.linear_layout_medicine);
        TextView timeOfDay = timeOfDayPrescriptionView.findViewById(R.id.text_view_time_of_day);
        timeOfDay.setVisibility(View.VISIBLE);
        TextView medicineText = getMedicineTextView(prescription);
        medicineLinearLayout.addView(medicineText);
    }

    /**
     * Generates a TextView for displaying information about a prescribed medicine.
     * This method creates a TextView with specific formatting and sets its text to
     * include the name of the medicine in the given prescription. It also handles the click event
     * to open the PrescriptionActivity for editing the corresponding prescription.
     *
     * @param prescription The Prescription object containing information about the medication.
     * @return A TextView configured to display the prescribed medicine information.
     */
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

    /**
     * Creates a GregorianCalendar object with all time components set to zero.
     * This method ensures that the hour, minute, second, and millisecond fields
     * of the GregorianCalendar are set to zero, facilitating time difference
     * calculations between days
     *
     * @return A GregorianCalendar object with the time components set to zero.
     */
    private GregorianCalendar getZeroHouredCalendar() {
        GregorianCalendar cal = new GregorianCalendar(Locale.FRANCE);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
}