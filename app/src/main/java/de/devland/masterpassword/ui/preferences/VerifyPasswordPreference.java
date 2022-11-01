package de.devland.masterpassword.ui.preferences;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lambdaworks.crypto.SCryptUtil;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.prefs.DefaultPrefs;
import lombok.Setter;

/**
 * Created by David Kunzler on 16.10.2014.
 */
public class VerifyPasswordPreference extends CheckBoxPreference implements Preference.OnPreferenceChangeListener {

    protected DefaultPrefs defaultPrefs;
    @Setter
    protected AppCompatActivity settingsActivity;

    public VerifyPasswordPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public VerifyPasswordPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerifyPasswordPreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getContext());
        setOnPreferenceChangeListener(this);
        App.get().getBus().register(this);
    }

    @Subscribe
    public void onVerifyPasswordCancel(VerifyPasswordCancelEvent event) {
        setChecked(false);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if ((Boolean) newValue) {
            VerifyPasswordDialog verifyPasswordDialog = new VerifyPasswordDialog();
            verifyPasswordDialog.show(settingsActivity.getSupportFragmentManager(), null);
        } else {
            defaultPrefs.masterPasswordHash(null);
        }
        return true;
    }

    public static class VerifyPasswordCancelEvent {
    }

    @SuppressLint("ValidFragment")
    public static class VerifyPasswordDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getActivity());
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    App.get().getBus().post(new VerifyPasswordCancelEvent());
                    dismiss();
                }
            });
            builder.setCancelable(false);
            builder.setTitle(R.string.title_confirmPassword);
            View dialogView = View.inflate(getContext(), R.layout.dialog_verifypassword, null);
            final EditText masterPassword = dialogView.findViewById(R.id.editText_masterPassword);
            final EditText masterPasswordConfirm = dialogView.findViewById(R.id.editText_masterPasswordConfirm);
            builder.setView(dialogView);


            final AlertDialog dialog = builder.create();
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
                            } else if (masterPasswordConfirm.getText() == null || masterPasswordConfirm.getText().toString().equals("")) {
                                masterPasswordConfirm.setError(getActivity().getString(R.string.errorEmpty));
                                return;
                            }

                            String password = masterPassword.getText().toString();
                            String passwordConfirm = masterPasswordConfirm.getText().toString();

                            if (password.equals(passwordConfirm)) {
                                String passwordHash = SCryptUtil.scrypt(password,
                                        1024, // N
                                        8, // r
                                        1);// p
                                defaultPrefs.masterPasswordHash(passwordHash);
                                dismiss();
                            } else {
                                masterPasswordConfirm.setError(getContext().getString(R.string.errorPasswordMatch));
                            }
                        }
                    });
                }
            });

            return dialog;
        }
    }
}
