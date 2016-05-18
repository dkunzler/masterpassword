package de.devland.masterpassword.ui.drawer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.devland.masterpassword.R;

/**
 * Created by David Kunzler on 04/09/14.
 */
public abstract class SettingsDrawerItem extends DrawerItem {

    @BindView(R.id.textView_settingsItem)
    protected TextView headerText;
    @BindView(R.id.imageView_settingsIcon)
    protected ImageView settingsIcon;


    public SettingsDrawerItem() {
        super(DrawerItemType.SETTING);
    }

    @Override
    public View getView(Context context, ViewGroup root) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.drawer_item_settings, root, false);
        ButterKnife.bind(this, view);
        headerText.setText(getHeaderRes());
        Drawable icon = ContextCompat.getDrawable(context, getImageRes());
        Drawable wrapped = DrawableCompat.wrap(icon);
        DrawableCompat.setTint(wrapped, ContextCompat.getColor(context, R.color.drawer_icon_tint));
        settingsIcon.setImageDrawable(wrapped);
        return view;
    }

    public abstract int getImageRes();

    public abstract int getHeaderRes();

}
