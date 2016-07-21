package com.dogzz.pim.screens;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ScrollView;
import android.widget.TextView;
import com.dogzz.pim.BuildConfig;
import com.dogzz.pim.R;

/**
 * About screen
 */
public class AboutFragment extends Fragment {
    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        final ScrollView licenses = (ScrollView) view.findViewById(R.id.scrollView);
        licenses.setVisibility(View.GONE);
        TextView licensesText = (TextView) view.findViewById(R.id.textLicenses);
        licensesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isVisible = licenses.getVisibility() == View.VISIBLE;
                if (isVisible) {
                    licenses.setVisibility(View.GONE);
                } else {
                    licenses.setVisibility(View.VISIBLE);
                }
            }
        });
        TextView textVersionValue = (TextView) view.findViewById(R.id.textVersionValue);
        textVersionValue.setText(BuildConfig.VERSION_NAME);
        return view;
    }
}
