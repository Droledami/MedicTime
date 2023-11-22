package be.helha.progmobile.medictime.views.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    DatePickerDialog.OnDateSetListener listener;

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        listener = (DatePickerDialog.OnDateSetListener) context;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstance){
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(requireContext(), this, year, month, dayOfMonth);
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Log.d("DATE PICKER", "A date was chosen !" + year + " " + month + " " + dayOfMonth);
        listener.onDateSet(view, year, month, dayOfMonth);
    }
}