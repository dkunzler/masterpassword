package de.devland.masterpassword.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lyndir.masterpassword.MasterPassword;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.devland.masterpassword.MasterPasswordUtil;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Site;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
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


    public SiteCard(Context context, Site site) {
        super(context, R.layout.card_site);
        this.site = site;
        this.setSwipeable(true);
        this.setId(String.valueOf(site.getId()));
        this.setOnSwipeListener(this);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        ButterKnife.inject(this, view);
        siteName.setText(site.getSiteName());
        userName.setText(site.getUserName());
        byte[] keyForPassword = MasterPasswordUtil.INSTANCE.getKeyForUserName(site.getUserName());
        String generatedPassword = MasterPassword.generateContent(site.getPasswordType(), site.getSiteName(), keyForPassword, site.getSiteCounter());
        password.setText(generatedPassword);
    }

    @Override
    public void onSwipe(Card card) {
        site.delete();
    }
}
