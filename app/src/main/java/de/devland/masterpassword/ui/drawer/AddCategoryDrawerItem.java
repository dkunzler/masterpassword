package de.devland.masterpassword.ui.drawer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.common.base.Strings;

import java.util.List;

import butterknife.ButterKnife;
import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.model.Category;
import de.devland.masterpassword.prefs.DefaultPrefs;
import de.devland.masterpassword.ui.BaseActivity;
import de.devland.masterpassword.util.event.ReloadDrawerEvent;

/**
 * Created by deekay on 02/11/14.
 */
public class AddCategoryDrawerItem extends DrawerItem {
    private final BaseActivity activity;

    protected DefaultPrefs defaultPrefs;

    public AddCategoryDrawerItem(BaseActivity activity) {
        super(DrawerItemType.SETTING);
        this.activity = activity;
        defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class, activity);
    }

    @Override
    public View getView(Context context, ViewGroup root) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.drawer_item_addcategory, root, false);
        return view;
    }

    @Override
    public void onClick(Context context) {
        AddCategoryDialog addCategoryDialog = new AddCategoryDialog();
        addCategoryDialog.show(activity.getSupportFragmentManager(), null);
    }

    @SuppressLint("ValidFragment")
    public class AddCategoryDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setNegativeButton(android.R.string.cancel, null);
            builder.setCancelable(false);
            builder.setTitle(R.string.title_addCategory);
            LayoutInflater layoutInflater = LayoutInflater.from(activity);
            View dialogView = layoutInflater.inflate(R.layout.dialog_addcategory, null);
            final EditText category = ButterKnife.findById(dialogView,
                    R.id.editText_category);
            builder.setView(dialogView);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String categoryText = category.getText().toString();
                    if (!Strings.isNullOrEmpty(categoryText)) {
                        List<Category> categories = defaultPrefs.categories();
                        categories.add(new Category(categoryText));
                        defaultPrefs.categories(categories);
                        App.get().getBus().post(new ReloadDrawerEvent());
                    }
                }
            });

            final AlertDialog dialog = builder.create();

            return dialog;
        }
    }
}
