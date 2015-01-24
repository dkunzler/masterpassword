package de.devland.masterpassword.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyndir.lhunath.opal.system.util.StringUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.prefs.InputStickPrefs;
import de.devland.masterpassword.service.ClearClipboardService;
import de.devland.masterpassword.shared.util.Intents;
import de.devland.masterpassword.util.MasterPasswordHolder;
import de.devland.masterpassword.util.ProKeyUtil;
import de.devland.masterpassword.util.SiteCardArrayAdapter;
import de.devland.masterpassword.util.event.PasswordCopyEvent;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.view.CardView;
import lombok.Getter;

/**
 * Created by David Kunzler on 24.08.2014.
 */
public class SiteCard extends Card implements PopupMenu.OnMenuItemClickListener {

    public static final String PASSWORD_DOT = "•";
    @Getter
    protected Site site;
    protected SiteCardArrayAdapter adapter;

    @InjectView(R.id.siteName)
    protected TextView siteName;
    @InjectView(R.id.userName)
    protected TextView userName;
    @InjectView(R.id.password)
    protected TextView password;
    @InjectView(R.id.imageInputstick)
    protected ImageView imageInputStick;

    protected InputStickPrefs inputStickPrefs;
    protected DefaultPrefs defaultPrefs;

    private String generatedPassword;

    public SiteCard(Context context, Site site, SiteCardArrayAdapter adapter) {
        super(context, R.layout.card_site);
        this.site = site;
        this.adapter = adapter;
        this.setId(String.valueOf(site.getId()));
        this.defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, context);
        this.inputStickPrefs = Esperandro.getPreferences(InputStickPrefs.class, context);
        setBackgroundResourceId(android.R.color.white);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        super.setupInnerViewElements(parent, view);
        CardView cardView = (CardView) getCardView();
        ButterKnife.inject(this, cardView);
        siteName.setText(site.getSiteName());
        siteName.setTypeface(Typeface.DEFAULT_BOLD);
        siteName.setTextColor(getContext().getResources().getColor(R.color.text));
        userName.setText(site.getUserName());
        if (site.getUserName().isEmpty()) {
            userName.setVisibility(View.GONE);
        } else {
            userName.setVisibility(View.VISIBLE);
        }
        if (inputStickPrefs.inputstickEnabled()) {
            imageInputStick.setVisibility(View.VISIBLE);
        } else {
            imageInputStick.setVisibility(View.GONE);
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
        App.get().getBus().post(new PasswordCopyEvent(this));
    }

    @OnClick(R.id.imageMore)
    void showMoreMenu(ImageView imageMore) {
        PopupMenu popupMenu = new PopupMenu(getContext(), imageMore);
        popupMenu.inflate(R.menu.card_site);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @OnClick(R.id.imageInputstick)
    void sentToInputStick() {
        if (ProKeyUtil.INSTANCE.isPro()) {
            Intent broadcast = new Intent();
            broadcast.setAction(Intents.ACTION_SENDTOINPUTSTICK);
            broadcast.putExtra(Intents.EXTRA_PASSWORD, generatedPassword);
            broadcast.putExtra(Intents.EXTRA_LAYOUT, inputStickPrefs.inputstickKeymap());

            getContext().sendBroadcast(broadcast);
        } else {
            ProKeyUtil.INSTANCE.showGoProDialog(App.get().getCurrentForegroundActivity());
        }
    }

    @OnLongClick(R.id.imageInputstick)
    public boolean showInputStickToast() {
        Toast.makeText(getContext(), R.string.msg_sentToInputstick, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        boolean result = true;
        switch (menuItem.getItemId()) {
            case R.id.card_menu_delete:
                site.delete();
                site.setId(null);
                collapseView(null);
                // TODO undobar
                break;
            case R.id.card_menu_show:
                if (password.getText().equals(generatedPassword)) {
                    password.setText(StringUtils.repeat(PASSWORD_DOT, generatedPassword.length()));
                } else {
                    password.setText(generatedPassword);
                }
                break;
            case R.id.card_menu_increment:
                site.setSiteCounter(site.getSiteCounter() + 1);
                site.touch();
                updatePassword();
                break;
            default:
                result = false;
                break;
        }

        return result;
    }

    protected void collapseView(Animation.AnimationListener animationListener) {
        final View v = (View) getCardView();
        final int initialHeight = v.getMeasuredHeight();
        v.setTag(true);

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

        if (animationListener != null) {
            anim.setAnimationListener(animationListener);
        }
        anim.setDuration(200);
        v.startAnimation(anim);
    }

    private void updatePassword() {
        generatedPassword = MasterPasswordHolder.INSTANCE
                .generatePassword(site.getPasswordType(), site.getSiteName(),
                        site.getSiteCounter());
        if (defaultPrefs.hidePasswords()) {
            password.setText(StringUtils.repeat(PASSWORD_DOT, generatedPassword.length()));
        } else {
            password.setText(generatedPassword);
        }
    }
}
