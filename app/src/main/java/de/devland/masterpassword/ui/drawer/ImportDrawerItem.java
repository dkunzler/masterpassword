package de.devland.masterpassword.ui.drawer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.RadioButton;

import butterknife.ButterKnife;
import de.devland.masterpassword.R;
import de.devland.masterpassword.export.ImportType;
import de.devland.masterpassword.export.Importer;
import de.devland.masterpassword.ui.BaseActivity;
import lombok.RequiredArgsConstructor;

/**
 * Created by David Kunzler on 06.10.2014.
 */
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class ImportDrawerItem extends SettingsDrawerItem {
    public static final int REQUEST_CODE_IMPORT = 1234;

    private final BaseActivity activity;
    private final DrawerLayout drawerLayout;

    @Override
    public int getImageRes() {
        return R.drawable.ic_drawer_import;
    }

    @Override
    public int getHeaderRes() {
        return R.string.caption_import;
    }

    @Override
    public void onClick(Context context) {
        if (drawerLayout != null) {
            drawerLayout.closeDrawers();
        }

        ImportTypeDialog dialog = new ImportTypeDialog();
        dialog.show(activity.getSupportFragmentManager(), null);
    }

    @SuppressLint("ValidFragment")
    public class ImportTypeDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            View dialogView = View.inflate(activity, R.layout.dialog_importtype, null);
            final RadioButton appendRadio = ButterKnife
                    .findById(dialogView, R.id.radioButton_append);
            final RadioButton overrideRadio = ButterKnife
                    .findById(dialogView, R.id.radioButton_override);
            appendRadio.setChecked(true);
            builder.setView(dialogView);
            builder.setTitle(R.string.title_importType);
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Importer importer = new Importer();
                    ImportType importType = ImportType.APPEND;
                    if (overrideRadio.isChecked()) {
                        importType = ImportType.OVERRIDE;
                    }
                    importer.startImportIntent(activity, importType);
                    dismiss();
                }
            });

            return builder.create();
        }
    }
}
