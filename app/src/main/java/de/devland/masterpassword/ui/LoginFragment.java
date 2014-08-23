package de.devland.masterpassword.ui;



import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.devland.masterpassword.MasterPasswordUtil;
import de.devland.masterpassword.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class LoginFragment extends Fragment {

    @InjectView(R.id.editText_masterPassword)
    protected EditText masterPassword;
    @InjectView(R.id.button_login)
    protected Button loginButton;

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


    @OnClick(R.id.button_login)
    public void onClick() {
        MasterPasswordUtil.INSTANCE.setMasterPassword(masterPassword.getText().toString());
        Intent intent = new Intent(getActivity(), PasswordViewActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }
}
