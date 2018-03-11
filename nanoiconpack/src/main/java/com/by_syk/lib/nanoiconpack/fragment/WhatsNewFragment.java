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

package com.by_syk.lib.nanoiconpack.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.by_syk.lib.nanoiconpack.MainActivity;
import com.by_syk.lib.nanoiconpack.R;
import com.by_syk.lib.nanoiconpack.bean.AppBean;
import com.by_syk.lib.nanoiconpack.bean.IconBean;
import com.by_syk.lib.nanoiconpack.util.LatestIconsGetter;
import com.by_syk.lib.nanoiconpack.util.PkgUtil;



/**
 * Created by By_syk on 2017-02-18.
 */

public class WhatsNewFragment extends Fragment implements View.OnClickListener/* implements IconsFragment.OnLoadDoneListener*/ {

    private View contentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(R.layout.activity_whats_new, container, false);
            init();
        }
        return contentView;
    }

    private void init() {
        int pageId = getArguments().getInt("pageId");
        IconsFragment fragment = IconsFragment.newInstance(pageId, new LatestIconsGetter(),
                getResources().getInteger(R.integer.whats_new_grid_item_mode));
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, fragment)
                .commit();
        Button button = (Button) contentView.findViewById(R.id.rate_button);
        button.setOnClickListener(this);
    }

    public static WhatsNewFragment newInstance(int id) {
        WhatsNewFragment fragment = new WhatsNewFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("pageId", id);
        fragment.setArguments(bundle);

        return fragment;
    }

    /*private void showHint() {
        Snackbar snackbar = Snackbar.make(contentView.findViewById(R.id.coordinator_layout),
                PkgUtil.getAppVer(contentView.getContext(), getString(R.string.toast_whats_new)),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.toast_rate, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).setActionTextColor(0xffff9ff5).show();
    }*/

    /*@Override
    public void onLoadDone(int pageId, int sum) {
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                showHint();
            }
        }, 400);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            //finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    public static WhatsNewFragment initRetainedFragment(@NonNull FragmentManager fragmentManager,
                                                        @NonNull String tag) {
        WhatsNewFragment fragment = (WhatsNewFragment) fragmentManager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new WhatsNewFragment();
            fragmentManager.beginTransaction().add(fragment, tag).commit();
        }
        return fragment;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.rate_button) {
            Uri uri = Uri.parse("market://details?id=" + MainActivity.PACKAGE_NAME);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
        }
    }
}
