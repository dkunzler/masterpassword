package de.devland.masterpassword.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import com.lyndir.lhunath.opal.system.util.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.service.ClearClipboardService;
import de.devland.masterpassword.util.MasterPasswordHolder;
import de.devland.masterpassword.util.SiteCardArrayAdapter;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import lombok.Getter;

/**
 * Created by David Kunzler on 24.08.2014.
 */
public class SiteCard extends Card implements CardHeader.OnClickCardHeaderPopupMenuListener {

    @Getter
    protected Site site;
    protected SiteCardArrayAdapter adapter;

    @InjectView(R.id.card_header_inner_simple_title)
    TextView siteName;
    @InjectView(R.id.userName)
    TextView userName;
    @InjectView(R.id.password)
    TextView password;

    Handler handler = new Handler();

    DefaultPrefs defaultPrefs;
    private String generatedPassword;

    public SiteCard(Context context, Site site, SiteCardArrayAdapter adapter) {
        super(context, R.layout.card_site);
        this.site = site;
        this.adapter = adapter;
        this.setId(String.valueOf(site.getId()));
        this.defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, context);
        CardHeader header = new CardHeader(context);
        header.setPopupMenu(R.menu.card_site, this);
        addCardHeader(header);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        ButterKnife.inject(this, getCardView());
        siteName.setText(site.getSiteName());
        siteName.setTypeface(Typeface.DEFAULT_BOLD);
        userName.setText(site.getUserName());
        if (site.getUserName().isEmpty()) {
            userName.setVisibility(View.GONE);
        } else {
            userName.setVisibility(View.VISIBLE);
        }
        updatePassword();
        Typeface typeface = Typeface
                .createFromAsset(getContext().getAssets(), "fonts/RobotoSlab-Light.ttf");
        password.setTypeface(typeface);
    }

    @OnClick(R.id.password)
    void copyPasswordToClipboard() {
        site.touch();
        final ClipboardManager clipboard = (ClipboardManager) getContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("password", generatedPassword);
        clipboard.setPrimaryClip(clip);

        Intent service = new Intent(getContext(), ClearClipboardService.class);
        getContext().startService(service);
    }


    @Override
    public void onMenuItemClick(BaseCard baseCard, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.card_menu_delete:
                site.delete();
                site.setId(null);
                collapse();
                // TODO undobar
                break;
            case R.id.card_menu_show:
                if (password.getText().equals(generatedPassword)) {
                    password.setText(StringUtils.repeat("•", generatedPassword.length()));
                } else {
                    password.setText(generatedPassword);
                }
                break;
            case R.id.card_menu_increment:
                site.setSiteCounter(site.getSiteCounter() + 1);
                site.touch();
                updatePassword();
                break;
        }
    }

    private void collapse() {
        final View v = getCardView();
        final int initialHeight = v.getMeasuredHeight();

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        anim.setDuration(200);
        v.startAnimation(anim);
    }

    private void updatePassword() {
        generatedPassword = MasterPasswordHolder.INSTANCE
                .generatePassword(site.getPasswordType(), site.getSiteName(),
                        site.getSiteCounter());
        if (defaultPrefs.hidePasswords()) {
            password.setText(StringUtils.repeat("•", generatedPassword.length()));
        } else {
            password.setText(generatedPassword);
        }
    }
}
