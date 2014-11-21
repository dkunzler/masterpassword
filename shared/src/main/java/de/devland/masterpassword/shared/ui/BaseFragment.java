package de.devland.masterpassword.shared.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.squareup.otto.Bus;

import de.devland.masterpassword.shared.BaseApp;

/**
 * Created by David Kunzler on 07.11.2014.
 */
public class BaseFragment extends Fragment {

    protected Bus bus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus = BaseApp.get().getBus();
    }

    @Override
    public void onResume() {
        super.onResume();
        BaseApp.get().getBus().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BaseApp.get().getBus().unregister(this);
    }
}
