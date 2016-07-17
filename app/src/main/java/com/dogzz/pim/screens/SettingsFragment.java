package com.dogzz.pim.screens;

import android.os.Bundle;

import android.support.v7.preference.PreferenceFragmentCompat;

import com.dogzz.pim.R;


/**
 *
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey);
    }
}
