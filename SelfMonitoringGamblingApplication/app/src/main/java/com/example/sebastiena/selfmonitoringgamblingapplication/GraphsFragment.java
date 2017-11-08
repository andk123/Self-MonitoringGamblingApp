package com.example.sebastiena.selfmonitoringgamblingapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;

import FireBase.DatabaseHelper;
import pl.polidea.view.ZoomView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GraphsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GraphsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GraphsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ZoomView zoomView;

    public GraphsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GraphsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GraphsFragment newInstance(String param1, String param2) {
        GraphsFragment fragment = new GraphsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_graphs, container, false);

        PieChart pieChart = (PieChart) view.findViewById(R.id.pie_chart);
        LineChart lineChart = (LineChart) view.findViewById(R.id.line_chart);
        DatabaseHelper dbHelper = new DatabaseHelper(FirebaseDatabase.getInstance().getReference());
        dbHelper.fetchDataAndDisplayGraphs(super.getActivity(),pieChart,lineChart);

        ZoomView zoomView = new ZoomView(getActivity());
        zoomView.addView(view);
        
        return zoomView;
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
