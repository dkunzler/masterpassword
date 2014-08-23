package de.devland.masterpassword.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.devland.masterpassword.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PasswordViewFragment extends Fragment {


    public PasswordViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_password_view, container, false);
        return rootView;
    }


}
