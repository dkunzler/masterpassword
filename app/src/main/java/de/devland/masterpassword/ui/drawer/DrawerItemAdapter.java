package de.devland.masterpassword.ui.drawer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by David Kunzler on 04/09/14.
 */
public class DrawerItemAdapter extends ArrayAdapter<DrawerItem> {
    public DrawerItemAdapter(Context context, List<DrawerItem> objects) {
        super(context, 0, objects);
    }

    @Override
    public int getViewTypeCount() {
        return DrawerItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType().ordinal();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DrawerItem item = getItem(position);

        return item.getView(getContext(), parent);
    }
}
