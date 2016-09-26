package de.devland.masterpassword.ui.drawer;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RadioButton;

import butterknife.ButterKnife;
import de.devland.masterpassword.R;
import de.devland.masterpassword.base.ui.BaseActivity;
import de.devland.masterpassword.export.ExportType;
import de.devland.masterpassword.export.Exporter;
import lombok.RequiredArgsConstructor;

/**
 * Created by David Kunzler on 06.10.2014.
 */
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class ExportDrawerItem extends SettingsDrawerItem {
    private final BaseActivity activity;
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
        FileFormatDialog dialog = new FileFormatDialog();
        dialog.show(activity.getSupportFragmentManager(), null);
    }

    @SuppressLint("ValidFragment")
    public class FileFormatDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            View dialogView = View.inflate(activity, R.layout.dialog_fileformat, null);
            final RadioButton jsonRadio = ButterKnife.findById(dialogView, R.id.radioButton_json);
            final RadioButton mpsitesRadio = ButterKnife
                    .findById(dialogView, R.id.radioButton_mpsites);
            jsonRadio.setChecked(true);
            builder.setView(dialogView);
            builder.setTitle(R.string.title_exportFormat);
            builder.setNegativeButton(android.R.string.cancel, null);
            int okButtonTextResource = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                    ? android.R.string.ok
                    : R.string.caption_selectFolder;
            builder.setPositiveButton(okButtonTextResource, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Exporter exporter = new Exporter();
                    ExportType exportType = ExportType.JSON;
                    if (mpsitesRadio.isChecked()) {
                        exportType = ExportType.MPSITES;
                    }
                    exporter.startExportIntent(activity, exportType);
                    dismiss();
                }
            });

            return builder.create();
        }
    }
}
