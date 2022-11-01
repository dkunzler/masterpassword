package de.devland.masterpassword.ui.preferences;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.util.Pair;
import androidx.appcompat.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import javax.crypto.Cipher;

import butterknife.ButterKnife;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.base.util.SnackbarUtil;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.util.FingerprintException;
import de.devland.masterpassword.util.FingerprintUtil;

/**
 * Created by deekay on 21.05.2017.
 */

@TargetApi(Build.VERSION_CODES.M)
public class UnlockFingerprintDialog extends DialogFragment {

    private Drawable fingerprint;
    private TextView helpText;

    public boolean success = false;
    private CancellationSignal cancellationSignal;
    private Cipher cipher;
    private FingerprintManager.AuthenticationCallback callback = new FingerprintManager.AuthenticationCallback() {


        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            if (fingerprint != null) {
                DrawableCompat.setTint(fingerprint, Color.RED);
            }
            helpText.setText(errString);
            helpText.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            super.onAuthenticationHelp(helpCode, helpString);
            if (fingerprint != null) {
                DrawableCompat.setTint(fingerprint, Color.YELLOW);
            }
            helpText.setText(helpString);
            helpText.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            super.onAuthenticationSucceeded(result);
            success = true;
            helpText.setText(R.string.fingerprint_dialog_success);
            cipher = result.getCryptoObject().getCipher();
            if (fingerprint != null) {
                DrawableCompat.setTint(fingerprint, Color.GREEN);
            }
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            if (fingerprint != null) {
                DrawableCompat.setTint(fingerprint, Color.RED);
            }
        }
    };


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getActivity());
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                App.get().getBus().post(new UnlockFingerprintPreference.UnlockFingerprintCancelEvent());
                dismiss();
            }
        });
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setCancelable(false);
        builder.setTitle(R.string.title_confirmPassword);
        View dialogView = View.inflate(getContext(), R.layout.dialog_unlockfingerprint, null);
        final EditText masterPassword = dialogView.findViewById(R.id.editText_masterPassword);
        final EditText fullName = dialogView.findViewById(R.id.editText_fullName);
        final ImageView fingerprintIcon = dialogView.findViewById(R.id.imageView_fingerprint);
        helpText = dialogView.findViewById(R.id.textView_fingerprintHelp);

        fullName.setText(defaultPrefs.defaultUserName());
        builder.setView(dialogView);
        fingerprint = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), R.drawable.ic_fingerprint_black_24dp));
        DrawableCompat.setTint(fingerprint, ContextCompat.getColor(getContext(), R.color.login_icon_tint));
        fingerprintIcon.setImageDrawable(fingerprint);


        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    App.get().getBus().post(new UnlockFingerprintPreference.UnlockFingerprintCancelEvent());
                }
                return false;
            }
        });
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @SuppressWarnings("MissingPermission") // already done before showing the dialog
            @Override
            public void onShow(DialogInterface unused) {
                FingerprintManager fingerprintManager = (FingerprintManager) getContext().getSystemService(Context.FINGERPRINT_SERVICE);
                cancellationSignal = new CancellationSignal();
                try {
                    fingerprintManager.authenticate(new FingerprintManager.CryptoObject(FingerprintUtil.initEncryptCipher()), cancellationSignal, 0, callback, null);
                } catch (FingerprintException e) {
                    dismiss();
                    defaultPrefs.fingerprintEnabled(false);
                    Throwable cause = e.getCause();
                    FingerprintUtil.resetFingerprintSettings();
                    if (cause.getClass().getSimpleName().equals("KeyPermanentlyInvalidatedException")) {
                        SnackbarUtil.showLong(getActivity(), R.string.msg_fingerprint_changed);
                    } else {
                        SnackbarUtil.showLong(getActivity(), e.getMessage());
                    }
                }


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
                        String name = fullName.getText().toString();

                        if (success) {
                            Pair<String, String> encryptPair = FingerprintUtil.tryEncrypt(cipher, password, name);
                            if (encryptPair != null) {
                                defaultPrefs.encrypted(encryptPair.first);
                                defaultPrefs.encryptionIV(encryptPair.second);
                                dismiss();
                            } else {
                                // TODO error handling
                            }
                        } else {
                            helpText.setText(R.string.fingerprint_scanNeeded);
                        }
                    }
                });
            }
        });

        return dialog;
    }
}
