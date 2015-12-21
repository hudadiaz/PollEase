package com.zaidhuda.pollease;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class PollCreateFragment extends Fragment implements POSTPoll.OnPOSTPollListener {
    private View view;
    private String question, password;
    private EditText questionET, passwordET;
    private OnFragmentInteractionListener mListener;
    private POSTPoll postPoll;

    public PollCreateFragment() {
    }

    public static PollCreateFragment newInstance() {
        return new PollCreateFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_poll_create, container, false);
        questionET = (EditText) view.findViewById(R.id.question_editText);
        passwordET = (EditText) view.findViewById(R.id.password_editText);
        view.findViewById(R.id.poll_create_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question = questionET.getText().toString();
                password = passwordET.getText().toString();
                if (question.length() > 10) {
                    submitPoll(question, password);
                }
            }
        });

        return view;
    }

    public void submitPoll(String question, String password) {
        postPoll = new POSTPoll(question, password, this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPollCreated(Poll poll) {
        if (mListener != null)
            mListener.onReceivePoll(poll);
        postPoll.detachListener();
    }

    public interface OnFragmentInteractionListener {
        void onReceivePoll(Poll poll);
    }
}
