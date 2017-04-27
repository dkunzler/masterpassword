package de.devland.masterpassword.ui.preferences;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.lambdaworks.crypto.SCryptUtil;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.util.FingerprintUtil;
import lombok.Setter;

/**
 * Created by David Kunzler on 16.10.2014.
 */
public class UnlockFingerprintPreference extends CheckBoxPreference implements Preference.OnPreferenceChangeListener {

    protected DefaultPrefs defaultPrefs;
    @Setter
    protected AppCompatActivity settingsActivity;

    public UnlockFingerprintPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public UnlockFingerprintPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UnlockFingerprintPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getContext());
        setOnPreferenceChangeListener(this);
        App.get().getBus().register(this);
    }

    @Subscribe
    public void onCancel(UnlockFingerprintCancelEvent event) {
        setChecked(false);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if ((Boolean) newValue) {
            if (!FingerprintUtil.canUseFingerprint(true)) {
                this.setChecked(false);
            } else {
                UnlockFingerprintDialog verifyPasswordDialog = new UnlockFingerprintDialog();
                verifyPasswordDialog.show(settingsActivity.getSupportFragmentManager(), null);
            }
        } else {
            defaultPrefs.masterPasswordHash(null);
        }
        return true;
    }

    public static class UnlockFingerprintCancelEvent {
    }

    @SuppressLint("ValidFragment")
    public static class UnlockFingerprintDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getActivity());
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    App.get().getBus().post(new UnlockFingerprintCancelEvent());
                    dismiss();
                }
            });
            builder.setCancelable(false);
            builder.setTitle(R.string.title_confirmPassword);
            View dialogView = View.inflate(getContext(), R.layout.dialog_unlockfingerprint, null);
            final EditText masterPassword = ButterKnife.findById(dialogView, R.id.editText_masterPassword);
            final EditText fullName = ButterKnife.findById(dialogView, R.id.editText_fullName);
            final ImageView fingerprintIcon = ButterKnife.findById(dialogView, R.id.imageView_fingerprint);

            fullName.setText(defaultPrefs.defaultUserName());
            builder.setView(dialogView);
            Drawable wrapped = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), R.drawable.ic_fingerprint_black_24dp));
            DrawableCompat.setTint(wrapped, ContextCompat.getColor(getContext(), R.color.login_icon_tint));
            fingerprintIcon.setImageDrawable(wrapped);


            final AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        App.get().getBus().post(new UnlockFingerprintCancelEvent());
                    }
                    return false;
                }
            });
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface unused) {
                    Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (masterPassword.getText() == null || masterPassword.getText().toString().equals("")) {
                                masterPassword.setError(getActivity().getString(R.string.errorEmpty));
                                return;
                            } else if (fullName.getText() == null || fullName.getText().toString().equals("")) {
                                fullName.setError(getActivity().getString(R.string.errorEmpty));
                                return;
                            }

                            String password = masterPassword.getText().toString();
                            String passwordConfirm = fullName.getText().toString();

                            if (password.equals(passwordConfirm)) {
                                String passwordHash = SCryptUtil.scrypt(password,
                                        1024, // N
                                        8, // r
                                        1);// p
                                defaultPrefs.masterPasswordHash(passwordHash);
                                dismiss();
                            } else {
                                fullName.setError(getContext().getString(R.string.errorPasswordMatch));
                            }
                        }
                    });
                }
            });

            return dialog;
        }
    }
}
