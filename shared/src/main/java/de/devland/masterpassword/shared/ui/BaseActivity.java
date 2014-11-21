package de.devland.masterpassword.shared.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.squareup.otto.Bus;

import de.devland.masterpassword.shared.BaseApp;
import de.devland.masterpassword.shared.util.RequestCodeManager;


/**
 * Created by David Kunzler on 23.10.2014.
 */
public abstract class BaseActivity extends ActionBarActivity {

    protected Bus bus;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        RequestCodeManager.INSTANCE.execute(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus = BaseApp.get().getBus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BaseApp.get().getBus().register(this);
        BaseApp.get().setCurrentForegroundActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseApp.get().getBus().unregister(this);
    }
}
