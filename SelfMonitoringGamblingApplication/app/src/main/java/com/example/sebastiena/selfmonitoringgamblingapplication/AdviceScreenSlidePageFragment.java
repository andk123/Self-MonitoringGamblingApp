/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.sebastiena.selfmonitoringgamblingapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.marcok.stepprogressbar.StepProgressBar;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 */
public class AdviceScreenSlidePageFragment extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";
    public static final String ARG_TOTAL_PAGES = "totalPages";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    private int nbrPages;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static AdviceScreenSlidePageFragment create(int pageNumber, int totalPages) {
        AdviceScreenSlidePageFragment fragment = new AdviceScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        args.putInt(ARG_TOTAL_PAGES, totalPages);
        fragment.setArguments(args);
        return fragment;
    }

    public AdviceScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        nbrPages = getArguments().getInt(ARG_TOTAL_PAGES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_advice_screen_slide_page, container, false);

        // Set the title view to show the page number.

        switch (mPageNumber) {
            case 0:
                ((TextView) rootView.findViewById(R.id.advice_title)).setText(getString(R.string.advice1title));
                ((TextView) rootView.findViewById(R.id.advice_text)).setText(getString(R.string.advice1));
                break;
            case 1:
                ((TextView) rootView.findViewById(R.id.advice_title)).setText(getString(R.string.advice2title));
                ((TextView) rootView.findViewById(R.id.advice_text)).setText(getString(R.string.advice2));
                break;
            case 2:
                ((TextView) rootView.findViewById(R.id.advice_title)).setText(getString(R.string.advice3title));
                ((TextView) rootView.findViewById(R.id.advice_text)).setText(getString(R.string.advice3));
                break;
            case 3:
                ((TextView) rootView.findViewById(R.id.advice_title)).setText(getString(R.string.advice4title));
                ((TextView) rootView.findViewById(R.id.advice_text)).setText(getString(R.string.advice4));
                break;
            case 4:
                ((TextView) rootView.findViewById(R.id.advice_title)).setText(getString(R.string.advice5title));
                ((TextView) rootView.findViewById(R.id.advice_text)).setText(getString(R.string.advice5));
                break;
            case 5:
                ((TextView) rootView.findViewById(R.id.advice_title)).setText(getString(R.string.advice6title));
                ((TextView) rootView.findViewById(R.id.advice_text)).setText(getString(R.string.advice6));
                break;
            case 6:
                ((TextView) rootView.findViewById(R.id.advice_title)).setText(getString(R.string.advice7title));
                ((TextView) rootView.findViewById(R.id.advice_text)).setText(getString(R.string.advice7));
                break;
            case 7:
                ((TextView) rootView.findViewById(R.id.advice_title)).setText(getString(R.string.advice8title));
                ((TextView) rootView.findViewById(R.id.advice_text)).setText(getString(R.string.advice8));
                break;
            default:

        }


        StepProgressBar mStepProgressBar = (StepProgressBar)rootView.findViewById(R.id.stepProgressBar);
        mStepProgressBar.setNumDots(nbrPages);
        mStepProgressBar.setCurrentProgressDot(mPageNumber);

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
