package de.devland.masterpassword.ui.preferences;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.base.util.RequestCodeManager;
import de.devland.masterpassword.prefs.FileSyncPrefs;

/**
 * Created by deekay on 26.10.2017.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class FileSyncPreference extends DialogPreference implements RequestCodeManager.RequestCodeCallback {

    public static final int REQUEST_CODE_FILE_SYNC = 123456;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FileSyncPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public FileSyncPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FileSyncPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FileSyncPreference(Context context) {
        super(context);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setTitle("File Sync")
                .setMessage("You can now choose an existing file that will be used for automatic " +
                        "syncing. This means that for each change this file will get updated and on " +
                        "App start the file will be read to incorporate changes into the App.")
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("FileSync", "onClick: " + i);
                            startFilePersist();
                    }
                });

        super.onPrepareDialogBuilder(builder);
    }

    protected void startFilePersist() {
        Log.e("FileSync", "startFilePersist");
        Intent intent;
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType("*/*");

        Bundle extraData = new Bundle();
        int requestCode = RequestCodeManager.INSTANCE
                .addRequest(REQUEST_CODE_FILE_SYNC, this.getClass(), this, extraData);
        App.get().getCurrentForegroundActivity().startActivityForResult(intent, requestCode);
    }

    @Override
    public void run(int resultCode, Intent intent, Bundle data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (intent != null) {
                uri = intent.getData();
                if (uri != null) {
                    final int takeFlags = intent.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
// Check for the freshest data.
                    App.get().getContentResolver().takePersistableUriPermission(uri, takeFlags);
                    FileSyncPrefs fileSyncPrefs = Esperandro.getPreferences(FileSyncPrefs.class, App.get());
                    fileSyncPrefs.fileSyncActivated(true);

                    fileSyncPrefs.fileSyncUri(intent.getDataString());
                    Log.e("FileSync", "activated file sync for " + uri);
                }
            }
        }
    }
}
