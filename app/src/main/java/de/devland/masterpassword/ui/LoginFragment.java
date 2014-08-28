package de.devland.masterpassword.ui;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.devland.masterpassword.MasterPasswordUtil;
import de.devland.masterpassword.R;
import de.devland.masterpassword.util.GenerateUserKeysAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    @InjectView(R.id.editText_masterPassword)
    protected EditText masterPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }


    @OnClick(R.id.imageView_login)
    public void onClick() {
        MasterPasswordUtil.INSTANCE.setMasterPassword(masterPassword.getText().toString());
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
