package de.devland.masterpassword.ui.passwordlist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.PersistableBundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyndir.lhunath.opal.system.util.StringUtils;

import org.joda.time.DateTime;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.Optional;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.base.util.Utils;
import de.devland.masterpassword.model.Site;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.prefs.InputStickPrefs;
import de.devland.masterpassword.service.ClearClipboardService;
import de.devland.masterpassword.util.InputStickUtil;
import de.devland.masterpassword.util.event.PasswordCopyEvent;
import de.devland.masterpassword.util.event.SiteCardEditEvent;
import de.devland.masterpassword.util.event.SiteDeleteEvent;
import lombok.Getter;

/**
 * Created by deekay on 07.06.2015.
 */
public class SiteCard extends Card implements PopupMenu.OnMenuItemClickListener {
    public static final String PASSWORD_DOT = "•";


    @Getter
    protected Site site;
    protected SiteCardViewHolder currentViewHolder;

    protected Context context;
    protected InputStickPrefs inputStickPrefs;
    protected DefaultPrefs defaultPrefs;

    private String generatedPassword;
    private String userName;
    private String siteName;

    public SiteCard(Context context, Site site) {
        this.context = context;
        this.site = site;

        this.defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, context);
        this.inputStickPrefs = Esperandro.getPreferences(InputStickPrefs.class, context);
        this.layoutId = inputStickPrefs.inputstickEnabled() ? R.layout.card_site_inputstick : R.layout.card_site;
    }

    @Override
    public void bindViewHolder(CardAdapter.CardViewHolder cardViewHolder) {
        SiteCardViewHolder viewHolder = (SiteCardViewHolder) cardViewHolder;
        currentViewHolder = viewHolder;
        ButterKnife.bind(this, viewHolder.itemView);
        siteName = site.getSiteName();
        viewHolder.siteName.setText(siteName);
        viewHolder.siteName.setTypeface(Typeface.DEFAULT_BOLD);
        viewHolder.siteName.setTextColor(ContextCompat.getColor(context, R.color.text));
        userName = site.getCurrentUserName();
        viewHolder.userName.setText(userName);
        if (userName.isEmpty()) {
            viewHolder.userName.setVisibility(View.GONE);
        } else {
            viewHolder.userName.setVisibility(View.VISIBLE);
        }
        if (inputStickPrefs.inputstickEnabled()) {
            Utils.tint(context, viewHolder.imageInputStickPassword, R.color.card_icon_tint);
            Utils.tint(context, viewHolder.imageInputStickSitename, R.color.card_icon_tint);
            Utils.tint(context, viewHolder.imageInputStickUsername, R.color.card_icon_tint);
        }
        Utils.tint(context, viewHolder.imageMore, R.color.card_icon_tint);
        updatePassword();
        Typeface typeface = Typeface
                .createFromAsset(context.getAssets(), "fonts/RobotoSlab-Light.ttf");
        viewHolder.password.setTypeface(typeface);
        int textColor = android.R.color.white;
        int passwordColor = R.color.password_normal;
        if (defaultPrefs.visualizePasswordAge()) {
            long lastChange = site.getLastChange().getTime();
            long now = new Date().getTime();
            long week = 1000 * 60 * 60 * 24 * 7;
            long weeksModerate = Integer.parseInt(defaultPrefs.passwordAgeModerate()) * week;
            long weeksCritical = Integer.parseInt(defaultPrefs.passwordAgeCritical()) * week;
            long diff = now - lastChange;
            new DateTime(lastChange);
            if (diff >= weeksModerate) {
                if (diff >= weeksCritical) {
                    textColor = android.R.color.white;
                    passwordColor = R.color.password_critical;
                } else {
                    textColor = android.R.color.black;
                    passwordColor = R.color.password_moderate;
                }
            }
        }
        viewHolder.password.setTextColor(ContextCompat.getColor(context, textColor));
        viewHolder.password.setBackgroundColor(ContextCompat.getColor(context, passwordColor));
    }

    @Override
    public long getId() {
        if (site != null) {
            return site.getId();
        } else {
            return -1;
        }
    }

    @Override
    public String getTitle() {
        return site.getSiteName();
    }

    @OnClick(R.id.password)
    void copyPasswordToClipboard() {
        site.touch();
        final ClipboardManager clipboard = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("password", generatedPassword);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PersistableBundle extras = new PersistableBundle();
            extras.putBoolean("android.content.extra.IS_SENSITIVE", true);
            clip.getDescription().setExtras(extras);
        }
        clipboard.setPrimaryClip(clip);

        Intent service = new Intent(context, ClearClipboardService.class);
        context.startService(service);
        App.get().getBus().post(new PasswordCopyEvent(this));
    }

    @OnClick(R.id.imageMore)
    void showMoreMenu(ImageView imageMore) {
        PopupMenu popupMenu = new PopupMenu(context, imageMore);
        popupMenu.inflate(R.menu.card_site);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Optional
    @OnClick(R.id.imageInputstickPassword)
    void sendPasswordToInputStick() {
        InputStickUtil.checkAndType(App.get().getCurrentForegroundActivity(), generatedPassword, inputStickPrefs.inputstickKeymap());
    }

    @Optional
    @OnClick(R.id.imageInputstickUsername)
    void sendUsernameToInputStick() {
        InputStickUtil.checkAndType(App.get().getCurrentForegroundActivity(), userName, inputStickPrefs.inputstickKeymap());
    }

    @Optional
    @OnClick(R.id.imageInputstickSitename)
    void sendSitenameToInputStick() {
        InputStickUtil.checkAndType(App.get().getCurrentForegroundActivity(), siteName, inputStickPrefs.inputstickKeymap());
    }

    @Optional
    @OnLongClick({R.id.imageInputstickPassword, R.id.imageInputstickUsername, R.id.imageInputstickSitename})
    public boolean showInputStickToast() {
        Toast.makeText(context, R.string.msg_sentToInputstick, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        boolean result = true;
        switch (menuItem.getItemId()) {
            case R.id.card_menu_delete:
                site.delete();
                site.setId(null);
                App.get().getBus().post(new SiteDeleteEvent(this));
                // TODO undobar
                break;
            case R.id.card_menu_edit:
                App.get().getBus().post(new SiteCardEditEvent(this));
                break;
            case R.id.card_menu_show:
                if (currentViewHolder.password.getText().equals(generatedPassword)) {
                    currentViewHolder.password.setText(StringUtils.repeat(PASSWORD_DOT, generatedPassword.length()));
                } else {
                    currentViewHolder.password.setText(generatedPassword);
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

    private void updatePassword() {
        generatedPassword = site.getCurrentPassword();
        if (defaultPrefs.hidePasswords()) {
            currentViewHolder.password.setText(StringUtils.repeat(PASSWORD_DOT, generatedPassword.length()));
        } else {
            currentViewHolder.password.setText(generatedPassword);
        }
    }

    public static class SiteCardViewHolder extends CardAdapter.CardViewHolder {
        @BindView(R.id.siteName)
        protected TextView siteName;
        @BindView(R.id.userName)
        protected TextView userName;
        @BindView(R.id.password)
        protected TextView password;
        @BindView(R.id.imageInputstickPassword)
        @Nullable
        protected ImageView imageInputStickPassword;
        @BindView(R.id.imageInputstickSitename)
        @Nullable
        protected ImageView imageInputStickSitename;
        @Nullable
        @BindView(R.id.imageInputstickUsername)
        protected ImageView imageInputStickUsername;
        @BindView(R.id.imageMore)
        protected ImageView imageMore;

        public SiteCardViewHolder(View itemView) {
            super(itemView);
        }
    }
}
