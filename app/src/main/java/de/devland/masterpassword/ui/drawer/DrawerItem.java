package de.devland.masterpassword.ui.drawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by David Kunzler on 04/09/14.
 */
@RequiredArgsConstructor(suppressConstructorProperties = true)
public abstract class DrawerItem {

    @Getter
    private final DrawerItemType type;

    public abstract View getView(Context context, ViewGroup root);

    public abstract void onClick(Context context);

}
