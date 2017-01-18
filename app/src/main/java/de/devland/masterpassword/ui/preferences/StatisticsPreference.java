package de.devland.masterpassword.ui.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.google.common.base.Strings;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Site;

/**
 * Created by deekay on 18.01.2017.
 */

public class StatisticsPreference extends DialogPreference {

    @BindView(R.id.textView_passwordCount)
    protected TextView passwordCountText;
    @BindView(R.id.textView_categoryCount)
    protected TextView categoryCountText;
    @BindView(R.id.textView_savedLoginCount)
    protected TextView savedLoginsCountText;
    @BindView(R.id.textView_loginCount)
    protected TextView generatedLoginsCountText;

    public StatisticsPreference(Context context, AttributeSet attributeset) {
        super(context, attributeset);
        setDialogLayoutResource(R.layout.dialog_statistics);
        setPersistent(false);
    }


    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        ButterKnife.bind(this, view);

        int passwordCount = 0;
        Set<String> categories = new HashSet<>();
        int generatedLoginCount = 0;
        int savedLoginCount = 0;

        Iterator<Site> sites = Site.findAll(Site.class);
        while (sites.hasNext()) {
            Site next = sites.next();
            passwordCount++;
            if (next.isGeneratedUserName()) {
                generatedLoginCount++;
            }
            if (!Strings.isNullOrEmpty(next.getStoredPassword())) {
                savedLoginCount++;
            }
            if (!Strings.isNullOrEmpty(next.getCategory())) {
                categories.add(next.getCategory());
            }
        }

        passwordCountText.setText(String.valueOf(passwordCount));
        categoryCountText.setText(String.valueOf(categories.size()));
        generatedLoginsCountText.setText(String.valueOf(generatedLoginCount));
        savedLoginsCountText.setText(String.valueOf(savedLoginCount));
    }
}
