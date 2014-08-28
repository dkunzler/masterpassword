package de.devland.masterpassword.ui;


import android.app.AlarmManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.MasterPasswordUtil;
import de.devland.masterpassword.R;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.util.GenerateUserKeysAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    @InjectView(R.id.editText_masterPassword)
    protected EditText masterPassword;
    @InjectView(R.id.editText_fullName)
    protected EditText fullName;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, rootView);
        fullName.setText(Esperandro.getPreferences(DefaultPrefs.class, getActivity()).defaultUserName());
        return rootView;
    }


    @OnClick(R.id.imageView_login)
    public void onClick() {
        if (checkInputs()) {
            MasterPasswordUtil.INSTANCE.setMasterPassword(masterPassword.getText().toString());
            Esperandro.getPreferences(DefaultPrefs.class, getActivity()).defaultUserName(fullName.getText().toString());
            GenerateUserKeysAsyncTask keysAsyncTask = new GenerateUserKeysAsyncTask(getActivity(), new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getActivity(), PasswordViewActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            });
            keysAsyncTask.execute();
        }
    }

    private boolean checkInputs() {
        boolean result = true;
        if (masterPassword.getText() == null || masterPassword.getText().toString().equals("")) {
            result = false;
            masterPassword.setError(getActivity().getString(R.string.errorEmpty));
        }
        if (fullName.getText() == null || fullName.getText().toString().equals("")) {
            result = false;
            fullName.setError(getActivity().getString(R.string.errorEmpty));
        }
        return result;
    }
}
