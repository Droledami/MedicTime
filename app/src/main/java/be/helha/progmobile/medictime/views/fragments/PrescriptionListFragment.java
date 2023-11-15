package be.helha.progmobile.medictime.views.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.helha.progmobile.medictime.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrescriptionListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrescriptionListFragment extends Fragment {
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_prescription_list, container, false);
        return v;
    }
}