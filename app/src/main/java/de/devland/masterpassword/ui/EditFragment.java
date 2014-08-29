package de.devland.masterpassword.ui;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.lyndir.masterpassword.MPElementType;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Site;
import lombok.NoArgsConstructor;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
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

    private String[] passwordTypeKeys;

    private long siteId = -1;
    private Site site;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
            case R.id.action_save:
                writeValues();
            case R.id.action_cancel:
                getActivity().navigateUpTo(getActivity().getParentActivityIntent());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        passwordTypeKeys = getResources().getStringArray(R.array.passwordTypeKeys);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);
        ButterKnife.inject(this, rootView);

        siteCounter.setMinValue(0);
        siteCounter.setMaxValue(Integer.MAX_VALUE);
        siteCounter.setWrapSelectorWheel(false);

        // TODO fill adapter with usernames
        // TODO pre fill with default user name when new site
        // TODO add x button to editText
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, Arrays.asList("test"));
        userName.setAdapter(adapter);
        userName.setThreshold(1);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readValues();
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
