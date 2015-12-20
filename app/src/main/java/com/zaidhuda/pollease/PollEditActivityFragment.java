package com.zaidhuda.pollease;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class PollEditActivityFragment extends Fragment {
    private View view;
    private Poll poll;
    private String question, password;
    private EditText questionET, passwordET;

    public PollEditActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_poll_edit, container, false);
        questionET = (EditText) view.findViewById(R.id.question_editText);
        passwordET = (EditText) view.findViewById(R.id.password_editText);
        view.findViewById(R.id.poll_create_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question = questionET.getText().toString();
                password = passwordET.getText().toString();
            }
        });

        return view;
    }
}
