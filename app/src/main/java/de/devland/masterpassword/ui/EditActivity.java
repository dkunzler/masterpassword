package de.devland.masterpassword.ui;

import android.os.Bundle;

import de.devland.masterpassword.R;


public class EditActivity extends LoginRequiringActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        if (savedInstanceState == null) {
            EditFragment editFragment = new EditFragment();
            editFragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction()
                    .add(R.id.container, editFragment)
                    .commit();
        }
    }

}
