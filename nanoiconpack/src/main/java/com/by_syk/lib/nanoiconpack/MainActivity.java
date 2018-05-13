/*
 * Copyright 2017 By_syk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.by_syk.lib.nanoiconpack;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import com.bolex.autoEx.AutoEx;
import com.by_syk.lib.nanoiconpack.dialog.ApplyDialog;
import com.by_syk.lib.nanoiconpack.fragment.AppsFragment;
import com.by_syk.lib.nanoiconpack.fragment.IconsFragment;
import com.by_syk.lib.nanoiconpack.fragment.WallpaperFragment;
import com.by_syk.lib.nanoiconpack.fragment.WhatsNewFragment;
import com.by_syk.lib.nanoiconpack.util.AllIconsGetter;
import com.by_syk.lib.nanoiconpack.util.ExtraUtil;
import com.by_syk.lib.nanoiconpack.util.LogUtil;
import com.by_syk.lib.nanoiconpack.util.MatchedIconsGetter;
import com.by_syk.lib.nanoiconpack.util.PkgUtil;
import com.by_syk.lib.nanoiconpack.util.SimplePageTransformer;
import com.by_syk.lib.nanoiconpack.util.adapter.AppAdapter;
import com.by_syk.lib.sp.SP;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

/**
 * Created by By_syk on 2016-07-16.
 */

