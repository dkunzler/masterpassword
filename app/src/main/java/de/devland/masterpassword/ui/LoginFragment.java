package de.devland.masterpassword.ui;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.lambdaworks.crypto.SCryptUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.R;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.shared.ui.BaseFragment;
import de.devland.masterpassword.util.GenerateUserKeysAsyncTask;
import de.devland.masterpassword.util.MasterPasswordHolder;
import de.devland.masterpassword.util.ShowCaseManager;
import lombok.NoArgsConstructor;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class LoginFragment extends BaseFragment {

    @InjectView(R.id.editText_masterPassword)
    protected EditText masterPassword;
    @InjectView(R.id.editText_fullName)
    protected EditText fullName;

    protected DefaultPrefs defaultPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getActivity());
        if (!MasterPasswordHolder.INSTANCE.needsLogin(false)) {
            Intent intent = new Intent(getActivity(), PasswordViewActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fullName.setText(
                Esperandro.getPreferences(DefaultPrefs.class, getActivity()).defaultUserName());

        ShowCaseManager.INSTANCE.showLoginShowCase(getActivity(), masterPassword);
    }

    @OnClick(R.id.imageView_login)
    public void onClick() {
        if (checkInputs()) {
            Esperandro.getPreferences(DefaultPrefs.class, getActivity())
                    .defaultUserName(fullName.getText().toString());
            GenerateUserKeysAsyncTask keysAsyncTask = new GenerateUserKeysAsyncTask(getActivity(),
                    new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getActivity(), PasswordViewActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }
                    });
            keysAsyncTask
                    .execute(fullName.getText().toString(), masterPassword.getText().toString());
        }
    }

    private boolean checkInputs() {
        boolean result = true;

        if (masterPassword.getText() == null || masterPassword.getText().toString().equals("")) {
            result = false;
            masterPassword.setError(getActivity().getString(R.string.errorEmpty));
        } else if (fullName.getText() == null || fullName.getText().toString().equals("")) {
            result = false;
            fullName.setError(getActivity().getString(R.string.errorEmpty));
        } else if (defaultPrefs.verifyPassword()) {
            try {
                result = SCryptUtil
                        .check(masterPassword.getText().toString(), defaultPrefs.masterPasswordHash());
                if (!result) {
                    masterPassword.setError(getActivity().getString(R.string.error_incorrectPassword));
                }
            } catch (Exception e) {
                result = false;
                defaultPrefs.masterPasswordHash(null);
                defaultPrefs.verifyPassword(false);
                VerificationFailedDialog verificationFailedDialog = new VerificationFailedDialog();
                verificationFailedDialog.show(getActivity().getSupportFragmentManager(), null);
            }
        }
        return result;
    }

    @SuppressLint("ValidFragment")
    public class VerificationFailedDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setPositiveButton(android.R.string.ok, null);

            builder.setTitle(R.string.title_verifyError);
            builder.setMessage(R.string.msg_verifyError);
            Dialog dialog = builder.create();

            return dialog;
        }
    }
}
