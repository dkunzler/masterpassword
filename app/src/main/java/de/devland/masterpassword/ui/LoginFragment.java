package de.devland.masterpassword.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.R;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.util.GenerateUserKeysAsyncTask;
import de.devland.masterpassword.util.MasterPasswordHolder;
import de.devland.masterpassword.util.ShowCaseManager;
import lombok.NoArgsConstructor;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class LoginFragment extends Fragment {

    @InjectView(R.id.editText_masterPassword)
    protected EditText masterPassword;
    @InjectView(R.id.editText_fullName)
    protected EditText fullName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        fullName.setText(Esperandro.getPreferences(DefaultPrefs.class, getActivity()).defaultUserName());

        ShowCaseManager.INSTANCE.showLoginShowCase(getActivity(), masterPassword);
    }

    @OnClick(R.id.imageView_login)
    public void onClick() {
        if (checkInputs()) {
            Esperandro.getPreferences(DefaultPrefs.class, getActivity()).defaultUserName(fullName.getText().toString());
            GenerateUserKeysAsyncTask keysAsyncTask = new GenerateUserKeysAsyncTask(getActivity(), new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getActivity(), PasswordViewActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            });
            keysAsyncTask.execute(fullName.getText().toString(), masterPassword.getText().toString());
        }
    }

    private boolean checkInputs() {
        boolean result = true;
        // TODO verify password if set
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
