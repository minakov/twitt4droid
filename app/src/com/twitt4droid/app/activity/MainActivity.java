/*
 * Copyright 2014 Daniel Pedraza-Arcega
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitt4droid.app.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.twitt4droid.Twitt4droid;
import com.twitt4droid.activity.UserProfileActivity;
import com.twitt4droid.app.R;
import com.twitt4droid.app.fragment.CustomFixedQueryTimelineFragment;
import com.twitt4droid.app.fragment.CustomQueryableTimelineFragment;
import com.twitt4droid.app.fragment.HomeFragment;
import com.twitt4droid.app.fragment.ListsFragment;
import com.twitt4droid.app.widget.DrawerItem;
import com.twitt4droid.app.widget.DrawerItemAdapter;
import com.twitt4droid.task.ImageLoader;
import com.twitt4droid.util.Strings;

import twitter4j.TwitterException;

import twitter4j.Twitter;
import twitter4j.User;

public class MainActivity extends ActionBarActivity {

    private static final String FRAGMENT_TAG = "CURRENT_FRAGMENT";
    private static final String CURRENT_TITLE_KEY = "CURRENT_TITLE";

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;
    private int currentTitleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpDrawer();
        if (savedInstanceState == null) {
            setUpFragment(new HomeFragment());
            setTitle(R.string.drawer_home_option);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_TITLE_KEY, currentTitleId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setTitle(savedInstanceState.getInt(CURRENT_TITLE_KEY));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Twitt4droid.isUserLoggedIn(getApplicationContext())) {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setUpDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,                
                drawerLayout,         
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.app_name);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(currentTitleId);
                supportInvalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setUpDrawerMenu();
    }

    private void setUpDrawerMenu() {
        drawerList = (ListView) drawerLayout.findViewById(R.id.left_drawer);
        DrawerItemAdapter drawerMenuAdapter = new DrawerItemAdapter(this);
        drawerMenuAdapter.add(new DrawerItem(R.drawable.twitt4droid_ic_home_holo_dark, R.string.drawer_home_option));
        drawerMenuAdapter.add(new DrawerItem(R.drawable.twitt4droid_ic_clock_holo_dark, R.string.drawer_lists_option));
        drawerMenuAdapter.add(new DrawerItem(R.drawable.twitt4droid_ic_hashtag_holo_dark, R.string.drawer_fixed_search_option));
        drawerMenuAdapter.add(new DrawerItem(R.drawable.twitt4droid_ic_search_holo_dark, R.string.drawer_search_option));
        drawerMenuAdapter.add(new DrawerItem(R.drawable.ic_settings, R.string.drawer_settings_option));
        View drawerListHeader = getLayoutInflater().inflate(R.layout.drawer_header, null);
        new DrawerHeaderSetUpTask(drawerListHeader).execute();
        drawerList.addHeaderView(drawerListHeader);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerList.setAdapter(drawerMenuAdapter);
    }
    
    private void setUpFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        if (currentFragment == null || !currentFragment.getClass().equals(fragment.getClass())) {
            fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment, FRAGMENT_TAG)
                .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setTitle(int titleId) {
        currentTitleId = titleId;
        getSupportActionBar().setTitle(currentTitleId);
    }

    private class DrawerHeaderSetUpTask extends AsyncTask<Void, Void, User> {

        private final View drawerHeader;
        private final Twitter twitter;
        private final String screenName;
        
        private DrawerHeaderSetUpTask(View drawerHeader) {
            this.drawerHeader = drawerHeader;
            this.twitter = Twitt4droid.getTwitter(MainActivity.this);
            this.screenName = Twitt4droid.getCurrentUser(MainActivity.this).getScreenName();
        }
        
        @Override
        protected void onPreExecute() {
            setUpUser(Twitt4droid.getCurrentUser(MainActivity.this));
        }
    
        @Override
        protected User doInBackground(Void... params) {
            try {
                return twitter.showUser(screenName);
            } catch (TwitterException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(User result) {
            if (result != null) setUpUser(result);
        }
        
        private void setUpUser(User user) {
            ImageView userProfileBannerImage = (ImageView) drawerHeader.findViewById(R.id.user_profile_banner_image);
            ImageView userProfileImage = (ImageView) drawerHeader.findViewById(R.id.user_profile_image);
            TextView userScreenName = (TextView) drawerHeader.findViewById(R.id.user_screen_name);
            TextView userName = (TextView) drawerHeader.findViewById(R.id.user_name);
            userScreenName.setText(getString(R.string.twitt4droid_username_format, user.getScreenName()));
            userName.setText(user.getName());
            if (!Strings.isNullOrBlank(user.getProfileBannerURL())) {
                new ImageLoader(MainActivity.this)
                    .setImageView(userProfileBannerImage)
                    .execute(user.getProfileBannerURL());
            }
            if (!Strings.isNullOrBlank(user.getProfileImageURL())) {
                new ImageLoader(MainActivity.this)
                    .setImageView(userProfileImage)
                    .execute(user.getProfileImageURL());
            }
        }
    }
    
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch(position) {
                case 0:
                    Intent profileIntent = UserProfileActivity.buildIntent(MainActivity.this, Twitt4droid.getCurrentUser(MainActivity.this).getScreenName());
                    startActivity(profileIntent);
                case 1: 
                    setUpFragment(new HomeFragment());
                    setTitle(R.string.drawer_home_option);
                    break;
                case 2: 
                    setUpFragment(new ListsFragment());
                    setTitle(R.string.drawer_lists_option);
                    break;
                case 3:
                    setUpFragment(CustomFixedQueryTimelineFragment.newInstance(getString(R.string.drawer_fixed_search_option)));
                    setTitle(R.string.drawer_fixed_search_option);
                    break;
                case 4:
                    setUpFragment(CustomQueryableTimelineFragment.newInstance());
                    setTitle(R.string.drawer_search_option);
                    break;
                case 5: 
                    Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    break;
            }
            drawerList.setItemChecked(position, true);
            drawerLayout.closeDrawer(drawerList);
        }        
    }
}