package be.helha.progmobile.medictime.views.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import be.helha.progmobile.medictime.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeOfDayCheckBoxesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeOfDayCheckBoxesFragment extends Fragment {

    public static final String KEY_FRAGMENT_TIMES_OF_DAY_RESULT = "TIMES_OF_DAY_CHECKBOXES";
    public static final String KEY_MORNING = "morning";
    public static final String KEY_NOON = "noon";
    public static final String KEY_EVENING = "evening";

    private boolean mMorningValue;
    private boolean mNoonValue;
    private boolean mEveningValue;
    private Bundle mCheckBoxesValues;

    private CheckBox mMorningCheckbox;
    private CheckBox mNoonCheckbox;
    private CheckBox mEveningCheckbox;

    public TimeOfDayCheckBoxesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param morningValue The value of the checkbox "morning"
     * @param noonValue The value of the checkbox "noon"
     * @param eveningValue The value of the checkbox "evening"
     * @return A new instance of fragment TimeOfDayCheckBoxesFragment.
     */
    public static TimeOfDayCheckBoxesFragment newInstance(boolean morningValue, boolean noonValue, boolean eveningValue) {
        TimeOfDayCheckBoxesFragment fragment = new TimeOfDayCheckBoxesFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_MORNING, morningValue);
        args.putBoolean(KEY_NOON, noonValue);
        args.putBoolean(KEY_EVENING, eveningValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mMorningValue = getArguments().getBoolean(KEY_MORNING);
            mNoonValue = getArguments().getBoolean(KEY_NOON);
            mEveningValue = getArguments().getBoolean(KEY_EVENING);
        }else{
            mMorningValue = false;
            mNoonValue = false;
            mEveningValue = false;
        }
        setTimeOfDayFragmentResult();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_time_of_day_check_boxes, container, false);
        mMorningCheckbox = v.findViewById(R.id.check_box_morning);
        mNoonCheckbox = v.findViewById(R.id.check_box_noon);
        mEveningCheckbox = v.findViewById(R.id.check_box_evening);

        mMorningCheckbox.setChecked(mMorningValue);
        mNoonCheckbox.setChecked(mNoonValue);
        mEveningCheckbox.setChecked(mEveningValue);

        mMorningCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMorningValue = !mMorningValue;
                setTimeOfDayFragmentResult();
            }
        });

        mNoonCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mNoonValue = !mNoonValue;
                setTimeOfDayFragmentResult();
            }
        });

        mEveningCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mEveningValue = !mEveningValue;
                setTimeOfDayFragmentResult();
            }
        });

        return v;
    }

    private void setTimeOfDayFragmentResult() {
        //Creates the bundle containing the fragment's values and setting a key for later retrieval
        //via the FragmentManager of the parent
        FragmentManager parentFragmentManager = getParentFragmentManager();
        mCheckBoxesValues = createBundleOfCheckBoxesValues(mMorningValue, mNoonValue, mEveningValue);
        parentFragmentManager.setFragmentResult(KEY_FRAGMENT_TIMES_OF_DAY_RESULT, mCheckBoxesValues);
    }

    public static Bundle createBundleOfCheckBoxesValues(boolean morningValue, boolean noonValue, boolean eveningValue){
        Bundle b = new Bundle();
        b.putBoolean(KEY_MORNING, morningValue);
        b.putBoolean(KEY_NOON, noonValue);
        b.putBoolean(KEY_EVENING, eveningValue);
        return b;
    }
}