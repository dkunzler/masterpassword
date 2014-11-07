package de.devland.masterpassword.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.squareup.otto.Bus;

import de.devland.masterpassword.App;

/**
 * Created by David Kunzler on 07.11.2014.
 */
public class BaseFragment extends Fragment {

    protected Bus bus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus = App.get().getBus();
    }

    @Override
    public void onResume() {
        super.onResume();
        App.get().getBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        App.get().getBus().unregister(this);
    }
}
