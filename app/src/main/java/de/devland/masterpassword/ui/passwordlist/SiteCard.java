package de.devland.masterpassword.ui.passwordlist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyndir.lhunath.opal.system.util.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
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
import de.devland.masterpassword.shared.util.Utils;
import de.devland.masterpassword.util.event.PasswordCopyEvent;
import de.devland.masterpassword.util.event.SiteCardClickEvent;
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

    public SiteCard(Context context, Site site) {
        super(R.layout.card_site);
        this.context = context;
        this.site = site;

        this.defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, context);
        this.inputStickPrefs = Esperandro.getPreferences(InputStickPrefs.class, context);
    }

    @Override
    public void bindViewHolder(CardAdapter.CardViewHolder cardViewHolder) {
        SiteCardViewHolder viewHolder = (SiteCardViewHolder) cardViewHolder;
        currentViewHolder = viewHolder;
        ButterKnife.bind(this, viewHolder.itemView);
        viewHolder.siteName.setText(site.getSiteName());
        viewHolder.siteName.setTypeface(Typeface.DEFAULT_BOLD);
        viewHolder.siteName.setTextColor(ContextCompat.getColor(context, R.color.text));
        String userName = site.getCurrentUserName();
        viewHolder.userName.setText(userName);
        if (userName.isEmpty()) {
            viewHolder.userName.setVisibility(View.GONE);
        } else {
            viewHolder.userName.setVisibility(View.VISIBLE);
        }
        if (inputStickPrefs.inputstickEnabled()) {
            viewHolder.imageInputStick.setVisibility(View.VISIBLE);
            Utils.tint(context, viewHolder.imageInputStick, R.color.card_icon_tint);
        } else {
            viewHolder.imageInputStick.setVisibility(View.GONE);
        }
        Utils.tint(context, viewHolder.imageMore, R.color.card_icon_tint);
        updatePassword();
        Typeface typeface = Typeface
                .createFromAsset(context.getAssets(), "fonts/RobotoSlab-Light.ttf");
        viewHolder.password.setTypeface(typeface);
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

    @OnClick(R.id.imageInputstick)
    void sentToInputStick() {
            Intent broadcast = new Intent();
            broadcast.setAction(Intents.ACTION_SENDTOINPUTSTICK);
            broadcast.putExtra(Intents.EXTRA_PASSWORD, generatedPassword);
            broadcast.putExtra(Intents.EXTRA_LAYOUT, inputStickPrefs.inputstickKeymap());

            context.sendBroadcast(broadcast);
    }

    @OnClick(R.id.card)
    void onSiteCardClick() {
        App.get().getBus().post(new SiteCardClickEvent(this));
    }

    @OnLongClick(R.id.imageInputstick)
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
        @BindView(R.id.imageInputstick)
        protected ImageView imageInputStick;
        @BindView(R.id.imageMore)
        protected ImageView imageMore;

        public SiteCardViewHolder(View itemView) {
            super(itemView);
        }
    }
}
