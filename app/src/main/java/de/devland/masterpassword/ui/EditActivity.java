package de.devland.masterpassword.ui;

import android.os.Bundle;

import de.devland.masterpassword.R;
import de.devland.masterpassword.shared.ui.BaseActivity;


public class EditActivity extends BaseActivity {

    EditFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        if (savedInstanceState == null) {
            fragment = new EditFragment();
            fragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
        getSupportActionBar().setTitle(R.string.title_activity_edit);
    }

    @Override
    public void onBackPressed() {
        fragment.onBackPressed();
        super.onBackPressed();
    }
}
