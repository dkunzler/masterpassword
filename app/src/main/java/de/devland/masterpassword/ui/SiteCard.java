package de.devland.masterpassword.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.service.ClearClipboardService;
import de.devland.masterpassword.util.MasterPasswordHolder;
import it.gmariotti.cardslib.library.internal.Card;
import lombok.Getter;

/**
 * Created by David Kunzler on 24.08.2014.
 */
public class SiteCard extends Card implements Card.OnSwipeListener {

    @Getter
    Site site;

    @InjectView(R.id.siteName)
    TextView siteName;
    @InjectView(R.id.userName)
    TextView userName;
    @InjectView(R.id.password)
    TextView password;

    Handler handler = new Handler();

    DefaultPrefs defaultPrefs;

    public SiteCard(Context context, Site site) {
        super(context, R.layout.card_site);
        this.site = site;
        this.setSwipeable(true);
        this.setId(String.valueOf(site.getId()));
        this.setOnSwipeListener(this);
        this.defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, context);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        ButterKnife.inject(this, view);
        // TODO check for icon visibility when they are there
        siteName.setText(site.getSiteName());
        siteName.setGravity(Gravity.CENTER);
        userName.setText(site.getUserName());
        userName.setGravity(Gravity.CENTER);
        if (site.getUserName().isEmpty()) {
            userName.setVisibility(View.GONE);
        } else {
            userName.setVisibility(View.VISIBLE);
        }
        String generatedPassword = MasterPasswordHolder.INSTANCE.generatePassword(site.getPasswordType(), site.getSiteName(), site.getSiteCounter());
        password.setText(generatedPassword);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/RobotoSlab-Light.ttf");
        password.setTypeface(typeface);
    }

    @Override
    public void onSwipe(Card card) {
        site.delete();
        site.setId(null);
    }

    @OnClick(R.id.password)
    void copyPasswordToClipboard() {
        final ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("password", password.getText());
        clipboard.setPrimaryClip(clip);

        Intent service = new Intent(getContext(), ClearClipboardService.class);
        getContext().startService(service);
    }
}
