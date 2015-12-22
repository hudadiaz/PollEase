package com.zaidhuda.pollease.custom.views;

import android.content.Context;
import android.widget.RadioButton;

/**
 * Created by Zaid on 18/12/2015.
 */
public class ChoiceRadioButton extends RadioButton {
    private int ChoiceID;

    public ChoiceRadioButton(Context context) {
        super(context);
    }

    public int getChoiceID() {
        return ChoiceID;
    }

    public void setChoiceID(int choiceID) {
        ChoiceID = choiceID;
    }
}
