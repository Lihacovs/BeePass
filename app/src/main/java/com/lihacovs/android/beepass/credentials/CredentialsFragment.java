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

package com.lihacovs.android.beepass.credentials;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lihacovs.android.beepass.R;
import com.lihacovs.android.beepass.about.AboutActivity;
import com.lihacovs.android.beepass.authentication.AuthenticationActivity;
import com.lihacovs.android.beepass.categories.CategoriesActivity;
import com.lihacovs.android.beepass.credentialdetails.CredentialDetailsActivity;
import com.lihacovs.android.beepass.data.model.Category;
import com.lihacovs.android.beepass.data.model.Credential;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import static android.support.v4.util.Preconditions.checkNotNull;

/**
 * Fragment with recycler view
 * {@link CategoryAdapter}
 */

@SuppressWarnings("FieldCanBeLocal")
public class CredentialsFragment extends Fragment implements CredentialsContract.View {

    private CredentialsContract.Presenter mPresenter;

    private CoordinatorLayout mCoordinatorLayout;

    private RecyclerView mCategoryRecyclerView;
    private CategoryAdapter mCategoryAdapter;

    private FloatingActionButton mAddFab, mAddCategoryFab, mAddCredentialFab;
    private LinearLayout mAddCategoryLayout, mAddCredentialLayout;
    private View mFabBgLayout;
    private boolean isFABOpen = false;

    public static CredentialsFragment newInstance() {
        return new CredentialsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull CredentialsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credentials, container, false);
        mCoordinatorLayout = view.findViewById(R.id.cl_credentials);

        mCategoryRecyclerView = view.findViewById(R.id.rv_credentials);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mCategoryRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(
                        mCategoryRecyclerView.getContext(),
                        layoutManager.getOrientation());
        mCategoryRecyclerView.addItemDecoration(dividerItemDecoration);

        //FAB menu made from native elements
        mAddCategoryLayout = view.findViewById(R.id.ll_credentials_category_fab);
        mAddCredentialLayout = view.findViewById(R.id.ll_credentials_credential_fab);
        mAddFab = view.findViewById(R.id.fab_credentials_add);
        mAddCategoryFab = view.findViewById(R.id.fab_credentials_category);
        mAddCredentialFab = view.findViewById(R.id.fab_credentials_credential);
        mFabBgLayout = view.findViewById(R.id.view_credentials_fab_bg);

        mAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        mFabBgLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFABMenu();
            }
        });

        mAddCategoryFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewCategory();
            }
        });

        mAddCredentialFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Category defaultCategory = mPresenter.getDefaultCategory();
                mPresenter.addNewCredential(defaultCategory.getCategoryId());
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isFABOpen) closeFABMenu();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.credentials_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_category:
                mPresenter.addNewCategory();
                return true;
            case R.id.new_credential:
                Category defaultCategory = mPresenter.getDefaultCategory();
                mPresenter.addNewCredential(defaultCategory.getCategoryId());
                mPresenter.loadCredentials();
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

    private void showFABMenu() {
        isFABOpen = true;
        mAddCategoryLayout.setVisibility(View.VISIBLE);
        mAddCredentialLayout.setVisibility(View.VISIBLE);
        mFabBgLayout.setVisibility(View.VISIBLE);

        mAddFab.animate().rotationBy(225);
        mAddCategoryLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_65));
        mAddCredentialLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_120));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        mFabBgLayout.setVisibility(View.GONE);
        mAddFab.animate().rotationBy(-225);
        mAddCategoryLayout.animate().translationY(0);
        mAddCredentialLayout.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isFABOpen) {
                    mAddCategoryLayout.setVisibility(View.GONE);
                    mAddCredentialLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void expandGroup(int gPos) {
        if (mCategoryAdapter.isGroupExpanded(gPos)) {
            return;
        }
        mCategoryAdapter.toggleGroup(gPos);
    }

    @Override
    public void showCategoryCredentials(List<CategoryGroup> groups) {
        mCategoryAdapter = new CategoryAdapter(groups);
        /*for (int i = mCategoryAdapter.getGroups().size() - 1; i >= 0; i--) {
            expandGroup(i);
        }*/
        mCategoryRecyclerView.setAdapter(mCategoryAdapter);
    }

    @Override
    public void showEditCategoryScreen(String categoryId, String userId) {
        Intent intent = CategoriesActivity.newIntent(getContext(), categoryId, userId);
        startActivity(intent);
    }

    private void showAboutAppScreen() {
        Intent intent = AboutActivity.newIntent(getContext());
        startActivity(intent);
    }

    @Override
    public void showSnackBar(String text) {
        Snackbar.make(mCoordinatorLayout, text,
                Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showAddCredential(String credentialId, String userId) {
        Intent intent = CredentialDetailsActivity.newIntent(getContext(), credentialId, userId);
        startActivity(intent);
    }

    @Override
    public void showCredentialDetailsScreen(String credentialId, String userId) {
        Intent intent = CredentialDetailsActivity.newIntent(getContext(), credentialId, userId);
        startActivity(intent);
    }

    @Override
    public void showLoginScreen() {
        Intent intent = AuthenticationActivity.newIntent(getActivity());
        startActivity(intent);
    }


    /**
     * Implementation of expandable recycler view
     */
    class CategoryViewHolder extends GroupViewHolder implements View.OnClickListener {

        private Category mCategory;
        private TextView mCategoryName;
        private ImageView mEditCategoryIv, mAddCredentialIv, mCategoryImageIv;

        CategoryViewHolder(View itemView) {
            super(itemView);
            mCategoryName = itemView.findViewById(R.id.tv_credentials_rv_category_name);
            mEditCategoryIv = itemView.findViewById(R.id.iv_credentials_rv_edit_category);
            mAddCredentialIv = itemView.findViewById(R.id.iv_credentials_rv_add_credential);
            mCategoryImageIv = itemView.findViewById(R.id.iv_credentials_rv_category_image);
        }

        void bind(final Category category) {
            mCategory = category;

            // Sets category name
            String categoryName = mPresenter.decryptCategoryName(mCategory);
            if (categoryName == null || categoryName.trim().equals("")) {
                mCategoryName.setText(R.string.category);
            } else {
                mCategoryName.setText(categoryName);
            }
            mCategoryName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mCategoryName.setSingleLine(true);
            mCategoryName.setMarqueeRepeatLimit(0);
            mCategoryName.setSelected(true);

            String categoryImageName = mPresenter.decryptCategoryImage(mCategory);
            int drawableResourceId = getActivity().getResources()
                    .getIdentifier(categoryImageName, "drawable", getActivity().getPackageName());
            mCategoryImageIv.setImageDrawable(ContextCompat.getDrawable(getActivity(), drawableResourceId));

            mEditCategoryIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.editCategory(category.getCategoryId());
                }
            });

            mAddCredentialIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.addNewCredential(category.getCategoryId());
                }
            });
        }

    }

    class CredentialViewHolder extends ChildViewHolder implements View.OnClickListener {

        private Credential mCredential;
        private TextView credentialName;
        private ImageView mDeleteCredentialIv;


        CredentialViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            credentialName = itemView.findViewById(R.id.tv_credentials_rv_credential_name);
            mDeleteCredentialIv = itemView.findViewById(R.id.iv_credentials_rv_delete_credential);
        }

        void bind(final Credential credential) {
            mCredential = credential;

            //sets credential title
            String credTitle = mPresenter.decryptCredentialName(mCredential);
            if (credTitle == null || credTitle.trim().equals("")) {
                credentialName.setText(R.string.credential);
            } else {
                credentialName.setText(credTitle);
            }
            credentialName.setSingleLine(true);
            credentialName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            credentialName.setMarqueeRepeatLimit(0);
            credentialName.setSelected(true);

            mDeleteCredentialIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDeleteCredentialDialog(credential);
                }
            });
        }

        @Override
        public void onClick(View view) {
            mPresenter.openCredentialDetails(mCredential);
        }

        private void showDeleteCredentialDialog(final Credential credential){
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

            String credentialName;
            String decryptedCredName = mPresenter.decryptCredentialName(credential);
            if(decryptedCredName == null || decryptedCredName.trim().equals("")){
                credentialName = getString(R.string.credential);
            }else{
                credentialName = decryptedCredName;
            }
            alert.setMessage(getString(R.string.confirm_delete) + credentialName);

            alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPresenter.deleteCredential(credential);
                    mPresenter.loadCredentials();
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
    }

    private class CategoryAdapter
            extends ExpandableRecyclerViewAdapter<CategoryViewHolder, CredentialViewHolder> {

        List<? extends ExpandableGroup> mGroups;

        CategoryAdapter(List<? extends ExpandableGroup> groups) {
            super(groups);
            mGroups = groups;
        }

        @Override
        public CategoryViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public CredentialViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_credential, parent, false);
            return new CredentialViewHolder(view);
        }

        @Override
        public void onBindChildViewHolder(CredentialViewHolder holder, int flatPosition, ExpandableGroup group,
                                          int childIndex) {
            final Credential credential = ((CategoryGroup) group).getItems().get(childIndex);
            holder.bind(credential);
            //holder.setCredentialName(credential.getTitle());
        }

        @Override
        public void onBindGroupViewHolder(CategoryViewHolder holder, int flatPosition,
                                          ExpandableGroup group) {
            //in our case getTitle is categoryId - required to fit custom API implementation
            final Category category = mPresenter.getCategory(group.getTitle());
            holder.bind(category);
        }

        /*@Override
        public void onGroupExpanded(int positionStart, int itemCount) {
            if (itemCount > 0) {
                int groupIndex = expandableList.getUnflattenedPosition(positionStart).groupPos;
                notifyItemRangeInserted(positionStart, itemCount);
                for (ExpandableGroup grp : mGroups) {
                    if (grp != mGroups.get(groupIndex)) {
                        if (this.isGroupExpanded(grp)) {
                            this.toggleGroup(grp);
                            this.notifyDataSetChanged();
                        }
                    }
                }
            }
        }*/
    }
}
