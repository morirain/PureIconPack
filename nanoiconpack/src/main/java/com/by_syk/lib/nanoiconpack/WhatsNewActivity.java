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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.by_syk.lib.nanoiconpack.fragment.IconsFragment;
import com.by_syk.lib.nanoiconpack.util.LatestIconsGetter;
import com.by_syk.lib.nanoiconpack.util.PkgUtil;

/**
 * Created by By_syk on 2017-01-30.
 */

public class WhatsNewActivity extends AppCompatActivity implements IconsFragment.OnLoadDoneListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whats_new);

        init();
    }

    private void init() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        IconsFragment fragment = IconsFragment.newInstance(0, new LatestIconsGetter(),
                getResources().getInteger(R.integer.whats_new_grid_item_mode));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, fragment)
                .commit();
    }

    private void showHint() {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout),
                PkgUtil.getAppVer(this, getString(R.string.toast_whats_new)),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.toast_rate, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("market://details?id="+getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setActionTextColor(0xffff9ff5).show();
    }

    @Override
    public void onLoadDone(int pageId, int sum) {
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                showHint();
            }
        }, 400);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}