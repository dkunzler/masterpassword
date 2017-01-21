package de.devland.masterpassword.ui;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.base.ui.BaseFragment;
import de.devland.masterpassword.model.Category;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.service.ClearClipboardService;
import de.devland.masterpassword.ui.view.SiteCounterView;
import de.devland.masterpassword.util.MasterPasswordHolder;
import de.devland.masterpassword.util.ShowCaseManager;
import lombok.NoArgsConstructor;

import static de.devland.masterpassword.util.MPUtils.extractMPSiteParameters;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class EditFragment extends BaseFragment {

    public static final String ARG_SITE_ID = "de.devland.masterpassword.EditFragment.siteId";
    public static final String ARG_HOSTNAME = "de.devland.masterpassword.EditFragment.hostnamae";

    protected DefaultPrefs defaultPrefs;

    @BindView(R.id.scrollView)
    protected ScrollView scrollView;
    @BindView(R.id.editText_siteName)
    protected EditText siteName;
    @BindView(R.id.editText_userName)
    protected AutoCompleteTextView userName;
    @BindView(R.id.spinner_passwordType)
    protected Spinner passwordType;
    @BindView(R.id.spinner_algorithmVersion)
    protected Spinner algorithmVersion;
    @BindView(R.id.spinner_category)
    protected Spinner categorySpinner;
    @BindView(R.id.numberPicker_siteCounter)
    protected SiteCounterView siteCounter;
    @BindView(R.id.password)
    protected TextView password;
    @BindView(R.id.checkbox_generateUsername)
    protected CheckBox generatedUsername;

    private String[] passwordTypeKeys;
    private String[] algorithmVersionKeys;
    private ArrayAdapter<String> categoryAdapter;

    private long siteId = -1;
    private Site site;

    private TextWatcher updatePasswordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            updatePasswordAndLogin();
        }
    };
    private AdapterView.OnItemSelectedListener updatePasswordItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            updatePasswordAndLogin();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
    private CompoundButton.OnCheckedChangeListener updateGeneratedUserNameCheckedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            updatePasswordAndLogin();
        }
    };


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
            Pair<MPSiteType, MPSiteVariant> passwordTypeParameters = extractMPSiteParameters(defaultPrefs.defaultPasswordType());
            site.setPasswordType(passwordTypeParameters.first);
            site.setPasswordVariant(passwordTypeParameters.second);
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
        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        passwordTypeKeys = getResources().getStringArray(R.array.passwordTypeKeys);
        algorithmVersionKeys = getResources().getStringArray(R.array.algorithmVersionKeys);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);
        ButterKnife.bind(this, rootView);

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
        categorySpinner.setAdapter(categoryAdapter);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Typeface typeface = Typeface
                .createFromAsset(getActivity().getAssets(), "fonts/RobotoSlab-Light.ttf");
        password.setTypeface(typeface);
        readValues();
        updatePasswordAndLogin();
        siteName.addTextChangedListener(updatePasswordTextWatcher);
        siteCounter.setOnChangeListener(updatePasswordTextWatcher);
        passwordType.setOnItemSelectedListener(updatePasswordItemSelectedListener);
        algorithmVersion.setOnItemSelectedListener(updatePasswordItemSelectedListener);
        generatedUsername.setOnCheckedChangeListener(updateGeneratedUserNameCheckedListener);

        ShowCaseManager.INSTANCE.showEditShowCase(getActivity(), userName);
    }

    private void updatePasswordAndLogin() {
        int passwordTypeIndex = passwordType.getSelectedItemPosition();
        String passwordTypeKey = passwordTypeKeys[passwordTypeIndex];
        Pair<MPSiteType, MPSiteVariant> siteParameters = extractMPSiteParameters(passwordTypeKey);
        MPSiteType siteType = siteParameters.first;
        MPSiteVariant variant = siteParameters.second;
        String name = siteName.getText().toString();
        int counter = siteCounter.getValue();
        int algorithmVersionIndex = algorithmVersion.getSelectedItemPosition();
        MasterKey.Version version = MasterKey.Version.valueOf(algorithmVersionKeys[algorithmVersionIndex]);
        if (name.length() > 0 && MasterPasswordHolder.INSTANCE.getMasterKey(version) != null) {
            //password.setVisibility(View.VISIBLE);
            String generatedPassword = MasterPasswordHolder.INSTANCE.generate(siteType, variant, name, counter, version);
            password.setText(generatedPassword);
            if (generatedUsername.isChecked()) {
                String generatedUserName = MasterPasswordHolder.INSTANCE.generate(MPSiteType.GeneratedName, MPSiteVariant.Login, name, counter, version);
                userName.setText(generatedUserName);
            }
        } else {
            password.setText(R.string.msg_previewNotAvailable);
            if (generatedUsername.isChecked()) {
                userName.setText(R.string.msg_previewNotAvailable);
            }
        }

        if (generatedUsername.isChecked()) {
            userName.setEnabled(false);
        } else {
            userName.setEnabled(true);
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
        updatePasswordTypeSpinner(site.getPasswordType(), site.getPasswordVariant());
        updateCategorySpinner(site.getCategory());
        updateAlgorithmVersionSpinner(site.getAlgorithmVersion());
        siteCounter.setValue(site.getSiteCounter());
        generatedUsername.setChecked(site.isGeneratedUserName());
        userName.setEnabled(!site.isGeneratedUserName());
        userName.setText(site.getCurrentUserName());
    }

    private void updateCategorySpinner(String category) {
        for (int i = 0; i < categoryAdapter.getCount(); i++) {
            String spinnerCategory = categoryAdapter.getItem(i);
            if (spinnerCategory.equals(category)) {
                categorySpinner.setSelection(i, true);
                break;
            }
        }
    }

    private void updatePasswordTypeSpinner(MPSiteType passwordTypeEnum, MPSiteVariant passwordVariant) {
        String passwordTypeName = passwordTypeEnum.toString();
        String passwordVariantName = passwordVariant.toString();
        String spinnerValue = passwordTypeName + ":" + passwordVariantName;
        for (int i = 0; i < passwordTypeKeys.length; i++) {
            String passwordTypeKey = passwordTypeKeys[i];
            if (passwordTypeKey.equals(spinnerValue)) {
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
        site.setGeneratedUserName(generatedUsername.isChecked());
        if (site.isGeneratedUserName()) {
            site.setUserName(null);
        } else {
            site.setUserName(userName.getText().toString());
        }
        int passwordTypeIndex = passwordType.getSelectedItemPosition();
        String passwordTypeVariant = passwordTypeKeys[passwordTypeIndex];
        Pair<MPSiteType, MPSiteVariant> passwordTypeParameters = extractMPSiteParameters(passwordTypeVariant);
        site.setPasswordType(passwordTypeParameters.first);
        site.setPasswordVariant(passwordTypeParameters.second);
        int algorithmVersionIndex = algorithmVersion.getSelectedItemPosition();
        site.setAlgorithmVersion(MasterKey.Version.valueOf(algorithmVersionKeys[algorithmVersionIndex]));
        site.setSiteCounter(siteCounter.getValue());
        site.setCategory(categorySpinner.getSelectedItem().toString());
        if (site.complete()) {
            site.change();
        }
    }

    public void onBackPressed() {
        writeValues();
    }
}
