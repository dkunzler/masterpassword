package de.devland.masterpassword.ui.drawer;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;

import de.devland.masterpassword.R;
import de.devland.masterpassword.export.ExportType;
import de.devland.masterpassword.export.Exporter;
import lombok.RequiredArgsConstructor;

/**
 * Created by David Kunzler on 06.10.2014.
 */
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class ExportDrawerItem extends SettingsDrawerItem {
    private final Activity activity;
    private final DrawerLayout drawerLayout;

    @Override
    public int getImageRes() {
        return R.drawable.ic_drawer_export;
    }

    @Override
    public int getHeaderRes() {
        return R.string.caption_export;
    }

    @Override
    public void onClick(Context context) {
        if (drawerLayout != null) {
            drawerLayout.closeDrawers();
        }
        // TODO dialog to choose file format
        Exporter exporter = new Exporter();
        exporter.startExportIntent(activity, ExportType.JSON);
    }
}
