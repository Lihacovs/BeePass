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

package com.lihacovs.android.beepass.credentialdetails;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.lihacovs.android.beepass.R;
import com.lihacovs.android.beepass.about.AboutActivity;
import com.lihacovs.android.beepass.authentication.AuthenticationActivity;
import com.lihacovs.android.beepass.categories.CategoriesActivity;
import com.lihacovs.android.beepass.credentialdetails.dialogs.FieldNameDialogFragment;
import com.lihacovs.android.beepass.data.model.Category;
import com.lihacovs.android.beepass.data.model.Field;
import com.lihacovs.android.beepass.utils.AppUtils;

import java.util.List;

import static android.support.v4.util.Preconditions.checkNotNull;
import static com.lihacovs.android.beepass.R.string.credential;

/**
 * Fragment for the credential detail screen.
 */

@SuppressWarnings("FieldCanBeLocal")
public class CredentialDetailsFragment extends Fragment implements CredentialDetailsContract.View {

    private static final String DIALOG_FIELD_NAME = "DialogFieldName";
    private static final int REQUEST_FIELD_NAME = 0;

    private CredentialDetailsContract.Presenter mPresenter;
    private CoordinatorLayout mCoordinatorLayout;

    private RecyclerView mFieldsRecyclerView;
    private FieldAdapter mFieldAdapter;
    private ArrayAdapter<Category> mSpinnerAdapter;

    private TextInputEditText mCredentialTitleTiet;
    private Button mAddFieldButton;
    private Spinner mCategorySpinner;
    private ImageView mAddCategoryIb, mEditCategoryIb;

    private Field mNewField;

