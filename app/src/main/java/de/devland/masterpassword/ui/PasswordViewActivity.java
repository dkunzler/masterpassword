package de.devland.masterpassword.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.devland.masterpassword.R;
import de.devland.masterpassword.ui.drawer.DrawerItem;
import de.devland.masterpassword.ui.drawer.DrawerItemAdapter;
import de.devland.masterpassword.ui.drawer.ExportDrawerItem;
import de.devland.masterpassword.ui.drawer.ImportDrawerItem;
import de.devland.masterpassword.ui.drawer.LogoutDrawerItem;
import de.devland.masterpassword.ui.drawer.PreferencesDrawerItem;


public class PasswordViewActivity extends LoginRequiringActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerItemAdapter drawerItemAdapter;

    private ImportDrawerItem importDrawerItem;
    private ExportDrawerItem exportDrawerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_view);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PasswordViewFragment())
                    .commit();
        }
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        if (drawerLayout != null) {
            drawerToggle = new ActionBarDrawerToggle(
                    this,                  /* host Activity */
                    drawerLayout,         /* DrawerLayout object */
                    R.string.drawer_open,  /* "open drawer" description */
                    R.string.drawer_close  /* "close drawer" description */
            ) {

                /**
                 * Called when a drawer has settled in a completely closed state.
                 */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);
                    //getActionBar().setTitle(mTitle);
                }

                /**
                 * Called when a drawer has settled in a completely open state.
                 */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    //getActionBar().setTitle(mDrawerTitle);
                }
            };

            // Set the drawer toggle as the DrawerListener
            drawerLayout.setDrawerListener(drawerToggle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        initializeDrawerItems();

        drawerList.setAdapter(drawerItemAdapter);
        drawerList.setOnItemClickListener(this);

        getSupportActionBar().setTitle(R.string.title_passwords);
    }

    private void initializeDrawerItems() {
        List<DrawerItem> drawerItems = new ArrayList<>();
        importDrawerItem = new ImportDrawerItem(this);
        exportDrawerItem = new ExportDrawerItem(this, drawerLayout);
        drawerItems.add(importDrawerItem);
        drawerItems.add(exportDrawerItem);
        drawerItems.add(new PreferencesDrawerItem(drawerLayout));
        drawerItems.add(new LogoutDrawerItem(this));
        drawerItemAdapter = new DrawerItemAdapter(this, drawerItems);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (drawerLayout != null) {
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (drawerLayout != null) {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerLayout != null) {
            if (drawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
        }

        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        drawerItemAdapter.getItem(i).onClick(this);
    }

}
