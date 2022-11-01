package de.devland.masterpassword.base.ui;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.otto.Bus;

import de.devland.masterpassword.base.BaseApp;
import de.devland.masterpassword.base.util.RequestCodeManager;


/**
 * Created by David Kunzler on 23.10.2014.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_SNACKBAR_MESSAGE = "de.devland.masterpassword.shared.ui.BaseActivity.MESSAGE";

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
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey(EXTRA_SNACKBAR_MESSAGE)) {
                String message = extras.getString(EXTRA_SNACKBAR_MESSAGE);
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                extras.remove(EXTRA_SNACKBAR_MESSAGE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        BaseApp.get().getBus().unregister(this);
    }
}
