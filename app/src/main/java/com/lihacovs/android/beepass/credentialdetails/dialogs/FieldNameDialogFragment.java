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

package com.lihacovs.android.beepass.credentialdetails.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.lihacovs.android.beepass.R;

/**
 * Simple dialog implementation to set custom field name
 * Uses callbacks to pass data back to the host
 */
public class FieldNameDialogFragment extends DialogFragment {

    private static final String ARG_FIELD_NAME = "fieldName";
    public static final String EXTRA_FIELD_NAME =
            "com.lihacovs.android.beepass.credentialdetail.dialogs.fieldName";

    // Method to pass argument from fragment to this fragment via Bundle
    // We can add or remove params to add additional data from fragment
    public static FieldNameDialogFragment newInstance(String fieldName) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FIELD_NAME, fieldName);
        FieldNameDialogFragment fragment = new FieldNameDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Getting argument from another fragment
        String fieldName = (String) getArguments().getSerializable(ARG_FIELD_NAME);

        //custom view for dialog
        @SuppressLint
                ("InflateParams") final View v = LayoutInflater.from(getActivity()).inflate(
                R.layout.item_credentialdetails_dialog, null);
        final EditText fieldNameEt = v.findViewById(R.id.et_credentialdetails_dialog_field_name);
        fieldNameEt.setText(fieldName);

        //Building dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.enter_field_name)
                .setView(v)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendResult(Activity.RESULT_OK, fieldNameEt.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        FieldNameDialogFragment.this.getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    //method to send Extra data back to fragment as a result via Intent
    private void sendResult(int resultCode, String fieldName) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_FIELD_NAME, fieldName);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
