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

package com.lihacovs.android.beepass.categories;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import com.lihacovs.android.beepass.R;
import com.lihacovs.android.beepass.about.AboutActivity;
import com.lihacovs.android.beepass.authentication.AuthenticationActivity;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * Main UI for the category detail screen.
 */

public class CategoriesFragment extends Fragment implements CategoriesContract.View {

    private CategoriesContract.Presenter mPresenter;
    private CoordinatorLayout mCoordinatorLayout;

    private EditText mCategoryName;

    //reference to available category images
    private Integer[] mThumbIds = {
            R.drawable.ic_category_account_balance_24px,
            R.drawable.ic_category_account_balance_wallet_24px,
            R.drawable.ic_category_assignment_24px,
            R.drawable.ic_category_credit_card_24px,
            R.drawable.ic_category_description_24px,
            R.drawable.ic_category_directions_car_24px,
            R.drawable.ic_category_email_24px,
            R.drawable.ic_category_folder_24px,
            R.drawable.ic_category_import_contacts_24px,
            R.drawable.ic_category_insert_drive_file_24px,
            R.drawable.ic_category_laptop_windows_24px,
            R.drawable.ic_category_phonelink_lock_24px,
            R.drawable.ic_category_receipt_24px,
            R.drawable.ic_category_room_24px,
            R.drawable.ic_category_sd_storage_24px,
            R.drawable.ic_category_storage_24px,
            R.drawable.ic_category_vpn_key_24px,
            R.drawable.ic_category_web_24px,
            R.drawable.ic_category_wifi_lock_24px,
            R.drawable.ic_category_work_24px
    };

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public void setPresenter(CategoriesContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        mCoordinatorLayout = view.findViewById(R.id.cl_credentialdetails);

        mCategoryName = view.findViewById(R.id.tiet_categories_category_name);
        mCategoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPresenter.updateCategoryName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        GridView gridview = view.findViewById(R.id.gv_categories_image);
        final ImageAdapter imageAdapter = new ImageAdapter(getActivity());
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                imageAdapter.setSelectedPosition(position);
                imageAdapter.notifyDataSetChanged();
                int imageId = mThumbIds[position];
                String imageName = getActivity().getResources().getResourceEntryName(imageId);
                mPresenter.updateCategoryImageName(imageName);
            }
        });

        mPresenter.start();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.category_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_category:
                showDeleteDialog();
                return true;
            case R.id.about:
                showAboutAppScreen();
                return true;
            case R.id.logout_user:
                mPresenter.logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showSnackBar(int strId) {
        Snackbar.make(mCoordinatorLayout, strId,
                Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showCredentialDetailScreen() {
        getActivity().finish();
    }

    private void showAboutAppScreen() {
        Intent intent = AboutActivity.newIntent(getContext());
        startActivity(intent);
    }

    private void showDeleteDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        String categoryName;
        String encryptedCategoryName = mPresenter.decryptCategoryName();
        if(encryptedCategoryName == null || encryptedCategoryName.trim().equals("")){
            categoryName = getString(R.string.category);
        }else{
            categoryName = encryptedCategoryName;
        }
        alert.setMessage(getString(R.string.confirm_delete) + categoryName);

        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.deleteCategory();
                dialog.dismiss();
            }
        });
        alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public void showLoginScreen() {
        Intent intent = AuthenticationActivity.newIntent(getContext());
        startActivity(intent);
    }

    @Override
    public void showCategoryName(String categoryName) {
        mCategoryName.setText(categoryName);
    }

    /**
     * Simple adapter to select image icon for category
     */
    private class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private int selectedPosition = -1;

        ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageResource(mThumbIds[position]);
            String imageName = getActivity().getResources().getResourceEntryName(mThumbIds[position]);
            if (mPresenter.decryptCategoryImageName().equals(imageName)) {
                setSelectedPosition(position);
            }

            if (position == selectedPosition) {
                imageView.setBackgroundColor(
                        ContextCompat.getColor(getActivity(), R.color.colorGray900));
            } else {
                imageView.setBackgroundColor(
                        ContextCompat.getColor(getActivity(), android.R.color.transparent));
            }

            return imageView;
        }

        private void setSelectedPosition(int position) {
            selectedPosition = position;
        }
    }
}
