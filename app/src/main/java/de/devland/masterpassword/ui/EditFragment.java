package de.devland.masterpassword.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
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
import android.widget.TextView;

import com.lyndir.masterpassword.MPElementType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.util.ShowCaseManager;
import lombok.NoArgsConstructor;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class EditFragment extends Fragment {

    public static final String ARG_SITE_ID = "de.devland.masterpassword.EditFragment.siteId";

    protected DefaultPrefs defaultPrefs;

    @InjectView(R.id.editText_siteName)
    protected EditText siteName;
    @InjectView(R.id.textView_userName)
    protected TextView userNameText;
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
        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getActivity());
        Bundle arguments = getArguments();
        if (arguments != null) {
            siteId = arguments.getLong(ARG_SITE_ID, -1);
        }
        site = Site.findById(Site.class, siteId);
        if (site == null) {
            site = new Site();
            site.setPasswordType(MPElementType.valueOf(defaultPrefs.defaultPasswordType()));
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
                NavUtils.navigateUpFromSameTask(getActivity());
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

        siteCounter.setMinValue(1);
        siteCounter.setMaxValue(Integer.MAX_VALUE);
        siteCounter.setWrapSelectorWheel(false);

        SortedSet<String> userNames = new TreeSet<String>();

        Iterator<Site> siteIterator = Site.findAll(Site.class);
        while (siteIterator.hasNext()) {
            Site site = siteIterator.next();
            String siteUserName = site.getUserName();
            if (siteUserName != null && !siteUserName.isEmpty()) {
                userNames.add(siteUserName);
            }
        }

        List<String> sortedUserNames = new ArrayList<String>();
        for (String siteUserName : userNames) {
            sortedUserNames.add(siteUserName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, sortedUserNames);
        userName.setAdapter(adapter);
        userName.setThreshold(1);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readValues();

        ShowCaseManager.INSTANCE.showEditShowCase(getActivity(), userNameText);
    }

    private void readValues() {
        siteName.setText(site.getSiteName());
        userName.setText(site.getUserName());
        updatePasswordTypeSpinner(site.getPasswordType());
        siteCounter.setValue(site.getSiteCounter());
    }

    private void updatePasswordTypeSpinner(MPElementType passwordTypeEnum) {
        String passwordTypeName = passwordTypeEnum.toString();
        for (int i = 0; i < passwordTypeKeys.length; i++) {
            String passwordTypeKey = passwordTypeKeys[i];
            if (passwordTypeKey.equals(passwordTypeName)) {
                passwordType.setSelection(i, true);
                break;
            }
        }
    }

    private void writeValues() {
        site.setSiteName(siteName.getText().toString());
        site.setUserName(userName.getText().toString());
        int passwordTypeIndex = passwordType.getSelectedItemPosition();
        site.setPasswordType(MPElementType.valueOf(passwordTypeKeys[passwordTypeIndex]));
        site.setSiteCounter(siteCounter.getValue());
        if (site.complete()) {
            site.touch();
        }
    }

    public void onBackPressed() {
        writeValues();
    }
}