public class MainActivity extends AppCompatActivity
        implements IconsFragment.OnLoadDoneListener, AppsFragment.OnLoadDoneListener, AppsFragment.OnInitAdapterListener, WallpaperFragment.OnLoadDoneListener {

    public static String PACKAGE_NAME;

    private SP sp;

    private ViewPager mViewPager;

    private BottomNavigationBar bottomNavigationView;
    /* create by morirain 2018/5/4 */
    private FloatingActionButton mFab;

    private boolean enableStatsModule = true;

    private int prevPagePos = 0;

    private BadgeItem mBadgeItemLost = new BadgeItem();
    private BadgeItem mBadgeItemNew = new BadgeItem();
    private BadgeItem mBadgeItemWallpaper = new BadgeItem();
    private BadgeItem mBadgeItemMatched = new BadgeItem();
    private BadgeItem mBadgeItemAll = new BadgeItem();

    private BadgeItem[] mBaseBadge = new BadgeItem[]{mBadgeItemLost, mBadgeItemNew, mBadgeItemWallpaper, mBadgeItemMatched, mBadgeItemAll};

/*    private NavigationView navigationView;
    private DrawerLayout drawerLayout;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        PACKAGE_NAME = getApplicationContext().getPackageName();

        if (false) {
            // Enable logging
            LogUtil.setLoggingTag(getString(R.string.app_name));
            LogUtil.setLoggingEnabled(true);
            // Enable AutoEx
            AutoEx.apply();
        }

        sp = new SP(this);

        enableStatsModule = getResources().getBoolean(R.bool.enable_req_stats_module);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        /* 初始化BadgeItem */
        for (BadgeItem aBaseBadge : mBaseBadge) {
            aBaseBadge.setBorderWidth(4)
                    .setAnimationDuration(200)
                    .setBackgroundColor(Color.RED)
                    .setHideOnSelect(false)
                    .setText("");
        }

        bottomNavigationView = (BottomNavigationBar) findViewById(R.id.bottom_navigation_view);

        bottomNavigationView.setMode(BottomNavigationBar.MODE_SHIFTING);

        bottomNavigationView.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        //bottomNavigationView.setBarBackgroundColor(R.color.color_primary);
        bottomNavigationView.addItem(new BottomNavigationItem(R.drawable.ic_nav_lost, R.string.nav_lost).setActiveColorResource(R.color.color_primary).setTextBadgeItem(mBadgeItemLost))
                .addItem(new BottomNavigationItem(R.drawable.ic_action_new_dark, R.string.menu_whats_new).setActiveColorResource(R.color.color_primary).setTextBadgeItem(mBadgeItemNew))
                .addItem(new BottomNavigationItem(R.drawable.ic_nav_wallpaper, getString(R.string.nav_wallpaper)).setActiveColorResource(R.color.color_primary).setTextBadgeItem(mBadgeItemWallpaper))//wallpaper
                .addItem(new BottomNavigationItem(R.drawable.ic_nav_matched, R.string.nav_matched).setActiveColorResource(R.color.color_primary).setTextBadgeItem(mBadgeItemMatched))
                .addItem(new BottomNavigationItem(R.drawable.ic_nav_all, R.string.nav_all).setActiveColorResource(R.color.color_primary).setTextBadgeItem(mBadgeItemAll))
                .setFirstSelectedPosition(1)
                .initialise();
        /* create by morirain 2018/5/4 : 这是申请适配的按钮 */
        mFab = (FloatingActionButton) findViewById(R.id.fab_request_apps);
        /* 把 fab 与 bottomNavigationView 绑定 */
        bottomNavigationView.setFab(mFab);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        mViewPager.setOffscreenPageLimit(4); // Keep all 3 pages alive.
        mViewPager.setPageTransformer(true, new SimplePageTransformer(getResources()
                .getInteger(R.integer.home_page_transform_anim)));

        mViewPager.setAdapter(new IconsPagerAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //bottomNavigationView.getMenu().getItem(prevPagePos).setChecked(false);
                //bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevPagePos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        bottomNavigationView.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {

            private long lastTapTime = 0;

            @Override
            public void onTabSelected(int position) {
                switch (position) {
                    case 0:
                        lastTapTime = System.currentTimeMillis();
                        /* create by morirain 2018/5/4 */
                        mFab.setVisibility(View.VISIBLE);
                        mViewPager.setCurrentItem(0);
                        break;
                    case 1:
                        lastTapTime = 0;
                        /* create by morirain 2018/5/4 */
                        mFab.setVisibility(View.INVISIBLE);
                        mViewPager.setCurrentItem(1);
                        break;
                    case 2:
                        lastTapTime = 0;
                        /* create by morirain 2018/5/4 */
                        mFab.setVisibility(View.INVISIBLE);
                        mViewPager.setCurrentItem(2);
                        break;
                    case 3:
                        lastTapTime = 0;
                        /* create by morirain 2018/5/4 */
                        mFab.setVisibility(View.INVISIBLE);
                        mViewPager.setCurrentItem(3);
                        break;
                    case 4:
                        lastTapTime = 0;
                        /* create by morirain 2018/5/4 */
                        mFab.setVisibility(View.INVISIBLE);
                        mViewPager.setCurrentItem(4);
                        break;
                }
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {
                switch (position) {
                    case 0:
                        if (System.currentTimeMillis() - lastTapTime < 40) {
                            //enterConsole();  不再允许进入控制台
                            lastTapTime = 0;
                        } else {
                            lastTapTime = System.currentTimeMillis();
                        }
                        break;
                }
            }


        });

        //navigationView.setCheckedItem(R.id.item_blue);
        /*navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                return false;
            }
        });*/

        // Set the default page to show.
        // 0: Lost, 1: Matched 2. All
        mViewPager.setCurrentItem(1);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //Did nothing.
        super.onConfigurationChanged(newConfig);
    }

    public void prepareReqPrompt() {
        if (sp.getBoolean("hintReq")) {
            return;
        }
        sp.save("hintReq", true);

        bottomNavigationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isDestroyed()) {
                    showReqPrompt();
                }
            }
        }, 800);
    }

    public void showReqPrompt() {
        (new MaterialTapTargetPrompt.Builder(this))
                //.setTarget(bottomNavigationView.findViewById(R.id.nav_lost))
                .setPrimaryText(getString(R.string.prompt_req))
                .setSecondaryText(getString(R.string.prompt_req_desc))
                .setBackgroundColourFromRes(R.color.color_primary)
                .setAutoDismiss(false)
                .setCaptureTouchEventOutsidePrompt(true)
                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                .show();
    }

    private void enterConsole() {
        if (!ExtraUtil.isNetworkConnected(this)) {
            return;
        }

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, ReqStatsActivity.class));
            }
        }, 100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // If latest icons is provided, show the entrance menu item.
        /*int lastIconsLength = getResources().getStringArray(R.array.latest_icons).length;
        mBadgeItemNew.setText(String.valueOf(lastIconsLength));
        if (lastIconsLength > 0) {
            menu.findItem(R.id.menu_whats_new).setVisible(true);
            if (menu.findItem(R.id.menu_apply).getIcon() == null) {
                if (!sp.getBoolean("hideLatest" + PkgUtil.getAppVer(this, "%1$s"))) {
                    menu.findItem(R.id.menu_whats_new).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    menu.findItem(R.id.menu_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                }
            }
        }*/

        return true;
    }/**/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_whats_new) {
            sp.save("hideLatest" + PkgUtil.getAppVer(this, "%1$s"), true);
            item.setIntent(new Intent(this, WhatsNewActivity.class));
            return super.onOptionsItemSelected(item);
        } else if (id == R.id.menu_search) {
            item.setIntent(new Intent(this, SearchActivity.class));
            return super.onOptionsItemSelected(item);
        } else if (id == R.id.menu_apply) {
            (new ApplyDialog()).show(getSupportFragmentManager(), "applyDialog");
            return true;
        } else if (id == R.id.menu_about) {
            item.setIntent(new Intent(this, AboutActivity.class));
            return super.onOptionsItemSelected(item);
        } else if (id == R.id.home) {
            /*drawerLayout.openDrawer(GravityCompat.START);*/
            return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadDone(int pageId, int sum) {
        switch (pageId) {
            case 0:
                mBadgeItemLost.setText(String.valueOf(sum));
                break;
            case 1:
                int lastIconsLength = getResources().getStringArray(R.array.latest_icons).length;
                mBadgeItemNew.setText(String.valueOf(lastIconsLength));
                break;
            case 2:

                mBadgeItemWallpaper.setText(String.valueOf(sum));
                break;
            case 3:
                mBadgeItemMatched.setText(String.valueOf(sum));
                break;
            case 4:
                mBadgeItemAll.setText(String.valueOf(sum));
                break;
        }
    }

    @Override
    public void onInitAdapter(AppAdapter appAdapter) {
        // 申请适配按钮
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray sparseArray = appAdapter.getCheckStates();
                if (sparseArray.size() <= 0) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("Request icon");
                    dialog.setMessage(R.string.request_notice);
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", null);
                    dialog.show();
                } else {
                    /* 清空sparseBooleanArray 并通知CheckBox改变 */
                    /* 申请适配 */
                    appAdapter.requestIcon(MainActivity.this);
                }
            }
        });
    }


    class IconsPagerAdapter extends FragmentPagerAdapter {
        IconsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return AppsFragment.newInstance(position);
                case 1:
                    return WhatsNewFragment.newInstance(position);
                case 2:
                    return WallpaperFragment.newInstance(position);
                case 3:
                    return IconsFragment.newInstance(position, new MatchedIconsGetter(),
                            getResources().getInteger(R.integer.home_grid_item_mode));
                case 4:
                    return IconsFragment.newInstance(position, new AllIconsGetter(),
                            getResources().getInteger(R.integer.home_grid_item_mode));
            }
            return null;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }


}
