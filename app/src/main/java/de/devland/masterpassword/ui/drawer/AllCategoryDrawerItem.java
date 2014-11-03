package de.devland.masterpassword.ui.drawer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.otto.Subscribe;

import de.devland.masterpassword.model.Category;
import de.devland.masterpassword.util.event.CategoryChangeEvent;

/**
 * Created by deekay on 02/11/14.
 */
public class AllCategoryDrawerItem extends CategoryDrawerItem {
    public AllCategoryDrawerItem(Context context) {
        super(Category.all(context));
        this.firstLetter = "A";
    }

    @Override
    public View getView(Context context, ViewGroup root) {
        View view = super.getView(context, root);
        deleteCategory.setVisibility(View.GONE);
        return view;
    }

    @Subscribe
    @Override
    public void activeCategoryChanged(CategoryChangeEvent event) {
        super.activeCategoryChanged(event);
    }
}
