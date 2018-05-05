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
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.by_syk.lib.nanoiconpack.MainActivity;
import com.by_syk.lib.nanoiconpack.R;
import com.by_syk.lib.nanoiconpack.util.LatestIconsGetter;


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
                .commitAllowingStateLoss();
        Button buttonRate = contentView.findViewById(R.id.rate_button);
        Button buttonFeedback = contentView.findViewById(R.id.feedback_button);
        buttonRate.setOnClickListener(this);
        buttonFeedback.setOnClickListener(this);
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



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.rate_button) {
            Uri uri = Uri.parse("market://details?id=" + MainActivity.PACKAGE_NAME);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if(i == R.id.feedback_button) {
            Uri uri = Uri.parse("mailto:morirain.dev@outlook.com");
            String[] email = {"morirain.dev@outlook.com"};
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra(Intent.EXTRA_EMAIL, email); // 收件人
            intent.putExtra(Intent.EXTRA_SUBJECT, "I have some questions"); // 主题
            intent.putExtra(Intent.EXTRA_TEXT, ""); // 正文
            startActivity(Intent.createChooser(intent, "Feedback"));
        }
    }
}
