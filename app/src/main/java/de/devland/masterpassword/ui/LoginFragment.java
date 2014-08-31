package de.devland.masterpassword.ui;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.R;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.prefs.ShowCasePrefs;
import de.devland.masterpassword.util.GenerateUserKeysAsyncTask;
import de.devland.masterpassword.util.MasterPasswordHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private ShowCasePrefs showCasePrefs;

    @InjectView(R.id.editText_masterPassword)
    protected EditText masterPassword;
    @InjectView(R.id.editText_fullName)
    protected EditText fullName;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!MasterPasswordHolder.INSTANCE.needsLogin(false)) {
            Intent intent = new Intent(getActivity(), PasswordViewActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        }

        showCasePrefs = Esperandro.getPreferences(ShowCasePrefs.class, getActivity());
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

        if (!showCasePrefs.loginShown()) {
            ShowcaseView.Builder showCaseBuilder = new ShowcaseView.Builder(getActivity(), true);
            showCaseBuilder.hideOnTouchOutside()
                    .setContentTitle("Your Credentials")
                    .setStyle(R.style.ShowcaseLightTheme)
                    .setContentText("The combination of your full name and a master password will be used to derive your different site passwords.")
                    .setTarget(new ViewTarget(masterPassword));
            showCaseBuilder.build().show();
            showCasePrefs.loginShown(true);
        }
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
            keysAsyncTask.execute(masterPassword.getText().toString(), fullName.getText().toString());
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
