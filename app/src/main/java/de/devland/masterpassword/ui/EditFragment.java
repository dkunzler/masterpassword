package de.devland.masterpassword.ui;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lyndir.masterpassword.MPSiteType;
import com.lyndir.masterpassword.MPSiteVariant;
import com.lyndir.masterpassword.MasterKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Category;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.service.ClearClipboardService;
import de.devland.masterpassword.shared.ui.BaseFragment;
import de.devland.masterpassword.shared.util.Utils;
import de.devland.masterpassword.ui.view.OffsetScrollView;
import de.devland.masterpassword.ui.view.SiteCounterView;
import de.devland.masterpassword.util.MasterPasswordHolder;
import de.devland.masterpassword.util.ShowCaseManager;
import lombok.NoArgsConstructor;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class EditFragment extends BaseFragment {

    public static final String ARG_SITE_ID = "de.devland.masterpassword.EditFragment.siteId";
    public static final String ARG_HOSTNAME = "de.devland.masterpassword.EditFragment.hostnamae";

    protected DefaultPrefs defaultPrefs;

    @InjectView(R.id.scrollView)
    protected OffsetScrollView scrollView;
    @InjectView(R.id.editText_siteName)
    protected EditText siteName;
    @InjectView(R.id.textView_userName)
    protected TextView userNameText;
    @InjectView(R.id.editText_userName)
    protected AutoCompleteTextView userName;
    @InjectView(R.id.spinner_passwordType)
    protected Spinner passwordType;
    @InjectView(R.id.spinner_algorithmVersion)
    protected Spinner algorithmVersion;
    @InjectView(R.id.spinner_category)
    protected Spinner categorySpiner;
    @InjectView(R.id.numberPicker_siteCounter)
    protected SiteCounterView siteCounter;
    @InjectView(R.id.password)
    protected TextView password;

    private String[] passwordTypeKeys;
    private String[] algorithmVersionKeys;
    private ArrayAdapter<String> categoryAdapter;

    private long siteId = -1;
    private Site site;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, getActivity());
        Bundle arguments = getArguments();
        String hostname = null;
        if (arguments != null) {
            siteId = arguments.getLong(ARG_SITE_ID, -1);
            hostname = arguments.getString(ARG_HOSTNAME, null);
        }
        site = Site.findById(Site.class, siteId);
        if (site == null) {
            site = new Site();
            site.setPasswordType(MPSiteType.valueOf(defaultPrefs.defaultPasswordType()));
        }
        if (hostname != null) {
            site.setSiteName(hostname);
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
         ((ActionBarActivity) activity).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        passwordTypeKeys = getResources().getStringArray(R.array.passwordTypeKeys);
        algorithmVersionKeys = getResources().getStringArray(R.array.algorithmVersionKeys);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);
        ButterKnife.inject(this, rootView);

        scrollView.setScrollOffset(Math.round(Utils.convertDpToPixel(58f, App.get())));

        SortedSet<String> userNames = new TreeSet<>();

        Iterator<Site> siteIterator = Site.findAll(Site.class);
        while (siteIterator.hasNext()) {
            Site site = siteIterator.next();
            String siteUserName = site.getUserName();
            if (siteUserName != null && !siteUserName.isEmpty()) {
                userNames.add(siteUserName);
            }
        }

        List<String> sortedUserNames = new ArrayList<>();
        for (String siteUserName : userNames) {
            sortedUserNames.add(siteUserName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.select_dialog_item, sortedUserNames);
        userName.setAdapter(adapter);
        userName.setThreshold(1);

        List<Category> categories = defaultPrefs.categories();
        Collections.sort(categories);
        List<String> categoryNames = new ArrayList<>();
        categoryNames.add("");
        for (Category cat : categories) {
            categoryNames.add(cat.getName());
        }
        categoryAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                categoryNames);
        categoryAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        categorySpiner.setAdapter(categoryAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readValues();
        updatePassword();
        siteName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePassword();
            }
        });
        siteCounter.setOnChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updatePassword();
            }
        });
        passwordType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePassword();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        algorithmVersion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePassword();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ShowCaseManager.INSTANCE.showEditShowCase(getActivity(), userNameText);
    }

    private void updatePassword() {
        int passwordTypeIndex = passwordType.getSelectedItemPosition();
        MPSiteType siteType = MPSiteType.valueOf(passwordTypeKeys[passwordTypeIndex]);
        String name = siteName.getText().toString();
        int counter = siteCounter.getValue();
        int algorithmVersionIndex = algorithmVersion.getSelectedItemPosition();
        MasterKey.Version version = MasterKey.Version.valueOf(algorithmVersionKeys[algorithmVersionIndex]);
        if (name != null && name.length() > 0 && MasterPasswordHolder.INSTANCE.getMasterKey(version) != null) {
            password.setVisibility(View.VISIBLE);
            String generatedPassword = MasterPasswordHolder.INSTANCE.generate(siteType, MPSiteVariant.Password, name, counter, version);
            password.setText(generatedPassword);
        } else {
            password.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.password)
    void copyPasswordToClipboard() {
        final ClipboardManager clipboard = (ClipboardManager) App.get()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("password", password.getText());
        clipboard.setPrimaryClip(clip);

        Intent service = new Intent(App.get(), ClearClipboardService.class);
        App.get().startService(service);
    }

    private void readValues() {
        siteName.setText(site.getSiteName());
        userName.setText(site.getUserName());
        updatePasswordTypeSpinner(site.getPasswordType());
        updateCategorySpinner(site.getCategory());
        updateAlgorithmVersionSpinner(site.getAlgorithmVersion());
        siteCounter.setValue(site.getSiteCounter());
    }

    private void updateCategorySpinner(String category) {
        for (int i = 0; i < categoryAdapter.getCount(); i++) {
            String spinnerCategory = categoryAdapter.getItem(i);
            if (spinnerCategory.equals(category)) {
                categorySpiner.setSelection(i, true);
                break;
            }
        }
    }

    private void updatePasswordTypeSpinner(MPSiteType passwordTypeEnum) {
        String passwordTypeName = passwordTypeEnum.toString();
        for (int i = 0; i < passwordTypeKeys.length; i++) {
            String passwordTypeKey = passwordTypeKeys[i];
            if (passwordTypeKey.equals(passwordTypeName)) {
                passwordType.setSelection(i, true);
                break;
            }
        }
    }

    private void updateAlgorithmVersionSpinner(MasterKey.Version algorithmVersionEnum) {
        String algorithmVersionName = algorithmVersionEnum.toString();
        for (int i = 0; i < algorithmVersionKeys.length; i++) {
            String algorithmVersionKey = algorithmVersionKeys[i];
            if (algorithmVersionKey.equals(algorithmVersionName)) {
                algorithmVersion.setSelection(i, true);
                break;
            }
        }
    }

    private void writeValues() {
        site.setSiteName(siteName.getText().toString());
        site.setUserName(userName.getText().toString());
        int passwordTypeIndex = passwordType.getSelectedItemPosition();
        site.setPasswordType(MPSiteType.valueOf(passwordTypeKeys[passwordTypeIndex]));
        int algorithmVersionIndex = algorithmVersion.getSelectedItemPosition();
        site.setAlgorithmVersion(MasterKey.Version.valueOf(algorithmVersionKeys[algorithmVersionIndex]));
        site.setSiteCounter(siteCounter.getValue());
        site.setCategory(categorySpiner.getSelectedItem().toString());
        if (site.complete()) {
            site.touch();
        }
    }

    public void onBackPressed() {
        writeValues();
    }
}
