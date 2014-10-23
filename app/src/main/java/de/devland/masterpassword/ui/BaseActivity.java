package de.devland.masterpassword.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import de.devland.masterpassword.App;
import de.devland.masterpassword.util.RequestCodeManager;
import de.keyboardsurfer.android.widget.crouton.Crouton;

/**
 * Created by David Kunzler on 23.10.2014.
 */
public class BaseActivity extends ActionBarActivity {
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        RequestCodeManager.INSTANCE.execute(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get().getBus().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.get().setCurrentForegroundActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
        App.get().getBus().unregister(this);
    }
}
