package org.invincible.cosstudent.misc;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import org.invincible.cosstudent.R;

/**
 * Created by kshivang on 04/09/16.
 */
public class TextHandler {

    /**
     * Variables
     */
    private String value;
    private Context mContext;
    private TextInputLayout textInputLayout;
    private TextWatcher textWatcher;
    private EditText editText;

    /**
     * Getter for variables
     */

    public String getValue() {
        if (textInputLayout.isErrorEnabled())
            return null;
        return value;
    }

    /**
     * TextHandler: Check whether given no. input is proper consumer no or not
     * and show alert accordingly
     * @param checkParam: This specify the check type
     * @param textInputLayout: This is TextInputLayout which is used to show alert
     *                       text just below the field
     */
    public TextHandler(final Context mContext, final int checkParam,
                       final TextInputLayout textInputLayout){

        this.mContext = mContext;

        this.textInputLayout = textInputLayout;
        this.editText = textInputLayout.getEditText();

        if(editText != null) {

            textWatcher = new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    value = editText.getText().toString();
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    onCheck(checkParam, editText, textInputLayout);
                }
            };

            editText.addTextChangedListener(textWatcher);
        }
    }

    /**
     * This function check for text input and select alert behaviour accordingly
     * @param checkParam: Parameter to be checked
     * @param editText: EditText of that parameter
     * @param textInputLayout: TextInputLayout of that parameter
     */
    private void onCheck(int checkParam, EditText editText,
                         TextInputLayout textInputLayout) {

        switch (checkParam){
            case Constants.CHECK_EMAIL:
                value = editText.getText().toString();
                if (value.length() == 0) {
                    textInputLayout.setError(mContext.getString(R.string.empty_necessary_field));
                } else if (!value.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {
                    textInputLayout.setError(mContext.getString(R.string.not_valid_email));
                    textInputLayout.setErrorEnabled(true);
                } else {
                    textInputLayout.setErrorEnabled(false);
                }
                break;

            case Constants.CHECK_PASSWORD:
                value = editText.getText().toString();
                if (value.length() == 0) {
                    textInputLayout.setError(mContext.getString(R.string.empty_necessary_field));
                } else if (value.length() < 6) {
                    textInputLayout.setError(mContext.getString(R.string.short_password));
                } else {
                    textInputLayout.setErrorEnabled(false);
                }
                break;
        }
    }

    public void onStop() {
        if (textWatcher != null)
            editText.removeTextChangedListener(textWatcher);
    }
}
