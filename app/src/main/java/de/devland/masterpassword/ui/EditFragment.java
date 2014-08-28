package de.devland.masterpassword.ui;


import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.lyndir.masterpassword.MPElementType;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Site;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    public static final String ARG_SITE_ID = "de.devland.masterpassword.EditFragment.siteId";

    @InjectView(R.id.editText_siteName)
    protected EditText siteName;
    @InjectView(R.id.editText_userName)
    protected AutoCompleteTextView userName;
    @InjectView(R.id.spinner_passwordType)
    protected Spinner passwordType;
    @InjectView(R.id.numberPicker_siteCounter)
    protected NumberPicker siteCounter;

    private String[] passwordTypeValues;
    private String[] passwordTypeKeys;

    private long siteId = -1;
    private Site site;

    public EditFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            siteId = arguments.getLong(ARG_SITE_ID, -1);
        }
        site = Site.findById(Site.class, siteId);
        if (site == null) {
            site = new Site();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        passwordTypeKeys = getResources().getStringArray(R.array.passwordTypeKeys);
        passwordTypeValues = getResources().getStringArray(R.array.passwordTypeValues);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);
        ButterKnife.inject(this, rootView);

        siteCounter.setMinValue(0);
        siteCounter.setMaxValue(Integer.MAX_VALUE);
        siteCounter.setWrapSelectorWheel(false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        readValues();
    }

    @Override
    public void onPause() {
        super.onPause();
        writeValues();
    }

    private void readValues() {
        siteName.setText(site.getSiteName());
        userName.setText(site.getUserName());
        String passwordTypeName = site.getPasswordType().toString();
        for (int i = 0; i < passwordTypeKeys.length; i++) {
            String passwordTypeKey = passwordTypeKeys[i];
            if (passwordTypeKey.equals(passwordTypeName)) {
                passwordType.setSelection(i, true);
                break;
            }
        }
        siteCounter.setValue(site.getSiteCounter());
    }

    private void writeValues() {
        site.setSiteName(siteName.getText().toString());
        site.setUserName(userName.getText().toString());
        int passwordTypeIndex = passwordType.getSelectedItemPosition();
        site.setPasswordType(MPElementType.valueOf(passwordTypeKeys[passwordTypeIndex]));
        site.setSiteCounter(siteCounter.getValue());
        if (site.complete()) {
            site.save();
        }
    }
}
