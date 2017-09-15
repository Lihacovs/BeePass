/*
 * Copyright (C) 2017. Konstantins Lihacovs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lihacovs.android.beepass.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lihacovs.android.beepass.BuildConfig;
import com.lihacovs.android.beepass.R;
import com.lihacovs.android.beepass.introduction.CloudPassIntro;
import com.lihacovs.android.beepass.utils.SharedPrefHelper;

/**
 * Main UI for the AboutApp screen.
 */

public class AboutFragment extends Fragment {

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        TextView appVersionTv = view.findViewById(R.id.tc_about_app_version);
        appVersionTv.setText("(Version " + BuildConfig.VERSION_NAME + " )");

        //Opens app introduction screens
        ImageView appFunctionsIv = view.findViewById(R.id.iv_about_link_functions);
        appFunctionsIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefHelper.watchAppIntro(false);
                Intent intent = new Intent(getActivity(), CloudPassIntro.class);
                startActivity(intent);
            }
        });

        //Opens email to send to developer
        ImageView sendEmailIv = view.findViewById(R.id.iv_about_send_email);
        sendEmailIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", getString(R.string.app_email), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject));
                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.email_body));
                startActivity(Intent.createChooser(emailIntent,
                        getString(R.string.email_chooser_title)));
            }
        });

        //Opens GooglePlay to rate app
        ImageView sendRateIv = view.findViewById(R.id.iv_about_send_rate);
        sendRateIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getContext().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        return view;
    }
}
