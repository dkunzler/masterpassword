package de.devland.masterpassword.ui;

import android.os.Bundle;

import de.devland.masterpassword.R;


public class PasswordViewActivity extends LoginRequiringActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_view);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PasswordViewFragment())
                    .commit();
        }
    }

}
