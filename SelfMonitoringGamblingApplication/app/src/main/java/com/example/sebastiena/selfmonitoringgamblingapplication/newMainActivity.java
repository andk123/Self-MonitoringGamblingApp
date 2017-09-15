package com.example.sebastiena.selfmonitoringgamblingapplication;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class newMainActivity extends AppCompatActivity implements GamblingSessionFragment.OnFragmentInteractionListener,SessionsFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener, GraphsFragment.OnFragmentInteractionListener,TimePickerDialog.OnTimeSetListener {
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvView;
    private ActionBarDrawerToggle drawerToggle;
    private Fragment fragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);

        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(drawerToggle);


        nvView = (NavigationView)findViewById(R.id.nvView);
        setupDrawerContent(nvView);
        getSupportFragmentManager().beginTransaction().add(R.id.flContent, new HomeFragment()).commit();


    }
    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        fragment = null;
        Class fragmentClass = null;
        boolean logout = false;
        switch(menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragmentClass = GamblingSessionFragment.class;
                break;
            case R.id.nav_second_fragment:
                fragmentClass = SessionsFragment.class;
                break;
            case R.id.nav_third_fragment:
                fragmentClass = GraphsFragment.class;
                break;
            case R.id.nav_fourth_fragment:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.nav_fifth_fragment:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_sixth_fragment:
                logout = true;
                break;
            default:
                fragmentClass = null;
        }

        if (logout){
            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Close the navigation drawer
            mDrawer.closeDrawers();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            try {
                auth.signOut();
                startActivity(new Intent(newMainActivity.this, SigninActivity.class));
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        else {

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            // Highlight the selected item has been done by NavigationView
            menuItem.setChecked(true);
            // Set action bar title
            setTitle(menuItem.getTitle());
            // Close the navigation drawer
            mDrawer.closeDrawers();
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        fragment = getSupportFragmentManager().findFragmentById(R.id.flContent);
        GamblingSessionFragment gs = (GamblingSessionFragment) fragment;
        gs.onTimeSet(view,hourOfDay,minute);
    }

}
