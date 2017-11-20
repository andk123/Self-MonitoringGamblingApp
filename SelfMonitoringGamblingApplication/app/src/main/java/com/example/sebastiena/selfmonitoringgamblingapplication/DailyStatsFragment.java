package com.example.sebastiena.selfmonitoringgamblingapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import FireBase.DatabaseHelper;
import Objects.UserEntity;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DailyStatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DailyStatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyStatsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DailyStatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DailyStatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailyStatsFragment newInstance(String param1, String param2) {
        DailyStatsFragment fragment = new DailyStatsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_daily_stats, container, false);
        final TextView spent = (TextView)view.findViewById(R.id.spent);
        final TextView made = (TextView)view.findViewById(R.id.made);
        final TextView lost = (TextView)view.findViewById(R.id.lost);
        final TextView outcome = (TextView)view.findViewById(R.id.outcome);
        final EditText budgetLimit = (EditText) view.findViewById(R.id.budget);
        final EditText timeLimit = (EditText) view.findViewById(R.id.time);
        final Button budgetButton = (Button) view.findViewById(R.id.budgetButton);
        DatabaseHelper dbHelper = new DatabaseHelper(FirebaseDatabase.getInstance().getReference());
        dbHelper.fetchDataAndDisplayDaily(super.getActivity(),spent,made,lost,outcome, budgetLimit,timeLimit);

        budgetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String dailyBudget = budgetLimit.getText().toString().trim();
                String dailyTime = timeLimit.getText().toString().trim();
                if (TextUtils.isEmpty(dailyBudget)) {
                    Toast.makeText(DailyStatsFragment.super.getContext(), "Enter Daily Budget Limit Amount!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(dailyTime)) {
                    Toast.makeText(DailyStatsFragment.super.getContext(), "Enter Daily Time Limit Amount!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int limit = Integer.valueOf(dailyBudget);
                int time = Integer.valueOf(dailyTime);

                DatabaseHelper dbHelper = new DatabaseHelper(FirebaseDatabase.getInstance().getReference());

                if(dbHelper.updateUserDailyLimit(FirebaseAuth.getInstance().getCurrentUser().getUid(),limit,time)){
                    Toast.makeText(DailyStatsFragment.super.getContext(), "Daily Limit Set", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(DailyStatsFragment.super.getContext(), newMainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(DailyStatsFragment.super.getContext(), "Error setting daily limit", Toast.LENGTH_LONG).show();
                    return;

                }

            }





        });





        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
