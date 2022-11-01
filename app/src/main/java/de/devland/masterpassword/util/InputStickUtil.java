package de.devland.masterpassword.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.inputstick.api.ConnectionManager;
import com.inputstick.api.InputStickStateListener;
import com.inputstick.api.basic.InputStickHID;
import com.inputstick.api.layout.KeyboardLayout;

import de.devland.masterpassword.R;
import de.devland.masterpassword.base.BaseApp;
import de.devland.masterpassword.base.util.SnackbarUtil;
import lombok.RequiredArgsConstructor;

/**
 * Created by David Kunzler on 25.09.2016.
 */
public class InputStickUtil {

    private static final String INPUTSTICK_PACKAGENAME = "com.inputstick.apps.inputstickutility";

    public static void checkAndType(AppCompatActivity activity, String text, String layout) {
        PackageManager pm = activity.getPackageManager();

        boolean exists = true;
        try {
            pm.getPackageInfo(INPUTSTICK_PACKAGENAME, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            exists = false;
        }

        if (exists) {
            InputStickHID.addStateListener(new TypeListener(activity, text, layout));
            int currentState = InputStickHID.getState();
            if (currentState == ConnectionManager.STATE_DISCONNECTED || currentState == ConnectionManager.STATE_FAILURE) {
                InputStickHID.connect(BaseApp.get());
            } else if (currentState == ConnectionManager.STATE_READY) {
                type(activity, text, layout);
            }
        } else {
            InputStickUtilityDownloadDialog downloadDialog = new InputStickUtilityDownloadDialog(activity);
            downloadDialog.show(activity.getSupportFragmentManager(), null);
        }
    }

    private static void type(AppCompatActivity activity, String text, String layout) {
        KeyboardLayout keyboardLayout = KeyboardLayout.getLayout(layout);
        keyboardLayout.type(text);
        SnackbarUtil.showShort(activity, R.string.msg_typedSuccessfully);
    }

    @RequiredArgsConstructor
    private static class TypeListener implements InputStickStateListener, DialogInterface.OnCancelListener {

        private final AppCompatActivity activity;
        private final String text;
        private final String layout;

        private ProgressDialog progressDialog;

        @Override
        public void onStateChanged(int state) {
            switch (state) {
                case ConnectionManager.STATE_CONNECTED:
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    break;
                case ConnectionManager.STATE_CONNECTING:
                    progressDialog = ProgressDialog.show(activity,
                            activity.getString(R.string.title_inputstick),
                            activity.getString(R.string.msg_connecting), true, true, this);
                    break;
                case ConnectionManager.STATE_DISCONNECTED:
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    break;
                case ConnectionManager.STATE_FAILURE:
                    SnackbarUtil.showShort(activity, R.string.msg_inputstickFailed);
                    break;
                case ConnectionManager.STATE_READY:
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    type(activity, text, layout);
                    break;
            }
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            InputStickHID.disconnect();
        }
    }

    @SuppressLint("ValidFragment")
    public static class InputStickUtilityDownloadDialog extends DialogFragment {
        private AppCompatActivity activity;

        public InputStickUtilityDownloadDialog(AppCompatActivity activity) {
            this.activity = activity;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            builder.setTitle(R.string.title_inputstick);
            builder.setMessage(R.string.msg_inputstickUtilityDownload);
            builder.setCancelable(true);
            builder.setNeutralButton(getString(R.string.caption_homepage), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://inputstick.com/index.php/developers/download")));
                }
            });
            builder.setPositiveButton(R.string.caption_playStore, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + INPUTSTICK_PACKAGENAME)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + INPUTSTICK_PACKAGENAME)));
                    }
                }
            });

            return builder.create();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
        }
    }
}
