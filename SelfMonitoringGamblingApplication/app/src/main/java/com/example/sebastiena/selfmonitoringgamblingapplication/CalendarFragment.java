package com.example.sebastiena.selfmonitoringgamblingapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sebastiena.selfmonitoringgamblingapplication.calendar.bean.CalendarDate;
import com.example.sebastiena.selfmonitoringgamblingapplication.calendar.utils.RandomUtils;
import com.example.sebastiena.selfmonitoringgamblingapplication.calendar.utils.StringUtils;
import com.example.sebastiena.selfmonitoringgamblingapplication.calendar.view.CommonCalendarView;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FireBase.DatabaseHelper;

public class CalendarFragment extends Fragment {

    private  CommonCalendarView calendarView;
    private Map<String,List> mYearMonthMap = new HashMap<>();

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_calendar, container, false);

        List<CalendarDate> mCalendarDateList = new ArrayList<>();

        DatabaseHelper dbHelper = new DatabaseHelper(FirebaseDatabase.getInstance().getReference());
        dbHelper.fetchDataAndDisplayCalendar(mCalendarDateList, view, getActivity());

        return view;
    }

}
