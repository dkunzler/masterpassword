package de.devland.masterpassword.pro.inputstick;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.inputstick.api.ConnectionManager;
import com.inputstick.api.InputStickStateListener;
import com.inputstick.api.basic.InputStickHID;
import com.inputstick.api.layout.KeyboardLayout;

import de.devland.masterpassword.pro.R;
import de.devland.masterpassword.pro.util.MainSnackbar;
import de.devland.masterpassword.shared.BaseApp;
import de.devland.masterpassword.shared.ui.BaseActivity;
import de.devland.masterpassword.shared.util.Intents;

/**
 * Created by David Kunzler on 21.11.2014.
 */
public class SendToInputStickActivity extends BaseActivity implements DialogInterface.OnCancelListener, InputStickStateListener {

    public static final String INPUTSTICK_PACKAGENAME = "com.inputstick.apps.inputstickutility";

    private ProgressDialog progressDialog;

    private String password;
    private String layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        password = getIntent().getStringExtra(Intents.EXTRA_PASSWORD);
        layout = getIntent().getStringExtra(Intents.EXTRA_LAYOUT);

        PackageManager pm = getPackageManager();

        boolean exists = true;
        try {
            pm.getPackageInfo(INPUTSTICK_PACKAGENAME, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            exists = false;
        }

        if (exists) {
            InputStickHID.addStateListener(this);
            int currentState = InputStickHID.getState();
            if (currentState == ConnectionManager.STATE_DISCONNECTED || currentState == ConnectionManager.STATE_FAILURE) {
                InputStickHID.connect(BaseApp.get());
            } else if (currentState == ConnectionManager.STATE_READY) {
                type();
            }
        } else {
            // TODO dialog to download inputstickutility
        }
    }

    @Override
    public void onStateChanged(int state) {
        switch (state) {
            case ConnectionManager.STATE_CONNECTED:
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                break;
            case ConnectionManager.STATE_CONNECTING:
                progressDialog = ProgressDialog.show(this, getString(R.string.title_inputstick), getString(R.string.msg_connecting), true, true, this);
                break;
            case ConnectionManager.STATE_DISCONNECTED:
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                finish();
                break;
            case ConnectionManager.STATE_FAILURE:
                finish();
                MainSnackbar.send(getApplicationContext(), getString(R.string.msg_inputstickFailed));
                break;
            case ConnectionManager.STATE_READY:
                Toast.makeText(this, "ready", Toast.LENGTH_SHORT).show();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                type();
                break;
        }
    }

    private void type() {
        KeyboardLayout keyboardLayout = KeyboardLayout.getLayout(layout);
        keyboardLayout.type(password);
        finish();
        MainSnackbar.send(getApplicationContext(), getString(R.string.msg_typedSuccessfully));
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        InputStickHID.disconnect();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InputStickHID.removeStateListener(this);
    }
}