    public static CredentialDetailsFragment newInstance() {
        return new CredentialDetailsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        //to update list after detail activity was changed or closed - destroyed();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_credentialdetails, container, false);
        mCoordinatorLayout = view.findViewById(R.id.cl_credentialdetails);

        mCredentialTitleTiet = view.findViewById(R.id.tiet_credentialdetails_credential_title);

        mCredentialTitleTiet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPresenter.updateCredential(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mFieldsRecyclerView = view.findViewById(R.id.rv_credentialdetails_fields);
        mFieldsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAddFieldButton = view.findViewById(R.id.btn_credentialdetails_add_field);
        mAddFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewField = mPresenter.addNewField();
                mPresenter.updateFieldsRv();

                //opens dialog to choose field name
                FragmentManager manager = getFragmentManager();
                FieldNameDialogFragment dialog =
                        FieldNameDialogFragment.newInstance(mNewField.getFieldName());
                // sets fragment to retrieve data from dialog fragment
                dialog.setTargetFragment(CredentialDetailsFragment.this, REQUEST_FIELD_NAME);
                //Starts dialog fragment with show() method
                dialog.show(manager, DIALOG_FIELD_NAME);
            }
        });

        //Spinner to choose category for this credential
        mCategorySpinner = view.findViewById(R.id.spinner_credentialdetails_category);

        //Updates credential's category
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                Category category = (Category) parent.getItemAtPosition(position);
                String categoryId = category.getCategoryId();
                mPresenter.updateCredentialCategory(categoryId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Button to add new category
        mAddCategoryIb = view.findViewById(R.id.iv_credentialdetails_add_category);
        mAddCategoryIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addCategory();
            }
        });

        mEditCategoryIb = view.findViewById(R.id.iv_credentialdetails_edit_category);
        mEditCategoryIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.editCategory();
            }
        });

        //Prevents button to show on soft keyboard appearing
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                view.getWindowVisibleDisplayFrame(r);
                int heightDiff = view.getRootView().getHeight() - (r.bottom - r.top);

                if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                    //ok now we know the keyboard is up...
                    mAddFieldButton.setVisibility(View.GONE);

                } else {
                    //ok now we know the keyboard is down...
                    mAddFieldButton.setVisibility(View.VISIBLE);
                }
            }
        });

        mPresenter.loadCredential();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_FIELD_NAME) {
            String fieldName = (String) data.getSerializableExtra(FieldNameDialogFragment.EXTRA_FIELD_NAME);
            String encryptedFieldName = mPresenter.encryptFieldText(fieldName);
            mNewField.setFieldName(encryptedFieldName);
            mPresenter.updateField(mNewField);
            mPresenter.updateFieldsRv();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.credentialdetails_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_credential:
                showDeleteCredentialDialog();
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

    private void showDeleteCredentialDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        String credentialName;
        String decryptedCredName = mPresenter.decryptCredentialName();
        if (decryptedCredName == null || decryptedCredName.trim().equals("")) {
            credentialName = getString(credential);
        } else {
            credentialName = decryptedCredName;
        }
        alert.setMessage(getString(R.string.confirm_delete) + credentialName);

        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.deleteCredential();
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

    private void showDeleteFieldDialog(final Field field) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        String fieldName;
        String decryptedFieldName = mPresenter.decryptFieldName(field);
        if (decryptedFieldName == null || decryptedFieldName.trim().equals("")) {
            fieldName = getString(R.string.field);
        } else {
            fieldName = decryptedFieldName;
        }
        alert.setMessage(getString(R.string.confirm_delete) + fieldName);

        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPresenter.deleteField(field);
                mPresenter.updateFieldsRv();
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
    public void setPresenter(CredentialDetailsContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void showCredentialTitle(String credentialName) {
        mCredentialTitleTiet.setText(credentialName);
    }

    @Override
    public void showCredentialListScreen() {
        getActivity().finish();
    }

    @Override
    public void showLoginScreen() {
        Intent intent = AuthenticationActivity.newIntent(getActivity());
        startActivity(intent);
    }

    @Override
    public void showFields(List<Field> fields) {
        if (mFieldAdapter == null) {
            mFieldAdapter = new FieldAdapter(fields);
            mFieldsRecyclerView.setAdapter(mFieldAdapter);
        } else {
            mFieldAdapter.setFields(fields);
            //mFieldAdapter.notifyItemChanged(mDetailPosition);
            mFieldAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showSpinnerCategories(List<Category> categories) {
        Category[] categoryArray = categories.toArray(new Category[categories.size()]);

        mSpinnerAdapter = new SpinnerAdapter(
                getActivity(),
                R.layout.item_credentialdetails_spinner,
                categoryArray);
        mCategorySpinner.setAdapter(mSpinnerAdapter);
    }

    private void showAboutAppScreen() {
        Intent intent = AboutActivity.newIntent(getContext());
        startActivity(intent);
    }

    /**
     * Custom adapter for spinner
     */
    private class SpinnerAdapter extends ArrayAdapter<Category> {
        private Context context;
        private Category[] values;

        SpinnerAdapter(Context context,
                       int textViewResourceId,
                       Category[] values) {
            super(context, textViewResourceId, values);
            this.context = context;
            this.values = values;
        }

        public int getCount() {
            return values.length;
        }

        public Category getItem(int position) {
            return values[position];
        }

        public long getItemId(int position) {
            return position;
        }


        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, parent);
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            return getCustomView(position, parent);
        }

        private View getCustomView(int position, ViewGroup parent) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_credentialdetails_spinner, parent, false);
            TextView label = view.findViewById(R.id.spinner_text);

            String decryptedCategoryName = mPresenter.decryptCategoryName(values[position]);
            if (decryptedCategoryName == null || decryptedCategoryName.trim().equals("")) {
                label.setText(R.string.category);
            } else {
                label.setText(decryptedCategoryName);
            }

            return view;
        }
    }

    @Override
    public void showSpinnerCategoryValue(Category category) {
        int spinnerPosition = 0;
        if (mSpinnerAdapter == null) {
            Log.e("SPINNER ERROR", " spinner adapter");
        } else {
            spinnerPosition = mSpinnerAdapter.getPosition(category);
        }
        mCategorySpinner.setSelection(spinnerPosition);
    }

    @Override
    public void showSnackBar(String text) {
        Snackbar.make(mCoordinatorLayout, text,
                Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showAddCategoryScreen(String categoryId, String userId) {
        Intent intent = CategoriesActivity.newIntent(getContext(), categoryId, userId);
        startActivity(intent);
    }

    @Override
    public void showEditCategoryScreen(String categoryId, String userId) {
        Intent intent = CategoriesActivity.newIntent(getContext(), categoryId, userId);
        startActivity(intent);
    }

    private class FieldHolder extends RecyclerView.ViewHolder {

        private Field mField;

        private TextInputLayout mFieldNameTil;
        private TextInputEditText mFieldValueTiet;
        private ImageView mDeleteFieldIv, mCopyFieldIb;

        private String fieldName;

        FieldHolder(LayoutInflater inflater, ViewGroup parent, int layout) {
            super(inflater.inflate(layout, parent, false));

            mFieldNameTil = itemView.findViewById(R.id.til_credentialdetails_field_name);
            mFieldNameTil.setHintAnimationEnabled(false);
            mFieldValueTiet = itemView.findViewById(R.id.tiet_credentialdetails_field_value);
            mDeleteFieldIv = itemView.findViewById(R.id.iv_credentialdetails_delete_field);
            mCopyFieldIb = itemView.findViewById(R.id.iv_credentialdetails_copy_field);


            mDeleteFieldIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppUtils.hideKeyboard(getActivity(), mDeleteFieldIv);
                    showDeleteFieldDialog(mField);
                }
            });

            mCopyFieldIb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboard = (ClipboardManager)
                            getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Copied text",
                            mPresenter.decryptFieldText(mField));
                    clipboard.setPrimaryClip(clip);
                    showSnackBar(mPresenter.decryptFieldName(mField) +
                            " " +
                            getString(R.string.copied));
                }
            });

            mFieldValueTiet.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String encryptedFieldText = mPresenter.encryptFieldText(charSequence.toString());
                    mField.setFieldText(encryptedFieldText);
                    mPresenter.updateField(mField);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        }

        void bind(Field field) {
            mField = field;

            mFieldNameTil.setHintAnimationEnabled(false);

            //Sets field name and text
            String decryptedFieldName = mPresenter.decryptFieldName(mField);
            if(decryptedFieldName == null || decryptedFieldName.trim().equals("")){
                fieldName = getString(R.string.field_name_with_colon);
            }else{
                fieldName = decryptedFieldName;
            }
            mFieldNameTil.setHint(fieldName);
            mFieldValueTiet.setText(mPresenter.decryptFieldText(mField));
            mFieldNameTil.setHintAnimationEnabled(true);
        }
    }

    //responsible for creating a list of fields
    private class FieldAdapter extends RecyclerView.Adapter<FieldHolder> {

        private List<Field> mFields;

        FieldAdapter(List<Field> fields) {
            mFields = fields;
        }

        @Override
        public FieldHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new FieldHolder(layoutInflater, parent, R.layout.item_credentialdetails_field);
        }

        @Override
        public void onBindViewHolder(FieldHolder holder, int position) {
            Field field = mFields.get(position);
            holder.bind(field);
        }

        @Override
        public int getItemCount() {
            return mFields.size();
        }

        void setFields(List<Field> fields) {
            mFields = fields;
        }
    }
}

